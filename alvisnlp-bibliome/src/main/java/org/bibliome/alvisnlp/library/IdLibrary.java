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

import java.util.UUID;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;

@Library("id")
public abstract class IdLibrary extends FunctionLibrary {
	@Function
	public static int unique(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		return System.identityHashCode(elt);
	}
	
	@Function
	public static String uuid() {
		return UUID.randomUUID().toString();
	}
	
	private static int enumerate(EvaluationContext ctx, Element elt, String key, String prefix, int n, Evaluator expr) {
		final int len = prefix.length();
		StringBuilder sb = new StringBuilder(prefix);
		for (Element e : Iterators.loop(expr.evaluateElements(ctx, elt))) {
			sb.setLength(len);
			sb.append(n++);
			ctx.registerSetFeature(e, key, sb.toString());
		}
		return n;
	}

	@Function(ftors=2, nameTypes={NameType.FEATURE})
	public static int enumerate(EvaluationContext ctx, Element elt, String key, String prefix, Evaluator expr, Evaluator start) {
		return enumerate(ctx, elt, key, prefix, start.evaluateInt(ctx, elt), expr);
	}

	@Function(ftors=2, nameTypes={NameType.FEATURE})
	public static int enumerate(EvaluationContext ctx, Element elt, String key, String prefix, Evaluator expr) {
		return enumerate(ctx, elt, key, prefix, 1, expr);
	}

	@Function(ftors=2, nameTypes={NameType.FEATURE})
	public static int enumerate0(EvaluationContext ctx, Element elt, String key, String prefix, Evaluator expr) {
		return enumerate(ctx, elt, key, prefix, 0, expr);
	}

	@Function(ftors=1, nameTypes={NameType.FEATURE})
	public static int enumerate(EvaluationContext ctx, Element elt, String key, Evaluator expr, Evaluator start) {
		return enumerate(ctx, elt, key, "", start.evaluateInt(ctx, elt), expr);
	}

	@Function(ftors=1, nameTypes={NameType.FEATURE})
	public static int enumerate(EvaluationContext ctx, Element elt, String key, Evaluator expr) {
		return enumerate(ctx, elt, key, "", 1, expr);
	}

	@Function(ftors=1, nameTypes={NameType.FEATURE})
	public static int enumerate0(EvaluationContext ctx, Element elt, String key, Evaluator expr) {
		return enumerate(ctx, elt, key, "", 0, expr);
	}
}
