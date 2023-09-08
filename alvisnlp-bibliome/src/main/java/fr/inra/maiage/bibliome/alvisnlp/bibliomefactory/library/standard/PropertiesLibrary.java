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

import java.util.Iterator;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DownCastElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ElementVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.AbstractStringEvaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Function;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.FunctionLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Library;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.StringCat;

@Library("properties")
public abstract class PropertiesLibrary extends FunctionLibrary {
	public static final String NAME = "properties";
	
	@Function
	public static final int start(EvaluationContext ctx, Element elt) {
		Annotation a = DownCastElement.toAnnotation(elt);
		if (a == null)
			return 0;
		return a.getStart();
	}
	
	@Function
	public static final int end(EvaluationContext ctx, Element elt) {
		Annotation a = DownCastElement.toAnnotation(elt);
		if (a == null)
			return 0;
		return a.getEnd();
	}
	
	@Function
	public static final int length(EvaluationContext ctx, Element elt) {
		return elt.accept(LENGTH_VISITOR, null);
	}
	
	private static final ElementVisitor<Integer,Void> LENGTH_VISITOR = new ElementVisitor<Integer,Void>() {
		@Override
		public Integer visit(Annotation a, Void param) {
			return a.getLength();
		}

		@Override
		public Integer visit(Corpus corpus, Void param) {
			return 0;
		}

		@Override
		public Integer visit(Document doc, Void param) {
			return 0;
		}

		@Override
		public Integer visit(Relation rel, Void param) {
			return 0;
		}

		@Override
		public Integer visit(Section sec, Void param) {
			return sec.getContents().length();
		}

		@Override
		public Integer visit(Tuple t, Void param) {
			return 0;
		}

		@Override
		public Integer visit(Element e, Void param) {
			return 0;
		}
	};
	
	@Function
	public static final String contents(EvaluationContext ctx, Element elt) {
		Section sec = DownCastElement.toSection(elt);
		if (sec == null)
			return "";
		return sec.getContents();
	}
	
	@Function
	public static String last(EvaluationContext ctx, Element elt, Evaluator key) {
		String featureKey = key.evaluateString(ctx, elt);
		if (elt.hasFeature(featureKey)) {
			return elt.getLastFeature(featureKey);
		}
		return "";
	}
	
	@Function(firstFtor="@", ftors=1)
	public static final Evaluator last(String key) {
		return new LastFeatureEvaluator(key);
	}

	private static final class LastFeatureEvaluator extends AbstractStringEvaluator {
		private final String key;
		
		private LastFeatureEvaluator(String key) {
			super();
			this.key = key;
		}

		@Override
		public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
			return elt.hasFeature(key);
		}

		@Override
		public String evaluateString(EvaluationContext ctx, Element elt) {
			if (elt.hasFeature(key))
				return elt.getLastFeature(key);
			return "";
		}

		@Override
		public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
			if (elt.hasFeature(key))
				strcat.append(elt.getLastFeature(key));
		}

		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			nameUsage.addNames(NameType.FEATURE, key);
		}
	}
	
	@Function
	public static final int order(EvaluationContext ctx, Element elt) {
		Section sec = DownCastElement.toSection(elt);
		if (sec == null) {
			return 0;
		}
		return sec.getOrder();
	}
}
