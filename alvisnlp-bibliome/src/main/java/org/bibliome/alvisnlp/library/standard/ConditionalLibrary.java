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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bibliome.util.Pair;
import org.bibliome.util.StringCat;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.AbstractEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.EvaluationType;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.documentation.Documentation;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

@Library("conditional")
public class ConditionalLibrary extends FunctionLibrary {
	public static final String NAME = "conditional";
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		checkExactFtors(ftors, 0);
		if (args.size() % 2 != 1)
			throw new ResolverException("conditional expects an odd number of arguments");
		final int nCases = args.size() / 2;
		List<Pair<Evaluator,Evaluator>> cases = new ArrayList<Pair<Evaluator,Evaluator>>(nCases);
		for (int i = 0; i < nCases; i += 2) {
			Evaluator cond = args.get(i).resolveExpressions(resolver);
			Evaluator val = args.get(i + 1).resolveExpressions(resolver);
			cases.add(new Pair<Evaluator,Evaluator>(cond, val));
		}
		return new ConditionalEvaluator(cases, args.get(args.size() - 1).resolveExpressions(resolver));
	}

	@Override
	public Documentation getDocumentation() {
		// TODO Auto-generated method stub
		return null;
	}

	private static final class ConditionalEvaluator extends AbstractEvaluator {
		private final List<Pair<Evaluator,Evaluator>> cases;
		private final Evaluator defaultCase;

		private ConditionalEvaluator(List<Pair<Evaluator,Evaluator>> cases, Evaluator defaultCase) {
			super();
			this.cases = cases;
			this.defaultCase = defaultCase;
		}

		@Override
		public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
			for (Pair<Evaluator,Evaluator> c : cases) {
				if (c.first.evaluateBoolean(ctx, elt))
					return c.second.evaluateBoolean(ctx, elt);
			}
			return defaultCase.evaluateBoolean(ctx, elt);
		}

		@Override
		public int evaluateInt(EvaluationContext ctx, Element elt) {
			for (Pair<Evaluator,Evaluator> c : cases) {
				if (c.first.evaluateBoolean(ctx, elt))
					return c.second.evaluateInt(ctx, elt);
			}
			return defaultCase.evaluateInt(ctx, elt);
		}

		@Override
		public double evaluateDouble(EvaluationContext ctx, Element elt) {
			for (Pair<Evaluator,Evaluator> c : cases) {
				if (c.first.evaluateBoolean(ctx, elt))
					return c.second.evaluateDouble(ctx, elt);
			}
			return defaultCase.evaluateDouble(ctx, elt);
		}

		@Override
		public String evaluateString(EvaluationContext ctx, Element elt) {
			for (Pair<Evaluator,Evaluator> c : cases) {
				if (c.first.evaluateBoolean(ctx, elt))
					return c.second.evaluateString(ctx, elt);
			}
			return defaultCase.evaluateString(ctx, elt);
		}

		@Override
		public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
			for (Pair<Evaluator,Evaluator> c : cases) {
				if (c.first.evaluateBoolean(ctx, elt)) {
					c.second.evaluateString(ctx, elt, strcat);
					return;
				}
			}
			defaultCase.evaluateString(ctx, elt, strcat);
		}

		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			for (Pair<Evaluator,Evaluator> c : cases) {
				if (c.first.evaluateBoolean(ctx, elt))
					return c.second.evaluateElements(ctx, elt);
			}
			return defaultCase.evaluateElements(ctx, elt);
		}

		@Override
		public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
			for (Pair<Evaluator,Evaluator> c : cases) {
				if (c.first.evaluateBoolean(ctx, elt))
					return c.second.evaluateList(ctx, elt);
			}
			return defaultCase.evaluateList(ctx, elt);
		}

		@Override
		public boolean testEquality(EvaluationContext ctx, Evaluator that, Element elt, boolean mayDelegate) {
			if (mayDelegate)
				return that.testEquality(ctx, this, elt, false);
			for (Pair<Evaluator,Evaluator> c : cases) {
				if (c.first.evaluateBoolean(ctx, elt))
					return c.second.testEquality(ctx, that, elt, true);
			}
			return defaultCase.testEquality(ctx, that, elt, true);
		}

		@Override
		public Collection<EvaluationType> getTypes() {
			Collection<EvaluationType> result = defaultCase.getTypes();
			for (Pair<Evaluator,Evaluator> c : cases)
				result = intersection(result, c.second.getTypes());
			return result;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			for (Pair<Evaluator,Evaluator> p : cases) {
				p.first.collectUsedNames(nameUsage, defaultType);
				p.second.collectUsedNames(nameUsage, defaultType);
			}
			defaultCase.collectUsedNames(nameUsage, defaultType);
		}
	}
}
