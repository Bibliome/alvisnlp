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
import fr.inra.maiage.bibliome.util.Iterators;

public class JsonSerializer implements ElementVisitor<JSONObject,Map<String,Element>> {
	public JsonSerializer() {
		super();
	}

	@SuppressWarnings("unchecked")
	private static JSONObject startElement(Element elt, Map<String,Element> param) {
		JSONObject result = new JSONObject();
		String id = elt.getStringId();
		result.put("id", id);
		result.put("f", serializeFeatures(elt));
		param.put(id, elt);
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
	public JSONObject visit(Annotation a, Map<String,Element> param) {
		JSONObject result = startElement(a, param);
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
	public JSONObject visit(Corpus corpus, Map<String,Element> param) {
		JSONObject result = startElement(corpus, param);
		result.put("documents", serializeDocuments(corpus, param));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray serializeDocuments(Corpus corpus, Map<String,Element> param) {
		JSONArray result = new JSONArray();
		for (Document doc : Iterators.loop(corpus.documentIterator())) {
			JSONObject jDoc = doc.accept(this, param);
			result.add(jDoc);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject visit(Document doc, Map<String,Element> param) {
		JSONObject result = startElement(doc, param);
		result.put("identifier", doc.getId());
		result.put("sections", serializeSections(doc, param));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray serializeSections(Document doc, Map<String,Element> param) {
		JSONArray result = new JSONArray();
		for (Section sec : Iterators.loop(doc.sectionIterator())) {
			JSONObject jSec = sec.accept(this, param);
			result.add(jSec);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject visit(Relation rel, Map<String,Element> param) {
		JSONObject result = startElement(rel, param);
		result.put("name", rel.getName());
		result.put("tuples", serializeTuples(rel, param));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray serializeTuples(Relation rel, Map<String,Element> param) {
		JSONArray result = new JSONArray();
		for (Tuple t : rel.getTuples()) {
			JSONObject jT = t.accept(this, param);
			result.add(jT);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject visit(Section sec, Map<String,Element> param) {
		JSONObject result = startElement(sec, param);
		result.put("name", sec.getName());
		result.put("contents", sec.getContents());
		result.put("annotations", serializeAnnotations(sec, param));
		result.put("layers", serializeLayers(sec));
		result.put("relations", serializeRelations(sec, param));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray serializeAnnotations(Section sec, Map<String,Element> param) {
		JSONArray result = new JSONArray();
		for (Annotation a : sec.getAllAnnotations()) {
			JSONObject jAnn = a.accept(this, param);
			result.add(jAnn);
		}
		return result;
	}
		
	@SuppressWarnings("unchecked")
	private JSONObject serializeLayers(Section sec) {
		JSONObject result = new JSONObject();
		for (Layer layer : sec.getAllLayers()) {
			result.put(layer.getName(), serializeAnnotationRefs(layer));
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
	private JSONArray serializeRelations(Section sec, Map<String,Element> param) {
		JSONArray relations = new JSONArray();
		for (Relation rel : sec.getAllRelations()) {
			JSONObject jRel = rel.accept(this, param);
			relations.add(jRel);
		}
		return relations;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject visit(Tuple t, Map<String,Element> param) {
		JSONObject result = startElement(t, param);
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
	public JSONObject visit(Element e, Map<String,Element> param) {
		throw new RuntimeException();
	}
}
