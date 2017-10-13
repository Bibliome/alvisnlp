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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.AbstractDoubleEvaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Function;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.FunctionLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Library;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.util.ArithmeticOperator;

@Library("arithmetic")
public abstract class ArithmeticLibrary extends FunctionLibrary {
	public static final String NAME = "arithmetic";
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		checkExactFtors(ftors, 1);
		checkExactArity(ftors, args, 2);
		ArithmeticOperator op = getOperator(ftors.get(0));
		return new ArithmeticEvaluator(op, args.get(0).resolveExpressions(resolver), args.get(1).resolveExpressions(resolver));
	}
	
	private static ArithmeticOperator getOperator(String op) throws ResolverException {
		switch (op) {
			case "+": return ArithmeticOperator.PLUS;
			case "-": return ArithmeticOperator.MINUS;
			case "*": return ArithmeticOperator.MULT;
			case "/": return ArithmeticOperator.DIV;
			case "%": return ArithmeticOperator.MOD;
			default:
				throw new ResolverException("unknown arithmetic operator: " + op);
		}
	}

	private static final class ArithmeticEvaluator extends AbstractDoubleEvaluator {
		private final ArithmeticOperator operator;
		private final Evaluator left;
		private final Evaluator right;

		private ArithmeticEvaluator(ArithmeticOperator operator, Evaluator left, Evaluator right) {
			super();
			this.operator = operator;
			this.left = left;
			this.right = right;
		}
		
		@Override
		public double evaluateDouble(EvaluationContext ctx, Element elt) {
			return operator.compute(left.evaluateDouble(ctx, elt), right.evaluateDouble(ctx, elt));
		}

		@Override
		public Collection<EvaluationType> getTypes() {
			if (isDouble(left) || isDouble(right))
				return Arrays.asList(EvaluationType.DOUBLE, EvaluationType.INT);
			return Arrays.asList(EvaluationType.INT, EvaluationType.DOUBLE);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			left.collectUsedNames(nameUsage, defaultType);
			right.collectUsedNames(nameUsage, defaultType);
		}
	}

	@Function(firstFtor="-")
	public static final Evaluator uminus(Evaluator e) {
		return new UnaryMinusEvaluator(e);
	}
	
	private static final class UnaryMinusEvaluator extends AbstractDoubleEvaluator {
		private final Evaluator expr;

		private UnaryMinusEvaluator(Evaluator expr) {
			super();
			this.expr = expr;
		}

		@Override
		public int evaluateInt(EvaluationContext ctx, Element elt) {
			return -expr.evaluateInt(ctx, elt);
		}

		@Override
		public Collection<EvaluationType> getTypes() {
			if (isDouble(expr))
				return Arrays.asList(EvaluationType.DOUBLE, EvaluationType.INT);
			return Arrays.asList(EvaluationType.INT, EvaluationType.DOUBLE);
		}

		@Override
		public double evaluateDouble(EvaluationContext ctx, Element elt) {
			return -expr.evaluateDouble(ctx, elt);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			expr.collectUsedNames(nameUsage, defaultType);
		}
	}
}
