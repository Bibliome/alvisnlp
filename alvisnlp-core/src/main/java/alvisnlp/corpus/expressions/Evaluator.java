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
import java.util.Iterator;
import java.util.List;

import org.bibliome.util.Checkable;
import org.bibliome.util.StringCat;
import org.bibliome.util.filters.Filter;
import org.bibliome.util.filters.ParamFilter;

import alvisnlp.corpus.Element;
import alvisnlp.module.NameUser;


/**
 * Element expression.
 * @author rbossy
 *
 */
public interface Evaluator extends Checkable, ParamFilter<Element,EvaluationContext>, NameUser {
	/**
	 * Evaluates this expression as a boolean.
	 * @param ctx TODO
	 * @param elt
	 */
	boolean evaluateBoolean(EvaluationContext ctx, Element elt);

	int evaluateInt(EvaluationContext ctx, Element elt);
	
	/**
	 * Evaluates this expression as a number.
	 * @param ctx TODO
	 * @param elt
	 */
	double evaluateDouble(EvaluationContext ctx, Element elt);
	
	/**
	 * Evaluates this expression as a string.
	 * @param ctx TODO
	 * @param elt
	 */
	String evaluateString(EvaluationContext ctx, Element elt);
	
	void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat);
	
	/**
	 * Evaluates this expression as an element iterator.
	 * @param ctx TODO
	 * @param elt
	 */
	Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt);

	List<Element> evaluateList(EvaluationContext ctx, Element elt);
	
	/**
	 * Tests either the evaluation of this expression ant that of the specified expression are equal.
	 * @param ctx TODO
	 * @param that
	 * @param elt
	 * @param mayDelegate
	 */
	boolean testEquality(EvaluationContext ctx, Evaluator that, Element elt, boolean mayDelegate);

	/**
	 * Returns the sensible types evaluation types for this expression.
	 */
	Collection<EvaluationType> getTypes();
	
	Filter<Element> getFilter(EvaluationContext ctx);
}
