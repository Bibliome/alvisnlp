package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.python;

import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ElementVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.util.Iterators;

public class JsonSerializer implements ElementVisitor<JSONObject,Void> {
	private final EvaluationContext evalCtx;
	private final PythonScript.PythonScriptResolvedObjects resObj;

	public JsonSerializer(EvaluationContext evalCtx, PythonScript.PythonScriptResolvedObjects resObj) {
		super();
		this.evalCtx = evalCtx;
		this.resObj = resObj;
	}

	@SuppressWarnings("unchecked")
	private static JSONObject startElement(Element elt) {
		JSONObject result = new JSONObject();
		String id = elt.getStringId();
		result.put("id", id);
		result.put("f", serializeFeatures(elt));
		return result;
	}

	@SuppressWarnings("unchecked")
	private static JSONObject serializeFeatures(Element elt) {
		JSONObject result = new JSONObject();
		for (String key : elt.getFeatureKeys()) {
			if (!elt.isStaticFeatureKey(key)) {
				JSONArray values = new JSONArray();
				for (String value : elt.getFeature(key)) {
					values.add(value);
				}
				result.put(key, values);
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject visit(Annotation a, Void param) {
		JSONObject result = startElement(a);
		result.put("off", serializeAnnotationOffsets(a));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static JSONArray serializeAnnotationOffsets(Annotation a) {
		JSONArray result = new JSONArray();
		result.add(a.getStart());
		result.add(a.getEnd());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject visit(Corpus corpus, Void param) {
		JSONObject result = startElement(corpus);
		result.put("documents", serializeDocuments(corpus, param));
		result.put("params", serializeScriptParams(corpus, param));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject serializeScriptParams(Corpus corpus, Void param) {
		JSONObject result = new JSONObject();
		for (Map.Entry<String,Evaluator> e : resObj.getScriptParams().entrySet()) {
			String key = e.getKey();
			Evaluator ev = e.getValue();
			String value = ev.evaluateString(evalCtx, corpus);
			result.put(key, value);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray serializeDocuments(Corpus corpus, Void param) {
		JSONArray result = new JSONArray();
		for (Document doc : Iterators.loop(corpus.documentIterator(evalCtx, resObj.getDocumentFilter()))) {
			JSONObject jDoc = doc.accept(this, param);
			result.add(jDoc);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject visit(Document doc, Void param) {
		JSONObject result = startElement(doc);
		result.put("identifier", doc.getId());
		result.put("sections", serializeSections(doc, param));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray serializeSections(Document doc, Void param) {
		JSONArray result = new JSONArray();
		for (Section sec : Iterators.loop(doc.sectionIterator(evalCtx, resObj.getSectionFilter()))) {
			JSONObject jSec = sec.accept(this, param);
			result.add(jSec);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject visit(Relation rel, Void param) {
		JSONObject result = startElement(rel);
		result.put("name", rel.getName());
		result.put("tuples", serializeTuples(rel, param));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray serializeTuples(Relation rel, Void param) {
		JSONArray result = new JSONArray();
		for (Tuple t : rel.getTuples()) {
			JSONObject jT = t.accept(this, param);
			result.add(jT);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject visit(Section sec, Void param) {
		JSONObject result = startElement(sec);
		result.put("name", sec.getName());
		result.put("contents", sec.getContents());
		result.put("annotations", serializeAnnotations(sec, param));
		result.put("layers", serializeLayers(sec));
		result.put("relations", serializeRelations(sec, param));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray serializeAnnotations(Section sec, Void param) {
		JSONArray result = new JSONArray();
		for (Annotation a : resObj.getAnnotations(sec)) {
			JSONObject jAnn = a.accept(this, param);
			result.add(jAnn);
		}
		return result;
	}
		
	@SuppressWarnings("unchecked")
	private JSONObject serializeLayers(Section sec) {
		JSONObject result = new JSONObject();
		for (Layer layer : sec.getAllLayers()) {
			if (resObj.acceptLayer(layer)) {
				result.put(layer.getName(), serializeAnnotationRefs(layer));
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static JSONArray serializeAnnotationRefs(Layer layer) {
		JSONArray result = new JSONArray();
		for (Annotation a : layer) {
			String aId = a.getStringId();
			result.add(aId);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray serializeRelations(Section sec, Void param) {
		JSONArray relations = new JSONArray();
		for (Relation rel : sec.getAllRelations()) {
			if (resObj.acceptRelation(rel)) {
				JSONObject jRel = rel.accept(this, param);
				relations.add(jRel);
			}
		}
		return relations;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject visit(Tuple t, Void param) {
		JSONObject result = startElement(t);
		result.put("args", serializeArgs(t));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static JSONObject serializeArgs(Tuple t) {
		JSONObject args = new JSONObject();
		for (String role : t.getRoles()) {
			Element a = t.getArgument(role);
			String id = a.getStringId();
			args.put(role, id);
		}
		return args;
	}

	@Override
	public JSONObject visit(Element e, Void param) {
		throw new RuntimeException();
	}
}
