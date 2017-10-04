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
import java.util.Collections;
import java.util.List;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;

@Library("sort")
public abstract class SortLibrary extends FunctionLibrary {
	@Function
	public static Evaluator comp(Evaluator list, Evaluator comparator) {
		return new SortComparatorEvaluator(list, false, false, comparator);
	}
	
	@Function
	public static final Evaluator ival(Evaluator list, Evaluator value) {
		return new SortIntEvaluator(list, false, false, value);
	}
	
	@Function
	public static final Evaluator dval(Evaluator list, Evaluator value) {
		return new SortDoubleEvaluator(list, false, false, value);
	}
	
	@Function
	public static final Evaluator sval(Evaluator list, Evaluator value) {
		return new SortStringEvaluator(list, false, false, value);
	}

	@Function
	public static Evaluator ucomp(Evaluator list, Evaluator comparator) {
		return new SortComparatorEvaluator(list, true, false, comparator);
	}
	
	@Function
	public static final Evaluator uival(Evaluator list, Evaluator value) {
		return new SortIntEvaluator(list, true, false, value);
	}
	
	@Function
	public static final Evaluator udval(Evaluator list, Evaluator value) {
		return new SortDoubleEvaluator(list, true, false, value);
	}
	
	@Function
	public static final Evaluator usval(Evaluator list, Evaluator value) {
		return new SortStringEvaluator(list, true, false, value);
	}

	@Function
	public static Evaluator ncomp(Evaluator list, Evaluator comparator) {
		return new SortComparatorEvaluator(list, false, true, comparator);
	}
	
	@Function
	public static final Evaluator nival(Evaluator list, Evaluator value) {
		return new SortIntEvaluator(list, false, true, value);
	}
	
	@Function
	public static final Evaluator ndval(Evaluator list, Evaluator value) {
		return new SortDoubleEvaluator(list, false, true, value);
	}
	
	@Function
	public static final Evaluator nsval(Evaluator list, Evaluator value) {
		return new SortStringEvaluator(list, false, true, value);
	}

	@Function
	public static final List<Element> reverse(List<Element> list) {
		List<Element> result = new ArrayList<Element>(list);
		Collections.reverse(result);
		return result;
	}
}
