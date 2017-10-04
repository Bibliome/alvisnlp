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


package org.bibliome.alvisnlp.library.standard;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bibliome.util.filters.Filters;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.AbstractListEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.EvaluationType;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

@Library("select")
public abstract class SelectLibrary extends FunctionLibrary {
	public static final String NAME = "select";
	
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
