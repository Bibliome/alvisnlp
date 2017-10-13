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

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DownCastElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Function;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.FunctionLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Library;

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
