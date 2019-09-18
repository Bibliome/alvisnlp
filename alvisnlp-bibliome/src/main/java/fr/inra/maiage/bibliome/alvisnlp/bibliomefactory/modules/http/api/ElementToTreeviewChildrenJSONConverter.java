package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api;

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

public enum ElementToTreeviewChildrenJSONConverter implements ElementVisitor<JSONArray,JSONArray> {
	INSTANCE;

	@SuppressWarnings("unchecked")
	private static JSONArray addFeaturesChild(JSONArray children, Element elt) {
		JSONObject child = new JSONObject();
		child.put("id", "features-" + elt.getStringId());
		child.put("text", "<span class=\"title-node features-node\">Features</span>");
		child.put("hasChildren", !elt.isFeatureless());
		child.put("imageHtml", "<img width=\"24\" height=\"24\" src=\"/res/icons/category.png\">");
		children.add(child);
		return children;
	}

	@Override
	public JSONArray visit(Annotation a, JSONArray param) {
		return addFeaturesChild(param, a);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray visit(Corpus corpus, JSONArray param) {
		addFeaturesChild(param, corpus);
		for (Document doc : Iterators.loop(corpus.documentIterator())) {
			JSONObject jDoc = doc.accept(ElementToTreeviewJSONConverter.INSTANCE, null);
			param.add(jDoc);
		}
		return param;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray visit(Document doc, JSONArray param) {
		addFeaturesChild(param, doc);
		for (Section sec : Iterators.loop(doc.sectionIterator())) {
			JSONObject jSec = sec.accept(ElementToTreeviewJSONConverter.INSTANCE, null);
			param.add(jSec);
		}
		return param;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray visit(Relation rel, JSONArray param) {
		addFeaturesChild(param, rel);
		for (Tuple t : rel.getTuples()) {
			JSONObject jT = t.accept(ElementToTreeviewJSONConverter.INSTANCE, null);
			param.add(jT);
		}
		return param;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray visit(Section sec, JSONArray param) {
		addFeaturesChild(param, sec);
		for (Layer layer : sec.getAllLayers()) {
			String name = layer.getName();
			JSONObject jLayer = new JSONObject();
			jLayer.put("id", String.format("annotations-%s-%s", sec.getStringId(), name));
			jLayer.put("text", String.format("<span class=\"layer-node\">%s</span>", name));
			jLayer.put("hasChildren", !layer.isEmpty());
			jLayer.put("imageHtml", "<img width=\"24\" height=\"24\" src=\"/res/icons/tags-label.png\">");
			param.add(jLayer);
		}
		for (Relation rel : sec.getAllRelations()) {
			JSONObject jRel = rel.accept(ElementToTreeviewJSONConverter.INSTANCE, null);
			param.add(jRel);
		}
		return param;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray visit(Tuple t, JSONArray param) {
		addFeaturesChild(param, t);
		for (String role : t.getRoles()) {
			Element arg = t.getArgument(role);
			JSONObject jArg = arg.accept(ElementToTreeviewJSONConverter.INSTANCE, String.format("<span class=\"argument-role\">%s</span>", role));
			param.add(jArg);
		}
		return param;
	}

	@Override
	public JSONArray visit(Element e, JSONArray param) {
		return addFeaturesChild(param, e);
	}
}
