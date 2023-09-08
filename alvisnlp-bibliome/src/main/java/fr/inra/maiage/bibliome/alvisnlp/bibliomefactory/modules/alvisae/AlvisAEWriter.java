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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisae;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.converters.expression.parser.ExpressionParser;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisae.AlvisAEWriter.AlvisAEExportResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.EvaluatorMapping;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.files.OutputDirectory;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

@AlvisNLPModule
public class AlvisAEWriter extends SectionModule<AlvisAEExportResolvedObjects> {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

	private Integer owner = 0;
	private AnnotationSet[] annotationSets;
	private OutputDirectory outDir;
	private Expression fileName = ExpressionParser.parseUnsafe("@id ^ \".json\"");
	private InputFile schemaFile;
	private Expression documentDescription;
	private ExpressionMapping documentProperties = new ExpressionMapping();
	private Boolean publish = false;

	class AlvisAEExportResolvedObjects extends SectionResolvedObjects {
		private final AnnotationSet.Resolved[] annotationSets;
		private final Evaluator fileName;
		private final Evaluator documentDescription;
		private final EvaluatorMapping documentProperties;

		private AlvisAEExportResolvedObjects(ProcessingContext ctx) throws ResolverException {
			super(ctx, AlvisAEWriter.this);
			annotationSets = rootResolver.resolveArray(AlvisAEWriter.this.annotationSets, AnnotationSet.Resolved.class);
			fileName = AlvisAEWriter.this.fileName.resolveExpressions(rootResolver);
			documentDescription = rootResolver.resolveNullable(AlvisAEWriter.this.documentDescription);
			documentProperties = rootResolver.resolveNullable(AlvisAEWriter.this.documentProperties);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesArray(annotationSets, defaultType);
			fileName.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesNullable(documentDescription, defaultType);
			nameUsage.collectUsedNamesNullable(documentProperties, defaultType);
		}
	}

	@Override
	protected AlvisAEExportResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new AlvisAEExportResolvedObjects(ctx);
	}

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		try {
			AlvisAEExportResolvedObjects resObj = getResolvedObjects();
			CadixeExportContext exportContext = new CadixeExportContext(ctx);
			if (schemaFile != null) {
				Reader r = new FileReader(schemaFile);
				exportContext.schema = JSONValue.parse(r);
				if (exportContext.schema == null)
					throw new ProcessingException("could not parse " + schemaFile + " as JSON");
				r.close();
			}
			for (Document doc : Iterators.loop(documentIterator(exportContext.evalCtx, corpus))) {
				JSONObject json = documentToJSON(doc, exportContext);
				OutputFile outFile = new OutputFile(outDir, resObj.fileName.evaluateString(exportContext.evalCtx, doc));
				TargetStream target = new FileTargetStream("UTF-8", outFile);
				Writer w = target.getWriter();
				json.writeJSONString(w);
				w.close();
			}
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject documentToJSON(Document doc, CadixeExportContext ctx) {
		AlvisAEExportResolvedObjects resObj = getResolvedObjects();
		JSONObject result = new JSONObject();
		result.put("document", ctx.newDocument(doc));
		JSONArray sets = new JSONArray();
		for (AnnotationSet.Resolved set : resObj.annotationSets)
			sets.add(set.toJSON(doc, resObj.getSectionFilter(), ctx));
		result.put("annotation", sets);
		if (ctx.schema != null)
			result.put("schema", ctx.schema);
		ctx.checkUnsoundReferences();
		return result;
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	class CadixeExportContext {
		private final ProcessingContext ctx;
		private final Logger logger;
		final EvaluationContext evalCtx;
		private int docID = 0;
		final String timestamp = DATE_FORMAT.format(new Date());
		private Object schema;
		private final AnnotationReference.Record<Element> annotations = new AnnotationReference.Record<Element>();

		CadixeExportContext(ProcessingContext ctx) {
			super();
			this.ctx = ctx;
			logger = getLogger(ctx);
			evalCtx = new EvaluationContext(logger);
		}

		void addAnnotationReference(Element elt, int annotationSet) {
			AnnotationReference ref = annotations.safeGet(elt);
			if (ref.getAnnotationSet() != null)
				getLogger(ctx).warning("duplicate export: " + elt);
			ref.setAnnotationSet(annotationSet);
		}

		AnnotationReference getAnnotationReference(Element elt) {
			return annotations.safeGet(elt);
		}

		boolean publish() {
			return getPublish();
		}

		@SuppressWarnings("unchecked")
		private JSONObject newDocument(Document doc) {
			AlvisAEExportResolvedObjects resObj = getResolvedObjects();
			annotations.clear();
			JSONObject result = new JSONObject();
			result.put("id", docID++);
			StringBuilder contents = new StringBuilder();
			for (Section sec : Iterators.loop(sectionIterator(evalCtx, doc)))
				contents.append(sec.getContents());
			result.put("contents", contents.toString());
			String descr = resObj.documentDescription.evaluateString(evalCtx, doc);
			if (descr.length() >= 128) {
				logger.info("document description too long: " + descr);
				descr = descr.substring(0, 128);
			}
			result.put("description", descr);
			JSONObject props = new JSONObject();
			if (resObj.documentProperties != null)
				for (Map.Entry<String,Evaluator> e : resObj.documentProperties.entrySet()) {
					JSONArray value = new JSONArray();
					value.add(e.getValue().evaluateString(evalCtx, doc));
					props.put(e.getKey(), value);
				}
			result.put("props", props);
			result.put("owner", owner);
			return result;
		}

		private void checkUnsoundReferences() {
			Logger logger = getLogger(ctx);
			for (Map.Entry<Element,AnnotationReference> e : annotations.entrySet()) {
				if (e.getValue().getAnnotationSet() == null)
					logger.warning("reference to unexported " + e.getKey());
			}
		}
	}

	@Param
	public Integer getOwner() {
		return owner;
	}

	@Param
	public AnnotationSet[] getAnnotationSets() {
		return annotationSets;
	}

	@Param
	public OutputDirectory getOutDir() {
		return outDir;
	}

	@Param(mandatory=false)
	public InputFile getSchemaFile() {
		return schemaFile;
	}

	@Param
	public Expression getDocumentDescription() {
		return documentDescription;
	}

	@Param(mandatory=false)
	public ExpressionMapping getDocumentProperties() {
		return documentProperties;
	}

	@Param
	public Expression getFileName() {
		return fileName;
	}

	@Param
	public Boolean getPublish() {
		return publish;
	}

	public void setPublish(Boolean publish) {
		this.publish = publish;
	}

	public void setFileName(Expression fileName) {
		this.fileName = fileName;
	}

	public void setOwner(Integer owner) {
		this.owner = owner;
	}

	public void setAnnotationSets(AnnotationSet[] annotationSets) {
		this.annotationSets = annotationSets;
	}

	public void setOutDir(OutputDirectory outDir) {
		this.outDir = outDir;
	}

	public void setSchemaFile(InputFile schemaFile) {
		this.schemaFile = schemaFile;
	}

	public void setDocumentDescription(Expression documentDescription) {
		this.documentDescription = documentDescription;
	}

	public void setDocumentProperties(ExpressionMapping documentProperties) {
		this.documentProperties = documentProperties;
	}
}
