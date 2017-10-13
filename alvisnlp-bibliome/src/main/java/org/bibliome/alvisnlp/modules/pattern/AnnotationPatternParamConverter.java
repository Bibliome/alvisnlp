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


package org.bibliome.alvisnlp.modules.pattern;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.bibliome.alvisnlp.converters.expression.parser.ExpressionParser;
import org.bibliome.alvisnlp.converters.expression.parser.ParseException;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.SimpleParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.util.pattern.Alternatives;
import fr.inra.maiage.bibliome.util.pattern.Any;
import fr.inra.maiage.bibliome.util.pattern.CapturingGroup;
import fr.inra.maiage.bibliome.util.pattern.Clause;
import fr.inra.maiage.bibliome.util.pattern.ClauseVisitor;
import fr.inra.maiage.bibliome.util.pattern.Group;
import fr.inra.maiage.bibliome.util.pattern.Predicate;
import fr.inra.maiage.bibliome.util.pattern.SequenceEnd;
import fr.inra.maiage.bibliome.util.pattern.SequenceStart;

@Converter(targetType=ElementPattern.class)
public class AnnotationPatternParamConverter extends SimpleParamConverter<ElementPattern> {
	public static final String PATTERN_ATTRIBUTE = "pattern";

	@Override
	protected ElementPattern convertTrimmed(String stringValue) throws ConverterException {
		Reader r = new StringReader(stringValue);
		ExpressionParser parser = new ExpressionParser(r);
		try {
			return parser.pattern();
		}
		catch (ParseException pe) {
			cannotConvertString(stringValue, pe.getMessage());
			return null;
		}
	}

	@Override
	public String[] getAlternateAttributes() {
		return new String[] { PATTERN_ATTRIBUTE };
	}
	
	@Override
	public String getStringValue(Object value) throws ConverterException {
		if (!(value instanceof ElementPattern)) {
			throw new RuntimeException();
		}
		ElementPattern eltPattern = (ElementPattern) value;
		StringBuilder sb = new StringBuilder();
		Group<Void,Void,ExpressionFilterProxy> grp = eltPattern.getTop();
		grp.accept(TO_STRING_VISITOR, sb);
		return sb.toString();
	}

	private static ClauseVisitor<StringBuilder,Void,Void,Void,ExpressionFilterProxy> TO_STRING_VISITOR = new ClauseVisitor<StringBuilder,Void,Void,Void,ExpressionFilterProxy>() {
		@Override
		public Void visit(Alternatives<Void,Void,ExpressionFilterProxy> alt, StringBuilder param) {
			param.append('(');
			boolean notFirst = false;
			for (Clause<Void,Void,ExpressionFilterProxy> clause : alt.getAlternatives()) {
				if (notFirst) {
					param.append('|');
				}
				else {
					notFirst = true;
				}
				clause.accept(this, param);
			}
			param.append(')');
			return null;
		}

		@Override
		public Void visit(Any<Void,Void,ExpressionFilterProxy> any, StringBuilder param) {
			throw new RuntimeException("cannot get value of Any");
		}

		@Override
		public Void visit(CapturingGroup<Void,Void,ExpressionFilterProxy> grp, StringBuilder param) {
			visitGroup(grp, grp.getName(), param);
			return null;
		}

		@Override
		public Void visit(Group<Void,Void,ExpressionFilterProxy> grp, StringBuilder param) {
			visitGroup(grp, null, param);
			return null;
		}
		
		private void visitGroup(Group<Void,Void,ExpressionFilterProxy> grp, String name, StringBuilder sb) {
			sb.append('(');
			if (name != null) {
				sb.append(name);
				sb.append(':');
			}
			for (Clause<Void,Void,ExpressionFilterProxy> clause : grp.getChildren()) {
				clause.accept(this, sb);
			}
			sb.append(')');
		}

		@Override
		public Void visit(Predicate<Void,Void,ExpressionFilterProxy> pred, StringBuilder param) {
			param.append('[');
			ExpressionFilterProxy filter = pred.getFilter();
			Expression expr = filter.getExpression();
			try {
				expr.toString(param);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
			param.append(']');
			return null;
		}

		@Override
		public Void visit(SequenceStart<Void,Void,ExpressionFilterProxy> start, StringBuilder param) {
			param.append("start");
			return null;
		}

		@Override
		public Void visit(SequenceEnd<Void,Void,ExpressionFilterProxy> start, StringBuilder param) {
			param.append("end");
			return null;
		}
	};
}
