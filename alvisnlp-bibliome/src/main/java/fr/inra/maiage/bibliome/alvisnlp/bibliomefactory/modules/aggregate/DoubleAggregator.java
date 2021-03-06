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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.aggregate;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicLong;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.util.BinaryNumericOperator;

class DoubleAggregator extends Aggregator {
	private final BinaryNumericOperator operator;
	private final DecimalFormat format;

	DoubleAggregator(Expression item, BinaryNumericOperator operator, DecimalFormat format) {
		super(item);
		this.operator = operator;
		this.format = format;
	}

	@Override
	protected Aggregator.Resolved resolveExpressions(LibraryResolver resolver, Evaluator item) {
		return new Resolved(item);
	}

	private class Resolved extends Aggregator.Resolved {
		public Resolved(Evaluator item) {
			super(item);
		}

		@Override
		public Object createValue() {
			return new AtomicLong();
		}

		@Override
		public void incorporateEntry(Object value, EvaluationContext ctx, Element elt) {
			AtomicLong longValue = (AtomicLong) value;
			double item = this.item.evaluateDouble(ctx, elt);
			double newValue = operator.compute(longValue.doubleValue(), item);
			longValue.set((long) newValue);
		}

		@Override
		public String toString(Object value) {
			AtomicLong longValue = (AtomicLong) value;
			return format.format(longValue.doubleValue());
		}
	}
}
