package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.treeview;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.Constants;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;

public class TreeviewLayerNode extends TreeviewNode<Section> {
	private final String layerName;
	
	public TreeviewLayerNode(Section elt, String layerName) {
		super(elt);
		this.layerName = layerName;
	}

	@Override
	protected String getIdSuffix() {
		return String.format("%s-%s", Constants.NodeIdFunctors.ANNOTATIONS, layerName);
	}

	@Override
	protected String getCSSClass() {
		return "layer-node";
	}

	@Override
	protected String getRawText() {
		return layerName;
	}

	@Override
	protected boolean hasChild() {
		return elt.hasLayer(layerName) && !elt.getLayer(layerName).isEmpty();
	}

	@Override
	protected String getIconURL() {
		return "/res/icons/tags-label.png";
	}

	@Override
	protected String getIconAlt() {
		return "Layer";
	}
}