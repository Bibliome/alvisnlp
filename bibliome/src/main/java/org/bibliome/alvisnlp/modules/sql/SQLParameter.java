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


package org.bibliome.alvisnlp.modules.sql;

import alvisnlp.corpus.expressions.EvaluationType;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.Resolvable;
import alvisnlp.corpus.expressions.ResolverException;

public class SQLParameter implements Resolvable<SQLParameterEvaluator> {
	private final EvaluationType type;
	private final Expression expression;
	
	SQLParameter(EvaluationType type, Expression expression) {
		super();
		this.type = type;
		this.expression = expression;
	}

	@Override
	public SQLParameterEvaluator resolveExpressions(LibraryResolver resolver) throws ResolverException {
		Evaluator evaluator = expression.resolveExpressions(resolver);
		return new SQLParameterEvaluator(type, evaluator);
	}
}
