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


package org.bibliome.alvisnlp.library;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;

@Library("bio")
public abstract class BIONotationLibrary extends FunctionLibrary {
	@Function(ftors=2)
	public static String get(EvaluationContext ctx, Element elt, String entityLayerName, String tokenLayerName, Evaluator suffix) {
		Annotation a = DownCastElement.toAnnotation(elt);
		if (a == null) {
			return "!NOTANNOTATION";
		}
		Section sec = a.getSection();
		if (!sec.hasLayer(tokenLayerName)) {
			return "!NOTOKENLAYER";
		}
		if (sec.hasLayer(entityLayerName)) {
			Layer entityLayer = sec.getLayer(entityLayerName);
			Layer enclosing = entityLayer.including(a);
			Layer tokenLayer = sec.getLayer(tokenLayerName);
			for (Annotation entity : enclosing) {
				Layer entityTokens = tokenLayer.between(entity);
				if (entityTokens.isEmpty()) {
					return "!WTFTOKENS";
				}
				String suf = suffix.evaluateString(ctx, entity);
				if (entityTokens.get(0) == a) {
					return "B-" + suf;
				}
				return "I-" + suf;
			}
		}
		return "O";
	}
}
