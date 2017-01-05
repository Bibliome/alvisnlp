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


package org.bibliome.alvisnlp.library;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.ElementType;
import alvisnlp.corpus.ElementVisitor;
import alvisnlp.corpus.expressions.AbstractIteratorEvaluator;
import alvisnlp.corpus.expressions.ConstantsLibrary;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

@Library("enum")
public abstract class EnumLibrary extends FunctionLibrary {
	public static final String NAME = "enum";

	private static class EnumElement implements Element {
		private static final String NUM_FEATURE_KEY = "n";
		
		private final Element element;
		private final String num;
		
		private EnumElement(Element element, String num) {
			super();
			this.element = element;
			this.num = num;
		}

		@Override
		public String getStaticFeatureValue() {
			return element.getStaticFeatureValue();
		}

		@Override
		public boolean hasFeature(String key) {
			return element.hasFeature(key) || NUM_FEATURE_KEY.equals(key);
		}

		@Override
		public Set<String> getFeatureKeys() {
			Set<String> result = new HashSet<String>(element.getFeatureKeys());
			result.add(NUM_FEATURE_KEY);
			return result;
		}

		@Override
		public List<String> getFeature(String key) {
			if (NUM_FEATURE_KEY.equals(key)) {
				return Collections.singletonList(num);
			}
			return element.getFeature(key);
		}

		@Override
		public boolean removeFeature(String key, String value) {
			if (NUM_FEATURE_KEY.equals(key))
				return false;
			return element.removeFeature(key, value);
		}

		@Override
		public List<String> removeFeatures(String key) {
			if (NUM_FEATURE_KEY.equals(key))
				return Collections.emptyList();
			return element.removeFeatures(key);
		}

		@Override
		public String getFirstFeature(String key) {
			if (NUM_FEATURE_KEY.equals(key)) {
				return num;
			}
			return element.getFirstFeature(key);
		}

		@Override
		public String getLastFeature(String key) {
			if (NUM_FEATURE_KEY.equals(key)) {
				return num;
			}
			return element.getLastFeature(key);
		}

		@Override
		public void addFeature(String key, String value) {
			element.addFeature(key, value);
		}

		@Override
		public void addFeatures(Map<String,String> mapping) {
			element.addFeatures(mapping);
		}

		@Override
		public void addMultiFeatures(Map<String,List<String>> mapping) {
			element.addMultiFeatures(mapping);
		}

		@Override
		public void featuresToXML(PrintStream out, String tag, String name, String value) throws IOException {
			element.featuresToXML(out, tag, name, value);
		}

		@Override
		public void write(PrintStream out, String s) throws IOException {
			element.write(out, s);
		}

		@Override
		public boolean isFeatureless() {
			return element.isFeatureless();
		}

		@Override
		public <R,P> R accept(ElementVisitor<R,P> visitor, P param) {
			return element.accept(visitor, param);
		}

		@Override
		public String getStringId() {
			return element.getStringId();
		}

		@Override
		public Map<String,List<String>> getFeatures() {
			Map<String,List<String>> result = new HashMap<String,List<String>>(element.getFeatures());
			result.put(NUM_FEATURE_KEY, Collections.singletonList(num));
			return result;
		}

		@Override
		public ElementType getType() {
			return element.getType();
		}

		@Override
		public Element getParent() {
			return element;
		}

		@Override
		public Element getOriginal() {
			return element.getOriginal();
		}

		@Override
		public String getStaticFeatureKey() {
			return element.getStaticFeatureKey();
		}

		@Override
		public boolean isStaticFeatureKey(String key) {
			return element.isStaticFeatureKey(key);
		}
	}
	
	private static class EnumIterator implements Iterator<Element> {
		private final Iterator<Element> matrix;
		private int num;

		private EnumIterator(Iterator<Element> matrix, int num) {
			super();
			this.matrix = matrix;
			this.num = num;
		}

		@Override
		public boolean hasNext() {
			return matrix.hasNext();
		}

		@Override
		public Element next() {
			Element elt = matrix.next();
			EnumElement result = new EnumElement(elt, Integer.toString(num));
			num++;
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static class EnumEvaluator extends AbstractIteratorEvaluator {
		private final Evaluator elements;
		private final Evaluator start;
		
		private EnumEvaluator(Evaluator elements, Evaluator start) {
			super();
			this.elements = elements;
			this.start = start;
		}

		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			Iterator<Element> matrix = this.elements.evaluateElements(ctx, elt);
			int start = this.start.evaluateInt(ctx, elt);
			return new EnumIterator(matrix, start);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			elements.collectUsedNames(nameUsage, defaultType);
			start.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	
	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		if (ftors.isEmpty()) {
			int arity = args.size();
			if (arity == 1) {
				Evaluator elements = args.get(0).resolveExpressions(resolver);
				Evaluator one = ConstantsLibrary.getInstance(1);
				return new EnumEvaluator(elements, one);
			}
			if (arity == 2) {
				Evaluator elements = args.get(0).resolveExpressions(resolver);
				Evaluator start = args.get(1).resolveExpressions(resolver);
				return new EnumEvaluator(elements, start);
			}
		}
		return cannotResolve(ftors, args);
	}
}
