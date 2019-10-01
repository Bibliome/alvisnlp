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
	private final String rawText;
	private final String specificCSSClass;
	private boolean hasChildren;
	private final String iconURL;
	private final String iconAlt;
	
	public TreeviewElementNode(Element elt, String role, String rawText, String specificCSSClass, boolean hasChildren, String iconURL, String iconAlt) {
		super(elt);
		this.role = role;
		this.rawText = rawText;
		this.specificCSSClass = specificCSSClass;
		this.hasChildren = hasChildren;
		this.iconURL = iconURL;
		this.iconAlt = iconAlt;
	}

	@Override
	protected String getIdSuffix() {
		return TreeviewConstants.NodeIdFunctors.CHILDREN;
	}

	private String getRoleHTML() {
		if (role == null) {
			return "";
		}
		return String.format("<span class=\"argument-role\">%s</span>", role);
	}

	@Override
	protected String getRawText() {
		return getRoleHTML() + rawText;
	}


	@Override
	protected String getCSSClass() {
		return "element-node " + specificCSSClass;
	}


	@Override
	protected boolean hasChild() {
		return hasChildren || !elt.isFeatureless();
	}


	@Override
	protected String getIconURL() {
		return iconURL;
	}
	
	@Override
	protected String getIconAlt() {
		return iconAlt;
	}

	public static TreeviewElementNode toTreeviewNode(Element elt, String role) {
		return elt.accept(ElementToTreeviewNode.INSTANCE, role);
	}

	private static enum ElementToTreeviewNode implements ElementVisitor<TreeviewElementNode,String> {
		INSTANCE;

		@Override
		public TreeviewElementNode visit(Annotation a, String param) {
			String rawText = String.format("<span class=\"annotation-offsets\">[%d-%d]</span> <span class=\"annotation-form\">%s</span>", a.getStart(), a.getEnd(), a.getForm());
			return new TreeviewElementNode(a, param, rawText, "annotation-node", false, "/res/icons/ui-text-field.png", "Annotation");
		}

		@Override
		public TreeviewElementNode visit(Corpus corpus, String param) {
			return new TreeviewElementNode(corpus, param, "Corpus", "corpus-node", corpus.documentIterator().hasNext(), "/res/icons/documents-stack.png", "Corpus");
		}

		@Override
		public TreeviewElementNode visit(Document doc, String param) {
			return new TreeviewElementNode(doc, param, doc.getId(), "document-node", doc.sectionIterator().hasNext(), "/res/icons/blue-document.png", "Document");
		}

		@Override
		public TreeviewElementNode visit(Relation rel, String param) {
			return new TreeviewElementNode(rel, param, rel.getName(), "relation-node", !rel.getTuples().isEmpty(), "/res/icons/node.png", "Relation");
		}

		@Override
		public TreeviewElementNode visit(Section sec, String param) {
			return new TreeviewElementNode(sec, param, sec.getName(), "section-node", (!sec.getAllRelations().isEmpty()) || (!sec.getAllAnnotations().isEmpty()), "/res/icons/document-text.png", "Section");
		}

		@Override
		public TreeviewElementNode visit(Tuple t, String param) {
			return new TreeviewElementNode(t, param, "Tuple", "tuple-node", t.getArity() > 0, "/res/icons/node-insert-child.png", "Tuple");
		}

		@Override
		public TreeviewElementNode visit(Element e, String param) {
			return new TreeviewElementNode(e, param, e.getClass().getSimpleName(), "element-node", false, "/res/icons/question-white.png", "Element");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JSONArray elementsToJSONArray(Iterable<? extends Element> elements) {
		JSONArray result = new JSONArray();
		for (Element elt : elements) {
			TreeviewNode node = elt.accept(ElementToTreeviewNode.INSTANCE, null);
			result.add(node.toJSON());
		}
		return result;
	}
}