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

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.util.Iterators;
import org.bibliome.util.StringCat;
import org.bibliome.util.Strings;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.AbstractBooleanEvaluator;
import alvisnlp.corpus.expressions.AbstractStringEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

@Library(
		value="str",
		externalStatic={
				"org.bibliome.util.Strings.levenshtein(java.lang.CharSequence,java.lang.CharSequence)",
				"org.bibliome.util.Strings.levenshteinSimilar(java.lang.CharSequence,java.lang.CharSequence,double)",
				}
		)
public abstract class StringLibrary extends FunctionLibrary {
	public static final String NAME = "str";
	
	@Function
	public static final String replace(String target, String search, String replace) {
		return target.replace(search, replace);
	}
	
	@Function
	public static final String upper(String target) {
		return target.toUpperCase();
	}
	
	@Function
	public static final String lower(String target) {
		return target.toLowerCase();
	}
	
	@Function
	public static final String sub(String target, int from, int to) {
		if (from < 0) {
			throw new IllegalArgumentException();
		}
		if (to > target.length()) {
			throw new IllegalArgumentException();
		}
		if (from > to) {
			throw new IllegalArgumentException("" + from + " > " + to);
		}
		return target.substring(from, to);
	}
	
	@Function
	public static final String sub(String target, int from) {
		return target.substring(from);
	}
	
	@Function
	public static final int len(String target) {
		return target.length();
	}
	
	@Function
	public static final String seds(String target, String pattern, String replace) {
		return target.replaceAll(pattern, replace);
	}

	@Function
	public static final String join(EvaluationContext ctx, Element elt, Iterator<Element> items, Evaluator string) {
		return join(ctx, elt, " ", items, string);
	}
	
	@Function(ftors=1)
	public static final String join(EvaluationContext ctx, Element elt, String separator, Iterator<Element> items, Evaluator string) {
		return join(ctx, elt, items, string, separator);
	}
	
	@Function
	public static final String join(EvaluationContext ctx, @SuppressWarnings("unused") Element elt, Iterator<Element> items, Evaluator string, String separator) {
		StringCat strcat = new StringCat();
		boolean notFirst = false;
		while (items.hasNext()) {
			Element e = items.next();
			if (notFirst)
				strcat.append(separator);
			else
				notFirst = true;
			strcat.append(string.evaluateString(ctx, e));
		}
		return strcat.toString();
	}
	
	@Function
	public static final boolean equalsIgnoreCase(String a, String b) {
		return a.equalsIgnoreCase(b);
	}
	
	@Function
	public static final int index(String s, String target) {
		return s.indexOf(target);
	}
	
	@Function
	public static final int rindex(String s, String target) {
		return s.lastIndexOf(target);
	}
	
	@Function
	public static final String trim(String s) {
		return s.trim();
	}
	
	@Function
	public static final String normalizeSpace(String s) {
		return Strings.normalizeSpace(s.trim());
	}
	
	@Function
	public static final String diacritics(String s) {
		return Strings.removeDiacritics(s);
	}
	
	@Function
	public static final String padr(String s, final int n) {
		return padr(" ", s, n);
	}
	
	@Function(ftors=1)
	public static final String padr(String filler, String s, final int n) {
		final int len = s.length();
		int tofill = n - len;
		if (tofill == 0) {
			return s;
		}
		if (tofill < 0) {
			return s.substring(0, n);
		}
		final int filllen = filler.length();
		StringBuilder sb = new StringBuilder(s);
		while (tofill > 0) {
			if (tofill >= filllen) {
				sb.append(filler);
				tofill -= filllen;
			}
			else {
				sb.append(filler, 0, tofill);
				tofill = 0;
			}
		}
		return sb.toString();
	}
	
	@Function
	public static final String padl(String s, final int n) {
		return padl(" ", s, n);
	}
	
	@Function(ftors=1)
	public static final String padl(String filler, String s, final int n) {
		final int len = s.length();
		int tofill = n - len;
		if (tofill == 0)
			return s;
		if (tofill < 0)
			return s.substring(0, n);
		final int filllen = filler.length();
		StringBuilder sb = new StringBuilder();
		while (tofill > 0) {
			if (tofill >= filllen) {
				sb.append(filler);
				tofill -= filllen;
			}
			else {
				sb.append(filler, 0, tofill);
				tofill = 0;
			}
			
		}
		sb.append(s);
		return sb.toString();
	}
	
	@Function
	public static final String basename(String s) {
		int slash = s.lastIndexOf(File.separatorChar);
		if (slash == -1)
			return s;
		return s.substring(slash + 1);
	}
	
	@Function(ftors=1)
	public static final String after(String sub, String target) {
		int i = target.indexOf(sub);
		if (i == -1)
			return target;
		return target.substring(i + sub.length());
	}
	
	@Function
	public static final String after(EvaluationContext ctx, Element elt, Evaluator target, Evaluator sub) {
		return after(sub.evaluateString(ctx, elt), target.evaluateString(ctx, elt));
	}
	
	@Function(ftors=1)
	public static final String before(String sub, String target) {
		int i = target.indexOf(sub);
		if (i == -1)
			return target;
		return target.substring(0, i);
	}
	
	@Function
	public static final String before(EvaluationContext ctx, Element elt, Evaluator target, Evaluator sub) {
		return before(sub.evaluateString(ctx, elt), target.evaluateString(ctx, elt));
	}
	
	@Function
	public static final int cmp(String a, String b) {
		return a.compareTo(b);		
	}
	
	@Function
	public static final String regrp(String s, String re, int grp) {
		if (grp < 0)
			return "";
		Pattern pat = Pattern.compile(re);
		Matcher m = pat.matcher(s);
		if (m.find()) {
			if (grp > m.groupCount())
				return "";
//			System.err.println("group = " + m.group(grp));
			return m.group(grp);
		}
		return "";
	}
	
	@Function(ftors=2, nameTypes={Function.NO_NAME_TYPE, NameType.FEATURE})
	public static Iterator<Element> split(EvaluationContext ctx, Element elt, String separator, String feature, Evaluator target) {
		String sTarget = target.evaluateString(ctx, elt);
		for (String s : Strings.split(sTarget, separator.charAt(0), 0))
			ctx.registerSetFeature(elt, feature, s);
		return Iterators.singletonIterator(elt);
	}
	
	@Function
	public static int hash(String s) {
		return s.hashCode();
	}

	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		if (ftors.size() == 1 && ftors.get(0).equals("concat") && args.size() > 1) {
			List<Evaluator> resolvedArgs = resolver.resolveList(args);
			return new ConcatEvaluator(resolvedArgs);
		}
		if (ftors.size() == 1 && ftors.get(0).equals("inset") && args.size() > 1) {
			Expression target = args.get(0);
			List<Expression> set = args.subList(1, args.size());
			Evaluator resolvedTarget = target.resolveExpressions(resolver);
			List<Evaluator> resolvedSet = resolver.resolveList(set);
			return new FindInSetEvaluator(resolvedTarget, resolvedSet);
		}
		return cannotResolve(ftors, args);
	}
	
	public static final class ConcatEvaluator extends AbstractStringEvaluator {
		private final List<Evaluator> args;

		public ConcatEvaluator(Evaluator... args) {
			this(Arrays.asList(args));
		}
		
		private ConcatEvaluator(List<Evaluator> args) {
			super();
			this.args = args;
		}

		@Override
		public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
			for (Evaluator e : args)
				e.evaluateString(ctx, elt, strcat);
		}

		@Override
		public String evaluateString(EvaluationContext ctx, Element elt) {
			StringCat strcat = new StringCat();
			evaluateString(ctx, elt, strcat);
			return strcat.toString();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			for (Evaluator a : args) {
				a.collectUsedNames(nameUsage, defaultType);
			}
		}
	}

	public static final class FindInSetEvaluator extends AbstractBooleanEvaluator {
		private final Evaluator target;
		private final List<Evaluator> set;
		
		public FindInSetEvaluator(Evaluator target, List<Evaluator> set) {
			super();
			this.target = target;
			this.set = set;
		}
		
		public FindInSetEvaluator(Evaluator target, Evaluator... set) {
			this(target, Arrays.asList(set));
		}

		@Override
		public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
			String target = this.target.evaluateString(ctx, elt);
			for (Evaluator e : set) {
				String s = e.evaluateString(ctx, elt);
				if (target.equals(s)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			target.collectUsedNames(nameUsage, defaultType);
			for (Evaluator e : set) {
				e.collectUsedNames(nameUsage, defaultType);
			}
		}
	}
}
