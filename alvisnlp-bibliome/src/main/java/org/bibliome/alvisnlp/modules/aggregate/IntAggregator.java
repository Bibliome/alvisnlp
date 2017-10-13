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

import java.util.concurrent.atomic.AtomicInteger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.util.BinaryNumericOperator;

class IntAggregator extends Aggregator {
	private final BinaryNumericOperator operator;
	
	IntAggregator(Expression item, BinaryNumericOperator operator) {
		super(item);
		this.operator = operator;
	}

	@Override
	protected Aggregator.Resolved resolveExpressions(LibraryResolver resolver, Evaluator item) {
		return new Resolved(item);
	}

	private class Resolved extends Aggregator.Resolved {
		private Resolved(Evaluator item) {
			super(item);
		}

		@Override
		public Object createValue() {
			return new AtomicInteger();
		}

		@Override
		public void incorporateEntry(Object value, EvaluationContext ctx, Element elt) {
			AtomicInteger intValue = (AtomicInteger) value;
			int item = this.item.evaluateInt(ctx, elt);
			int newValue = operator.compute(intValue.get(), item);
			intValue.set(newValue);
		}

		@Override
		public String toString(Object value) {
			return value.toString();
		}
	}
}
