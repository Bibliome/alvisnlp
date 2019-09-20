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
	protected String getText() {
		return String.format("<span class=\"feature-node\"><span class=\"feature-key\">%s</span> <span class=\"feature-value\">%s</span></span>", key, elt.getLastFeature(key));
	}

	@Override
	protected boolean hasChild() {
		return false;
	}

	@Override
	protected String getIconURL() {
		// TODO Auto-generated method stub
		return null;
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
}