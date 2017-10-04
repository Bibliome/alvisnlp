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
import alvisnlp.corpus.ElementComparator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

public class ByOrderEvaluator extends AbstractSortEvaluator {
	private final AbstractSortEvaluator secondarySortEvaluator;
	
	public ByOrderEvaluator(Evaluator list, boolean removeDuplicates, boolean removeEquivalent, AbstractSortEvaluator secondarySortEvaluator) {
		super("ordermore", list, removeDuplicates, removeEquivalent, null);
		this.secondarySortEvaluator = secondarySortEvaluator;
	}

	@Override
	protected Comparator<Element> getComparator(final EvaluationContext ctx) {
		return new Comparator<Element>() {
			@Override
			public int compare(Element o1, Element o2) {
				int r = ElementComparator.INSTANCE.compare(o1, o2);
				if (r != 0) {
					return r;
				}
				return secondarySortEvaluator.getComparator(ctx).compare(o1, o2);
			}
			
		};
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		super.collectUsedNames(nameUsage, defaultType);
		secondarySortEvaluator.collectUsedNames(nameUsage, defaultType);
	}
}
