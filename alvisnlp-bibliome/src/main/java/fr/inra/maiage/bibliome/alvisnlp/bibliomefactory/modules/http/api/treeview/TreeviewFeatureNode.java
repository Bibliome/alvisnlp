package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.treeview;

import java.util.ArrayList;
import java.util.Collection;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;

public class TreeviewFeatureNode extends TreeviewNode<Element> {
	private final String key;

	public TreeviewFeatureNode(Element elt, String key) {
		super(elt);
		this.key = key;
	}

	@Override
	protected String getIdSuffix() {
		return "values-" + key;
	}

	@Override
	protected String getCSSClass() {
		return "feature-node";
	}

	@Override
	protected String getRawText() {
		return String.format("<span class=\"feature-key\">%s</span> <span class=\"feature-value\">%s</span>", key, elt.getLastFeature(key));
	}

	@Override
	protected boolean hasChild() {
		return false;
	}

	@Override
	protected String getIconURL() {
		return "/res/icons/tick-small-red.png";
	}
	
	@SuppressWarnings("rawtypes")
	public static Collection<TreeviewNode> getElementFeatureNodes(Element elt) {
		Collection<TreeviewNode> result = new ArrayList<TreeviewNode>();
		for (String key : elt.getFeatureKeys()) {
			TreeviewNode<Element> node = new TreeviewFeatureNode(elt, key);
			result.add(node);
		}
		return result;
	}

	@Override
	protected String getIconAlt() {
		return "Feature";
	}
}