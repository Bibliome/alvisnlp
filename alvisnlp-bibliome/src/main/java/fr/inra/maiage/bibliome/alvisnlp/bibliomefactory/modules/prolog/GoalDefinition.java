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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.prolog;

import alice.tuprolog.Prolog;
import alice.tuprolog.PrologException;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Term;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Resolvable;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUser;
import fr.inra.maiage.bibliome.util.Iterators;

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
