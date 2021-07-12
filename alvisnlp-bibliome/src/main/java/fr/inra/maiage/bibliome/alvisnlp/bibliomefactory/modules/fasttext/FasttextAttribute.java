package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.fasttext;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Resolvable;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUser;

public class FasttextAttribute implements Resolvable<FasttextAttribute.Resolved> {
	private Expression tokens;
	private Expression form;
	
	FasttextAttribute(Expression tokens, Expression form) {
		super();
		this.tokens = tokens;
		this.form = form;
	}
	
	@Override
	public FasttextAttribute.Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new FasttextAttribute.Resolved(tokens.resolveExpressions(resolver), form.resolveExpressions(resolver));
	}

	static class Resolved implements NameUser {
		private final Evaluator tokens;
		private final Evaluator form;
		
		Resolved(Evaluator tokens, Evaluator form) {
			super();
			this.tokens = tokens;
			this.form = form;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			tokens.collectUsedNames(nameUsage, defaultType);
			form.collectUsedNames(nameUsage, defaultType);
		}

		Evaluator getTokens() {
			return tokens;
		}

		Evaluator getForm() {
			return form;
		}
	}
}
