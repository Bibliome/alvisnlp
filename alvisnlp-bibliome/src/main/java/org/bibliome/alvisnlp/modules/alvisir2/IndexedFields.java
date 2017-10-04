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

import java.util.List;

import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.Resolvable;
import alvisnlp.corpus.expressions.ResolverException;
import fr.inra.mig_bibliome.alvisir.core.index.NormalizationOptions;

public class IndexedFields implements Resolvable<IndexedFieldsEvaluator> {
	private final Expression instances;
	private final Expression fieldName;
	private final Expression fieldValue;
	private final IndexedTokens indexedTokens;
	private final List<IndexedTokens> indexedAnnotations;
	private final NormalizationOptions normalizationOptions;

	IndexedFields(Expression instances, Expression fieldName, Expression fieldValue, IndexedTokens indexedTokens, List<IndexedTokens> indexedAnnotations, NormalizationOptions normalizationOptions) {
		super();
		this.instances = instances;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.indexedTokens = indexedTokens;
		this.indexedAnnotations = indexedAnnotations;
		this.normalizationOptions = normalizationOptions;
	}

	@Override
	public IndexedFieldsEvaluator resolveExpressions(LibraryResolver resolver) throws ResolverException {
		Evaluator instances = this.instances.resolveExpressions(resolver);
		Evaluator fieldName = this.fieldName.resolveExpressions(resolver);
		Evaluator fieldValue = this.fieldValue.resolveExpressions(resolver);
		IndexedTokensEvaluator indexedTokens = this.indexedTokens.resolveExpressions(resolver);
		List<IndexedTokensEvaluator> indexedAnnotations = resolver.resolveList(this.indexedAnnotations);
		return new IndexedFieldsEvaluator(instances, fieldName, fieldValue, indexedTokens, indexedAnnotations, normalizationOptions);
	}
}
