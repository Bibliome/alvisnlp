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


package alvisnlp.corpus.expressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.bibliome.util.filters.Filter;

import alvisnlp.corpus.Element;

public abstract class AbstractEvaluator implements Evaluator {
	protected AbstractEvaluator() {
		super();
	}
	
	protected static <T> Collection<T> intersection(Collection<T> a, Collection<T> b) {
		Collection<T> result = new ArrayList<T>(a);
		result.retainAll(b);
		return result;
	}

	@Override
	public Filter<Element> getFilter(EvaluationContext ctx) {
		return new ExpressionFilter(ctx);
	}
	
	private final class ExpressionFilter implements Filter<Element> {
		private final EvaluationContext ctx;

		private ExpressionFilter(EvaluationContext ctx) {
			super();
			this.ctx = ctx;
		}

		@Override
		public boolean accept(Element x) {
			return evaluateBoolean(ctx, x);
		}
	}

	@Override
	public boolean check(Logger logger) {
		return true;
	}

	@Override
	public boolean accept(Element x, EvaluationContext param) {
		return evaluateBoolean(param, x);
	}
}
