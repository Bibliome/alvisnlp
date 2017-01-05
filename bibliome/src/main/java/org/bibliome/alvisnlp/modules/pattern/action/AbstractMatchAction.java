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


package org.bibliome.alvisnlp.modules.pattern.action;

import java.util.Iterator;

import org.bibliome.util.pattern.SequenceMatcher;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;


abstract class AbstractMatchAction implements MatchAction {
	protected final Expression target;
	protected final Evaluator resolvedTarget;
	
	protected AbstractMatchAction(Expression target, Evaluator resolvedTarget) {
		super();
		this.target = target;
		this.resolvedTarget = resolvedTarget;
	}

	@Override
	public void process(MatchActionContext ctx, Section section, SequenceMatcher<Element> matcher) {
		process(ctx, section, matcher, resolvedTarget.evaluateElements(ctx.getEvaluationContext(), section));
	}
	
	protected abstract void process(MatchActionContext ctx, Section section, SequenceMatcher<Element> matcher, Iterator<Element> elements);

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		if (resolvedTarget != null) {
			resolvedTarget.collectUsedNames(nameUsage, defaultType);
		}
	}
}
