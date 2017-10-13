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


package org.bibliome.alvisnlp.modules.weka;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;

class IntExpressionAttributeDefinition extends AbstractExpressionAttributeDefinition {
	IntExpressionAttributeDefinition(boolean classAttribute, String name, Expression expression, Evaluator resolvedExpression) {
		super(classAttribute, name, true, expression, resolvedExpression);
	}

	@Override
	double evaluate(EvaluationContext ctx, Element example) {
		return resolvedExpression.evaluateDouble(ctx, example);
	}

	@Override
	public AttributeDefinition resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new IntExpressionAttributeDefinition(isClassAttribute(), getName(), expression, expression.resolveExpressions(resolver));
	}
}
