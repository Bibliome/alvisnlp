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
import java.util.Iterator;

import org.bibliome.util.Iterators;
import org.bibliome.util.pattern.SequenceMatcher;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

public class RemoveAnnotations extends AbstractMatchAction {
	private final String[] layerNames;
	
	public RemoveAnnotations(Expression target, Evaluator resolvedTarget, String[] layerNames) {
		super(target, resolvedTarget);
		this.layerNames = layerNames;
	}

	@Override
	protected void process(MatchActionContext ctx, Section section, SequenceMatcher<Element> matcher, Iterator<Element> elements) {
		Collection<Annotation> annotations = new ArrayList<Annotation>();
		for (Element elt : Iterators.loop(elements)) {
			Annotation a = DownCastElement.toAnnotation(elt);
			if (a != null)
				annotations.add(a);
		}
//		for (String ln : layerNames) {
			for (Annotation a : annotations)
				ctx.removeAnnotation(a);
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
