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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.AbstractIteratorEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.EvaluationType;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

final class UnionEvaluator extends AbstractIteratorEvaluator {
	private final List<Evaluator> exprs;

	UnionEvaluator(List<Evaluator> exprs) {
		super();
		this.exprs = exprs;
	}

	@Override
	public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
		List<Iterator<Element>> itl = new ArrayList<Iterator<Element>>(exprs.size());
		for (Evaluator e : exprs)
			itl.add(e.evaluateElements(ctx, elt));
		return Iterators.flatten(itl.iterator());
	}

	@Override
	public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
		List<Element> result = new ArrayList<Element>();
		for (Evaluator e : exprs)
			Iterators.fill(e.evaluateElements(ctx, elt), result);
		return result;
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
		for (Evaluator e : exprs)
			if (e.evaluateElements(ctx, elt).hasNext())
				return true;
		return false;
	}

	@Override
	public double evaluateDouble(EvaluationContext ctx, Element elt) {
		int result = 0;
		for (Evaluator e : exprs)
			result += e.evaluateDouble(ctx, elt);
		return result;
	}

	@Override
	public Collection<EvaluationType> getTypes() {
		return Collections.singleton(EvaluationType.ELEMENTS);
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		for (Evaluator e : exprs) {
			e.collectUsedNames(nameUsage, defaultType);
		}
	}
}