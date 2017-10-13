/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pattern;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Resolvable;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.VariableLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.VariableLibrary.Variable;
import fr.inra.maiage.bibliome.util.filters.ParamFilter;
import fr.inra.maiage.bibliome.util.mappers.Mapper;

public class ExpressionFilterProxy implements ParamFilter<Void,Void>, Resolvable<EvaluatorFilterProxy> {
	private final Expression expression;

	public ExpressionFilterProxy(Expression expression) {
		super();
		this.expression = expression;
	}

	public Expression getExpression() {
		return expression;
	}

	@Override
	public boolean accept(Void x, Void param) {
		return false;
	}

	@Override
	public EvaluatorFilterProxy resolveExpressions(LibraryResolver resolver) throws ResolverException {
		VariableLibrary varLib = new VariableLibrary("element");
		Variable var = varLib.newVariable(null);
		LibraryResolver varResolver = varLib.newLibraryResolver(resolver);
		Evaluator eval = expression.resolveExpressions(varResolver);
		return new EvaluatorFilterProxy(var, eval);
	}
	
	public static class ResolveMapper implements Mapper<ExpressionFilterProxy,EvaluatorFilterProxy> {
		private final LibraryResolver resolver;

		public ResolveMapper(LibraryResolver resolver) {
			super();
			this.resolver = resolver;
		}

		@Override
		public EvaluatorFilterProxy map(ExpressionFilterProxy x) {
			try {
				return x.resolveExpressions(resolver);
			}
			catch (ResolverException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
