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

import java.io.IOException;
import java.util.List;

import org.bibliome.util.StringCat;
import org.bibliome.util.service.Service;

import alvisnlp.corpus.Element;
import alvisnlp.documentation.Documentation;
import alvisnlp.documentation.ResourceDocumentation;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

@Service(FunctionLibrary.class)
public class ConstantsLibrary extends FunctionLibrary {
	public static final String NAME = "constant";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		if (ftors.size() != 2)
			return null;
		String first = ftors.get(0);
		String sValue = ftors.get(1);
		switch (first) {
			case "boolean":
				switch (sValue) {
					case "false": return EVALUATOR_FALSE;
					case "true": return EVALUATOR_TRUE;
					default: throw new ResolverException("unknown boolean value: " + sValue);
				}
			case "int":
				try {
					return new IntConstantEvaluator(Integer.parseInt(sValue));
				}
				catch (NumberFormatException e) {
					throw new ResolverException("malformed int: " + sValue, e);
				}
			case "double":
				try {
					return new DoubleConstantEvaluator(Double.parseDouble(sValue));
				}
				catch (NumberFormatException e) {
					throw new ResolverException("malformed double: " + sValue, e);
				}
			case "string":
				return new StringConstantEvaluator(sValue);
			default:
				return null;
		}
	}

	@Override
	public Documentation getDocumentation() {
        return new ResourceDocumentation(getClass().getCanonicalName() + "Doc");
	}

	public static final Expression TRUE = new Expression(NAME, "boolean", "true") {
		@Override
		public void toString(Appendable a) throws IOException {
			a.append("true");
		}

		@Override
		public String toString() {
			return "true";
		}
		
	};
	
	public static final Expression FALSE = new Expression(NAME, "boolean", "false") {
		@Override
		public void toString(Appendable a) throws IOException {
			a.append("false");
		}

		@Override
		public String toString() {
			return "false";
		}
		
	};

	public static Expression create(int i) {
		final String s = Integer.toString(i);
		return new Expression(NAME, "int", s) {
			@Override
			public void toString(Appendable a) throws IOException {
				a.append(s);
			}

			@Override
			public String toString() {
				return s;
			}
		};
	}

	public static Expression create(double d) {
		final String s = Double.toString(d);
		return new Expression(NAME, "double", s) {
			@Override
			public void toString(Appendable a) throws IOException {
				a.append(s);
			}

			@Override
			public String toString() {
				return s;
			}
		};
	}

	public static Expression create(String s) {
		return new Expression(NAME, "string", s);
	}

	public static final Evaluator getInstance(boolean value) {
		if (value)
			return EVALUATOR_TRUE;
		return EVALUATOR_FALSE;
	}
	
	public static final Evaluator getInstance(int value) {
		return new IntConstantEvaluator(value);
	}
	
	public static final Evaluator getInstance(double value) {
		return new DoubleConstantEvaluator(value);
	}
	
	public static final Evaluator getInstance(String value) {
		return new StringConstantEvaluator(value);
	}

	public static final BooleanConstantEvaluator EVALUATOR_TRUE = new BooleanConstantEvaluator(true);
	public static final BooleanConstantEvaluator EVALUATOR_FALSE = new BooleanConstantEvaluator(false);

	private static final class BooleanConstantEvaluator extends AbstractBooleanEvaluator {
		private final boolean value;

		private BooleanConstantEvaluator(boolean value) {
			super();
			this.value = value;
		}

		@Override
		public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
			return value;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	}

	private static final class IntConstantEvaluator extends AbstractIntEvaluator {
		private final int value;

		private IntConstantEvaluator(int value) {
			super();
			this.value = value;
		}

		@Override
		public int evaluateInt(EvaluationContext ctx, Element elt) {
			return value;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	}

	private static final class DoubleConstantEvaluator extends AbstractDoubleEvaluator {
		private final double value;

		private DoubleConstantEvaluator(double value) {
			super();
			this.value = value;
		}

		@Override
		public double evaluateDouble(EvaluationContext ctx, Element elt) {
			return value;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	}

	private static final class StringConstantEvaluator extends AbstractStringEvaluator {
		private final String value;

		private StringConstantEvaluator(String value) {
			super();
			this.value = value;
		}

		@Override
		public String evaluateString(EvaluationContext ctx, Element elt) {
			return value;
		}

		@Override
		public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
			strcat.append(value);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	}
}
