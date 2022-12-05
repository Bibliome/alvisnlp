package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json;

import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;

public class Features implements JsonValue, JsonValue.Resolved {
	private final String[] features;

	public Features(String[] features) {
		super();
		this.features = features;
	}

	@Override
	public Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object create(EvaluationContext evalCtx, Element elt) {
		JSONObject result = new JSONObject();
		for (String key : features) {
			String value = elt.getLastFeature(key);
			result.put(key, value);
		}
		return result;
	}
}
