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


package org.bibliome.alvisnlp.modules.shell;

import java.util.Iterator;
import java.util.List;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.creators.DocumentCreator;
import alvisnlp.corpus.creators.RelationCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.AbstractIteratorEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.documentation.Documentation;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

public class AllowLibrary extends FunctionLibrary {
	private final DocumentCreator documentCreator;
	private final SectionCreator sectionCreator;
	private final AnnotationCreator annotationCreator;
	private final RelationCreator relationCreator;
	private final TupleCreator tupleCreator;

	public AllowLibrary(DocumentCreator documentCreator, SectionCreator sectionCreator, AnnotationCreator annotationCreator, RelationCreator relationCreator, TupleCreator tupleCreator) {
		super();
		this.documentCreator = documentCreator;
		this.sectionCreator = sectionCreator;
		this.annotationCreator = annotationCreator;
		this.relationCreator = relationCreator;
		this.tupleCreator = tupleCreator;
	}

	@Override
	public String getName() {
		return "allow";
	}

	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		if (ftors.isEmpty())
			return cannotResolve(ftors, args);
		String firstFtor = ftors.get(0);
		if (firstFtor.equals("everything") && ftors.size() == 1 && args.size() == 0) {
			return allowEverything;
		}
		if (firstFtor.equals("nothing") && ftors.size() == 1 && args.size() == 0) {
			return allowNothing;
		}
		if (firstFtor.equals("create") && ftors.size() == 2 && args.size() == 0) {
			String elementType = ftors.get(1);
			switch (elementType) {
				case "anything": return allowCreateAnything;
				case "annotations": return allowCreateAnnotations;
				case "documents": return allowCreateDocuments;
				case "sections": return allowCreateSections;
				case "relations": return allowCreateRelations;
				case "tuples": return allowCreateTuples;
			}
		}
		if (firstFtor.equals("delete") && ftors.size() == 1 && args.size() == 0) {
			return allowDeleteElements;
		}
		if (firstFtor.equals("add") && ftors.size() == 1 && args.size() == 0) {
			return allowAddAnnotations;
		}
		if (firstFtor.equals("remove") && ftors.size() == 1 && args.size() == 0) {
			return allowRemoveAnnotations;
		}
		if (firstFtor.equals("features") && ftors.size() == 1 && args.size() == 0) {
			return allowSetFeatures;
		}
		if (firstFtor.equals("args") && ftors.size() == 1 && args.size() == 0) {
			return allowSetArguments;
		}
		return cannotResolve(ftors, args);
	}
	
	private final Evaluator allowNothing = new AbstractIteratorEvaluator() {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			ctx.setAllowAddAnnotation(false);
			ctx.setAllowDeleteElement(false);
			ctx.setAllowRemoveAnnotation(false);
			ctx.setAllowSetArgument(false);
			ctx.setAllowSetFeature(false);
			ctx.setDocumentCreator(null);
			ctx.setSectionCreator(null);
			ctx.setAnnotationCreator(null);
			ctx.setRelationCreator(null);
			ctx.setTupleCreator(null);
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
	
	private final Evaluator allowEverything = new AbstractIteratorEvaluator() {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			ctx.setAllowAddAnnotation(true);
			ctx.setAllowDeleteElement(true);
			ctx.setAllowRemoveAnnotation(true);
			ctx.setAllowSetArgument(true);
			ctx.setAllowSetFeature(true);
			ctx.setDocumentCreator(documentCreator);
			ctx.setSectionCreator(sectionCreator);
			ctx.setAnnotationCreator(annotationCreator);
			ctx.setRelationCreator(relationCreator);
			ctx.setTupleCreator(tupleCreator);
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
	
	private final Evaluator allowDeleteElements = new AbstractIteratorEvaluator() {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			ctx.setAllowDeleteElement(true);
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
	
	private final Evaluator allowAddAnnotations = new AbstractIteratorEvaluator() {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			ctx.setAllowAddAnnotation(true);
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
	
	private final Evaluator allowRemoveAnnotations = new AbstractIteratorEvaluator() {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			ctx.setAllowRemoveAnnotation(true);
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
	
	private final Evaluator allowSetFeatures = new AbstractIteratorEvaluator() {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			ctx.setAllowSetFeature(true);
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
	
	private final Evaluator allowSetArguments = new AbstractIteratorEvaluator() {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			ctx.setAllowSetArgument(true);
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
	
	private final Evaluator allowCreateAnything = new AbstractIteratorEvaluator() {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			ctx.setDocumentCreator(documentCreator);
			ctx.setSectionCreator(sectionCreator);
			ctx.setAnnotationCreator(annotationCreator);
			ctx.setRelationCreator(relationCreator);
			ctx.setTupleCreator(tupleCreator);
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
	
	private final Evaluator allowCreateDocuments = new AbstractIteratorEvaluator() {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			ctx.setDocumentCreator(documentCreator);
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
	
	private final Evaluator allowCreateSections = new AbstractIteratorEvaluator() {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			ctx.setSectionCreator(sectionCreator);
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
	
	private final Evaluator allowCreateAnnotations = new AbstractIteratorEvaluator() {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			ctx.setAnnotationCreator(annotationCreator);
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
	
	private final Evaluator allowCreateRelations = new AbstractIteratorEvaluator() {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			ctx.setRelationCreator(relationCreator);
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
	
	private final Evaluator allowCreateTuples = new AbstractIteratorEvaluator() {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			ctx.setTupleCreator(tupleCreator);
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
	
	@Override
	public Documentation getDocumentation() {
		// TODO Auto-generated method stub
		return null;
	}
}
