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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library;

import java.util.ArrayList;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.FunctionLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Library;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;

@Library("user")
public abstract class UserLibrary extends FunctionLibrary {
	private final List<UserFunction> functions = new ArrayList<UserFunction>();
	
	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		for (UserFunction fun : functions) {
			if (fun.match(ftors, args)) {
				return fun.resolve(resolver, ftors, args);
			}
		}
		return null;
	}

	public void addFunction(UserFunction fun) {
		functions.add(fun);
	}

	public void addFunction(List<String> ftors, List<String> params, Expression body) {
		addFunction(new UserFunction(ftors, params, body));
	}
}
