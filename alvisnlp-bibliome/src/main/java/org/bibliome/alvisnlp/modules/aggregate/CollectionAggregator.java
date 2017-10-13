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


package org.bibliome.alvisnlp.modules.aggregate;

import java.util.Collection;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.util.Strings;

class CollectionAggregator extends Aggregator {
	private final CollectionFactory collectionFactory;
	private final char separator;
	private final boolean freq;
	
	CollectionAggregator(Expression item, CollectionFactory collectionFactory, char separator, boolean freq) {
		super(item);
		this.collectionFactory = collectionFactory;
		this.separator = separator;
		this.freq = freq;
	}

	@Override
	protected Resolved resolveExpressions(LibraryResolver resolver, Evaluator item) {
		return new Resolved(item);
	}

	private class Resolved extends Aggregator.Resolved {
		public Resolved(Evaluator item) {
			super(item);
		}

		@Override
		Object createValue() {
			Collection<String> result = collectionFactory.createCollection();
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		String toString(Object value) {
			if (freq) {
				return Integer.toString(((Collection<String>) value).size());
			}
			return Strings.join((Collection<String>) value, separator);
		}

		@SuppressWarnings("unchecked")
		@Override
		void incorporateEntry(Object value, EvaluationContext ctx, Element entry) {
			String itemString = item.evaluateString(ctx, entry);
			((Collection<String>) value).add(itemString);
		}
	}
}
