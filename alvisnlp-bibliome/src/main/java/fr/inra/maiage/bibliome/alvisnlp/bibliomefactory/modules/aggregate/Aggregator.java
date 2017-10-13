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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.aggregate;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Resolvable;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUser;

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
