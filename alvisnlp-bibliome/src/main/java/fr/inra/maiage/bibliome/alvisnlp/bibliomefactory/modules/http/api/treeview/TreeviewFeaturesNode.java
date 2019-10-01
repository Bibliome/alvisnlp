package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.treeview;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;

public class TreeviewFeaturesNode extends TreeviewNode<Element> {
	public TreeviewFeaturesNode(Element elt) {
		super(elt);
	}

	@Override
	protected String getIdSuffix() {
		return TreeviewConstants.NodeIdFunctors.FEATURES;
	}

	@Override
	protected String getCSSClass() {
		return "features-node";
	}

	@Override
	protected String getRawText() {
		return "Features";
	}

	@Override
	protected boolean hasChild() {
		return !elt.isFeatureless();
	}

	@Override
	protected String getIconURL() {
		return "/res/icons/category.png";
	}

	@Override
	protected String getIconAlt() {
		return "Element features";
	}
}