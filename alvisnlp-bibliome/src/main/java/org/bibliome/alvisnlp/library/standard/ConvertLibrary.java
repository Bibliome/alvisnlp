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

import java.util.Iterator;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;

@Library("convert")
public abstract class ConvertLibrary extends FunctionLibrary {
	public static final String NAME = "convert";
	
	@Function(firstFtor="bool")
	public static final boolean convertBoolean(EvaluationContext ctx, Element elt, Evaluator e) {
		return e.evaluateBoolean(ctx, elt);
	}
	
	@Function(firstFtor="int")
	public static final int convertInt(EvaluationContext ctx, Element elt, Evaluator e) {
		return e.evaluateInt(ctx, elt);
	}
	
	@Function(firstFtor="double")
	public static final double convertDouble(EvaluationContext ctx, Element elt, Evaluator e) {
		return e.evaluateDouble(ctx, elt);
	}
	
	@Function(firstFtor="elements")
	public static final Iterator<Element> convertElements(EvaluationContext ctx, Element elt, Evaluator e) {
		return e.evaluateElements(ctx, elt);
	}
	
	@Function(firstFtor="string")
	public static final String convertString(EvaluationContext ctx, Element elt, Evaluator e) {
		return e.evaluateString(ctx, elt);
	}
}
