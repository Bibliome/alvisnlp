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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bibliome.util.Iterators;
import org.bibliome.util.StringCat;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.AbstractEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.EvaluationType;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

@Library("random")
public abstract class RandomLibrary extends FunctionLibrary {
	private long seed = System.currentTimeMillis();
	private final Random random = new Random(seed);
	
	@Function
	public double seed() {
		return seed;
	}
	
	@Function
	public double init(double seed) {
		this.seed = (long) seed;
		random.setSeed(this.seed);
		return seed;
	}
	
	@Function
	public double init() {
		return init(System.currentTimeMillis());
	}

	@Function
	public Evaluator next() {
		return new RandomEvaluator();
	}
	
	@Function
	public Evaluator next(Evaluator max) {
		return new RandomEvaluator(max);
	}
	
	@Function
	public double gaussian() {
		return random.nextGaussian();
	}
	
	@Function
	public List<Element> shuffle(Iterator<Element> it) {
		List<Element> list = new ArrayList<Element>();
		Iterators.fill(it, list);
		Collections.shuffle(list, random);
		return list;
	}

	private final class RandomEvaluator extends AbstractEvaluator {
		private final Evaluator maxInt;

		private RandomEvaluator(Evaluator maxInt) {
			super();
			this.maxInt = maxInt;
		}
		
		private RandomEvaluator() {
			this(null);
		}

		@Override
		public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
			return random.nextBoolean();
		}

		@Override
		public int evaluateInt(EvaluationContext ctx, Element elt) {
			if (maxInt == null)
				return random.nextInt();
			int maxInt = this.maxInt.evaluateInt(ctx, elt);
			return random.nextInt(maxInt);
		}

		@Override
		public double evaluateDouble(EvaluationContext ctx, Element elt) {
			return random.nextDouble();
		}

		@Override
		public String evaluateString(EvaluationContext ctx, Element elt) {
			return Integer.toString(evaluateInt(ctx, elt));
		}

		@Override
		public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
			strcat.append(evaluateInt(ctx, elt));
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
		public boolean testEquality(EvaluationContext ctx, Evaluator that, Element elt, boolean mayDelegate) {
			Collection<EvaluationType> types = that.getTypes();
			EvaluationType type = types.iterator().next();
			switch (type) {
				case BOOLEAN:
					return that.evaluateBoolean(ctx, elt) == evaluateBoolean(ctx, elt);
				case DOUBLE:
					return that.evaluateDouble(ctx, elt) == evaluateDouble(ctx, elt);
				case INT:
					return that.evaluateInt(ctx, elt) == evaluateInt(ctx, elt);
				case STRING:
					return that.evaluateString(ctx, elt).equals(evaluateString(ctx, elt));
				case UNDEFINED:
					if (mayDelegate)
						return that.testEquality(ctx, this, elt, false);
					return that.evaluateInt(ctx, elt) == evaluateInt(ctx, elt);
				default:
					return that.evaluateList(ctx, elt).isEmpty();
			}
		}

		@Override
		public Collection<EvaluationType> getTypes() {
			return Arrays.asList(EvaluationType.INT, EvaluationType.BOOLEAN, EvaluationType.DOUBLE);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			maxInt.collectUsedNames(nameUsage, defaultType);
		}
	}
}
