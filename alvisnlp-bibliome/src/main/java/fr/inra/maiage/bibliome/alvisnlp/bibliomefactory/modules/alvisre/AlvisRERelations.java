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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisre;

import java.io.PrintStream;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.converters.expression.parser.ExpressionParser;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DefaultExpressions;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisre.AlvisREAnnotations.ResolvedAlvisREAnnotation;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisre.AlvisRERelations.Resolved;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ArgumentElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ConstantsLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.util.Iterators;

public class AlvisRERelations extends AlvisREAnnotations<Resolved> {
	private final Expression arguments;
	private final Expression role;
	private final Expression label;
	
	AlvisRERelations(Expression items, Expression type, Expression arguments, Expression role, Expression label) {
		super(items, type);
		this.arguments = arguments;
		this.role = role;
		this.label = label;
	}
	
	static AlvisRERelations getTupleAlvisRERelation(Expression items, Expression type, Expression label) {
		Expression arguments = ExpressionParser.parseUnsafe("nav:arguments");
		Expression role = DefaultExpressions.feature(ArgumentElement.ROLE_FEATURE_KEY);
		return new AlvisRERelations(items, type, arguments, role, label);
	}
	
	static AlvisRERelations getDependenciesAlvisRERelation() {
		Expression items = DefaultExpressions.SECTION_DEPENDENCIES;
		Expression type = ConstantsLibrary.create("Dependency");
		Expression label = DefaultExpressions.feature(DefaultNames.getDependencyLabelFeatureName());
		return getTupleAlvisRERelation(items, type, label);
	}

	@Override
	public Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new Resolved(resolver, this);
	}

	public static class Resolved extends ResolvedAlvisREAnnotation {
		private final Evaluator arguments;
		private final Evaluator role;
		private final Evaluator label;

		private Resolved(LibraryResolver resolver, AlvisRERelations rels) throws ResolverException {
			super(resolver, rels);
			this.arguments = rels.arguments.resolveExpressions(resolver);
			this.role = rels.role.resolveExpressions(resolver);
			this.label = resolver.resolveNullable(rels.label);
		}

		@Override
		protected String addElement(SectionsMerger merger, Element elt) {
			return merger.addRelation(elt);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			arguments.collectUsedNames(nameUsage, defaultType);
			role.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesNullable(label, defaultType);
		}

		@Override
		protected void printInfo(PrintStream out, SectionsMerger merger, EvaluationContext ctx, Element elt) {
			for (Element p : Iterators.loop(arguments.evaluateElements(ctx, elt))) {
				String role = this.role.evaluateString(ctx, p);
				String argId = merger.getId(p);
				out.format(" %s:%s", role, argId);
			}
			if (this.label != null) {
				String label = this.label.evaluateString(ctx, elt);
				out.format(" label:%s", label);
			}
		}
	}
}
