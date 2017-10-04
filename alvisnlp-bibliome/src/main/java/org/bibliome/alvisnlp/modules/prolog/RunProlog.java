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

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.alvisnlp.modules.prolog.RunProlog.RunPrologResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.Timer;
import org.bibliome.util.streams.SourceStream;

import alice.tuprolog.Prolog;
import alice.tuprolog.PrologException;
import alice.tuprolog.Theory;
import alice.tuprolog.lib.JavaLibrary;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ActionInterface;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

@AlvisNLPModule(beta=true)
public abstract class RunProlog extends CorpusModule<RunPrologResolvedObjects> implements ActionInterface {
	private Expression target;
	private FactDefinition[] facts;
	private SourceStream theory;
	private GoalDefinition[] goals;
	
	static class RunPrologResolvedObjects extends ResolvedObjects {
		private final Evaluator target;
		private final FactDefinition[] facts;
		private final SolveInfoLibrary solveInfoLibrary;
		private final GoalDefinition[] goals;

		private RunPrologResolvedObjects(ProcessingContext<Corpus> ctx, RunProlog module) throws ResolverException {
			super(ctx, module);
			target = rootResolver.resolveNullable(module.target);
			facts = rootResolver.resolveArray(module.facts, FactDefinition.class);
			solveInfoLibrary = new SolveInfoLibrary();
			LibraryResolver goalResolver = new LibraryResolver(rootResolver);
			goalResolver.addLibrary(solveInfoLibrary);
			goals = goalResolver.resolveArray(module.goals, GoalDefinition.class);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesNullable(target, defaultType);
			nameUsage.collectUsedNamesArray(facts, defaultType);
			nameUsage.collectUsedNamesArray(goals, defaultType);
		}
	}
	
	@Override
	protected RunPrologResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new RunPrologResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		RunPrologResolvedObjects resObj = getResolvedObjects();
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		EvaluationContext actionCtx = new EvaluationContext(logger, this);
		try {
			Theory theory = buildTheory(ctx);
			Timer<TimerCategory> runTimer = getTimer(ctx, "run", TimerCategory.MODULE, true);
			for (Element e : Iterators.loop(resObj.target.evaluateElements(evalCtx, corpus))) {
				Prolog engine = new Prolog();
				JavaLibrary javaLibrary = (JavaLibrary) engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
				engine.setTheory(theory);
				for (FactDefinition f : resObj.facts)
					engine.addTheory(f.getTheory(javaLibrary, evalCtx, e));
				resObj.solveInfoLibrary.setJavaLibrary(javaLibrary);
				for (GoalDefinition g : resObj.goals)
					g.solve(engine, resObj.solveInfoLibrary, actionCtx, e);
			}
			runTimer.stop();
			commit(ctx, actionCtx);
		}
		catch (IOException|PrologException e) {
			rethrow(e);
		}
	}

	@TimeThis(task="load-program", category=TimerCategory.LOAD_RESOURCE)
	protected Theory buildTheory(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx) throws IOException {
		try (InputStream is = theory.getInputStream()) {
			return new Theory(is);
		}
	}

	@Param
	public Expression getTarget() {
		return target;
	}

	@Param
	public FactDefinition[] getFacts() {
		return facts;
	}

	@Param
	public GoalDefinition[] getGoals() {
		return goals;
	}

	@Param
	public SourceStream getTheory() {
		return theory;
	}

	public void setTarget(Expression target) {
		this.target = target;
	}

	public void setFacts(FactDefinition[] facts) {
		this.facts = facts;
	}

	public void setTheory(SourceStream theory) {
		this.theory = theory;
	}

	public void setGoals(GoalDefinition[] goals) {
		this.goals = goals;
	}
}
