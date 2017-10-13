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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.standard;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.AbstractEvaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.VariableLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.util.StringCat;

final class AssignEvaluator extends AbstractEvaluator {
	private final Evaluator expression;
	private final VariableLibrary varLib;
	
	AssignEvaluator(Evaluator expression, VariableLibrary varLib) {
		super();
		this.expression = expression;
		this.varLib = varLib;
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
		varLib.getVariable(null).set(elt);
		return expression.evaluateBoolean(ctx, elt);
	}
	
	@Override
	public int evaluateInt(EvaluationContext ctx, Element elt) {
		varLib.getVariable(null).set(elt);
		return expression.evaluateInt(ctx, elt);
	}
	
	@Override
	public double evaluateDouble(EvaluationContext ctx, Element elt) {
		varLib.getVariable(null).set(elt);
		return expression.evaluateDouble(ctx, elt);
	}
	
	@Override
	public String evaluateString(EvaluationContext ctx, Element elt) {
		varLib.getVariable(null).set(elt);
		return expression.evaluateString(ctx, elt);
	}
	
	@Override
	public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
		varLib.getVariable(null).set(elt);
		expression.evaluateString(ctx, elt, strcat);
	}
	
	@Override
	public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
		varLib.getVariable(null).set(elt);
		return expression.evaluateElements(ctx, elt);
	}
	
	@Override
	public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
		varLib.getVariable(null).set(elt);
		return expression.evaluateList(ctx, elt);
	}
	
	@Override
	public boolean testEquality(EvaluationContext ctx, Evaluator that, Element elt, boolean mayDelegate) {
		varLib.getVariable(null).set(elt);
		return expression.testEquality(ctx, that, elt, mayDelegate);
	}
	
	@Override
	public Collection<EvaluationType> getTypes() {
		return expression.getTypes();
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		expression.collectUsedNames(nameUsage, defaultType);
	}
}