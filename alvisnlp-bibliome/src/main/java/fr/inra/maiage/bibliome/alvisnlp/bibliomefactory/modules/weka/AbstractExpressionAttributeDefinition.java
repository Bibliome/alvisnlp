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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.weka;

import java.util.Collection;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import weka.core.Attribute;

abstract class AbstractExpressionAttributeDefinition extends AttributeDefinition {
	protected final Expression expression;
	protected final Evaluator resolvedExpression;
	
	protected AbstractExpressionAttributeDefinition(boolean classAttribute, Attribute attribute, Expression expression, Evaluator resolvedExpression) {
		super(classAttribute, attribute);
		this.expression = expression;
		this.resolvedExpression = resolvedExpression;
	}

	protected AbstractExpressionAttributeDefinition(boolean classAttribute, String name, boolean numeric, Expression expression, Evaluator resolvedExpression) {
		super(classAttribute, name, numeric);
		this.expression = expression;
		this.resolvedExpression = resolvedExpression;
	}

	protected AbstractExpressionAttributeDefinition(boolean classAttribute, String name, Collection<String> values, Expression expression, Evaluator resolvedExpression) {
		super(classAttribute, name, values);
		this.expression = expression;
		this.resolvedExpression = resolvedExpression;
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		if (resolvedExpression != null) {
			resolvedExpression.collectUsedNames(nameUsage, defaultType);
		}
	}
}
