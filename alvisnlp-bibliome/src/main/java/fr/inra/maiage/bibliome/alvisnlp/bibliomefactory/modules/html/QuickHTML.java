package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.html;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.ProcessingException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DefaultExpressions;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.html.QuickHTML.QuickHTMLResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.OutputDirectory;
import fr.inra.maiage.bibliome.util.files.OutputFile;

@AlvisNLPModule(beta=true)
public class QuickHTML extends SectionModule<QuickHTMLResolvedObjects> {
	private OutputDirectory outDir;
	private String layoutLayer = null;
	private String tagFeature = "tag";
	private String[] mentionLayers = null;
	private String typeFeature = null;
	private String[] features = new String[0];
	private Mapping colorMap = null;
	private String[] colors = null;
	private Expression documentTitle = DefaultExpressions.DOCUMENT_ID;

	public static class QuickHTMLResolvedObjects extends SectionResolvedObjects {
		private final Evaluator documentTitle;

		public QuickHTMLResolvedObjects(ProcessingContext<Corpus> ctx, QuickHTML module) throws ResolverException {
			super(ctx, module);
			this.documentTitle = module.documentTitle.resolveExpressions(rootResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			documentTitle.collectUsedNames(nameUsage, defaultType);
		}
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		try {
			if (!outDir.exists() && !outDir.mkdirs()) {
				throw new ProcessingException("could not create directory " + outDir.getAbsolutePath());
			}
			copyResource(logger, "index.html");
			copyResource(logger, "fragments.js");
			copyResource(logger, "quick-html.js");
			copyResource(logger, "quick-html.css");
			writeData(logger, ctx, corpus);
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

	private OutputStream copyResource(Logger logger, String name) throws IOException {
		logger.info("copying " + name);
		try (InputStream is = QuickHTML.class.getResourceAsStream(name)) {
			OutputStream out = null;
			try {
				out = new FileOutputStream(new OutputFile(outDir, name));
				byte[] buf = new byte[1024];
				while (true) {
					int n = is.read(buf);
					if (n == -1) {
						break;
					}
					out.write(buf, 0, n);
				}
				return out;
			}
			catch (IOException e) {
				if (out != null) {
					out.close();
				}
				throw e;
			}
		}
	}
	
	private void writeData(Logger logger, ProcessingContext<Corpus> ctx, Corpus corpus) throws FileNotFoundException {
		logger.info("writing data.js");
		try (PrintStream out = new PrintStream(new OutputFile(outDir, "data.js"))) {
			JSONObject j = buildCorpusJSON(ctx, corpus);
			String s = j.toJSONString();
			out.print("DATA = ");
			out.print(s);
			out.println(";");
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildCorpusJSON(ProcessingContext<Corpus> ctx, Corpus corpus) {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		JSONObject result = new JSONObject();
		result.put("documents", buildDocumentsJSON(evalCtx, corpus));
		result.put("colors", buildColorsJSON());
		result.put("features", buildFeaturesJSON());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray buildFeaturesJSON() {
		JSONArray result = new JSONArray();
		for (String f : features) {
			result.add(f);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private Object buildColorsJSON() {
		if (colorMap != null) {
			JSONObject result = new JSONObject();
			for (Map.Entry<String,String> e : colorMap.entrySet()) {
				result.put(e.getKey(), e.getValue());
			}
			return result;
		}
		if (colors != null) {
			JSONArray result = new JSONArray();
			for (String c : colors) {
				result.add(c);
			}
			return result;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray buildDocumentsJSON(EvaluationContext evalCtx, Corpus corpus) {
		JSONArray result = new JSONArray();
		for (Document doc : Iterators.loop(documentIterator(evalCtx, corpus))) {
			JSONObject jDoc = buildDocumentJSON(evalCtx, doc);
			result.add(jDoc);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildDocumentJSON(EvaluationContext evalCtx, Document doc) {
		JSONObject result = new JSONObject();
		result.put("id", doc.getId());
		QuickHTMLResolvedObjects resObj = getResolvedObjects();
		result.put("title", resObj.documentTitle.evaluateString(evalCtx, doc));
		result.put("sections", buildSectionsJSON(evalCtx, doc));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	JSONArray buildSectionsJSON(EvaluationContext evalCtx, Document doc) {
		JSONArray result = new JSONArray();
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, doc))) {
			result.add(buildSectionJSON(evalCtx, sec));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildSectionJSON(EvaluationContext evalCtx, Section sec) {
		JSONObject result = new JSONObject();
		result.put("name", sec.getName());
		result.put("text", sec.getContents());
		JSONArray layouts = buildLayoutsJSON(sec);
		result.put("layouts", layouts);
		JSONArray mentions = buildMentionsJSON(sec);
		result.put("mentions", mentions);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray buildLayoutsJSON(Section sec) {
		JSONArray result = new JSONArray();
		if ((layoutLayer != null) && sec.hasLayer(layoutLayer)) {
			for (Annotation a : sec.getLayer(layoutLayer)) {
				JSONArray jLay = buildFragCtor(a, tagFeature);
				result.add(jLay);
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray buildMentionsJSON(Section sec) {
		JSONArray result = new JSONArray();
		for (String mLayer : mentionLayers) {
			if (sec.hasLayer(mLayer)) {
				for (Annotation a : sec.getLayer(mLayer)) {
					JSONObject jMent = buildMentionJSON(a);
					result.add(jMent);
				}
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject buildMentionJSON(Annotation a) {
		JSONObject result = new JSONObject();
		JSONArray ctor = buildFragCtor(a, typeFeature);
		result.put("ctor", ctor);
		JSONObject data = buildMentionData(a);
		result.put("data", data);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray buildFragCtor(Annotation a, String headFeature) {
		JSONArray result = new JSONArray();
		String head = a.getLastFeature(headFeature);
		result.add(head);
		result.add(a.getStart());
		result.add(a.getEnd());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject buildMentionData(Annotation a) {
		JSONObject result = new JSONObject();
		for (String k : features) {
			result.put(k, a.getLastFeature(k));
		}
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

	@Override
	protected QuickHTMLResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new QuickHTMLResolvedObjects(ctx, this);
	}

	@Param
	public OutputDirectory getOutDir() {
		return outDir;
	}

	@Param(mandatory = false, nameType = NameType.LAYER)
	public String getLayoutLayer() {
		return layoutLayer;
	}

	@Param(nameType = NameType.FEATURE)
	public String getTagFeature() {
		return tagFeature;
	}

	@Param(nameType = NameType.LAYER)
	public String[] getMentionLayers() {
		return mentionLayers;
	}

	@Param(nameType = NameType.FEATURE)
	public String getTypeFeature() {
		return typeFeature;
	}

	@Param(nameType = NameType.FEATURE)
	public String[] getFeatures() {
		return features;
	}

	@Param(mandatory = false)
	public Mapping getColorMap() {
		return colorMap;
	}

	@Deprecated
	@Param(mandatory = false)
	public String[] getColors() {
		return colors;
	}

	@Deprecated
	@Param(nameType = NameType.FEATURE)
	public String getClassFeature() {
		return typeFeature;
	}
	
	@Deprecated
	@Param(nameType = NameType.LAYER)
	public String[] getLayers() {
		return mentionLayers;
	}
	
	@Param
	public Expression getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(Expression documentTitle) {
		this.documentTitle = documentTitle;
	}

	@Deprecated
	public void setLayers(String[] layers) {
		this.mentionLayers = layers;
	}
	
	@Deprecated
	public void setClassFeature(String classFeature) {
		this.typeFeature = classFeature;
	}
	
	@Deprecated
	public void setColors(String[] colors) {
		this.colors = colors;
	}

	public void setOutDir(OutputDirectory outDir) {
		this.outDir = outDir;
	}

	public void setLayoutLayer(String layoutLayer) {
		this.layoutLayer = layoutLayer;
	}

	public void setTagFeature(String tagFeature) {
		this.tagFeature = tagFeature;
	}

	public void setMentionLayers(String[] mentionLayers) {
		this.mentionLayers = mentionLayers;
	}

	public void setTypeFeature(String typeFeature) {
		this.typeFeature = typeFeature;
	}

	public void setFeatures(String[] features) {
		this.features = features;
	}

	public void setColorMap(Mapping colorMap) {
		this.colorMap = colorMap;
	}
}
