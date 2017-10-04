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

import java.util.Iterator;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;

@Library(
		value="m",
		externalStatic={
				"java.lang.Math.abs(int)",
				"absd=java.lang.Math.abs(double)",
				"java.lang.Math.exp(double)",
				"log=java.lang.Math.log10(double)",
				"logn=java.lang.Math.log(double)",
				"java.lang.Math.min(int,int)",
				"java.lang.Math.max(int,int)",
				"mind=java.lang.Math.min(double,double)",
				"maxd=java.lang.Math.max(double,double)",
				"java.lang.Math.sqrt(double)",
				"java.lang.Math.pow(double,double)",
				"java.lang.Math.hypot(double,double)",
				"java.lang.Math.ceil(double)",
				"java.lang.Math.floor(double)"
		})
public abstract class MathLibrary extends FunctionLibrary {
	@Function
	public static final int sum(EvaluationContext ctx, @SuppressWarnings("unused") Element elt, Iterator<Element> elements, Evaluator value) {
		int result = 0;
		for (Element e : Iterators.loop(elements))
			result += value.evaluateInt(ctx, e);
		return result;
	}
	
	@Function
	public static final double sumd(EvaluationContext ctx, @SuppressWarnings("unused") Element elt, Iterator<Element> elements, Evaluator value) {
		double result = 0;
		for (Element e : Iterators.loop(elements))
			result += value.evaluateDouble(ctx, e);
		return result;
	}

	@Function
	public static final double prod(EvaluationContext ctx, @SuppressWarnings("unused") Element elt, Iterator<Element> elements, Evaluator value) {
		double result = 1;
		for (Element e : Iterators.loop(elements))
			result *= value.evaluateDouble(ctx, e);
		return result;
	}

	@Function
	public static final int prodi(EvaluationContext ctx, @SuppressWarnings("unused") Element elt, Iterator<Element> elements, Evaluator value) {
		int result = 1;
		for (Element e : Iterators.loop(elements))
			result *= value.evaluateInt(ctx, e);
		return result;
	}
	
	@Function
	public static final int mmin(EvaluationContext ctx, @SuppressWarnings("unused") Element elt, Iterator<Element> elements, Evaluator value) {
		int result = Integer.MAX_VALUE;
		for (Element e : Iterators.loop(elements))
			result = Math.min(result, value.evaluateInt(ctx, e));
		return result;
	}
	
	@Function
	public static final int mmax(EvaluationContext ctx, @SuppressWarnings("unused") Element elt, Iterator<Element> elements, Evaluator value) {
		int result = Integer.MIN_VALUE;
		for (Element e : Iterators.loop(elements))
			result = Math.max(result, value.evaluateInt(ctx, e));
		return result;
	}
	
	@Function
	public static final double mmind(EvaluationContext ctx, @SuppressWarnings("unused") Element elt, Iterator<Element> elements, Evaluator value) {
		double result = Double.MAX_VALUE;
		for (Element e : Iterators.loop(elements))
			result = Math.min(result, value.evaluateInt(ctx, e));
		return result;
	}
	
	@Function
	public static final double mmaxd(EvaluationContext ctx, @SuppressWarnings("unused") Element elt, Iterator<Element> elements, Evaluator value) {
		double result = Double.MIN_VALUE;
		for (Element e : Iterators.loop(elements))
			result = Math.max(result, value.evaluateInt(ctx, e));
		return result;
	}
	
	@Function
	public static final double round(double d) {
		return Math.round(d);
	}
}
