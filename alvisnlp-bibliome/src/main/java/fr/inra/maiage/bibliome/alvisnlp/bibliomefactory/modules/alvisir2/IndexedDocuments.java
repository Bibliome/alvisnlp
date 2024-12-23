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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisir2;

import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Resolvable;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;

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
