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
import java.util.List;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.VariableLibrary;
import alvisnlp.corpus.expressions.VariableLibrary.Variable;

public class ComparatorContext {
	private final VariableLibrary library;
	private final Variable other;

	public ComparatorContext() {
		super();
		library = new VariableLibrary("other");
		other = library.newVariable(null);
	}
	
	public boolean evaluateBoolean(Evaluator comp, EvaluationContext ctx, Element elt, Element other) {
		this.other.set(other);
		return comp.evaluateBoolean(ctx, elt);
	}
	
	public int evaluateInt(Evaluator comp, EvaluationContext ctx, Element elt, Element other) {
		this.other.set(other);
		return comp.evaluateInt(ctx, elt);
	}
	
	public double evaluateDouble(Evaluator comp, EvaluationContext ctx, Element elt, Element other) {
		this.other.set(other);
		return comp.evaluateDouble(ctx, elt);
	}
	
	public String evaluateString(Evaluator comp, EvaluationContext ctx, Element elt, Element other) {
		this.other.set(other);
		return comp.evaluateString(ctx, elt);
	}
	
	public Iterator<Element> evaluateElements(Evaluator comp, EvaluationContext ctx, Element elt, Element other) {
		this.other.set(other);
		return comp.evaluateElements(ctx, elt);
	}
	
	public List<Element> evaluateList(Evaluator comp, EvaluationContext ctx, Element elt, Element other) {
		this.other.set(other);
		return comp.evaluateList(ctx, elt);
	}
}
