package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json;

import org.json.simple.JSONArray;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.util.Iterators;

public class JsonArray implements JsonValue {
	private final Expression items;
	private final JsonValue value;
	
	public JsonArray(Expression items, JsonValue item) {
		super();
		this.items = items;
		this.value = item;
	}
	
	public static class Resolved implements JsonValue.Resolved {
		private final Evaluator items;
		private final JsonValue.Resolved value;
		
		public Resolved(Evaluator itemList,
				fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json.JsonValue.Resolved item) {
			super();
			this.items = itemList;
			this.value = item;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object create(EvaluationContext evalCtx, Element elt) {
			JSONArray result = new JSONArray();
			for (Element e : Iterators.loop(items.evaluateElements(evalCtx, elt))) {
				Object i = value.create(evalCtx, e);
				result.add(i);
			}
			return result;
		}
	}

	@Override
	public JsonValue.Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new Resolved(items.resolveExpressions(resolver), value.resolveExpressions(resolver));
	}
}
