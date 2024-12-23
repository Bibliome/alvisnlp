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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library;

import java.util.Comparator;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;

class SortDoubleEvaluator extends AbstractSortEvaluator {
	SortDoubleEvaluator(Evaluator list, boolean removeDuplicates, boolean removeEquivalent, Evaluator value) {
		super("dval", list, removeDuplicates, removeEquivalent, value);
	}

	@Override
	protected Comparator<Element> getComparator(EvaluationContext ctx) {
		return new DoubleExpressionComparator(ctx);
	}
	
	private final class DoubleExpressionComparator implements Comparator<Element> {
		private final EvaluationContext ctx;

		private DoubleExpressionComparator(EvaluationContext ctx) {
			super();
			this.ctx = ctx;
		}

		@Override
		public int compare(Element a, Element b) {
			return Double.compare(value.evaluateDouble(ctx, a), value.evaluateDouble(ctx, b));
		}
	}
}
