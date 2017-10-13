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


package org.bibliome.alvisnlp.modules.alvisir2;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Resolvable;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;

public class TokenFragments implements Resolvable<TokenFragmentsEvaluator> {
	private final Expression instances;
	private final Expression start;
	private final Expression end;

	TokenFragments(Expression instances, Expression start, Expression end) {
		super();
		this.instances = instances;
		this.start = start;
		this.end = end;
	}

	@Override
	public TokenFragmentsEvaluator resolveExpressions(LibraryResolver resolver) throws ResolverException {
		Evaluator instances = this.instances.resolveExpressions(resolver);
		Evaluator start = this.start.resolveExpressions(resolver);
		Evaluator end = this.end.resolveExpressions(resolver);
		return new TokenFragmentsEvaluator(instances, start, end);
	}
}
