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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.standard;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ElementVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.AbstractListEvaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.util.Pair;

abstract class AbstractAnnotationSiblings extends AbstractListEvaluator implements ElementVisitor<List<Element>,Pair<EvaluationContext,Element>> {
	protected final Evaluator layerNameEvaluator;
	protected final String layerName;
	protected final boolean excludeSelf;

	protected AbstractAnnotationSiblings(String layerName, boolean excludeSelf) {
		super();
		this.layerName = layerName;
		this.layerNameEvaluator = null;
		this.excludeSelf = excludeSelf;
	}

	protected AbstractAnnotationSiblings(Evaluator layerNameEvaluator, boolean excludeSelf) {
		super();
		this.layerName = null;
		this.layerNameEvaluator = layerNameEvaluator;
		this.excludeSelf = excludeSelf;
	}
	
	private String getLayerName(EvaluationContext ctx, Element elt) {
		if (layerNameEvaluator == null)
			return layerName;
		return layerNameEvaluator.evaluateString(ctx, elt);
	}

	@Override
	public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
		return elt.accept(this, new Pair<EvaluationContext,Element>(ctx, elt));
	}

	@Override
	public List<Element> visit(Corpus corpus, Pair<EvaluationContext,Element> param) {
		return Collections.emptyList();
	}

	@Override
	public List<Element> visit(Document doc, Pair<EvaluationContext,Element> param) {
		return Collections.emptyList();
	}

	@Override
	public List<Element> visit(Relation rel, Pair<EvaluationContext,Element> param) {
		return Collections.emptyList();
	}

	@Override
	public List<Element> visit(Section sec, Pair<EvaluationContext,Element> param) {
		return Collections.emptyList();
	}

	@Override
	public List<Element> visit(Tuple t, Pair<EvaluationContext,Element> param) {
		return Collections.emptyList();
	}

	@Override
	public List<Element> visit(Element e, Pair<EvaluationContext,Element> param) {
		return Collections.emptyList();
	}

	@Override
	public List<Element> visit(Annotation a, Pair<EvaluationContext,Element> param) {
		Section sec = a.getSection();
		String layerName = getLayerName(param.first, param.second);
		if (!sec.hasLayer(layerName))
			return Collections.emptyList();
		Layer layer = getAnnotations(sec.getLayer(layerName), a);
		if (excludeSelf) {
			layer.remove(a);
		}
		return layer.asElementList();
	}
	
	protected abstract Layer getAnnotations(Layer layer, Annotation a);

	@Override
	public Collection<EvaluationType> getTypes() {
		return Collections.singleton(EvaluationType.ANNOTATIONS);
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		if (layerName != null) {
			nameUsage.addNames(NameType.LAYER, layerName);
		}
		if (layerNameEvaluator != null) {
			layerNameEvaluator.collectUsedNames(nameUsage, defaultType);
		}
	}
}
