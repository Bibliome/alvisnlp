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


package alvisnlp.corpus.expressions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bibliome.util.Iterators;
import org.bibliome.util.StringCat;

import alvisnlp.corpus.Element;

public abstract class AbstractIntEvaluator extends AbstractEvaluator {
	protected AbstractIntEvaluator() {
		super();
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
		return evaluateInt(ctx, elt) != 0;
	}

	@Override
	public double evaluateDouble(EvaluationContext ctx, Element elt) {
		return evaluateInt(ctx, elt);
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
		evaluateInt(ctx, elt);
		return Iterators.emptyIterator();
	}

	@Override
	public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
		evaluateInt(ctx, elt);
		return Collections.emptyList();
	}

	@Override
	public Collection<EvaluationType> getTypes() {
		return Arrays.asList(EvaluationType.INT);
	}

	@Override
	public boolean testEquality(EvaluationContext ctx, Evaluator that, Element elt, boolean mayDelegate) {
		Collection<EvaluationType> types = that.getTypes();
		if (types.contains(EvaluationType.DOUBLE) || types.contains(EvaluationType.INT))
			return evaluateDouble(ctx, elt) == that.evaluateDouble(ctx, elt);
		return false;
	}
}
