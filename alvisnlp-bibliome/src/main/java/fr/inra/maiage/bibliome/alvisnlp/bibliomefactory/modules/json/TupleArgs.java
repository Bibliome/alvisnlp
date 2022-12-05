package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json;

import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DownCastElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;

public class TupleArgs implements JsonValue {
	private final JsonValue value;

	public TupleArgs(JsonValue value) {
		super();
		this.value = value;
	}
	
	public static class Resolved implements JsonValue.Resolved {
		private final JsonValue.Resolved value;

		public Resolved(JsonValue.Resolved value) {
			super();
			this.value = value;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object create(EvaluationContext evalCtx, Element elt) {
			JSONObject result = new JSONObject();
			Tuple t = DownCastElement.toTuple(elt);
			for (String role : t.getRoles()) {
				Element arg = t.getArgument(role);
				Object value = this.value.create(evalCtx, arg);
				result.put(role, value);
			}
			return result;
		}
	}

	@Override
	public JsonValue.Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new Resolved(value.resolveExpressions(resolver));
	}
}
