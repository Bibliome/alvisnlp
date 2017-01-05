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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bibliome.util.StringCat;
import org.bibliome.util.Strings;

import alvisnlp.corpus.Element;
import alvisnlp.documentation.Documentation;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

public class VariableLibrary extends FunctionLibrary {
	private final String name;
	private final Map<String,Variable> variables = new HashMap<String,Variable>();

	public VariableLibrary(String name) {
		super();
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	private String getVarName(String var) {
		if (var == null)
			return getName();
		return getName() + ':' + var;
	}

	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		checkMaxFtors(ftors, 1);
		checkExactArity(ftors, args, 0);
		String var = ftors.isEmpty() ? null : ftors.get(0);
		if (!variables.containsKey(var))
			throw new RuntimeException("unassigned variable " + getVarName(var) + " (" + Strings.join(variables.keySet(), ", ") + ')');
		return variables.get(var);
	}

	public Variable newVariable(String var) {
		if (variables.containsKey(var))
			throw new RuntimeException("duplicate variable " + getVarName(var));
		Variable result = new Variable();
		variables.put(var, result);
		return result;
	}

	public boolean hasVariable(String var) {
		return variables.containsKey(var);
	}

	public Variable getVariable(String var) {
		return variables.get(var);
	}

	@Override
	public Documentation getDocumentation() {
		return null;
	}

	public class Variable extends AbstractEvaluator {
		private Evaluator value;

		private Variable(Evaluator value) {
			super();
			this.value = value;
		}

		public Variable() {
			this(ConstantsLibrary.EVALUATOR_FALSE);
		}

		@Override
		public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
			return value.evaluateBoolean(ctx, elt);
		}

		@Override
		public int evaluateInt(EvaluationContext ctx, Element elt) {
			return value.evaluateInt(ctx, elt);
		}

		@Override
		public double evaluateDouble(EvaluationContext ctx, Element elt) {
			return value.evaluateDouble(ctx, elt);
		}

		@Override
		public String evaluateString(EvaluationContext ctx, Element elt) {
			return value.evaluateString(ctx, elt);
		}

		@Override
		public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
			value.evaluateString(ctx, elt, strcat);
		}

		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			return value.evaluateElements(ctx, elt);
		}

		@Override
		public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
			return value.evaluateList(ctx, elt);
		}

		@Override
		public boolean testEquality(EvaluationContext ctx, Evaluator that, Element elt, boolean mayDelegate) {
			return value.testEquality(ctx, that, elt, mayDelegate);
		}

		@Override
		public Collection<EvaluationType> getTypes() {
			return value.getTypes();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			value.collectUsedNames(nameUsage, defaultType);
		}

		public void set(Evaluator value) {
			this.value = value;
		}
		
		public void set(boolean value) {
			this.value = ConstantsLibrary.getInstance(value);
		}

		public void set(int value) {
			this.value = ConstantsLibrary.getInstance(value);
		}

		public void set(double value) {
			this.value = ConstantsLibrary.getInstance(value);
		}

		public void set(String value) {
			this.value = ConstantsLibrary.getInstance(value);
		}

		public void set(List<Element> value) {
			this.value = new ElementListConstantEvaluator(value);
		}

		public void set(Element elt) {
			set(Collections.singletonList(elt));
		}

		public String getName() {
			return name;
		}

		private class ElementListConstantEvaluator extends AbstractListEvaluator {
			private final List<Element> listValue;

			private ElementListConstantEvaluator(List<Element> value) {
				super();
				this.listValue = value;
			}

			@Override
			public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
				return listValue;
			}

			@Override
			public Collection<EvaluationType> getTypes() {
				return EnumSet.allOf(EvaluationType.class);
			}

			@Override
			public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			}
		}
	}
}
