package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;

abstract class ItemsRetriever<P extends Element,I> {
	protected final ElementType<P> parentType;
	
	ItemsRetriever(ElementType<P> parentType) {
		super();
		this.parentType = parentType;
	}
	
	protected abstract Iterator<I> getIterator(Map<String,List<String>> params, P parent) throws Exception;
	protected abstract JSONObject convert(I item);

	static final ItemsRetriever<Element,Map.Entry<String,List<String>>> ELEMENT_FEATURES = new ItemsRetriever<Element,Map.Entry<String,List<String>>>(ElementType.ANY) {
		@Override
		protected Iterator<Map.Entry<String,List<String>>> getIterator(Map<String,List<String>> params, Element parent) throws Exception {
			return parent.getFeatures().entrySet().iterator();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected JSONObject convert(Map.Entry<String,List<String>> item) {
			JSONObject result = new JSONObject();
			String key = item.getKey();
			JSONArray values = new JSONArray();
			values.addAll(item.getValue());
			result.put("key", key);
			result.put("values", values);
			return result;
		}
	};
	
	static final ItemsRetriever<Section,Layer> SECTION_LAYERS = new ItemsRetriever<Section,Layer>(ElementType.SECTION) {
		@Override
		protected Iterator<Layer> getIterator(Map<String,List<String>> params, Section parent) throws Exception {
			return parent.getAllLayers().iterator();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected JSONObject convert(Layer item) {
			JSONObject result = new JSONObject();
			String layerName = item.getName();
			result.put("type", "layer");
			result.put("section", item.getSection().getStringId());
			result.put("name", layerName);
			return result;
		}
	};
}