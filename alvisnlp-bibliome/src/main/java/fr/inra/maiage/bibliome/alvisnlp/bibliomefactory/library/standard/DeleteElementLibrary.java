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

import java.util.Collections;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.AbstractListEvaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.FunctionLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Library;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.documentation.Documentation;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;

@Library("delete")
public class DeleteElementLibrary extends FunctionLibrary {
	public static final String NAME = "delete";
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		checkExactFtors(ftors, 0);
		checkExactArity(ftors, args, 0);
		return EVALUATOR;
	}

	@Override
	public Documentation getDocumentation() {
		// TODO Auto-generated method stub
		return null;
	}

	private static final Evaluator EVALUATOR = new AbstractListEvaluator() {
		@Override
		public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
//			if (!ctx.isAllowDeleteElement())
//				throw new RuntimeException("element deletion is not allowed");
			ctx.registerDeleteElement(elt);
			return Collections.singletonList(elt);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
}
