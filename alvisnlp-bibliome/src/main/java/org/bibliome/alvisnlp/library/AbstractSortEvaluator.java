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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.AbstractListEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.EvaluationType;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

abstract class AbstractSortEvaluator extends AbstractListEvaluator {
	protected final String ftor;
	protected final Evaluator list;
	protected final Evaluator value;
	protected final boolean removeDuplicates;
	protected final boolean removeEquivalent;

	protected AbstractSortEvaluator(String ftor, Evaluator list, boolean removeDuplicates, boolean removeEquivalent, Evaluator value) {
		super();
		if (removeDuplicates)
			this.ftor = "u" + ftor;
		else if (removeEquivalent)
			this.ftor = "n" + ftor;
		else
			this.ftor = ftor;
		this.list = list;
		this.removeDuplicates = removeDuplicates;
		this.removeEquivalent = removeEquivalent;
		this.value = value;
	}

	@Override
	public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
		Iterator<Element> list = this.list.evaluateElements(ctx, elt);
		List<Element> result = new ArrayList<Element>();
		Iterators.fill(list, result);
		Comparator<Element> comparator = getComparator(ctx);
		Collections.sort(result, comparator);
		if (removeDuplicates) {
			Iterator<Element> it = result.iterator();
			Element prev = null;
			for (Element e : Iterators.loop(it)) {
				if (e.equals(prev))
					it.remove();
				prev = e;
			}
		}
		if (removeEquivalent) {
			Iterator<Element> it = result.iterator();
			if (it.hasNext()) {
				Element prev = it.next();
				for (Element e : Iterators.loop(it)) {
					if (comparator.compare(e, prev) == 0)
						it.remove();
					prev = e;
				}
			}
		}
		return result;
	}
	
	protected abstract Comparator<Element> getComparator(EvaluationContext ctx);

	@Override
	public Collection<EvaluationType> getTypes() {
		Collection<EvaluationType> result = new ArrayList<EvaluationType>(list.getTypes());
		Iterator<EvaluationType> it = result.iterator();
		for (EvaluationType t : Iterators.loop(it))
			if (!t.element)
				it.remove();
		if (result.isEmpty())
			return Collections.singleton(EvaluationType.ELEMENTS);
		return result;
	}
	
	protected String prefixFtor() {
		if (removeDuplicates)
			return "u" + ftor;
		if (removeEquivalent)
			return "n" + ftor;
		return ftor;
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		list.collectUsedNames(nameUsage, defaultType);
		value.collectUsedNames(nameUsage, defaultType);
	}
}
