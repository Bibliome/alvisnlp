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


package fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions;

import java.util.Collections;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ElementVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;

public abstract class AbstractVisitorListEvaluator extends AbstractListEvaluator implements ElementVisitor<List<Element>,Void> {
	protected AbstractVisitorListEvaluator() {
		super();
	}

	@Override
	public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
		return elt.accept(this, null);
	}

	@Override
	public List<Element> visit(Annotation a, Void param) {
		return Collections.emptyList();
	}

	@Override
	public List<Element> visit(Corpus corpus, Void param) {
		return Collections.emptyList();
	}

	@Override
	public List<Element> visit(Document doc, Void param) {
		return Collections.emptyList();
	}

	@Override
	public List<Element> visit(Relation rel, Void param) {
		return Collections.emptyList();
	}

	@Override
	public List<Element> visit(Section sec, Void param) {
		return Collections.emptyList();
	}

	@Override
	public List<Element> visit(Tuple t, Void param) {
		return Collections.emptyList();
	}

	@Override
	public List<Element> visit(Element e, Void param) {
		return Collections.emptyList();
	}
}
