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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.bibliome.util.pattern.SequenceMatcher;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.types.EvaluatorMapping;
import alvisnlp.module.types.ExpressionMapping;

public class CreateTuple extends AbstractSetFeatures<Tuple> {
	private final String relationName;
	private final ExpressionMapping arguments;
	private final EvaluatorMapping resolvedArguments;
	
	public CreateTuple(Expression target, Evaluator resolvedTarget, ExpressionMapping features, EvaluatorMapping resolvedFeatures, String relationName, ExpressionMapping arguments, EvaluatorMapping resolvedArguments) {
		super(target, resolvedTarget, features, resolvedFeatures);
		this.relationName = relationName;
		this.arguments = arguments;
		this.resolvedArguments = resolvedArguments;
	}

	@Override
	protected Collection<Tuple> getElements(MatchActionContext ctx, Section section, SequenceMatcher<Element> matcher, Iterator<Element> elements) {
		Relation relation = section.ensureRelation(ctx.getOwner(), relationName);
		Tuple tuple = new Tuple(ctx.getOwner(), relation);
		for (Map.Entry<String,Evaluator> e : this.resolvedArguments.entrySet()) {
			Iterator<Element> it = e.getValue().evaluateElements(ctx.getEvaluationContext(), tuple);
			if (!it.hasNext())
				continue;
			Annotation arg = DownCastElement.toAnnotation(it.next());
			if (arg == null)
				continue;
			tuple.setArgument(e.getKey(), arg);
		}
		return Collections.singleton(tuple);
	}

	@Override
	public MatchAction resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new CreateTuple(target, target.resolveExpressions(resolver), features, features.resolveExpressions(resolver), relationName, arguments, arguments.resolveExpressions(resolver));
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		super.collectUsedNames(nameUsage, defaultType);
		nameUsage.addNames(NameType.RELATION, relationName);
		nameUsage.addNames(NameType.ARGUMENT, resolvedArguments.keySet());
		resolvedArguments.collectUsedNames(nameUsage, defaultType);
	}
}
