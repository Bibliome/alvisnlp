/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


package org.bibliome.alvisnlp.modules.rdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.jena.fuseki.embedded.FusekiEmbeddedServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.shared.PrefixMapping;
import org.apache.log4j.BasicConfigurator;
import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.alvisnlp.modules.rdf.RDFExport.RDFExportResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.files.OutputDirectory;
import org.bibliome.util.files.OutputFile;
import org.bibliome.util.streams.FileTargetStream;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;
import alvisnlp.module.types.Mapping;

@AlvisNLPModule(beta=true)
public class RDFExport extends CorpusModule<RDFExportResolvedObjects> {
	private OutputDirectory outDir;
	private Expression files;
	private Expression fileName;
	private String charset = "UTF-8";
	private Expression[] statements;
	private RDFFormat format = RDFFormat.RDFXML_ABBREV;
	private Mapping prefixes = new Mapping();
	private Boolean startServer = false;

	static class RDFExportResolvedObjects extends ResolvedObjects {
		private final Evaluator files;
		private final Evaluator fileName;
		private final Model model;
		private final Evaluator[] statements;
		
		private RDFExportResolvedObjects(ProcessingContext<Corpus> ctx, RDFExport module) throws ResolverException {
			super(ctx, module);
			files = module.files.resolveExpressions(rootResolver);
			fileName = module.fileName.resolveExpressions(rootResolver);
			model = createModel(module);
			LibraryResolver stmtResolver = getStatementResolver();
			statements = stmtResolver.resolveArray(module.statements, Evaluator.class);
		}

		private static Model createModel(RDFExport module) {
			BasicConfigurator.configure();
			Model model = ModelFactory.createDefaultModel();
			model.setNsPrefixes(PrefixMapping.Standard);
			model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
			model.setNsPrefixes(module.prefixes);
			return model;
		}

		private LibraryResolver getStatementResolver() throws ResolverException {
			LibraryResolver result = new LibraryResolver(rootResolver);
			ElementResourceMap resourceMap = new ElementResourceMap(model);
			URILibrary uriLib = new URILibrary(resourceMap);
			result.addLibrary(uriLib);
			StatementLibrary stmtLib = new StatementLibrary(model, resourceMap);
			result.addLibrary(stmtLib);
			Map<String,String> map = model.getNsPrefixMap();
			for (Map.Entry<String,String> e : map.entrySet()) {
				String name = e.getKey();
				String prefix = e.getValue();
				PrefixLibrary lib = new PrefixLibrary(name, prefix);
				result.addLibrary(lib);
			}
			return result;
		}
		
		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			files.collectUsedNames(nameUsage, defaultType);
			fileName.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesArray(statements, defaultType);
		}
	}
	
	@Override
	protected RDFExportResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new RDFExportResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		RDFExportResolvedObjects resObj = getResolvedObjects();
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);

		int count = 0;
		for (Element elt : Iterators.loop(resObj.files.evaluateElements(evalCtx, corpus))) {
			fillModel(ctx, evalCtx, elt);
			count += resObj.model.size();
			String fn = resObj.fileName.evaluateString(evalCtx, elt);
			OutputFile f = new OutputFile(outDir, fn);
			TargetStream ts = new FileTargetStream(charset, f);
			try (OutputStream out = ts.getOutputStream()) {
				writeModel(ctx, out, resObj.model);
				resObj.model.removeAll();
			}
			catch (IOException e) {
				resObj.model.close();
				rethrow(e);
			}
		}
		logger.info("wrote " + count + " statements");
		if (startServer) {
			Dataset ds = DatasetFactory.assemble(resObj.model);
			FusekiEmbeddedServer server = FusekiEmbeddedServer.create().add("alvisnlp", ds).build();
			server.start();
		}
	}
	
	@TimeThis(task="write-rdf", category=TimerCategory.EXPORT)
	protected void writeModel(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, OutputStream out, Model model) {
		RDFDataMgr.write(out, model, format);
	}

	@TimeThis(task="create-model")
	protected void fillModel(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, EvaluationContext evalCtx, Element elt) {
		RDFExportResolvedObjects resObj = getResolvedObjects();
		for (Evaluator stev : resObj.statements) {
			Iterators.deplete(stev.evaluateElements(evalCtx, elt));
		}
	}

	@Param
	public OutputDirectory getOutDir() {
		return outDir;
	}

	@Param
	public Expression getFiles() {
		return files;
	}

	@Param
	public Expression getFileName() {
		return fileName;
	}

	@Param
	public String getCharset() {
		return charset;
	}

	@Param
	public Expression[] getStatements() {
		return statements;
	}

	@Param
	public RDFFormat getFormat() {
		return format;
	}

	@Param
	public Mapping getPrefixes() {
		return prefixes;
	}

	public void setOutDir(OutputDirectory outDir) {
		this.outDir = outDir;
	}

	public void setFiles(Expression files) {
		this.files = files;
	}

	public void setFileName(Expression fileName) {
		this.fileName = fileName;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setStatements(Expression[] statements) {
		this.statements = statements;
	}

	public void setFormat(RDFFormat format) {
		this.format = format;
	}

	public void setPrefixes(Mapping prefixes) {
		this.prefixes = prefixes;
	}
}
