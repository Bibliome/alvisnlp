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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bibliome.util.Iterators;
import org.bibliome.util.pattern.SequenceMatcher;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.types.EvaluatorMapping;
import alvisnlp.module.types.ExpressionMapping;

public class CreateAnnotation extends AbstractSetFeatures<Annotation> {
	private final String[] targetLayerNames;


	public CreateAnnotation(Expression target, Evaluator resolvedTarget, ExpressionMapping features, EvaluatorMapping resolvedFeatures, String[] targetLayerNames) {
		super(target, resolvedTarget, features, resolvedFeatures);
		this.targetLayerNames = targetLayerNames;
	}

	@Override
	protected Collection<Annotation> getElements(MatchActionContext ctx, Section section, SequenceMatcher<Element> matcher, Iterator<Element> elements) {
		List<Element> list = new ArrayList<Element>();
		Iterators.fill(elements, list);
		if (list.isEmpty())
			return Collections.emptyList();
		Annotation start = DownCastElement.toAnnotation(list.get(0));
		if (start == null)
			return Collections.emptyList();
		Annotation end = DownCastElement.toAnnotation(list.get(list.size() - 1));
		if (end == null)
			return Collections.emptyList();
		Annotation a = new Annotation(ctx.getOwner(), section, start.getStart(), end.getEnd());
		for (String ln : targetLayerNames) {
			if (ln == ctx.getMatchedLayerName())
				ctx.addAnnotation(a);
			else
				section.ensureLayer(ln).add(a);
		}
		return Collections.singleton(a);
	}

	@Override
	public MatchAction resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new CreateAnnotation(target, target.resolveExpressions(resolver), features, features.resolveExpressions(resolver), targetLayerNames);
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		super.collectUsedNames(nameUsage, defaultType);
		nameUsage.addNames(NameType.LAYER, targetLayerNames);
	}
}
