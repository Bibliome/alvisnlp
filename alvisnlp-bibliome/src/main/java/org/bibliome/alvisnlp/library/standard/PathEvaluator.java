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

import org.bibliome.util.Iterators;
import org.bibliome.util.StringCat;
import org.bibliome.util.mappers.Mapper;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.AbstractEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.EvaluationType;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

final class PathEvaluator extends AbstractEvaluator {
	private final Evaluator left;
	private final Evaluator right;

	PathEvaluator(Evaluator left, Evaluator right) {
		super();
		this.left = left;
		this.right = right;
	}

	@Override
	public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
		Iterator<Element> leftIt = left.evaluateElements(ctx, elt);
		return Iterators.mapAndFlatten(leftIt, new PathMapper(ctx));
	}

	private final class PathMapper implements Mapper<Element,Iterator<Element>> {
		private final EvaluationContext ctx;

		private PathMapper(EvaluationContext ctx) {
			super();
			this.ctx = ctx;
		}

		@Override
		public Iterator<Element> map(Element x) {
			return right.evaluateElements(ctx, x);
		}
	}

	@Override
	public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
		List<Element> result = new ArrayList<Element>();
		for (Element e : Iterators.loop(left.evaluateElements(ctx, elt))) {
			Iterators.fill(right.evaluateElements(ctx, e), result);
		}
		return result;
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
		for (Element e : Iterators.loop(left.evaluateElements(ctx, elt))) {
			if (right.evaluateBoolean(ctx, e))
				return true;
		}
		return false;
	}

	@Override
	public int evaluateInt(EvaluationContext ctx, Element elt) {
		int result = 0;
		for (Element e : Iterators.loop(left.evaluateElements(ctx, elt))) {
			result += right.evaluateInt(ctx, e);
		}
		return result;
	}

	@Override
	public double evaluateDouble(EvaluationContext ctx, Element elt) {
		double result = 0;
		for (Element e : Iterators.loop(left.evaluateElements(ctx, elt))) {
			result += right.evaluateDouble(ctx, e);
		}
		return result;
	}

	@Override
	public String evaluateString(EvaluationContext ctx, Element elt) {
		StringCat strcat = new StringCat();
		evaluateString(ctx, elt, strcat);
		return strcat.toString();
	}

	@Override
	public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
		for (Element e : Iterators.loop(left.evaluateElements(ctx, elt)))
			right.evaluateString(ctx, e, strcat);
	}

	@Override
	public boolean testEquality(EvaluationContext ctx, Evaluator that, Element elt, boolean mayDelegate) {
		if (mayDelegate)
			return that.testEquality(ctx, this, elt, false);
		Collection<EvaluationType> types = right.getTypes();
		Iterator<EvaluationType> typeIt = types.iterator();
		EvaluationType type = typeIt.next();
		switch (type) {
			case BOOLEAN:
				return evaluateBoolean(ctx, elt) == that.evaluateBoolean(ctx, elt);
			case INT:
			case DOUBLE:
				return evaluateDouble(ctx, elt) == that.evaluateDouble(ctx, elt);
			case STRING:
				return evaluateString(ctx, elt).equals(that.evaluateString(ctx, elt));
			default: {
				Iterator<Element> thisIt = evaluateElements(ctx, elt);
				Iterator<Element> thatIt = that.evaluateElements(ctx, elt);
				while (thisIt.hasNext() && thatIt.hasNext()) {
					Element thisElt = thisIt.next();
					Element thatElt = thatIt.next();
					if (!thisElt.equals(thatElt)) {
						return false;
					}
				}
				return !(thisIt.hasNext() || thatIt.hasNext());
			}
		}
	}

	@Override
	public Collection<EvaluationType> getTypes() {
		return right.getTypes();
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		left.collectUsedNames(nameUsage, defaultType);
		right.collectUsedNames(nameUsage, defaultType);
	}
}