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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.AbstractIteratorEvaluator;
import alvisnlp.corpus.expressions.ConstantsLibrary;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.corpus.expressions.VariableLibrary;
import alvisnlp.corpus.expressions.VariableLibrary.Variable;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

@Library("pred")
public abstract class PredicateLibrary extends FunctionLibrary {
	@Function
	public static final boolean all(EvaluationContext ctx, Element elt, Evaluator c, Evaluator pred) {
		for (Element e : Iterators.loop(c.evaluateElements(ctx, elt)))
			if (!pred.evaluateBoolean(ctx, e))
				return false;
		return true;
	}

	@Function
	public static final boolean any(EvaluationContext ctx, Element elt, Evaluator c, Evaluator pred) {
		for (Element e : Iterators.loop(c.evaluateElements(ctx, elt)))
			if (pred.evaluateBoolean(ctx, e))
				return true;
		return false;
	}
	
	private static final class EnumerateEvaluator extends AbstractIteratorEvaluator {
		private final Evaluator start;
		private final Evaluator end;
		private final Variable var;
		private final Evaluator fun;
		private final Evaluator condition;
		
		private EnumerateEvaluator(LibraryResolver resolver, Evaluator start, Evaluator end, String varName, Expression expr, Expression condition) throws ResolverException {
			this.start = start;
			this.end = end;
			VariableLibrary varLib = new VariableLibrary(varName);
			var = varLib.newVariable(null);
			LibraryResolver varResolver = varLib.newLibraryResolver(resolver);
			fun = expr.resolveExpressions(varResolver);
			this.condition = condition.resolveExpressions(varResolver);
		}

		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			int start = this.start.evaluateInt(ctx, elt);
			Integer end = this.end == null ? null : this.end.evaluateInt(ctx, elt);
			Collection<Iterator<Element>> c = new ArrayList<Iterator<Element>>();
			for (int i = start; isContinue(ctx, elt, i, end); ++i) {
				var.set(i);
				c.add(fun.evaluateElements(ctx, elt));
			}
			return Iterators.flatten(c.iterator());
		}
		
		private boolean isContinue(EvaluationContext ctx, Element elt, int i, Integer end) {
			if ((end != null) && (i > end))
				return false;
			return condition.evaluateBoolean(ctx, elt);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			start.collectUsedNames(nameUsage, defaultType);
			if (end != null) {
				end.collectUsedNames(nameUsage, defaultType);
			}
			fun.collectUsedNames(nameUsage, defaultType);
			condition.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Function(ftors=1, firstFtor="enum-while")
	public static final Evaluator enumerate(LibraryResolver resolver, String varName, Evaluator start, Evaluator end, Expression expr, Expression condition) throws ResolverException {
		return new EnumerateEvaluator(resolver, start, end, varName, expr, condition);
	}
	
	@Function(ftors=1)
	public static final Evaluator enumerate(LibraryResolver resolver, String varName, Evaluator start, Evaluator end, Expression expr) throws ResolverException {
		return new EnumerateEvaluator(resolver, start, end, varName, expr, ConstantsLibrary.TRUE);
	}
	
	@Function(ftors=1, firstFtor="enum-while")
	public static final Evaluator enumerate(LibraryResolver resolver, String varName, Evaluator start, Expression expr, Expression condition) throws ResolverException {
		return new EnumerateEvaluator(resolver, start, null, varName, expr, condition);
	}
}
