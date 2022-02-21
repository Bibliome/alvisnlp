package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.python;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

public class PythonScriptExternalHandler extends ExternalHandler<Corpus,PythonScript> {
	public PythonScriptExternalHandler(ProcessingContext<Corpus> processingContext, PythonScript module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		JsonSerializer serializer = new JsonSerializer();
		Map<String,Element> elementMap = new HashMap<String,Element>();
		JSONObject json = getAnnotable().accept(serializer, elementMap);
		File input = getInputFile();
		TargetStream target = new FileTargetStream("UTF-8", input.getAbsolutePath());
		try (Writer out = target.getWriter()) {
			json.writeJSONString(out);
		}
	}

	@Override
	protected void collect() throws IOException, ModuleException {
		File output = getOutputFile();
		SourceStream source = new FileSourceStream("UTF-8", output.getAbsolutePath());
		try (Reader r = source.getReader()) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(r);
			deserializeCorpus(json);
		}
		catch (ParseException e) {
			throw new ModuleException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void deserializeCorpus(JSONObject json) {
		Corpus corpus = getAnnotable();
		corpus.clearDocuments();
		corpus.clearFeatures();
		corpus.addFeatures((JSONObject) json.get("f"));
		deserializeDocuments(corpus, (JSONArray) json.get("documents"));
	}
	
	private static void setElementFeaturesAndMap(Map<String,Element> elementMap, Element elt, JSONObject j) {
		elementMap.put((String) j.get("id"), elt);
		JSONObject jFeatures = (JSONObject) j.get("f");
		for (Object oKey : jFeatures.keySet()) {
			String key = (String) oKey;
			JSONArray values = (JSONArray) jFeatures.get(key);
			for (Object oValue : values) {
				String value = (String) oValue;
				elt.addFeature(key, value);
			}
		}
	}
	
	private void deserializeDocuments(Corpus corpus, JSONArray jDocs) {
		for (Object oDoc : jDocs) {
			Map<String,Element> elementMap = new HashMap<String,Element>();
			JSONObject jDoc = (JSONObject) oDoc;
			Document doc = Document.getDocument(getModule(), corpus, (String) jDoc.get("identifier"));
			setElementFeaturesAndMap(elementMap, doc, jDoc);
			deserializeSections(elementMap, doc, (JSONArray) jDoc.get("sections"));
		}
	}
	
	private void deserializeSections(Map<String,Element> elementMap, Document doc, JSONArray jSecs) {
		for (Object oSec : jSecs) {
			JSONObject jSec = (JSONObject) oSec;
			Section sec = new Section(getModule(), doc, (String) jSec.get("name"), (String) jSec.get("contents"));
			setElementFeaturesAndMap(elementMap, sec, jSec);
			Map<String,Annotation> annotationMap = deserializeAnnotations(elementMap, sec, (JSONArray) jSec.get("annotations"));
			deserializeLayers(annotationMap, sec, (JSONObject) jSec.get("layers"));
			deserializeRelations(elementMap, sec, (JSONArray) jSec.get("relations"));
		}
	}
	
	private Map<String, Annotation> deserializeAnnotations(Map<String,Element> elementMap, Section sec, JSONArray jAnnotations) {
		Map<String,Annotation> annotationMap = new HashMap<String,Annotation>();
		for (Object oA : jAnnotations) {
			JSONObject jA = (JSONObject) oA;
			JSONArray jOff = (JSONArray) jA.get("off");
			Annotation a = new Annotation(getModule(), sec, (int) (long) jOff.get(0), (int) (long) jOff.get(1));
			setElementFeaturesAndMap(elementMap, a, jA);
			annotationMap.put((String) jA.get("id"), a);
		}
		return annotationMap;
	}
	
	private void deserializeLayers(Map<String,Annotation> annotationMap, Section sec, JSONObject jLayers) {
		for (Object oLn : jLayers.keySet()) {
			String ln = (String) oLn;
			Layer layer = sec.ensureLayer(ln);
			JSONArray jAnnotationRefs = (JSONArray) jLayers.get(oLn);
			for (Object aref : jAnnotationRefs) {
				Annotation a = annotationMap.get(aref);
				layer.add(a);
			}
		}
	}
	
	private void deserializeRelations(Map<String,Element> elementMap, Section sec, JSONArray jRels) {
		for (Object oRel : jRels) {
			JSONObject jRel = (JSONObject) oRel;
			Relation rel = sec.ensureRelation(getModule(), (String) jRel.get("name"));
			setElementFeaturesAndMap(elementMap, rel, jRel);
			JSONArray jTuples = (JSONArray) jRel.get("tuples");
			Map<String,Tuple> tupleMap = deserializeTuples(elementMap, rel, jTuples);
			setTupleArguments(tupleMap, elementMap, jTuples);
		}
	}
	
	private Map<String, Tuple> deserializeTuples(Map<String,Element> elementMap, Relation rel, JSONArray jTuples) {
		Map<String,Tuple> tupleMap = new HashMap<String,Tuple>();
		for (Object oT : jTuples) {
			JSONObject jT = (JSONObject) oT;
			Tuple t = new Tuple(getModule(), rel);
			setElementFeaturesAndMap(elementMap, t, jT);
			tupleMap.put((String) jT.get("id"), t);
		}
		return tupleMap;
	}
	
	private static void setTupleArguments(Map<String,Tuple> tupleMap, Map<String,Element> elementMap, JSONArray jTuples) {
		for (Object oT : jTuples) {
			JSONObject jT = (JSONObject) oT;
			Tuple t = tupleMap.get(jT.get("id"));
			JSONObject jArgs = (JSONObject) jT.get("args");
			for (Object oRole : jArgs.keySet()) {
				String role = (String) oRole;
				String ref = (String) jArgs.get(role);
				Element arg = elementMap.get(ref);
				t.setArgument(role, arg);
			}
		}
	}

	@Override
	protected String getPrepareTask() {
		return "alvisnlp-to-json";
	}

	@Override
	protected String getExecTask() {
		return "exec-script";
	}

	@Override
	protected String getCollectTask() {
		return "json-to-alvisnlp";
	}

	@Override
	protected List<String> getCommandLine() {
		return Arrays.asList(getModule().getCommandLine());
	}

	@Override
	protected void updateEnvironment(Map<String,String> env) {
		Mapping environment = getModule().getEnvironment();
		if (environment != null) {
			env.putAll(environment);
		}
	}

	@Override
	protected File getWorkingDirectory() {
		return getModule().getWorkingDirectory();
	}

	@Override
	protected String getInputFileame() {
		return "input.json";
	}

	@Override
	protected String getOutputFilename() {
		return "output.json";
	}

}
