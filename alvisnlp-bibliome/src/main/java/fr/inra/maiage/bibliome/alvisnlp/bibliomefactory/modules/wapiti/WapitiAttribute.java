package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti.WapitiAttribute.Resolved;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Resolvable;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;

public class WapitiAttribute implements Resolvable<Resolved> {
	private final Expression value;
	private final int[] window;
	
	public WapitiAttribute(Expression value, int[] window) {
		super();
		this.value = value;
		this.window = window;
	}
	
	public static class Resolved {
		private final Evaluator value;
		private final int[] window;
		
		public Resolved(Evaluator value, int[] window) {
			super();
			this.value = value;
			this.window = window;
		}

		public Evaluator getValue() {
			return value;
		}

		public int[] getWindow() {
			return window;
		}
	}

	@Override
	public Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new Resolved(value.resolveExpressions(resolver), window);
	}
}
