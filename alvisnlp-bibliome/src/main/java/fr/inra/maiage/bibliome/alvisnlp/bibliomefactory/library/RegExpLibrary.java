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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.AbstractListEvaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Function;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.FunctionLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Library;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.util.Iterators;

@Library("re")
public abstract class RegExpLibrary extends FunctionLibrary {
	private static final class FindallEvaluator extends AbstractListEvaluator {
		private final Evaluator target;
		private final Evaluator fun;
		private final FindallLibrary lib;
		private final boolean justOne;
		
		private FindallEvaluator(LibraryResolver resolver, Pattern pattern, Evaluator target, Expression fun, boolean justOne) throws ResolverException {
			this.target = target;
			this.lib = FunctionLibrary.load(FindallLibrary.class);
			this.lib.setPattern(pattern);
			LibraryResolver resolver2 = this.lib.newLibraryResolver(resolver);
			this.fun = fun.resolveExpressions(resolver2);
			this.justOne = justOne;
		}

		@Override
		public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
			List<Element> result = new ArrayList<Element>();
			String target = this.target.evaluateString(ctx, elt);
			lib.init(target);
			while (lib.findNext()) {
				Iterators.fill(fun.evaluateElements(ctx, elt), result);
				if (justOne)
					break;
			}
			return result;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			target.collectUsedNames(nameUsage, defaultType);
			fun.collectUsedNames(nameUsage, defaultType);
		}
	}

	@Function(ftors=1)
	public static final Evaluator findall(LibraryResolver resolver, String pattern, Evaluator target, Expression fun) throws ResolverException {
		return new FindallEvaluator(resolver, Pattern.compile(pattern), target, fun, false);
	}

	@Function(ftors=1)
	public static final Evaluator find(LibraryResolver resolver, String pattern, Evaluator target, Expression fun) throws ResolverException {
		return new FindallEvaluator(resolver, Pattern.compile(pattern), target, fun, true);
	}
}
