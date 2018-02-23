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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.standard;

import java.util.Iterator;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DownCastElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Function;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.FunctionLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Library;
import fr.inra.maiage.bibliome.util.Iterators;

@Library("setlayer")
public abstract class SetLayerLibrary extends FunctionLibrary {
	public static final String NAME = "setlayer";
	
	@Function(ftors=1, nameTypes={NameType.LAYER})
	public static final Iterator<Element> add(EvaluationContext ctx, Element elt, String layerName) {
//		if (!ctx.isAllowAddAnnotation())
//			throw new RuntimeException("layers addition is not allowed");
		Annotation a = DownCastElement.toAnnotation(elt);
		if (a == null)
			return Iterators.emptyIterator();
		ctx.registerAddAnnotation(a, layerName);
		return Iterators.singletonIterator(a);
	}
	
	@Function
	public static final Iterator<Element> add(EvaluationContext ctx, Element elt, Evaluator layerName) {
//		if (!ctx.isAllowAddAnnotation())
//			throw new RuntimeException("layers addition is not allowed");
		Annotation a = DownCastElement.toAnnotation(elt);
		if (a == null)
			return Iterators.emptyIterator();
		ctx.registerAddAnnotation(a, layerName.evaluateString(ctx, a));
		return Iterators.singletonIterator(a);
	}

	@Function(ftors=1, nameTypes={NameType.LAYER})
	public static final Iterator<Element> remove(EvaluationContext ctx, Element elt, String layerName) {
//		if (!ctx.isAllowRemoveAnnotation())
//			throw new RuntimeException("layer removal is not allowed");
		Annotation a = DownCastElement.toAnnotation(elt);
		if (a == null)
			return Iterators.emptyIterator();
		ctx.registerRemoveAnnotation(a, layerName);
		return Iterators.singletonIterator(a);
	}
	
	@Function(ftors=1, nameTypes={NameType.LAYER})
	public static final Iterator<Element> clear(EvaluationContext ctx, Element elt, String layerName) {
		Section sec = DownCastElement.toSection(elt);
		if (sec == null) {
			return Iterators.emptyIterator();
		}
		ctx.registerClearedLayer(sec, layerName);
		return Iterators.singletonIterator(sec);
	}
}
