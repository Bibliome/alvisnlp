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
	private static JSONObject createObject(Element elt, String text, String icon, boolean hasChildren) {
		JSONObject result = new JSONObject();
		result.put("id", "children-" + elt.getStringId());
		result.put("text", text);
		result.put("hasChildren", hasChildren);
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
		return createObject(a, prefix(param, String.format("[%d-%d] %s", a.getStart(), a.getEnd(), a.getForm())), null, !a.isFeatureless());
	}

	@Override
	public JSONObject visit(Corpus corpus, String param) {
		boolean hasChildren = (!corpus.isFeatureless()) || corpus.documentIterator().hasNext();
		return createObject(corpus, prefix(param, "Corpus"), null, hasChildren);
	}

	@Override
	public JSONObject visit(Document doc, String param) {
		boolean hasChildren = (!doc.isFeatureless()) || doc.sectionIterator().hasNext();
		return createObject(doc, prefix(param, "Document: " + doc.getId()), null, hasChildren);
	}

	@Override
	public JSONObject visit(Relation rel, String param) {
		boolean hasChildren = (!rel.isFeatureless()) || (!rel.getTuples().isEmpty());
		return createObject(rel, prefix(param, "Relation: " + rel.getName()), null, hasChildren);
	}

	@Override
	public JSONObject visit(Section sec, String param) {
		boolean hasChildren = (!sec.isFeatureless()) || (!sec.getAllRelations().isEmpty()) || (!sec.getAllAnnotations().isEmpty());
		return createObject(sec, prefix(param, "Section: " + sec.getName()), null, hasChildren);
	}

	@Override
	public JSONObject visit(Tuple t, String param) {
		boolean hasChildren = (!t.isFeatureless()) || (t.getArity() > 0);
		return createObject(t, prefix(param, "Tuple"), null, hasChildren);
	}

	@Override
	public JSONObject visit(Element e, String param) {
		return createObject(e, prefix(param, "Element"), null, !e.isFeatureless());
	}
}
