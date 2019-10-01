package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.treeview;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;

public abstract class TreeviewNode<T extends Element> {
	protected final T elt;
	
	public TreeviewNode(T elt) {
		super();
		this.elt = elt;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject result = new JSONObject();
		result.put("id", getId());
		result.put("text", getText());
		result.put("hasChildren", hasChild());
		result.put("imageHtml", getImageHTML());
		return result;
	}
	
	private String getId() {
		return String.format("%s-%s", elt.getStringId(), getIdSuffix());
	}
	
	private String getImageHTML() {
		return String.format("<img width=\"24\" height=\"24\" src=\"%s\" alt=\"%s\">", getIconURL(), getIconAlt());
	}
	
	private String getText() {
		return String.format("<span class=\"tree-node %s\">%s</span>", getCSSClass(), getRawText());
	}

	protected abstract String getIdSuffix();
	protected abstract String getRawText();
	protected abstract String getCSSClass();
	protected abstract boolean hasChild();
	protected abstract String getIconURL();
	protected abstract String getIconAlt();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JSONArray nodesToJSONArray(Iterable<TreeviewNode> nodes) {
		JSONArray result = new JSONArray();
		for (TreeviewNode node : nodes) {
			result.add(node.toJSON());
		}
		return result;
	}
}
