package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;

public class JsonObject implements JsonValue {
	private final Map<String,JsonValue> entries;

	public JsonObject(Map<String,JsonValue> entries) {
		super();
		this.entries = entries;
	}
	
	public static class Resolved implements JsonValue.Resolved {
		private final Map<String,JsonValue.Resolved> entries;

		public Resolved(Map<String,JsonValue.Resolved> entries) {
			super();
			this.entries = entries;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object create(EvaluationContext evalCtx, Element elt) {
			JSONObject result = new JSONObject();
			for (Map.Entry<String,JsonValue.Resolved> e : entries.entrySet()) {
				String key = e.getKey();
				Object value = e.getValue().create(evalCtx, elt);
				result.put(key, value);
			}
			return result;
		}
	}

	@Override
	public JsonValue.Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		Map<String,JsonValue.Resolved> entries = new HashMap<String,JsonValue.Resolved>();
		for (Map.Entry<String,JsonValue> e : this.entries.entrySet()) {
			String key = e.getKey();
			JsonValue.Resolved value = e.getValue().resolveExpressions(resolver);
			entries.put(key, value);
		}
		return new Resolved(entries);
	}
}
