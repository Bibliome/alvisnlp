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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.bibliome.util.StringCat;

import alvisnlp.corpus.Element;

public abstract class AbstractListEvaluator extends AbstractEvaluator {
	protected AbstractListEvaluator() {
		super();
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
		return !evaluateList(ctx, elt).isEmpty();
	}

	@Override
	public int evaluateInt(EvaluationContext ctx, Element elt) {
		return evaluateList(ctx, elt).size();
	}

	@Override
	public double evaluateDouble(EvaluationContext ctx, Element elt) {
		return evaluateInt(ctx, elt);
	}

	@Override
	public String evaluateString(EvaluationContext ctx, Element elt) {
		StringCat strcat = new StringCat();
		evaluateString(ctx, elt, strcat);
		return strcat.toString();
	}

	@Override
	public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
		for (Element e : evaluateList(ctx, elt))
			strcat.append(e.getStaticFeatureValue());
	}

	@Override
	public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
		return evaluateList(ctx, elt).iterator();
	}

	@Override
	public boolean testEquality(EvaluationContext ctx, Evaluator that, Element elt, boolean mayDelegate) {
		if (mayDelegate)
			return that.testEquality(ctx, this, elt, false);
		if (intersection(getTypes(), that.getTypes()).isEmpty()) {
			return false;
		}
		Iterator<Element> thisIt = evaluateElements(ctx, elt);
		Iterator<Element> thatIt = evaluateElements(ctx, elt);
		while (true) {
			if (thisIt.hasNext()) {
				if (thatIt.hasNext()) {
					Element a = thisIt.next();
					Element b = thatIt.next();
					if (a.equals(b))
						continue;
				}
				return false;
			}
			return !thatIt.hasNext();
		}
	}

	@Override
	public Collection<EvaluationType> getTypes() {
		return Collections.singleton(EvaluationType.ELEMENTS);
	}
}
