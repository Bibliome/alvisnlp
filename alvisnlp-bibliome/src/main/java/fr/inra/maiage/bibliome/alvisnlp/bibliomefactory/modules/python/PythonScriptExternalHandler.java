package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.python;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Pair;
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
			JSONObject jCorpus = (JSONObject) parser.parse(r);
			boolean update = (boolean) jCorpus.get("update");
			if (!update) {
				Corpus corpus = getAnnotable();
				corpus.clearDocuments();
				corpus.clearFeatures();
			}
			updateCorpus(jCorpus, update);
		}
		catch (ParseException e) {
			throw new ModuleException(e);
		}
	}
	
	private static String getString(JSONObject j, String key) {
		return (String) j.get(key);
	}
	
	private static JSONObject getObject(JSONObject j, String key) {
		return (JSONObject) j.get(key);
	}
	
	private static JSONArray getArray(JSONObject j, String key) {
		return (JSONArray) j.get(key);
	}
		
	@SuppressWarnings("unchecked")
	private static List<JSONObject> getObjectList(JSONObject j, String key) {
		return (JSONArray) j.get(key);
	}
	
	@SuppressWarnings("unchecked")
	private static Collection<String> getKeys(JSONObject j) {
		return j.keySet();
	}
	
	private void updateCorpus(JSONObject jCorpus, boolean update) {
		Corpus corpus = getAnnotable();
		addFeatures(corpus, jCorpus, update);
		if (update) {
			for (JSONObject jDoc : getObjectList(jCorpus, "ddocuments")) {
				processDocument(jDoc, true);
			}
		}
		for (JSONObject jDoc : getObjectList(jCorpus, "documents")) {
			processDocument(jDoc, false);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addFeatures(Element elt, JSONObject j, boolean update) {
		elt.addMultiFeatures(getObject(j, update ? "df" : "f"));
	}

	private void processDocument(JSONObject jDoc, boolean update) {
		Corpus corpus = getAnnotable();
		Document doc = Document.getDocument(getModule(), corpus, getString(jDoc, "identifier"));
		addFeatures(doc, jDoc, update);
		Collection<Pair<Tuple,JSONObject>> tupleArgs = new ArrayList<Pair<Tuple,JSONObject>>();
		Map<String,Element> elementMap;
		if (update) {
			elementMap = collectElements(doc);
			JSONObject dsections = getObject(jDoc, "dsections");
			for (Section sec : Iterators.loop(doc.sectionIterator())) {
				String id = sec.getStringId();
				if (dsections.containsKey(id)) {
					updateSection(elementMap, tupleArgs, sec, getObject(dsections, id));
				}
			}
		}
		else {
			elementMap = new HashMap<String,Element>();
			addToMap(elementMap, jDoc, doc);
		}
		for (JSONObject jSec : getObjectList(jDoc, "sections")) {
			createSection(elementMap, tupleArgs, doc, jSec);
		}
		setTupleArguments(elementMap, tupleArgs);
	}

	private static Map<String,Element> collectElements(Document doc) {
		Map<String,Element> result = new HashMap<String,Element>();
		result.put(doc.getStringId(), doc);
		for (Section sec : Iterators.loop(doc.sectionIterator())) {
			result.put(sec.getStringId(), sec);
			for (Annotation a : sec.getAllAnnotations()) {
				result.put(a.getStringId(), a);
			}
			for (Relation rel : sec.getAllRelations()) {
				result.put(rel.getStringId(), rel);
				for (Tuple t : rel.getTuples()) {
					result.put(t.getStringId(), t);
				}
			}
		}
		return result;
	}

	private void updateSection(Map<String,Element> elementMap, Collection<Pair<Tuple,JSONObject>> tupleArgs, Section sec, JSONObject jSec) {
		addFeatures(sec, jSec, true);
		JSONObject dannotations = getObject(jSec, "dannotations");
		Map<String,Annotation> annotationMap = new HashMap<String,Annotation>();
		for (Annotation a : sec.getAllAnnotations()) {
			String id = a.getStringId();
			annotationMap.put(id, a);
			if (dannotations.containsKey(id)) {
				updateAnnotation(a, getObject(dannotations, id));
			}
		}
		createAnnotations(elementMap, annotationMap, sec, jSec);
		fillLayers(annotationMap, sec, jSec, "dlayers");
		for (JSONObject jRel : getObjectList(jSec, "drelations")) {
			String relName = getString(jRel, "name");
			Relation rel = sec.ensureRelation(getModule(), relName);
			updateRelation(elementMap, tupleArgs, rel, jRel);
		}
		createRelations(elementMap, tupleArgs, sec, jSec);
	}
	
	private void createAnnotations(Map<String,Element> elementMap, Map<String,Annotation> annotationMap, Section sec, JSONObject jSec) {
		for (JSONObject jA : getObjectList(jSec, "annotations")) {
			Annotation a = createAnnotation(sec, jA);
			addToMap(annotationMap, jA, a);
			addToMap(elementMap, jA, a);
		}
	}
	
	private void fillLayers(Map<String,Annotation> annotationMap, Section sec, JSONObject jSec, String key) {
		JSONObject jLayers = getObject(jSec, key);
		for (String layerName : getKeys(jLayers)) {
			fillLayer(sec, annotationMap, jLayers, layerName);
		}
	}
	
	private void createRelations(Map<String,Element> elementMap, Collection<Pair<Tuple,JSONObject>> tupleArgs, Section sec, JSONObject jSec) {
		for (JSONObject jRel : getObjectList(jSec, "relations")) {
			Relation rel = createRelation(elementMap, tupleArgs, sec, jRel);
			addToMap(elementMap, jRel, rel);
		}
	}

	private void updateAnnotation(Annotation a, JSONObject jA) {
		addFeatures(a, jA, true);
	}
	
	private void updateRelation(Map<String,Element> elementMap, Collection<Pair<Tuple,JSONObject>> tupleArgs, Relation rel, JSONObject jRel) {
		addFeatures(rel, jRel, true);
		JSONObject dtuples = getObject(jRel, "dtuples");
		for (Tuple t : rel.getTuples()) {
			String id = t.getStringId();
			if (dtuples.containsKey(id)) {
				JSONObject jT = getObject(dtuples, id);
				updateTuple(t, jT);
				tupleArgs.add(new Pair<Tuple,JSONObject>(t, getObject(jT, "dargs")));
			}
		}
		createTuples(elementMap, tupleArgs, rel, jRel);
	}
	
	private void createTuples(Map<String,Element> elementMap, Collection<Pair<Tuple,JSONObject>> tupleArgs, Relation rel, JSONObject jRel) {
		for (JSONObject jT : getObjectList(jRel, "tuples")) {
			Tuple t = createTuple(rel, jT);
			addToMap(elementMap, jT, t);
			tupleArgs.add(new Pair<Tuple,JSONObject>(t, getObject(jT, "args")));
		}
	}

	private void updateTuple(Tuple t, JSONObject jT) {
		addFeatures(t, jT, true);
	}

	private static <E extends Element> void addToMap(Map<String,E> elementMap, JSONObject j, E elt) {
		elementMap.put(getString(j, "id"), elt);
	}

	private void createSection(Map<String,Element> elementMap, Collection<Pair<Tuple,JSONObject>> tupleArgs, Document doc, JSONObject jSec) {
		Section sec = new Section(getModule(), doc, getString(jSec, "name"), getString(jSec, "contents"));
		addFeatures(sec, jSec, false);
		Map<String,Annotation> annotationMap = new HashMap<String,Annotation>();
		createAnnotations(elementMap, annotationMap, sec, jSec);
		fillLayers(annotationMap, sec, jSec, "layers");
		createRelations(elementMap, tupleArgs, sec, jSec);
	}

	private Annotation createAnnotation(Section sec, JSONObject jA) {
		JSONArray jOff = (JSONArray) jA.get("off");
		Annotation a = new Annotation(getModule(), sec, (int) (long) jOff.get(0), (int) (long) jOff.get(1));
		addFeatures(a, jA, false);
		return a;
	}

	private void fillLayer(Section sec, Map<String, Annotation> annotationMap, JSONObject jLayers, String layerName) {
		Layer layer = sec.ensureLayer(layerName);
		for (Object oRef : getArray(jLayers, layerName)) {
			Annotation a = annotationMap.get(oRef);
			layer.add(a);
		}
	}
	
	private Relation createRelation(Map<String,Element> elementMap, Collection<Pair<Tuple,JSONObject>> tupleArgs, Section sec, JSONObject jRel) {
		Relation rel = sec.ensureRelation(getModule(), getString(jRel, "name"));
		addFeatures(rel, jRel, false);
		createTuples(elementMap, tupleArgs, rel, jRel);
		return rel;
	}
	
	private Tuple createTuple(Relation rel, JSONObject jT) {
		Tuple t = new Tuple(getModule(), rel);
		addFeatures(t, jT, false);
		return t;
	}
	
	private void setTupleArguments(Map<String,Element> elementMap, Collection<Pair<Tuple,JSONObject>> tupleArgs) {
		for (Pair<Tuple,JSONObject> p : tupleArgs) {
			Tuple t = p.first;
			JSONObject j = p.second;
			for (String role : getKeys(j)) {
				String ref = getString(j, role);
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
