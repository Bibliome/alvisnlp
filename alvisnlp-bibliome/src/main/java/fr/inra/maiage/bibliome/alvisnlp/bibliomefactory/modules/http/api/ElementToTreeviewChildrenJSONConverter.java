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
		return addChild(children, "features", elt, "Features");
	}

	@Override
	public JSONArray visit(Annotation a, JSONArray param) {
		return addFeaturesChild(param, a);
	}

	@Override
	public JSONArray visit(Corpus corpus, JSONArray param) {
		addFeaturesChild(param, corpus);
		return addChild(param, "documents", corpus, "Documents");
	}

	@Override
	public JSONArray visit(Document doc, JSONArray param) {
		addFeaturesChild(param, doc);
		return addChild(param, "sections", doc, "Sections");
	}

	@Override
	public JSONArray visit(Relation rel, JSONArray param) {
		addFeaturesChild(param, rel);
		return addChild(param, "tuples", rel, "Tuples");
	}

	@Override
	public JSONArray visit(Section sec, JSONArray param) {
		addFeaturesChild(param, sec);
		addChild(param, "layers", sec, "Layers");
		return addChild(param, "relations", sec, "Relations");
	}

	@Override
	public JSONArray visit(Tuple t, JSONArray param) {
		addFeaturesChild(param, t);
		return addChild(param, "arguments", t, "Arguments");
	}

	@Override
	public JSONArray visit(Element e, JSONArray param) {
		return addFeaturesChild(param, e);
	}
}
