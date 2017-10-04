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


package org.bibliome.alvisnlp.modules.rdf;

import java.util.List;

import org.bibliome.alvisnlp.library.StringLibrary;

import alvisnlp.corpus.expressions.ConstantsLibrary;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.documentation.Documentation;

class PrefixLibrary extends FunctionLibrary {
	private final String name;
	private final String prefix;

	PrefixLibrary(String name, String prefix) {
		super();
		this.name = name;
		this.prefix = prefix;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		checkRangeFtors(ftors, 0, 1);
		if (ftors.isEmpty()) {
			checkMaxArity(ftors, args, 1);
			if (args.isEmpty()) {
				return ConstantsLibrary.getInstance(prefix);
			}
			Expression nameExpr = args.get(0);
			Evaluator name = nameExpr.resolveExpressions(resolver);
			return new StringLibrary.ConcatEvaluator(ConstantsLibrary.getInstance(prefix), name);
		}
		String localName = ftors.get(0);
		return ConstantsLibrary.getInstance(prefix + localName);
	}

	@Override
	public Documentation getDocumentation() {
		// TODO Auto-generated method stub
		return null;
	}
}
