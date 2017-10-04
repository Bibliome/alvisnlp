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


package org.bibliome.alvisnlp.modules.prolog;

import org.bibliome.util.Iterators;

import alice.tuprolog.Prolog;
import alice.tuprolog.PrologException;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Term;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.Resolvable;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;

public class GoalDefinition implements Resolvable<GoalDefinition>, NameUser {
	private final Term goal;
	private final Expression action;
	private final Evaluator resolvedAction;

	GoalDefinition(Term goal, Expression action, Evaluator resolvedAction) {
		super();
		this.goal = goal;
		this.action = action;
		this.resolvedAction = resolvedAction;
	}

	@Override
	public GoalDefinition resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new GoalDefinition(goal, action, action.resolveExpressions(resolver));
	}
	
	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		if (resolvedAction != null) {
			resolvedAction.collectUsedNames(nameUsage, defaultType);
		}
	}

	public void solve(Prolog engine, SolveInfoLibrary solveInfoLibrary, EvaluationContext evalCtx, Element elt) throws PrologException {
		SolveInfo solveInfo = engine.solve(goal);
		while (solveInfo.isSuccess()) {
			solveInfoLibrary.setSolveInfo(solveInfo);
			Iterators.deplete(resolvedAction.evaluateElements(evalCtx, elt));
			if (solveInfo.hasOpenAlternatives())
				solveInfo = engine.solveNext();
			else
				break;
		}
	}
}
