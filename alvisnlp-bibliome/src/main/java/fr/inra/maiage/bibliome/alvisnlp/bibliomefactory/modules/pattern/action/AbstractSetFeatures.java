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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pattern.action;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.EvaluatorMapping;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping;
import fr.inra.maiage.bibliome.util.pattern.SequenceMatcher;

public abstract class AbstractSetFeatures<T extends Element> extends AbstractMatchAction {
	protected final ExpressionMapping features;
	protected final EvaluatorMapping resolvedFeatures;

	public AbstractSetFeatures(Expression target, Evaluator resolvedTarget, ExpressionMapping features, EvaluatorMapping resolvedFeatures) {
		super(target, resolvedTarget);
		this.features = features;
		this.resolvedFeatures = resolvedFeatures;
	}

	@Override
	protected void process(MatchActionContext ctx, Section section, SequenceMatcher<Element> matcher, Iterator<Element> elements) {
		ctx.getEvaluationContext().setAllowSetFeature(true);
		for (T elt : getElements(ctx, section, matcher, elements)) {
			setFeatures(ctx.getEvaluationContext(), elt);
		}
	}

	protected void setFeatures(EvaluationContext evalCtx, T elt) {
		for (Map.Entry<String,Evaluator> e : resolvedFeatures.entrySet()) {
			String key = e.getKey();
			String value = e.getValue().evaluateString(evalCtx, elt);
			evalCtx.registerSetFeature(elt, key, value);
		}
	}
	
	protected abstract Collection<T> getElements(MatchActionContext ctx, Section section, SequenceMatcher<Element> matcher, Iterator<Element> elements);

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		super.collectUsedNames(nameUsage, defaultType);
		if (resolvedFeatures != null) {
			nameUsage.addNames(NameType.FEATURE, resolvedFeatures.keySet());
		}
		if (resolvedFeatures != null) {
			resolvedFeatures.collectUsedNames(nameUsage, defaultType);
		}
	}
}
