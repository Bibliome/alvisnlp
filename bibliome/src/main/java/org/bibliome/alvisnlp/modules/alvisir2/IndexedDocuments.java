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

public class IndexedDocuments implements Resolvable<IndexedDocumentsEvaluator> {
	private final Expression instances;
	private final List<IndexedFields> indexedFields;
	
	IndexedDocuments(Expression instances, List<IndexedFields> indexedFields) {
		super();
		this.instances = instances;
		this.indexedFields = indexedFields;
	}

	@Override
	public IndexedDocumentsEvaluator resolveExpressions(LibraryResolver resolver) throws ResolverException {
		Evaluator instances = this.instances.resolveExpressions(resolver);
		List<IndexedFieldsEvaluator> indexedFields = resolver.resolveList(this.indexedFields);
		return new IndexedDocumentsEvaluator(instances, indexedFields);
	}
}
