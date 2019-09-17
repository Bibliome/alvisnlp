package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ElementVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;

public enum ElementToTreeviewChildrenJSONConverter implements ElementVisitor<JSONArray,JSONArray> {
	INSTANCE;
	
	@SuppressWarnings("unchecked")
	private static JSONArray addChild(JSONArray children, String prefix, Element elt, String text) {
		JSONObject child = new JSONObject();
		child.put("id", prefix + "-" + elt.getStringId());
		child.put("text", text);
		child.put("hasChildren", true);
		children.add(child);
		return children;
	}
	
	private static JSONArray addFeaturesChild(JSONArray children, Element elt) {
		if (elt.isFeatureless()) {
			return children;
		}
		return addChild(children, "features", elt, "Features");
	}

	@Override
	public JSONArray visit(Annotation a, JSONArray param) {
		return addFeaturesChild(param, a);
	}

	@Override
	public JSONArray visit(Corpus corpus, JSONArray param) {
		addFeaturesChild(param, corpus);
		if (corpus.documentIterator().hasNext()) {
			addChild(param, "documents", corpus, "Documents");
		}
		return param;
	}

	@Override
	public JSONArray visit(Document doc, JSONArray param) {
		addFeaturesChild(param, doc);
		if (doc.sectionIterator().hasNext()) {
			addChild(param, "sections", doc, "Sections");
		}
		return param;
	}

	@Override
	public JSONArray visit(Relation rel, JSONArray param) {
		addFeaturesChild(param, rel);
		if (!rel.getTuples().isEmpty()) {
			addChild(param, "tuples", rel, "Tuples");
		}
		return param;
	}

	@Override
	public JSONArray visit(Section sec, JSONArray param) {
		addFeaturesChild(param, sec);
		if (!sec.getAllAnnotations().isEmpty()) {
			addChild(param, "layers", sec, "Layers");
		}
		if (!sec.getAllRelations().isEmpty()) {
			addChild(param, "relations", sec, "Relations");
		}
		return param;
	}

	@Override
	public JSONArray visit(Tuple t, JSONArray param) {
		addFeaturesChild(param, t);
		if (t.getArity() > 0) {
			addChild(param, "arguments", t, "Arguments");
		}
		return param;
	}

	@Override
	public JSONArray visit(Element e, JSONArray param) {
		return addFeaturesChild(param, e);
	}
}
