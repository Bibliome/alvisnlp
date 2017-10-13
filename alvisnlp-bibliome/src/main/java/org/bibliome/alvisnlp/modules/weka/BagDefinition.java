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


package org.bibliome.alvisnlp.modules.weka;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Resolvable;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUser;
import fr.inra.maiage.bibliome.util.Checkable;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

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
