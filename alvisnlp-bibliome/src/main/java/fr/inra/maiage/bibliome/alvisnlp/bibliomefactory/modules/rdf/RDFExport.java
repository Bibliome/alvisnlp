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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rdf;

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
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rdf.RDFExport.RDFExportResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.LoggingUtils;
import fr.inra.maiage.bibliome.util.files.OutputDirectory;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

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
			LoggingUtils.configureSilentLog4J();
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
				throw new ProcessingException(e);
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
