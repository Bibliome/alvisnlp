package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.treeview;

import org.json.simple.JSONArray;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ElementVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;

public class TreeviewElementNode extends TreeviewNode<Element> {
	private final String role;
	
	public TreeviewElementNode(Element elt, String role) {
		super(elt);
		this.role = role;
	}

	@Override
	protected String getIdSuffix() {
		return "children";
	}
	
	private static enum ElementText implements ElementVisitor<String,Void> {
		INSTANCE;
		
		@Override
		public String visit(Annotation a, Void param) {
			return String.format("<span class=\"annotation-offsets\">[%d-%d]</span> <span class=\"annotation-form\">%s</span>", a.getStart(), a.getEnd(), a.getForm());
		}

		@Override
		public String visit(Corpus corpus, Void param) {
			return "Corpus";
		}

		@Override
		public String visit(Document doc, Void param) {
			return doc.getId();
		}

		@Override
		public String visit(Relation rel, Void param) {
			return rel.getName();
		}

		@Override
		public String visit(Section sec, Void param) {
			return sec.getName();
		}

		@Override
		public String visit(Tuple t, Void param) {
			return "Tuple";
		}

		@Override
		public String visit(Element e, Void param) {
			return e.getClass().getSimpleName();
		}
	}
	
	private static enum ElementCSSClass implements ElementVisitor<String,Void> {
		INSTANCE;

		@Override
		public String visit(Annotation a, Void param) {
			return "annotation-node";
		}

		@Override
		public String visit(Corpus corpus, Void param) {
			return "corpus-node";
		}

		@Override
		public String visit(Document doc, Void param) {
			return "document-node";
		}

		@Override
		public String visit(Relation rel, Void param) {
			return "relation-node";
		}

		@Override
		public String visit(Section sec, Void param) {
			return "section-node";
		}

		@Override
		public String visit(Tuple t, Void param) {
			return "tuple-node";
		}

		@Override
		public String visit(Element e, Void param) {
			return "element-node";
		}
	}
	
	@Override
	protected String getCSSClass() {
		return "element-node " + elt.accept(ElementCSSClass.INSTANCE, null);
	}

	private static String getRoleHTML(String role) {
		if (role == null) {
			return "";
		}
		return String.format("<span class=\"argument-role\">%s</span>", role);
	}

	@Override
	protected String getRawText() {
		return getRoleHTML(role) + elt.accept(ElementText.INSTANCE, null);
//		return String.format("<span class=\"element-node %s\">%s%s</span>", elt.accept(ElementCSSClass.INSTANCE, null), );
	}
	
	private static enum ElementHasChildren implements ElementVisitor<Boolean,Void> {
		INSTANCE;
		
		@Override
		public Boolean visit(Annotation a, Void param) {
			return !a.isFeatureless();
		}

		@Override
		public Boolean visit(Corpus corpus, Void param) {
			return (!corpus.isFeatureless()) || corpus.documentIterator().hasNext();
		}

		@Override
		public Boolean visit(Document doc, Void param) {
			return (!doc.isFeatureless()) || doc.sectionIterator().hasNext();
		}

		@Override
		public Boolean visit(Relation rel, Void param) {
			return (!rel.isFeatureless()) || (!rel.getTuples().isEmpty());
		}

		@Override
		public Boolean visit(Section sec, Void param) {
			return (!sec.isFeatureless()) || (!sec.getAllRelations().isEmpty()) || (!sec.getAllAnnotations().isEmpty());
		}

		@Override
		public Boolean visit(Tuple t, Void param) {
			return (!t.isFeatureless()) || (t.getArity() > 0);
		}

		@Override
		public Boolean visit(Element e, Void param) {
			return !e.isFeatureless();
		}
	}

	@Override
	protected boolean hasChild() {
		return elt.accept(ElementHasChildren.INSTANCE, null);
	}
	
	private static enum ElementIcon implements ElementVisitor<String,Void> {
		INSTANCE;

		@Override
		public String visit(Annotation a, Void param) {
			return "/res/icons/ui-text-field.png";
		}

		@Override
		public String visit(Corpus corpus, Void param) {
			return "/res/icons/documents-stack.png";
		}

		@Override
		public String visit(Document doc, Void param) {
			return "/res/icons/blue-document.png";
		}

		@Override
		public String visit(Relation rel, Void param) {
			return "/res/icons/node.png";
		}

		@Override
		public String visit(Section sec, Void param) {
			return "/res/icons/document-text.png";
		}

		@Override
		public String visit(Tuple t, Void param) {
			return "/res/icons/node-insert-child.png";
		}

		@Override
		public String visit(Element e, Void param) {
			return "/res/icons/question-white.png";
		}
	}

	@Override
	protected String getIconURL() {
		return elt.accept(ElementIcon.INSTANCE, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JSONArray elementsToJSONArray(Iterable<? extends Element> elements) {
		JSONArray result = new JSONArray();
		for (Element elt : elements) {
			TreeviewNode node = new TreeviewElementNode(elt, null);
			result.add(node.toJSON());
		}
		return result;
	}
}