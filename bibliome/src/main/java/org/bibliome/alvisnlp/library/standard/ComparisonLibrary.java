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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.util.Iterators;
import org.bibliome.util.StringCat;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.streams.StreamFactory;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.AbstractBooleanEvaluator;
import alvisnlp.corpus.expressions.AbstractEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.EvaluationType;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

@Library("comparison")
public abstract class ComparisonLibrary extends FunctionLibrary {
	public static final String NAME = "comparison";

	@Function(firstFtor="==")
	public static final boolean eq(EvaluationContext ctx, Element elt, Evaluator left, Evaluator right) {
		return left.testEquality(ctx, right, elt, true);
	}

	@Function(firstFtor="!=")
	public static final boolean ne(EvaluationContext ctx, Element elt, Evaluator left, Evaluator right) {
		return !left.testEquality(ctx, right, elt, true);
	}
	
	@Function(firstFtor="<")
	public static final boolean lt(double left, double right) {
		return left < right;
	}
	
	@Function(firstFtor=">")
	public static final boolean gt(double left, double right) {
		return left > right;
	}
	
	@Function(firstFtor="<=")
	public static final boolean le(double left, double right) {
		return left <= right;
	}
	
	@Function(firstFtor=">=")
	public static final boolean ge(double left, double right) {
		return left >= right;
	}
	
	@Function(firstFtor="?=")
	public static final boolean contains(String left, String right) {
		return left.contains(right);
	}
	
	@Function(firstFtor="^=")
	public static final boolean startsWith(String left, String right) {
		return left.startsWith(right);
	}
	
	@Function(firstFtor="=^")
	public static final boolean endsWith(String left, String right) {
		return left.endsWith(right);
	}
	
	@Function(firstFtor="=~", ftors=1)
	public static final Evaluator matchRE(String pattern, Evaluator target) {
		return new RegExpEvaluator(target, Pattern.compile(pattern));
	}
	
	@Function(firstFtor="in", ftors=1)
	public static final Evaluator matchDict(String url, Evaluator s) throws ResolverException {
		try {
			StreamFactory sf = new StreamFactory();
			SourceStream source = sf.getSourceStream(url);
			return new MatchDictionaryEvaluator(source, s);
		}
		catch (Exception e) {
			throw new ResolverException(e);
		}
	}
	
	@Function(ftors=1, nameTypes={NameType.FEATURE})
	public static final boolean any(EvaluationContext ctx, Element elt, String key, Evaluator value) {
		if (elt.hasFeature(key)) {
			String sValue = value.evaluateString(ctx, elt);
			for (String v : elt.getFeature(key))
				if (v.equals(sValue))
					return true;
		}
		return false;
	}

	private static final class MatchDictionaryEvaluator extends AbstractBooleanEvaluator {
		private final Evaluator expr;
		private final Collection<String> dict;

		private MatchDictionaryEvaluator(Evaluator expr, Collection<String> dict) {
			super();
			this.expr = expr;
			this.dict = dict;
		}

		private MatchDictionaryEvaluator(SourceStream source, Evaluator expr) throws IOException {
			this(expr, loadDict(source));
		}

		private static final Collection<String> loadDict(SourceStream source) throws IOException {
			Collection<String> result = new HashSet<String>();
			BufferedReader r = source.getBufferedReader();
			while (true) {
				String entry = r.readLine();
				if (entry == null)
					break;
				result.add(entry);
			}
			return result;
		}

		@Override
		public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
			return dict.contains(expr.evaluateString(ctx, elt));
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			expr.collectUsedNames(nameUsage, defaultType);
		}
	}

	private static final class RegExpEvaluator extends AbstractEvaluator {
		private final Evaluator target;
		private final Pattern pattern;

		private RegExpEvaluator(Evaluator target, Pattern pattern) {
			super();
			this.target = target;
			this.pattern = pattern;
		}

		private Matcher getMatcher(EvaluationContext ctx, Element elt) {
			return pattern.matcher(target.evaluateString(ctx, elt));
		}

		@Override
		public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
			return getMatcher(ctx, elt).find();
		}

		@Override
		public int evaluateInt(EvaluationContext ctx, Element elt) {
			int result = 0;
			Matcher m = getMatcher(ctx, elt);
			while (m.find())
				result++;
			return result;
		}

		@Override
		public double evaluateDouble(EvaluationContext ctx, Element elt) {
			return evaluateInt(ctx, elt);
		}

		@Override
		public String evaluateString(EvaluationContext ctx, Element elt) {
			Matcher m = getMatcher(ctx, elt);
			if (m.find())
				return m.group();
			return "";
		}

		@Override
		public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
			Matcher m = getMatcher(ctx, elt);
			if (m.find())
				strcat.append(m.group());
		}

		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			return Iterators.emptyIterator();
		}

		@Override
		public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
			return Collections.emptyList();
		}

		@Override
		public Collection<EvaluationType> getTypes() {
			return Collections.singleton(EvaluationType.STRING);
		}

		@Override
		public boolean testEquality(EvaluationContext ctx, Evaluator that, Element elt, boolean mayDelegate) {
			if (mayDelegate)
				return that.testEquality(ctx, this, elt, false);
			if (that.getTypes().contains(EvaluationType.STRING))
				return evaluateString(ctx, elt).equals(that.evaluateString(ctx, elt));
			return false;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			target.collectUsedNames(nameUsage, defaultType);
		}
	}
}
