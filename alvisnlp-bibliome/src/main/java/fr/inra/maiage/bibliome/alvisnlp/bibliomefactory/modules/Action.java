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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules;

import java.util.Iterator;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.Action.ActionResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.VariableLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.VariableLibrary.Variable;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ActionInterface;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;

@AlvisNLPModule
public abstract class Action extends CorpusModule<ActionResolvedObjects> implements ActionInterface {
	private Expression target;
	private Expression action;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		ActionResolvedObjects res = new ActionResolvedObjects(ctx);
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		EvaluationContext actionCtx = new EvaluationContext(logger, this);
		int n = 0;
		for (Element e : Iterators.loop(res.target.evaluateElements(evalCtx, corpus))) {
			res.targetVariable.set(e);
			Iterator<Element> it = res.action.evaluateElements(actionCtx, e);
			Iterators.deplete(it);
			n++;
		}
		if (n == 0) {
			logger.warning("no targets visited");
		}
		else {
			logger.info("targets visited: " + n);
		}
		logger.info("committing changes");
		commit(ctx, actionCtx);
	}
	
	class ActionResolvedObjects extends ResolvedObjects {
		private final Evaluator target;
		private final Evaluator action;
		private final Variable targetVariable;
		
		private ActionResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
			super(ctx, Action.this);
			VariableLibrary targetLib = new VariableLibrary("target");
			targetVariable = targetLib.newVariable(null);
			LibraryResolver actionResolver = targetLib.newLibraryResolver(rootResolver);
			target = rootResolver.resolveNullable(Action.this.target);
			action = actionResolver.resolveNullable(Action.this.action);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			target.collectUsedNames(nameUsage, defaultType);
			action.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Override
	protected ActionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ActionResolvedObjects(ctx);
	}

	@Param
	public Expression getTarget() {
		return target;
	}

	@Param
	public Expression getAction() {
		return action;
	}

	public void setTarget(Expression target) {
		this.target = target;
	}

	public void setAction(Expression action) {
		this.action = action;
	}
}
