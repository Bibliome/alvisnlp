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


package org.bibliome.alvisnlp.modules.alvisir2;

import java.util.Map;

import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.Resolvable;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.types.ExpressionMapping;

public class IndexedTokens implements Resolvable<IndexedTokensEvaluator> {
	private final Expression instances;
	private final Expression text;
	private final TokenFragments tokenFragments;
	private final Expression identifier;
	private final ExpressionMapping arguments;
	private final ExpressionMapping properties;

	IndexedTokens(Expression instances, Expression text, TokenFragments tokenFragments, Expression identifier, ExpressionMapping arguments, ExpressionMapping properties) {
		super();
		this.instances = instances;
		this.text = text;
		this.tokenFragments = tokenFragments;
		this.identifier = identifier;
		this.arguments = arguments;
		this.properties = properties;
	}

	@Override
	public IndexedTokensEvaluator resolveExpressions(LibraryResolver resolver) throws ResolverException {
		Evaluator instances = this.instances.resolveExpressions(resolver);
		Evaluator text = this.text.resolveExpressions(resolver);
		TokenFragmentsEvaluator tokenFragments = this.tokenFragments.resolveExpressions(resolver);
		Evaluator identifier = this.identifier.resolveExpressions(resolver);
		Map<String,Evaluator> arguments = this.arguments.resolveExpressions(resolver);
		Map<String,Evaluator> properties = this.properties.resolveExpressions(resolver);
		return new IndexedTokensEvaluator(instances, text, tokenFragments, identifier, arguments, properties);
	}
}
