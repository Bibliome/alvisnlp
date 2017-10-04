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


package org.bibliome.alvisnlp.library.standard;

import java.util.Iterator;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;

@Library("set")
public abstract class SetLibrary extends FunctionLibrary {
	public static final String NAME = "set";
	
	@Function(ftors=1, nameTypes={NameType.FEATURE})
	public static final Iterator<Element> feat(EvaluationContext ctx, Element elt, String key, Evaluator value) {
		ctx.registerSetFeature(elt, key, value.evaluateString(ctx, elt));
		return Iterators.singletonIterator(elt);
	}
	
	@Function
	public static final Iterator<Element> feat(EvaluationContext ctx, Element elt, Evaluator key, Evaluator value) {
		return feat(ctx, elt, key.evaluateString(ctx, elt), value);
	}

	@Function(firstFtor="remove-feature", ftors=1, nameTypes={NameType.FEATURE})
	public static final String removeFeature(EvaluationContext ctx, Element elt, String key) {
		ctx.registerRemoveFeature(elt, key);
		String result = elt.getLastFeature(key);
		return result == null ? "" : result;
	}
	
	@Function(firstFtor="remove-arg", ftors=1, nameTypes={NameType.ARGUMENT})
	public static final Iterator<Element> removeArg(EvaluationContext ctx, Element elt, String role) {
		Tuple t = DownCastElement.toTuple(elt);
		if (t == null)
			return Iterators.emptyIterator();
		ctx.registerSetArgument(t, role, null);
		if (t.hasArgument(role)) {
			return Iterators.singletonIterator(t.getArgument(role));
		}
		return Iterators.emptyIterator();
	}
	
	@Function(firstFtor="remove-arg")
	public static final Iterator<Element> removeArg(EvaluationContext ctx, Element elt, Evaluator role) {
		return removeArg(ctx, elt, role.evaluateString(ctx, elt));
	}
	
	@Function(ftors=1, nameTypes={NameType.ARGUMENT})
	public static final Iterator<Element> arg(EvaluationContext ctx, Element elt, String role, Evaluator arg) {
		Tuple t = DownCastElement.toTuple(elt);
		if (t == null)
			return Iterators.emptyIterator();
		Iterator<Element> args = arg.evaluateElements(ctx, elt);
		if (!args.hasNext())
			return Iterators.emptyIterator();
		ctx.registerSetArgument(t, role, args.next());
		return Iterators.singletonIterator(elt);
	}
	
	@Function
	public static final Iterator<Element> arg(EvaluationContext ctx, Element elt, Evaluator role, Evaluator arg) {
		return arg(ctx, elt, role.evaluateString(ctx, elt), arg);
	}
}
