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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.EvaluationType;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;
import alvisnlp.module.ProcessingException;

public class SQLParameterEvaluator implements NameUser {
	private final EvaluationType type;
	private final Evaluator evaluator;

	public SQLParameterEvaluator(EvaluationType type, Evaluator evaluator) {
		super();
		this.type = type;
		this.evaluator = evaluator;
	}
	
	void updateStatement(PreparedStatement statement, int parameterIndex, EvaluationContext ctx, Element elt) throws SQLException, ProcessingException {
		switch (type) {
			case BOOLEAN:
				boolean b = evaluator.evaluateBoolean(ctx, elt);
				statement.setBoolean(parameterIndex, b);
				break;
			case DOUBLE:
				double d = evaluator.evaluateDouble(ctx, elt);
				statement.setDouble(parameterIndex, d);
				break;
			case INT:
				int i = evaluator.evaluateInt(ctx, elt);
				statement.setInt(parameterIndex, i);
				break;
			case STRING:
				String s = evaluator.evaluateString(ctx, elt);
				statement.setString(parameterIndex, s);
				break;
			default:
				throw new ProcessingException("unhandled type " + type);
		}
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		evaluator.collectUsedNames(nameUsage, defaultType);
	}
}
