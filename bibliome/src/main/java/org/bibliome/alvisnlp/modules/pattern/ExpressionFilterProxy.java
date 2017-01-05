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


package org.bibliome.alvisnlp.modules.pattern;

import org.bibliome.util.filters.ParamFilter;
import org.bibliome.util.mappers.Mapper;

import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.Resolvable;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.corpus.expressions.VariableLibrary;
import alvisnlp.corpus.expressions.VariableLibrary.Variable;

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
