package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.python;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.Files;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

public class PythonScriptExternalHandler extends ExternalHandler<PythonScriptBase> {
	private final EvaluationContext evalCtx;

	public PythonScriptExternalHandler(ProcessingContext processingContext, PythonScriptBase module, Corpus annotable) {
		super(processingContext, module, annotable);
		this.evalCtx = new EvaluationContext(getLogger());
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		JsonSerializer serializer = new JsonSerializer(evalCtx, getModule().getResolvedObjects());
		JSONObject json = getCorpus().accept(serializer, null);
		File input = getInputFile();
		TargetStream target = new FileTargetStream("UTF-8", input.getAbsolutePath());
		try (Writer out = target.getWriter()) {
			json.writeJSONString(out);
		}
		if (getModule().isScriptCopy()) {
			copyScript();
		}
	}
	
	private void copyScript() throws IOException {
		try (InputStream is = getModule().getScript().getInputStream()) {
			File scriptFile = getScriptCopy();
			Files.copy(is, scriptFile, 1024, true);
		}
	}
	
	private File getScriptCopy() {
		return new File(getTempDir(), "script.py");
	}

	@Override
	public File getOutputFile() {
		File outFile = getModule().getOutputFile();
		if (outFile != null) {
			return outFile;
		}
		return super.getOutputFile();
	}

	@Override
	protected void collect() throws IOException, ModuleException {
		File outFile = getModule().getOutputFile();
		if (outFile != null) {
			return;
		}
		File output = getOutputFile();
		SourceStream source = new FileSourceStream("UTF-8", output.getAbsolutePath());
		try (Reader r = source.getReader()) {
			JSONParser parser = new JSONParser();
			JSONObject jCorpus = (JSONObject) parser.parse(r);
			handleCorpusEvents(jCorpus);
		}
		catch (ParseException e) {
			throw new ModuleException(e);
		}
	}
	
	private static String getString(JSONObject j, String key) {
		return (String) j.get(key);
	}
	
	private static int getInt(JSONObject j, String key) {
		return (int) (long) j.get(key);
	}
	
	private static JSONObject getObject(JSONObject j, String key) {
		return (JSONObject) j.get(key);
	}
	
	@SuppressWarnings("unchecked")
	private static List<JSONObject> getObjectList(JSONObject j, String key) {
		return (JSONArray) j.get(key);
	}
	
	@SuppressWarnings("unchecked")
	private static Collection<String> getKeys(JSONObject j) {
		return j.keySet();
	}
	
	private static class EventCode {
		private static final int ADD_FEATURE = 1;
		private static final int REMOVE_FEATURE = 2;
		private static final int CREATE_DOCUMENT = 3;
		private static final int DELETE_DOCUMENT = 4;
		private static final int CREATE_SECTION = 5;
		private static final int DELETE_SECTION = 6;
		private static final int CREATE_ANNOTATION = 7;
		private static final int ADD_TO_LAYER = 9;
		private static final int REMOVE_FROM_LAYER = 10;
		private static final int CREATE_RELATION = 11;
		private static final int DELETE_RELATION = 12;
		private static final int CREATE_TUPLE = 13;
		private static final int DELETE_TUPLE = 14;
		private static final int SET_ARGUMENT = 15;
	}
	
	private int getCode(JSONObject jEvent) {
		return getInt(jEvent, "_");
	}
	
	private String getElementId(JSONObject jEvent) {
		return getString(jEvent, "_id");
	}
	
	private List<JSONObject> getEvents(JSONObject j) {
		return getObjectList(j, "_ev");
	}
	
	private boolean handleFeatureEvent(Element elt, JSONObject jEvent) {
		switch (getCode(jEvent)) {
			case EventCode.ADD_FEATURE: {
				String key = getString(jEvent, "k");
				String value = getString(jEvent, "v");
				elt.addFeature(key, value);
				return true;
			}
			case EventCode.REMOVE_FEATURE: {
				String key = getString(jEvent, "k");
				Object oValue = jEvent.get("v");
				if (oValue == null) {
					elt.removeFeatures(key);
				}
				else {
					elt.removeFeature(key, (String) oValue);
				}
				return true;
			}
		}
		return false;
	}
	
	private void handleCorpusEvents(JSONObject j) {
		Corpus corpus = getCorpus();
		Map<String,Document> docMap = new HashMap<String,Document>();
		PythonScriptBase.PythonScriptResolvedObjects resObj = getModule().getResolvedObjects();
		for (Document doc : Iterators.loop(corpus.documentIterator(evalCtx, resObj.getDocumentFilter()))) {
			docMap.put(doc.getStringId(), doc);
		}
		for (JSONObject jEvent : getEvents(j)) {
			handleCorpusEvent(docMap, jEvent);
		}
		JSONObject jDocs = getObject(j, "docs");
		for (String _id : getKeys(jDocs)) {
			Document doc = docMap.get(_id);
			handleDocumentEvents(doc, getObject(jDocs, _id));
		}
	}

	private void handleCorpusEvent(Map<String,Document> docMap, JSONObject jEvent) {
		Corpus corpus = getCorpus();
		if (handleFeatureEvent(corpus, jEvent)) {
			return;
		}
		switch (getCode(jEvent)) {
			case EventCode.CREATE_DOCUMENT: {
				String id = getString(jEvent, "id");
				Document doc = Document.getDocument(getModule(), corpus, id);
				String _id = getElementId(jEvent);
				docMap.put(_id, doc);
				break;
			}
			case EventCode.DELETE_DOCUMENT: {
				String _id = getElementId(jEvent);
				Document doc = docMap.get(_id);
				corpus.removeDocument(doc);
				docMap.remove(_id);
				break;
			}
			default: {
				throw new RuntimeException();
			}
		}
	}
	
	private void handleDocumentEvents(Document doc, JSONObject j) {
		Map<String,Element> eltMap = new HashMap<String,Element>();
		eltMap.put(doc.getStringId(), doc);
		Map<String,Section> secMap = new HashMap<String,Section>();
		PythonScriptBase.PythonScriptResolvedObjects resObj = getModule().getResolvedObjects();
		for (Section sec : Iterators.loop(doc.sectionIterator(evalCtx, resObj.getSectionFilter()))) {
			secMap.put(sec.getStringId(), sec);
			eltMap.put(sec.getStringId(), sec);
		}
		for (JSONObject jEvent : getEvents(j)) {
			handleDocumentEvent(doc, secMap, eltMap, jEvent);
		}
		Collection<SetTupleArg> setTupleArgs = new ArrayList<SetTupleArg>();
		JSONObject jSecs = getObject(j, "secs");
		for (String _id : getKeys(jSecs)) {
			Section sec = secMap.get(_id);
			handleSectionEvents(sec, setTupleArgs, eltMap, getObject(jSecs, _id));
		}
		for (SetTupleArg sta : setTupleArgs) {
			Element arg = eltMap.get(sta.ref);
			sta.t.setArgument(sta.role, arg);
		}
	}

	private void handleDocumentEvent(Document doc, Map<String,Section> secMap, Map<String,Element> eltMap, JSONObject jEvent) {
		if (handleFeatureEvent(doc, jEvent)) {
			return;
		}
		switch (getCode(jEvent)) {
			case EventCode.CREATE_SECTION: {
				String name = getString(jEvent, "name");
				String contents = getString(jEvent, "contents");
				Section sec = new Section(getModule(), doc, name, contents);
				String _id = getElementId(jEvent);
				secMap.put(_id, sec);
				eltMap.put(_id, sec);
				break;
			}
			case EventCode.DELETE_SECTION: {
				String _id = getElementId(jEvent);
				Section sec = secMap.get(_id);
				doc.removeSection(sec);
				secMap.remove(_id);
				eltMap.remove(_id);
				break;
			}
			default: {
				throw new RuntimeException();
			}
		}
	}
	
	private void handleSectionEvents(Section sec, Collection<SetTupleArg> setTupleArgs, Map<String,Element> eltMap, JSONObject j) {
		PythonScriptBase.PythonScriptResolvedObjects resObj = getModule().getResolvedObjects();
		Map<String,Annotation> annMap = new HashMap<String,Annotation>();
		for (Annotation a : resObj.getAnnotations(sec)) {
			annMap.put(a.getStringId(), a);
			eltMap.put(a.getStringId(), a);
		}
		Map<String,Relation> relMap = new HashMap<String,Relation>();
		for (Relation rel : sec.getAllRelations()) {
			if (resObj.acceptRelation(rel)) {
				relMap.put(rel.getStringId(), rel);
				eltMap.put(rel.getStringId(), rel);
			}
		}
		for (JSONObject jEvent : getEvents(j)) {
			handleSectionEvent(sec, annMap, relMap, eltMap, jEvent);
		}
		JSONObject jAnns = getObject(j, "as");
		for (String _id : getKeys(jAnns)) {
			Annotation a = annMap.get(_id);
			handleAnnotationEvents(a, getObject(jAnns, _id));
		}
		JSONObject jRels = getObject(j, "rels");
		for (String _id : getKeys(jRels)) {
			Relation rel = relMap.get(_id);
			handleRelationEvents(rel, setTupleArgs, eltMap, getObject(jRels, _id));
		}
	}

	private void handleSectionEvent(Section sec, Map<String,Annotation> annMap, Map<String,Relation> relMap, Map<String,Element> eltMap, JSONObject jEvent) {
		if (handleFeatureEvent(sec, jEvent)) {
			return;
		}
		switch (getCode(jEvent)) {
			case EventCode.CREATE_ANNOTATION: {
				int start = getInt(jEvent, "s");
				int end = getInt(jEvent, "e");
				Annotation a = new Annotation(getModule(), sec, start, end);
				String _id = getElementId(jEvent);
				annMap.put(_id, a);
				eltMap.put(_id, a);
				break;
			}
			case EventCode.CREATE_RELATION: {
				String name = getString(jEvent, "name");
				Relation rel = sec.ensureRelation(getModule(), name);
				String _id = getElementId(jEvent);
				relMap.put(_id, rel);
				eltMap.put(_id, rel);
				break;
			}
			case EventCode.DELETE_RELATION: {
				String _id = getElementId(jEvent);
				Relation rel = relMap.get(_id);
				sec.removeRelation(rel);
				relMap.remove(_id);
				eltMap.remove(_id);
				break;
			}
			default: {
				throw new RuntimeException();
			}
		}
	}

	private void handleAnnotationEvents(Annotation a, JSONObject j) {
		for (JSONObject jEvent : getEvents(j)) {
			handleAnnotationEvent(a, jEvent);
		}
	}

	private void handleAnnotationEvent(Annotation a, JSONObject jEvent) {
		if (handleFeatureEvent(a, jEvent)) {
			return;
		}
		switch (getCode(jEvent)) {
			case EventCode.ADD_TO_LAYER: {
				String layerName = getString(jEvent, "l");
				Section sec = a.getSection();
				Layer layer = sec.ensureLayer(layerName);
				layer.add(a);
				break;
			}
			case EventCode.REMOVE_FROM_LAYER: {
				String layerName = getString(jEvent, "l");
				Section sec = a.getSection();
				Layer layer = sec.ensureLayer(layerName);
				layer.remove(a);
				break;
			}
			default: {
				throw new RuntimeException();
			}
		}
	}

	private void handleRelationEvents(Relation rel, Collection<SetTupleArg> setTupleArgs, Map<String,Element> eltMap, JSONObject j) {
		Map<String,Tuple> tupleMap = new HashMap<String,Tuple>();
		for (Tuple t : rel.getTuples()) {
			tupleMap.put(t.getStringId(), t);
			eltMap.put(t.getStringId(), t);
		}
		for (JSONObject jEvent : getEvents(j)) {
			handleRelationEvent(rel, tupleMap, eltMap, jEvent);
		}
		JSONObject jTuples = getObject(j, "ts");
		for (String _id : getKeys(jTuples)) {
			Tuple t = tupleMap.get(_id);
			if (t == null) {
				getLogger().warning("_id = " + _id);
			}
			handleTupleEvents(t, setTupleArgs, getObject(jTuples, _id));
		}
	
	}

	private void handleRelationEvent(Relation rel, Map<String,Tuple> tupleMap, Map<String,Element> eltMap, JSONObject jEvent) {
		if (handleFeatureEvent(rel, jEvent)) {
			return;
		}
		switch (getCode(jEvent)) {
			case EventCode.CREATE_TUPLE: {
				Tuple t = new Tuple(getModule(), rel);
				String _id = getElementId(jEvent);
				tupleMap.put(_id, t);
				eltMap.put(_id, t);
				break;
			}
			case EventCode.DELETE_TUPLE: {
				String _id = getElementId(jEvent);
				Tuple t = tupleMap.get(_id);
				rel.removeTuple(t);
				tupleMap.remove(_id);
				eltMap.remove(_id);
				break;
			}
			default: {
				throw new RuntimeException();
			}
		}
	}

	private void handleTupleEvents(Tuple t, Collection<SetTupleArg> setTupleArgs, JSONObject j) {
		for (JSONObject jEvent : getEvents(j)) {
			handleTupleEvent(t, setTupleArgs, jEvent);
		}
	}

	private static class SetTupleArg {
		private final Tuple t;
		private final String role;
		private final String ref;
		
		private SetTupleArg(Tuple t, String role, String ref) {
			super();
			this.t = t;
			this.role = role;
			this.ref = ref;
		}
	}
	
	private void handleTupleEvent(Tuple t, Collection<SetTupleArg> setTupleArgs, JSONObject jEvent) {
		if (handleFeatureEvent(t, jEvent)) {
			return;
		}
		switch (getCode(jEvent)) {
			case EventCode.SET_ARGUMENT: {
				String role = getString(jEvent, "r");
				String ref = getString(jEvent, "a");
				SetTupleArg sta = new SetTupleArg(t, role, ref);
				setTupleArgs.add(sta);
				break;
			}
			default: {
				throw new RuntimeException();
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
		List<String> result = new ArrayList<String>();
		PythonScriptBase owner = getModule();
		if (owner.getCondaEnvironment() != null) {
			if (owner.getConda() == null) {
				result.add("conda");
			}
			else {
				result.add(owner.getConda().getAbsolutePath());
			}
			result.add("run");
			result.add("--no-capture-output");
			result.add("--name");
			result.add(owner.getCondaEnvironment());
		}
		if (owner.getCallPython()) {
			if (owner.getPython() == null) {
				result.add("python");
			}
			else {
				result.add(owner.getPython().getAbsolutePath());
			}
		}
		if (owner.isScriptCopy()) {
			result.add(getScriptCopy().getAbsolutePath());
		}
		else {
			result.add(owner.getScript().getStreamNames().iterator().next());
		}
		result.addAll(Arrays.asList(owner.getCommandLine()));
		return result;
	}

	@Override
	protected void updateEnvironment(Map<String,String> env) {
		PythonScriptBase owner = getModule();
		Mapping environment = owner.getEnvironment();
		if (environment != null) {
			env.putAll(environment);
		}
		if (env.containsKey("PYTHONPATH")) {
			String pp = env.get("PYTHONPATH");
			env.put("PYTHONPATH", owner.getAlvisnlpPythonDirectory().getAbsolutePath() + ":" + pp);
		}
		else {
			env.put("PYTHONPATH", owner.getAlvisnlpPythonDirectory().getAbsolutePath());
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
