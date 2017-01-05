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


package org.bibliome.alvisnlp.modules.classifiers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.bibliome.util.Checkable;
import org.bibliome.util.streams.SourceStream;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.Resolvable;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;

class BagDefinition implements Resolvable<BagDefinition>, Checkable, NameUser {
	private final String prefix;
	private final String featureKey;
	private final boolean count;
	private final Expression expression;
	private final Evaluator resolvedExpression;
	private final Map<String,Integer> attributeIndexes = new TreeMap<String,Integer>();
	private SourceStream valuesFile;
	
	BagDefinition(String prefix, String featureKey, boolean count, Expression expression, Evaluator resolvedExpression) {
		super();
		this.prefix = prefix;
		this.featureKey = featureKey;
		this.count = count;
		this.expression = expression;
		this.resolvedExpression = resolvedExpression;
	}

	Collection<String> getValueSpace() {
		return Collections.unmodifiableCollection(attributeIndexes.keySet());
	}
	
	void setValuesFile(SourceStream valuesFile) {
		this.valuesFile = valuesFile;
	}

	void addValue(String value) {
		attributeIndexes.put(value, null);
	}
	
	String getPrefix() {
		return prefix;
	}

	void setAttributeIndex(String value, int index) {
		attributeIndexes.put(value, index);
	}
	
	Integer getAttributeIndex(String value) {
		return attributeIndexes.get(value);
	}
	
	Iterator<Element> getBag(EvaluationContext ctx, Element example) {
		return resolvedExpression.evaluateElements(ctx, example);
	}

	String getFeatureKey() {
		return featureKey;
	}
	
	boolean isCount() {
		return count;
	}
	
	void init() throws IOException {
		if (valuesFile == null)
			return;
		try (BufferedReader r = valuesFile.getBufferedReader()) {
			while (true) {
				String value = r.readLine();
				if (value == null)
					break;
				addValue(value);
			}
		}
	}

	@Override
	public BagDefinition resolveExpressions(LibraryResolver resolver) throws ResolverException {
		BagDefinition result = new BagDefinition(prefix, featureKey, count, expression, expression.resolveExpressions(resolver));
		result.setValuesFile(valuesFile);
		return result;
	}

	@Override
	public boolean check(Logger logger) {
		return valuesFile.check(logger);
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		nameUsage.addNames(NameType.FEATURE, featureKey);
		if (resolvedExpression != null) {
			resolvedExpression.collectUsedNames(nameUsage, defaultType);
		}
	}
}
