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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.standard;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.AbstractListEvaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ConstantsLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Function;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.FunctionLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Library;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.filters.Filters;

@Library("select")
public abstract class SelectLibrary extends FunctionLibrary {
	public static final String NAME = "select";
	
	@Function
	public static final Iterator<Element> until(EvaluationContext ctx, Element elt, Evaluator e, Evaluator until) {
		return new FromUntilIterator(e.evaluateElements(ctx, elt), ctx, ConstantsLibrary.EVALUATOR_TRUE, until);
	}
	
	@Function
	public static final Iterator<Element> from(EvaluationContext ctx, Element elt, Evaluator e, Evaluator from) {
		return new FromUntilIterator(e.evaluateElements(ctx, elt), ctx, from, ConstantsLibrary.EVALUATOR_TRUE);
	}
	
	@Function(firstFtor = "from-until")
	public static final Iterator<Element> fromUntil(EvaluationContext ctx, Element elt, Evaluator e, Evaluator from, Evaluator until) {
		return new FromUntilIterator(e.evaluateElements(ctx, elt), ctx, from, until);
	}
	
	private static final class FromUntilIterator implements Iterator<Element> {
		private final Iterator<Element> matrix;
		private final EvaluationContext evalCtx;
		private final Evaluator until;
		private Element first;
		private boolean last = false;
		
		private FromUntilIterator(Iterator<Element> matrix, EvaluationContext evalCtx, Evaluator from, Evaluator until) {
			super();
			this.matrix = matrix;
			this.evalCtx = evalCtx;
			this.until = until;
			this.first = lookupFirst(matrix, evalCtx, from);
		}
		
		private static Element lookupFirst(Iterator<Element> matrix, EvaluationContext evalCtx, Evaluator from) {
			for (Element elt : Iterators.loop(matrix)) {
				if (from.evaluateBoolean(evalCtx, elt)) {
					return elt;
				}
			}
			return null;
		}

		@Override
		public boolean hasNext() {
			return (first != null) || (matrix.hasNext() && !last);
		}

		@Override
		public Element next() {
			Element result = first;
			if (result == null) {
				if (last) {
					throw new NoSuchElementException();
				}
				result = matrix.next();
			}
			else {
				first = null;
			}
			last = until.evaluateBoolean(evalCtx, result);
			return result;
		}
	}
	
	@Function(firstFtor="[]")
	public static final Iterator<Element> filter(EvaluationContext ctx, Element elt, Evaluator e, Evaluator filter) {
		return Filters.apply(filter.getFilter(ctx), e.evaluateElements(ctx, elt));
	}

	@Function(firstFtor="{}")
	public static final Evaluator range(Evaluator e, Evaluator start, Evaluator end) {
		return new RangeEvaluator(e, start, end);
	}

	@Function(firstFtor="{}")
	public static final Evaluator range(Evaluator e, Evaluator start) {
		return new RangeEvaluator(e, start, null);
	}
	
	private static final class RangeEvaluator extends AbstractListEvaluator {
		private final Evaluator expr;
		private final Evaluator start;
		private final Evaluator end;

		private RangeEvaluator(Evaluator expr, Evaluator start, Evaluator end) {
			super();
			this.expr = expr;
			this.start = start;
			this.end = end;
		}

		@Override
		public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
			List<Element> list = expr.evaluateList(ctx, elt);
			if (list.isEmpty())
				return Collections.emptyList();
			int len = list.size();
			int start = this.start.evaluateInt(ctx, elt);
			if (end == null) {
				Integer index = getSingleIndex(len, start);
				if (index == null) {
					return Collections.emptyList();
				}
				return Collections.singletonList(list.get(index));
			}
			int startIndex = getStartIndex(len, start);
			int end = this.end.evaluateInt(ctx, elt);
			int endIndex = getEndIndex(len, startIndex, end);
			return list.subList(startIndex, endIndex);
		}

		private static Integer getSingleIndex(int len, int start) {
			if (len == 0)
				return null;
			if (start >= len)
				return null;
			if (start < 0)
				start += len;
			if (start < 0)
				return null;
			return start;
		}

		private static int getStartIndex(int len, int start) {
			if (len == 0)
				return 0;
			if (start >= len)
				return len - 1;
			if (start < 0)
				start += len;
			if (start < 0)
				return 0;
			return start;
		}

		private static int getEndIndex(int len, int start, int end) {
			if (len == 0)
				return 0;
			if (end < 0)
				end += len + 1;
			if (end < start)
				return start;
			if (end > len)
				return len;
			return end;
		}

		@Override
		public Collection<EvaluationType> getTypes() {
			return expr.getTypes();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			expr.collectUsedNames(nameUsage, defaultType);
			start.collectUsedNames(nameUsage, defaultType);
			if (end != null) {
				end.collectUsedNames(nameUsage, defaultType);
			}
		}
	}
}
