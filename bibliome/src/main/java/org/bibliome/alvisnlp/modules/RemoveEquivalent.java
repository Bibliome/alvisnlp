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


package org.bibliome.alvisnlp.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.RemoveEquivalent.RemoveEquivalentResolvedObjects;
import org.bibliome.util.Iterators;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.corpus.expressions.VariableLibrary;
import alvisnlp.corpus.expressions.VariableLibrary.Variable;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(beta=true)
public class RemoveEquivalent extends CorpusModule<RemoveEquivalentResolvedObjects> {
	private Expression target;
	private Expression equivalency;
	private Expression priority;
	
	public RemoveEquivalent() {
		super();
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		RemoveEquivalentResolvedObjects resObj = getResolvedObjects();
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		evalCtx.setAllowDeleteElement(true);
		Collection<List<Element>> equivalenceSets = new ArrayList<List<Element>>();
		int n = 0;
		for (Element elt : Iterators.loop(resObj.target.evaluateElements(evalCtx, corpus))) {
			++n;
			List<Element> set = lookupEquivalenceSet(equivalenceSets, evalCtx, elt);
			set.add(elt);
		}
		logger.fine("visited: " + n);
		logger.fine("equivalence sets: " + equivalenceSets.size());
		int remain = purgeSingletons(equivalenceSets);
		logger.fine("non-singleton equivalence sets: " + equivalenceSets.size());
		logger.fine("elements to remove: " + (remain - equivalenceSets.size()));
		for (List<Element> set : equivalenceSets) {
			if (resObj.priority != null) {
				Collections.sort(set, resObj.priority);
			}
			for (Element elt : set.subList(0, set.size() - 1)) {
				evalCtx.registerDeleteElement(elt);
			}
		}
		evalCtx.commit();
	}
	
	private static int purgeSingletons(Collection<List<Element>> equivalenceSets) {
		int result = 0;
		Iterator<List<Element>> it = equivalenceSets.iterator();
		while (it.hasNext()) {
			List<Element> set = it.next();
			if (set.size() == 1) {
				it.remove();
			}
			else {
				result += set.size();
			}
		}
		return result;
	}
	
	private List<Element> lookupEquivalenceSet(Collection<List<Element>> equivalenceSets, EvaluationContext evalCtx, Element elt) {
		RemoveEquivalentResolvedObjects resObj = getResolvedObjects();
		resObj.other.set(elt);
		for (List<Element> set : equivalenceSets) {
			for (Element elt2 : set) {
				if (resObj.equivalency.evaluateBoolean(evalCtx, elt2)) {
					return set;
				}
			}
		}
		List<Element> result = new ArrayList<Element>();
		equivalenceSets.add(result);
		return result;
	}

	@Override
	protected RemoveEquivalentResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new RemoveEquivalentResolvedObjects(ctx);
	}

	@SuppressWarnings("hiding")
	class RemoveEquivalentResolvedObjects extends ResolvedObjects {
		private final Evaluator target;
		private final Variable other;
		private final Evaluator equivalency;
		private final Comparator<Element> priority;
		
		private RemoveEquivalentResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
			super(ctx, RemoveEquivalent.this);
			this.target = RemoveEquivalent.this.target.resolveExpressions(rootResolver);
			VariableLibrary otherLib = new VariableLibrary("other");
			this.other = otherLib.newVariable(null);
			LibraryResolver equivResolver = otherLib.newLibraryResolver(rootResolver);
			this.equivalency = RemoveEquivalent.this.equivalency.resolveExpressions(equivResolver);
			if (RemoveEquivalent.this.priority == null) {
				this.priority = null;
			}
			else {
				final Evaluator priority = equivResolver.resolveNullable(RemoveEquivalent.this.priority);
				final EvaluationContext evalCtx = new EvaluationContext(getLogger(ctx));
				this.priority = new Comparator<Element>() {
					@Override
					public int compare(Element o1, Element o2) {
						other.set(o2);
						return priority.evaluateInt(evalCtx, o1);
					}
				};
			}
		}
	}

	@Param
	public Expression getEquivalency() {
		return equivalency;
	}

	@Param(mandatory=false)
	public Expression getPriority() {
		return priority;
	}

	@Param
	public Expression getTarget() {
		return target;
	}

	public void setTarget(Expression target) {
		this.target = target;
	}

	public void setEquivalency(Expression equivalency) {
		this.equivalency = equivalency;
	}

	public void setPriority(Expression priority) {
		this.priority = priority;
	}
	
	
}
