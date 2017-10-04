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


package org.bibliome.alvisnlp.library;

import java.util.Comparator;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;

public class SortComparatorEvaluator extends AbstractSortEvaluator {
	private final ComparatorContext comparatorContext;

	private SortComparatorEvaluator(Evaluator list, boolean removeDuplicates, boolean removeEquivalent, Evaluator comparator, ComparatorContext comparatorContext) {
		super("comp", list, removeDuplicates, removeEquivalent, comparator);
		this.comparatorContext = comparatorContext;
	}
	
	public SortComparatorEvaluator(Evaluator list, boolean removeDuplicates, boolean removeEquivalent, Evaluator comparator) {
		this(list, removeDuplicates, removeEquivalent, comparator, new ComparatorContext());
	}

	@Override
	protected Comparator<Element> getComparator(EvaluationContext ctx) {
		return new ExpressionComparator(ctx);
	}
	
	private class ExpressionComparator implements Comparator<Element> {
		private final EvaluationContext ctx;

		private ExpressionComparator(EvaluationContext ctx) {
			super();
			this.ctx = ctx;
		}

		@Override
		public int compare(Element a, Element b) {
			return comparatorContext.evaluateInt(value, ctx, a, b);
		}
	}
}
