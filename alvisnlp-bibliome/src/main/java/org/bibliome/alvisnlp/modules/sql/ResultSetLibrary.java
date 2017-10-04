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


package org.bibliome.alvisnlp.modules.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bibliome.util.Iterators;
import org.bibliome.util.StringCat;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.AbstractEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.EvaluationType;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.documentation.Documentation;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

public class ResultSetLibrary extends FunctionLibrary {
	private ResultSet resultSet;
	
	void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	@Override
	public String getName() {
		return "resultset";
	}

	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		int nFtors = ftors.size();
		int nArgs = args.size();
		if (nFtors == 0 && nArgs == 1) {
			Evaluator columnIndex = args.get(0).resolveExpressions(resolver);
			return new ColumnIndexEvaluator(columnIndex);
		}
		if (nFtors == 1 && nArgs == 0) {
			String columnName = ftors.get(0);
			return new ColumnNameEvaluator(columnName);
		}
		return cannotResolve(ftors, args);
	}

	@Override
	public Documentation getDocumentation() {
		return null;
	}
	
	private abstract class AbstractColumnEvaluator extends AbstractEvaluator {
		@Override
		public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
			String s = evaluateString(ctx, elt);
			strcat.append(s);
		}

		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			return Iterators.emptyIterator();
		}

		@Override
		public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
			return Collections.emptyList();
		}

		@Override
		public boolean testEquality(EvaluationContext ctx, Evaluator that, Element elt, boolean mayDelegate) {
			if (mayDelegate) {
				return that.testEquality(ctx, this, elt, false);
			}
			String thisValue = evaluateString(ctx, elt);
			String thatValue = that.evaluateString(ctx, elt);
			return thisValue.equals(thatValue);
		}

		@Override
		public Collection<EvaluationType> getTypes() {
			return Arrays.asList(EvaluationType.BOOLEAN, EvaluationType.INT, EvaluationType.DOUBLE, EvaluationType.STRING);
		}
	}

	private class ColumnIndexEvaluator extends AbstractColumnEvaluator {
		private final Evaluator columnIndex;

		private ColumnIndexEvaluator(Evaluator columnIndex) {
			super();
			this.columnIndex = columnIndex;
		}

		@Override
		public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
			try {
				int columnIndex = this.columnIndex.evaluateInt(ctx, elt);
				return resultSet.getBoolean(columnIndex);
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public int evaluateInt(EvaluationContext ctx, Element elt) {
			try {
				int columnIndex = this.columnIndex.evaluateInt(ctx, elt);
				return resultSet.getInt(columnIndex);
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public double evaluateDouble(EvaluationContext ctx, Element elt) {
			try {
				int columnIndex = this.columnIndex.evaluateInt(ctx, elt);
				return resultSet.getDouble(columnIndex);
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String evaluateString(EvaluationContext ctx, Element elt) {
			try {
				int columnIndex = this.columnIndex.evaluateInt(ctx, elt);
				return resultSet.getString(columnIndex);
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			columnIndex.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	private class ColumnNameEvaluator extends AbstractColumnEvaluator {
		private final String columnName;

		private ColumnNameEvaluator(String columnName) {
			super();
			this.columnName = columnName;
		}

		@Override
		public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
			try {
				return resultSet.getBoolean(columnName);
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public int evaluateInt(EvaluationContext ctx, Element elt) {
			try {
				return resultSet.getInt(columnName);
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public double evaluateDouble(EvaluationContext ctx, Element elt) {
			try {
				return resultSet.getDouble(columnName);
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String evaluateString(EvaluationContext ctx, Element elt) {
			try {
				return resultSet.getString(columnName);
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	}
}
