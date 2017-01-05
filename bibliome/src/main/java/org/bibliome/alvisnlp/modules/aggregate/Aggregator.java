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


package org.bibliome.alvisnlp.modules.aggregate;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.Resolvable;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;

public abstract class Aggregator implements Resolvable<Aggregator.Resolved> {
	private final Expression item;
	
	protected Aggregator(Expression item) {
		super();
		this.item = item;
	}

	@Override
	public Aggregator.Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		Evaluator item = this.item.resolveExpressions(resolver);
		return resolveExpressions(resolver, item);
	}

	protected abstract Aggregator.Resolved resolveExpressions(LibraryResolver resolver, Evaluator item);

	public static abstract class Resolved implements NameUser {
		protected final Evaluator item;
		
		protected Resolved(Evaluator item) {
			super();
			this.item = item;
		}
		
		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			item.collectUsedNames(nameUsage, defaultType);
		}

		abstract Object createValue();

		abstract String toString(Object value);

		abstract void incorporateEntry(Object value, EvaluationContext ctx, Element entry);
	}
}
