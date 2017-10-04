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

import java.util.List;

import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.corpus.expressions.VariableLibrary;

public class UserFunction {
	private final List<String> ftors;
	private final List<String> params;
	private Expression body;
	
	public UserFunction(List<String> ftors, List<String> params, Expression body) {
		super();
		this.ftors = ftors;
		this.params = params;
		this.body = body;
	}

	private VariableLibrary getFtorsLibrary(List<String> ftors) {
		VariableLibrary result = new VariableLibrary("ftor");
		for (int i = 1; i < ftors.size(); ++i) {
			String name = this.ftors.get(i);
			String value = ftors.get(i);
			VariableLibrary.Variable var = result.newVariable(name);
			var.set(value);
		}
		return result;
	}
	
	private VariableLibrary getParamsLibrary(LibraryResolver resolver, List<Expression> args) throws ResolverException {
		VariableLibrary result = new VariableLibrary("param");
		for (int i = 0; i < args.size(); ++i) {
			String name = params.get(i);
			Expression expr = args.get(i);
			Evaluator value = expr.resolveExpressions(resolver);
			VariableLibrary.Variable var = result.newVariable(name);
			var.set(value);
		}
		return result;
	}
	
	private LibraryResolver getBodyResolver(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		LibraryResolver result = new LibraryResolver(resolver);
		VariableLibrary ftorsLib = getFtorsLibrary(ftors);
		VariableLibrary paramsLib = getParamsLibrary(resolver, args);
		result.addLibrary(ftorsLib);
		result.addLibrary(paramsLib);
		return result;
	}
	
	boolean match(List<String> ftors, List<Expression> args) {
		return
				ftors.size() == this.ftors.size() &&
				args.size() == params.size() &&
				ftors.get(0).equals(this.ftors.get(0));
	}
	
	Evaluator resolve(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		if (match(ftors, args)) {
			LibraryResolver bodyResolver = getBodyResolver(resolver, ftors, args);
			return body.resolveExpressions(bodyResolver);
		}
		return null;
	}
}