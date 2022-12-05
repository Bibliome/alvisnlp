package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;

public abstract class JsonScalar implements JsonValue {
	protected final Expression value;

	public JsonScalar(Expression value) {
		super();
		this.value = value;
	}
	
	public static abstract class Resolved implements JsonValue.Resolved {
		protected final Evaluator value;

		public Resolved(Evaluator value) {
			super();
			this.value = value;
		}
	}
}
