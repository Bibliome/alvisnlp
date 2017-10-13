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

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Resolvable;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.util.mappers.Mapper;
import fr.inra.maiage.bibliome.util.pattern.Group;
import fr.inra.maiage.bibliome.util.pattern.SequencePattern;

public class ElementPattern extends SequencePattern<Void,Void,ExpressionFilterProxy> implements Resolvable<SequencePattern<Element,EvaluationContext,EvaluatorFilterProxy>> {
	public ElementPattern(Group<Void,Void,ExpressionFilterProxy> top) {
		super(top);
	}

	@Override
	public SequencePattern<Element,EvaluationContext,EvaluatorFilterProxy> resolveExpressions(LibraryResolver resolver) throws ResolverException {
		Mapper<ExpressionFilterProxy,EvaluatorFilterProxy> mapper = new ExpressionFilterProxy.ResolveMapper(resolver);
		return copy(mapper);
	}
}
