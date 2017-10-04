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


package alvisnlp.corpus.expressions;

import java.util.Iterator;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.ElementVisitor;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;

public abstract class AbstractVisitorIteratorEvaluator extends AbstractIteratorEvaluator implements ElementVisitor<Iterator<Element>,Void> {
	protected AbstractVisitorIteratorEvaluator() {
		super();
	}

	@Override
	public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
		return elt.accept(this, null);
	}

	@Override
	public Iterator<Element> visit(Annotation a, Void param) {
		return Iterators.emptyIterator();
	}

	@Override
	public Iterator<Element> visit(Corpus corpus, Void param) {
		return Iterators.emptyIterator();
	}

	@Override
	public Iterator<Element> visit(Document doc, Void param) {
		return Iterators.emptyIterator();
	}

	@Override
	public Iterator<Element> visit(Relation rel, Void param) {
		return Iterators.emptyIterator();
	}

	@Override
	public Iterator<Element> visit(Section sec, Void param) {
		return Iterators.emptyIterator();
	}

	@Override
	public Iterator<Element> visit(Tuple t, Void param) {
		return Iterators.emptyIterator();
	}

	@Override
	public Iterator<Element> visit(Element e, Void param) {
		return Iterators.emptyIterator();
	}
}
