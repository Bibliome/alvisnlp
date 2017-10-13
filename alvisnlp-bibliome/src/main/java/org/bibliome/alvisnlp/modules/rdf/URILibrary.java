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

import java.util.Iterator;
import java.util.List;

import org.apache.jena.rdf.model.Resource;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.AbstractIteratorEvaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.AbstractStringEvaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ConstantsLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.FunctionLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.documentation.Documentation;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.StringCat;

class URILibrary extends FunctionLibrary {
	private final ElementResourceMap resourceMap;

	URILibrary(ElementResourceMap uriCache) {
		super();
		this.resourceMap = uriCache;
	}

	@Override
	public String getName() {
		return "uri";
	}

	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		checkExactFtors(ftors, 1);
		String ftor = ftors.get(0);
		switch (ftor) {
			case "get":
				checkExactArity(ftors, args, 0);
				return uriEvaluator;
			case "set": {
				checkExactArity(ftors, args, 1);
				Expression uriExpr = args.get(0);
				Evaluator uri = uriExpr.resolveExpressions(resolver);
				return new SetURIEvaluator(uri);
			}
			case "blank":
			case "bnode":
			case "none":
			case "anon":
			case "anonymous": {
				checkExactArity(ftors, args, 0);
				return new SetURIEvaluator(ConstantsLibrary.getInstance(""));
			}
			default:
				return cannotResolve(ftors, args);
		}
	}

	@Override
	public Documentation getDocumentation() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class SetURIEvaluator extends AbstractIteratorEvaluator {
		private final Evaluator uri;

		private SetURIEvaluator(Evaluator uri) {
			super();
			this.uri = uri;
		}

		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			String uri = this.uri.evaluateString(ctx, elt);
			resourceMap.set(elt, uri);
			return Iterators.singletonIterator(elt);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			uri.collectUsedNames(nameUsage, defaultType);
		}
	}

	public final Evaluator uriEvaluator = new AbstractStringEvaluator() {
		@Override
		public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
			strcat.append(evaluateString(ctx, elt));
		}
		
		@Override
		public String evaluateString(EvaluationContext ctx, Element elt) {
			Resource res = resourceMap.get(elt);
			if (res.isAnon()) {
				return "";
			}
			return res.getURI();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
}
