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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pattern;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.VariableLibrary.Variable;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUser;
import fr.inra.maiage.bibliome.util.filters.ParamFilter;

public class EvaluatorFilterProxy implements ParamFilter<Element,EvaluationContext>, NameUser {
	private final Variable elementVariable;
	private final Evaluator resolvedExpr;
	
	public EvaluatorFilterProxy(Variable elementVariable, Evaluator resolvedExpr) {
		super();
		this.elementVariable = elementVariable;
		this.resolvedExpr = resolvedExpr;
	}

	@Override
	public boolean accept(Element x, EvaluationContext ctx) {
		elementVariable.set(x);
		return resolvedExpr.evaluateBoolean(ctx, x);
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		resolvedExpr.collectUsedNames(nameUsage, defaultType);
	}
}
