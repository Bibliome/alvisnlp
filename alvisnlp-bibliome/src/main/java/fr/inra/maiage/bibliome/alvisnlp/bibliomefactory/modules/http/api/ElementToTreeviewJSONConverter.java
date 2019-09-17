package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api;

import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ElementVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;

public enum ElementToTreeviewJSONConverter implements ElementVisitor<JSONObject,String> {
	INSTANCE;
	
	@SuppressWarnings("unchecked")
	private static JSONObject createObject(Element elt, String text) {
		JSONObject result = new JSONObject();
		result.put("id", "children-" + elt.getStringId());
		result.put("text", text);
		result.put("hasChildren", true);
		return result;
	}
	
	private static String prefix(String prefix, String suffix) {
		if ((prefix == null) || prefix.isEmpty()){
			return suffix;
		}
		return String.format("%s %s", prefix, suffix);
	}

	@Override
	public JSONObject visit(Annotation a, String param) {
		return createObject(a, prefix(param, String.format("[%d-%d] %s", a.getStart(), a.getEnd(), a.getForm())));
	}

	@Override
	public JSONObject visit(Corpus corpus, String param) {
		return createObject(corpus, prefix(param, "Corpus"));
	}

	@Override
	public JSONObject visit(Document doc, String param) {
		return createObject(doc, prefix(param, "Document: " + doc.getId()));
	}

	@Override
	public JSONObject visit(Relation rel, String param) {
		return createObject(rel, prefix(param, "Relation: " + rel.getName()));
	}

	@Override
	public JSONObject visit(Section sec, String param) {
		return createObject(sec, prefix(param, "Section: " + sec.getName()));
	}

	@Override
	public JSONObject visit(Tuple t, String param) {
		return createObject(t, prefix(param, "Tuple"));
	}

	@Override
	public JSONObject visit(Element e, String param) {
		return createObject(e, prefix(param, "Element"));
	}
}
