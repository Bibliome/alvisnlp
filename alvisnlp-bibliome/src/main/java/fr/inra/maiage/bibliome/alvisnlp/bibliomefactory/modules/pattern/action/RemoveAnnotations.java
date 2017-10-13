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

import java.util.Iterator;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DownCastElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.pattern.SequenceMatcher;

public class RemoveAnnotations extends AbstractMatchAction {
	private final String[] layerNames;
	
	public RemoveAnnotations(Expression target, Evaluator resolvedTarget, String[] layerNames) {
		super(target, resolvedTarget);
		this.layerNames = layerNames;
	}

	@Override
	protected void process(MatchActionContext ctx, Section section, SequenceMatcher<Element> matcher, Iterator<Element> elements) {
		EvaluationContext evalCtx = ctx.getEvaluationContext();
		evalCtx.setAllowRemoveAnnotation(true);
		for (Element elt : Iterators.loop(elements)) {
			Annotation a = DownCastElement.toAnnotation(elt);
			if (a == null) {
				continue;
			}
			for (String ln : layerNames) {
				evalCtx.registerRemoveAnnotation(a, ln);
			}
		}
//		for (String ln : layerNames) {
//			for (Annotation a : annotations)
//				ctx.removeAnnotation(a);
//			if (ln.equals(ctx.getMatchedLayerName()))
//				for (Annotation a : annotations)
//					ctx.removeAnnotation(a);
//			else {
//				Layer layer = section.ensureLayer(ln);
//				for (Annotation a : annotations)
//						layer.remove(a);
//			}
//		}
	}

	@Override
	public MatchAction resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new RemoveAnnotations(target, target.resolveExpressions(resolver), layerNames);
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		super.collectUsedNames(nameUsage, defaultType);
		nameUsage.addNames(NameType.LAYER, layerNames);
	}
}
