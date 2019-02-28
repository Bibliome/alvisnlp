package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pubannotation;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.DocumentCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule(beta=true)
public abstract class PubAnnotationReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
	private SourceStream source;

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Param
	public SourceStream getSource() {
		return source;
	}

	public void setSource(SourceStream source) {
		this.source = source;
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		JSONParser parser = new JSONParser();
		try {
			for (BufferedReader br : Iterators.loop(source.getBufferedReaders())) {
				try {
					Object json = parser.parse(br);
					if (json instanceof JSONObject) {
						convertDocument(logger, corpus, (JSONObject) json);
						continue;
					}
					if (json instanceof JSONArray) {
						for (Object jsonDoc: (JSONArray) json) {
							convertDocument(logger, corpus, (JSONObject) jsonDoc); 
						}
					}
				}
				finally {
					br.close();
				}
			}
		}
		catch (IOException|ParseException e) {
			throw new ProcessingException(e);
		}
	}

	private void convertDocument(Logger logger, Corpus corpus, JSONObject jsonDoc) {
		String target = (String) jsonDoc.get("target");
		String sourcedb = (String) jsonDoc.get("sourcedb");
		String sourceid = (String) jsonDoc.get("sourceid");
		String text = (String) jsonDoc.get("text");
		String docId = sourcedb + ":" + sourceid;
		Document doc = Document.getDocument(this, corpus, docId);
		doc.addFeature("sourcedb", sourcedb);
		doc.addFeature("sourceid", sourceid);
		doc.addFeature("target", target);
		Section sec = new Section(this, doc, "text", text);
		if (jsonDoc.containsKey("project")) {
			convertTrack(logger, sec, jsonDoc);
		}
		if (jsonDoc.containsKey("tracks")) {
			JSONArray tracks = (JSONArray) jsonDoc.get("tracks");
			for (Object track : tracks) {
				convertTrack(logger, sec, (JSONObject) track);
			}
		}
	}

	private void convertTrack(Logger logger, Section sec, JSONObject track) {
		String project = (String) track.get("project");
		if (track.containsKey("denotations")) {
			Map<String,Element> elementMap = new HashMap<String,Element>();
			JSONArray denotations = (JSONArray) track.get("denotations");
			for (Object denotation : denotations) {
				convertDenotation(logger, elementMap, sec, project, (JSONObject) denotation);
			}
			if (track.containsKey("relations")) {
				Map<Tuple,JSONObject> relationMap = new HashMap<Tuple,JSONObject>();
				JSONArray relations = (JSONArray) track.get("relations");
				for (Object o : relations) {
					JSONObject jsonRel = (JSONObject) o;
					Tuple t = convertRelation(logger, elementMap, sec, project, jsonRel);
					relationMap.put(t, jsonRel);
				}
				for (Map.Entry<Tuple,JSONObject> e : relationMap.entrySet()) {
					Tuple t = e.getKey();
					JSONObject jsonRel = e.getValue();
					setArguments(logger, elementMap, t, jsonRel);
				}
			}
			if (track.containsKey("modifications")) {
				JSONArray modifications = (JSONArray) track.get("modifications");
				for (Object mod : modifications) {
					convertModification(logger, elementMap, (JSONObject) mod);
				}
			}
		}
	}

	private void convertDenotation(Logger logger, Map<String,Element> elementMap, Section sec, String project, JSONObject denotation) {
		String id = (String) denotation.get("id");
		String obj = (String) denotation.get("obj");
		Object span = denotation.get("span");
		Element e;
		if (span instanceof JSONObject) {
			e = createAnnotation(sec, obj, (JSONObject) span, project, id);
		}
		else {
			e = createSpanTuple(sec, obj, (JSONArray) span, project, id);
		}
		if (elementMap.containsKey(id)) {
			logger.warning("duplicate denotation identifier: " + id);
		}
		elementMap.put(id, e);
	}

	private Annotation createAnnotation(Section sec, String obj, JSONObject span, String project, String id) {
		int begin = (int) span.get("begin");
		int end = (int) span.get("end");
		Layer layer = sec.ensureLayer(obj);
		Annotation result = new Annotation(this, layer, begin, end); 
		result.addFeature("obj", obj);
		result.addFeature("project", project);
		result.addFeature("id", id);
		return result;
	}

	private Tuple createSpanTuple(Section sec, String obj, JSONArray span, String project, String id) {
		Relation rel = sec.ensureRelation(this, obj);
		Tuple result = new Tuple(this, rel);
		result.addFeature("project", project);
		result.addFeature("id", id);
		for (int i = 0; i < span.size(); ++i) {
			JSONObject frag = (JSONObject) span.get(i);
			Annotation a = createAnnotation(sec, obj, frag, project, id);
			result.setArgument("frag" + i, a);
		}
		return result;
	}

	private Tuple convertRelation(Logger logger, Map<String,Element> elementMap, Section sec, String project, JSONObject jsonRel) {
		String pred = (String) jsonRel.get("pred");
		Relation rel = sec.ensureRelation(this, pred);
		Tuple result = new Tuple(this, rel);
		result.addFeature("project", project);
		String id = (String) jsonRel.get("id");
		result.addFeature("id", id);
		if (elementMap.containsKey(id)) {
			logger.warning("duplicate relation identifier: " + id);
		}
		elementMap.put(id, result);
		return result;
	}

	private static void setArguments(Logger logger, Map<String,Element> elementMap, Tuple t, JSONObject jsonRel) {
		String subjId = (String) jsonRel.get("subj");
		if (elementMap.containsKey(subjId)) {
			t.setArgument("subj", elementMap.get(subjId));
		}
		else {
			logger.warning("unknown resource identifier for " + t.getLastFeature("id") + ".subj: " + subjId);
		}
		String objId = (String) jsonRel.get("obj");
		if (elementMap.containsKey(objId)) {
			t.setArgument("obj", elementMap.get(objId));
		}
		else {
			logger.warning("unknown resource identifier for " + t.getLastFeature("id") + ".obj: " + objId);
		}
	}

	private static void convertModification(Logger logger, Map<String,Element> elementMap, JSONObject mod) {
		String id = (String) mod.get("id");
		String objId = (String) mod.get("obj");
		if (elementMap.containsKey(objId)) {
			Element e = elementMap.get(objId);
			String pred = (String) mod.get("pred");
			e.addFeature(pred, id);
		}
		else {
			logger.warning("unknown resource identifier for " + id + ".obj: " + objId);
		}
	}
}
