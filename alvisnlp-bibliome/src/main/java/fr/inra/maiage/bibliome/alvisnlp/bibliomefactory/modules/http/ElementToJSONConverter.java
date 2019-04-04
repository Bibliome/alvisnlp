package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http;

import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ElementVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;

class ElementToJSONConverter implements ElementVisitor<JSONObject,Void> {
	@SuppressWarnings("unchecked")
	private static JSONObject createObject(Element elt) {
		JSONObject result = new JSONObject();
		result.put("type", elt.getType().toString());
		result.put("UID", elt.getStringId());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject visit(Annotation a, Void param) {
		JSONObject result = createObject(a);
		result.put("start", a.getStart());
		result.put("end", a.getEnd());
		return result;
	}

	@Override
	public JSONObject visit(Corpus corpus, Void param) {
		return createObject(corpus);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject visit(Document doc, Void param) {
		JSONObject result = createObject(doc);
		result.put("id", doc.getId());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject visit(Relation rel, Void param) {
		JSONObject result = createObject(rel);
		result.put("name", rel.getName());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject visit(Section sec, Void param) {
		JSONObject result = createObject(sec);
		result.put("name", sec.getName());
		result.put("order", sec.getOrder());
		return result;
	}

	@Override
	public JSONObject visit(Tuple t, Void param) {
		return createObject(t);
	}

	@Override
	public JSONObject visit(Element e, Void param) {
		return createObject(e);
	}
}
