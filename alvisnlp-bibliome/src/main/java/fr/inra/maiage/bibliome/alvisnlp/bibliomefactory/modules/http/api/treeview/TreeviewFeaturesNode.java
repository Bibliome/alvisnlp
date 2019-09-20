package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.treeview;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;

public class TreeviewFeaturesNode extends TreeviewNode<Element> {
	public TreeviewFeaturesNode(Element elt) {
		super(elt);
	}

	@Override
	protected String getIdSuffix() {
		return "features";
	}

	@Override
	protected String getText() {
		return "<span class=\"title-node features-node\">Features</span>";
	}

	@Override
	protected boolean hasChild() {
		return !elt.isFeatureless();
	}

	@Override
	protected String getIconURL() {
		return "/res/icons/category.png";
	}
}