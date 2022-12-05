package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;

public class JsonString extends JsonScalar {
	public JsonString(Expression value) {
		super(value);
	}

	public static class Resolved extends JsonScalar.Resolved {
		public Resolved(Evaluator value) {
			super(value);
		}

		@Override
		public Object create(EvaluationContext evalCtx, Element elt) {
			return value.evaluateString(evalCtx, elt);
		}
	}

	@Override
	public JsonValue.Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new Resolved(value.resolveExpressions(resolver));
	}
}
