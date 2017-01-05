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


package org.bibliome.alvisnlp.library.standard;

import java.util.Iterator;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.creators.DocumentCreator;
import alvisnlp.corpus.creators.RelationCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;

@Library("new")
public abstract class CreateElementLibrary extends FunctionLibrary {
	public static final String NAME = "new";
	
	private static final Iterator<Element> createAnnotation(String layerName, EvaluationContext ctx, Element elt, int start, int end) {
		AnnotationCreator ac = ctx.getAnnotationCreator();
		Section sec = DownCastElement.toSection(elt);
		if (sec == null)
			return Iterators.emptyIterator();
		Annotation result = new Annotation(ac, sec, start, end);
		ctx.registerCreateElement(result);
		ctx.registerAddAnnotation(result, layerName);
		return Iterators.singletonIterator(result);		
	}
	
	@Function(ftors=1, nameTypes={NameType.LAYER})
	public static final Iterator<Element> annotation(EvaluationContext ctx, Element elt, String layerName, Evaluator startEvaluator, Evaluator endEvaluator) {
		int start = startEvaluator.evaluateInt(ctx, elt);
		int end = endEvaluator.evaluateInt(ctx, elt);
		return createAnnotation(layerName, ctx, elt, start, end);
	}
	
	@Function(ftors=1, nameTypes={NameType.LAYER})
	public static final Iterator<Element> annotation(EvaluationContext ctx, Element elt, String layerName, Evaluator included) {
		int start = Integer.MAX_VALUE;
		int end = Integer.MIN_VALUE;
		for (Element e : Iterators.loop(included.evaluateElements(ctx, elt))) {
			Annotation a = DownCastElement.toAnnotation(e);
			if (a == null)
				continue;
			if (a.getStart() < start)
				start = a.getStart();
			if (a.getEnd() > end)
				end = a.getEnd();
		}
		if (end == Integer.MIN_VALUE)
			return Iterators.emptyIterator();
		return createAnnotation(layerName, ctx, elt, start, end);
	}

	@Function
	public static final Iterator<Element> document(EvaluationContext ctx, Element elt, Evaluator id) {
		DocumentCreator dc = ctx.getDocumentCreator();
		Corpus corpus = DownCastElement.toCorpus(elt);
		if (corpus == null)
			return Iterators.emptyIterator();
		Document doc = Document.getDocument(dc, corpus, id.evaluateString(ctx, elt), false);
		ctx.registerCreateElement(doc);
		return Iterators.singletonIterator(doc);
	}
	
	@Function(ftors=1, nameTypes={NameType.SECTION})
	public static final Iterator<Element> section(EvaluationContext ctx, Element elt, String name, Evaluator contents) {
		SectionCreator sc = ctx.getSectionCreator();
//		if (sc == null)
//			throw new RuntimeException("section creation is not allowed");
		Document doc = DownCastElement.toDocument(elt);
		if (doc == null)
			return Iterators.emptyIterator();
		Section result = new Section(sc, doc, name, contents.evaluateString(ctx, elt), false);
		ctx.registerCreateElement(result);
		return Iterators.singletonIterator(result);
	}
	
	@Function
	public static final Iterator<Element> section(EvaluationContext ctx, Element elt, Evaluator name, Evaluator contents) {
		return section(ctx, elt, name.evaluateString(ctx, elt), contents);
	}
	
	@Function(ftors=1, nameTypes={NameType.RELATION})
	public static final Iterator<Element> relation(EvaluationContext ctx, Element elt, String name) {
		RelationCreator rc = ctx.getRelationCreator();
//		if (rc == null)
//			throw new RuntimeException("relation creation is not allowed");
		Section sec = DownCastElement.toSection(elt);
		if (sec == null)
			return Iterators.emptyIterator();
		Relation result = sec.ensureRelation(rc, name, false);
		ctx.registerCreateElement(result);
		return Iterators.singletonIterator(result);
	}
	
	@Function
	public static final Iterator<Element> relation(EvaluationContext ctx, Element elt, Evaluator name) {
		return relation(ctx, elt, name.evaluateString(ctx, elt));
	}
	
	@Function
	public static final Iterator<Element> tuple(EvaluationContext ctx, Element elt) {
		TupleCreator tc = ctx.getTupleCreator();
//		if (tc == null)
//			throw new RuntimeException("tuple creation is not allowed");
		Relation rel = DownCastElement.toRelation(elt);
		if (rel == null)
			return Iterators.emptyIterator();
		Tuple result = new Tuple(tc, rel, false);
		ctx.registerCreateElement(result);
		return Iterators.singletonIterator(result);
	}
}
