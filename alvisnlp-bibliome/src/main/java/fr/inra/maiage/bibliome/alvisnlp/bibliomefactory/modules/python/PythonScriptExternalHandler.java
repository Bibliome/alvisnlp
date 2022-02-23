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
			if (!getModule().getUpdate()) {
				Corpus corpus = getAnnotable();
				corpus.clearDocuments();
				corpus.clearFeatures();
			}
			updateCorpus(jCorpus);
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
	
	@SuppressWarnings("unchecked")
	private static <T> List<T> getList(JSONArray j) {
		return j;
	}

	private void updateCorpus(JSONObject jCorpus) {
		Corpus corpus = getAnnotable();
		PythonScript owner = getModule();
		if (owner.getUpdate()) {
			updateFeatures(corpus, jCorpus);
		}
		else {
			addFeatures(corpus, jCorpus);
		}
		if (owner.getUpdate()) {
			for (JSONObject jDoc : getObjectList(jCorpus, "ddocuments")) {
				updateDocument(jDoc);
			}
		}
		for (JSONObject jDoc : getObjectList(jCorpus, "documents")) {
			createDocument(jDoc);
		}
	}

	@SuppressWarnings("unchecked")
	private void updateFeatures(Element elt, JSONObject j) {
		elt.addMultiFeatures(getObject(j, "df"));
	}
	
	@SuppressWarnings("unchecked")
	private void addFeatures(Element elt, JSONObject j) {
		elt.addMultiFeatures(getObject(j, "f"));
	}

	private void updateDocument(JSONObject jDoc) {
		Corpus corpus = getAnnotable();
		Document doc = Document.getDocument(getModule(), corpus, getString(jDoc, "identifier"));
		updateFeatures(doc, jDoc);
		Collection<Pair<Tuple,JSONObject>> tupleArgs = new ArrayList<Pair<Tuple,JSONObject>>();
		Map<String,Element> elementMap = collectElements(doc); 
		JSONObject dsections = getObject(jDoc, "dsections");
		for (Section sec : Iterators.loop(doc.sectionIterator())) {
			String id = sec.getStringId();
			if (dsections.containsKey(id)) {
				updateSection(elementMap, tupleArgs, sec, getObject(dsections, id));
			}
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
		updateFeatures(sec, jSec);
		JSONObject dannotations = getObject(jSec, "dannotations");
		Map<String,Annotation> annotationMap = new HashMap<String,Annotation>();
		for (Annotation a : sec.getAllAnnotations()) {
			String id = a.getStringId();
			annotationMap.put(id, a);
			if (dannotations.containsKey(id)) {
				updateAnnotation(a, getObject(dannotations, id));
			}
		}
		for (JSONObject jA : getObjectList(jSec, "annotations")) {
			Annotation a = createAnnotation(sec, jA);
			addToMap(annotationMap, jA, a);
			addToMap(elementMap, jA, a);
		}
		JSONObject jLayers = getObject(jSec, "dlayers");
		for (String layerName : getKeys(jLayers)) {
			fillLayer(sec, annotationMap, jLayers, layerName);
		}
		for (JSONObject jRel : getObjectList(jSec, "drelations")) {
			String relName = getString(jRel, "name");
			Relation rel = sec.ensureRelation(getModule(), relName);
			updateRelation(elementMap, tupleArgs, rel, jRel);
		}
		for (JSONObject jRel : getObjectList(jSec, "relations")) {
			Relation rel = createRelation(elementMap, tupleArgs, sec, jRel);
			addToMap(elementMap, jRel, rel);
		}
	}

	private void updateAnnotation(Annotation a, JSONObject jA) {
		updateFeatures(a, jA);
	}
	
	private void updateRelation(Map<String,Element> elementMap, Collection<Pair<Tuple,JSONObject>> tupleArgs, Relation rel, JSONObject jRel) {
		updateFeatures(rel, jRel);
		JSONObject dtuples = getObject(jRel, "dtuples");
		for (Tuple t : rel.getTuples()) {
			String id = t.getStringId();
			if (dtuples.containsKey(id)) {
				JSONObject jT = getObject(dtuples, id);
				updateTuple(t, jT);
				tupleArgs.add(new Pair<Tuple,JSONObject>(t, getObject(jT, "dargs")));
			}
		}
		for (JSONObject jT : getObjectList(jRel, "tuples")) {
			Tuple t = createTuple(rel, jT);
			addToMap(elementMap, jT, t);
			tupleArgs.add(new Pair<Tuple,JSONObject>(t, getObject(jT, "args")));
		}
	}

	private void updateTuple(Tuple t, JSONObject jT) {
		updateFeatures(t, jT);
	}

	private void createDocument(JSONObject jDoc) {
		Corpus corpus = getAnnotable();
		Document doc = Document.getDocument(getModule(), corpus, getString(jDoc, "identifier"));
		addFeatures(doc, jDoc);
		Collection<Pair<Tuple,JSONObject>> tupleArgs = new ArrayList<Pair<Tuple,JSONObject>>();
		Map<String,Element> elementMap = new HashMap<String,Element>();
		addToMap(elementMap, jDoc, doc);
		for (JSONObject jSec : getObjectList(jDoc, "sections")) {
			createSection(elementMap, tupleArgs, doc, jSec);
		}
		setTupleArguments(elementMap, tupleArgs);
	}

	private static <E extends Element> void addToMap(Map<String,E> elementMap, JSONObject j, E elt) {
		elementMap.put(getString(j, "id"), elt);
	}

	private void createSection(Map<String,Element> elementMap, Collection<Pair<Tuple,JSONObject>> tupleArgs, Document doc, JSONObject jSec) {
		Section sec = new Section(getModule(), doc, getString(jSec, "name"), getString(jSec, "contents"));
		addFeatures(sec, jSec);
		Map<String,Annotation> annotationMap = new HashMap<String,Annotation>();
		for (JSONObject jA : getObjectList(jSec, "annotations")) {
			Annotation a = createAnnotation(sec, jA);
			addToMap(annotationMap, jA, a);
			addToMap(elementMap, jA, a);
		}
		JSONObject jLayers = getObject(jSec, "layers");
		for (String layerName : getKeys(jLayers)) {
			fillLayer(sec, annotationMap, jLayers, layerName);
		}
		for (JSONObject jRel : getObjectList(jSec, "relations")) {
			Relation rel = createRelation(elementMap, tupleArgs, sec, jRel);
			addToMap(elementMap, jRel, rel);
		}
	}

	private Annotation createAnnotation(Section sec, JSONObject jA) {
		JSONArray jOff = (JSONArray) jA.get("off");
		Annotation a = new Annotation(getModule(), sec, (int) (long) jOff.get(0), (int) (long) jOff.get(1));
		addFeatures(a, jA);
		return a;
	}

	private void fillLayer(Section sec, Map<String, Annotation> annotationMap, JSONObject jLayers, String layerName) {
		Layer layer = sec.ensureLayer(layerName);
		JSONArray jRefs = getArray(jLayers, layerName);
		List<String> refs = getList(jRefs); // XXX should be shorter
		for (String ref : refs) {
			Annotation a = annotationMap.get(ref);
			layer.add(a);
		}
	}
	
	private Relation createRelation(Map<String,Element> elementMap, Collection<Pair<Tuple,JSONObject>> tupleArgs, Section sec, JSONObject jRel) {
		Relation rel = sec.ensureRelation(getModule(), getString(jRel, "name"));
		addFeatures(rel, jRel);
		for (JSONObject jT : getObjectList(jRel, "tuples")) {
			Tuple t = createTuple(rel, jT);
			addToMap(elementMap, jT, t);
			tupleArgs.add(new Pair<Tuple,JSONObject>(t, getObject(jT, "args")));
		}
		return rel;
	}
	
	private Tuple createTuple(Relation rel, JSONObject jT) {
		Tuple t = new Tuple(getModule(), rel);
		addFeatures(t, jT);
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
