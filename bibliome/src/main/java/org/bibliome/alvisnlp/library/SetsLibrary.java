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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.AbstractIteratorEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

@Library("sets")
public abstract class SetsLibrary extends FunctionLibrary {
	private static final Set<Element> asSet(Iterator<Element> it)  {
		Set<Element> result = new HashSet<Element>();
		Iterators.fill(it, result);
		return result;
	}
	
	@Function
	public static final Iterator<Element> union(Iterator<Element> a, Iterator<Element> b) {
		Set<Element> result = asSet(a);
		Iterators.fill(b, result);
		return result.iterator();
	}

	private static final class InterEvaluator extends AbstractIteratorEvaluator {
		private final Evaluator a;
		private final Evaluator b;
		
		private InterEvaluator(Evaluator a, Evaluator b) {
			super();
			this.a = a;
			this.b = b;
		}

		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			Set<Element> result = asSet(a.evaluateElements(ctx, elt));
			result.retainAll(asSet(b.evaluateElements(ctx, elt)));
			return result.iterator();
		}
		
		@Override
		public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
			Set<Element> aSet = asSet(a.evaluateElements(ctx, elt));
			for (Element e : Iterators.loop(b.evaluateElements(ctx, elt)))
				if (!aSet.contains(e))
					return false;
			return true;
		}

		@Override
		public int evaluateInt(EvaluationContext ctx, Element elt) {
			Set<Element> aSet = asSet(a.evaluateElements(ctx, elt));
			int result = 0;
			for (Element e : Iterators.loop(b.evaluateElements(ctx, elt)))
				if (aSet.contains(e))
					result++;
			return result;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			a.collectUsedNames(nameUsage, defaultType);
			b.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Function
	public static final Evaluator inter(Evaluator a, Evaluator b) {
		return new InterEvaluator(a, b);
	}

	@Function
	public static final Iterator<Element> diff(Iterator<Element> a, Iterator<Element> b) {
		Set<Element> result = asSet(a);
		result.removeAll(asSet(b));
		return result.iterator();
	}

	@Function
	public static final boolean included(Iterator<Element> a, Iterator<Element> b) {
		Set<Element> bSet = asSet(b);
		for (Element e : Iterators.loop(a))
			if (!bSet.contains(e))
				return false;
		return true;
	}
}
