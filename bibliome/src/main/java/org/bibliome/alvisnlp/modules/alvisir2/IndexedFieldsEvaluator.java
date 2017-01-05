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


package org.bibliome.alvisnlp.modules.alvisir2;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;
import fr.inra.mig_bibliome.alvisir.core.index.AlvisIRIndexedFields;
import fr.inra.mig_bibliome.alvisir.core.index.AlvisIRIndexedTokens;
import fr.inra.mig_bibliome.alvisir.core.index.NormalizationOptions;

public class IndexedFieldsEvaluator implements AlvisIRIndexedFields<Element,Element,Element>, NameUser {
	private final Evaluator instances;
	private final Evaluator fieldName;
	private final Evaluator fieldValue;
	private final IndexedTokensEvaluator indexedTokens;
	private final List<IndexedTokensEvaluator> indexedAnnotations;
	private final NormalizationOptions normalizationOptions;

	private EvaluationContext evaluationContext;
	private Element doc;

	IndexedFieldsEvaluator(Evaluator instances, Evaluator fieldName, Evaluator fieldValue, IndexedTokensEvaluator indexedTokens, List<IndexedTokensEvaluator> indexedAnnotations, NormalizationOptions normalizationOptions) {
		super();
		this.instances = instances;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.indexedTokens = indexedTokens;
		this.indexedAnnotations = indexedAnnotations;
		this.normalizationOptions = normalizationOptions;
	}

	void setDocument(Element doc) {
		this.doc = doc;
	}

	void setEvaluationContext(EvaluationContext evaluationContext) {
		this.evaluationContext = evaluationContext;
		indexedTokens.setEvaluationContext(evaluationContext);
		for (IndexedTokensEvaluator ann : indexedAnnotations)
			ann.setEvaluationContext(evaluationContext);
	}

	@Override
	public Iterator<Element> getFieldInstances() {
		return instances.evaluateElements(evaluationContext, doc);
	}

	@Override
	public String getFieldName(Element field) {
		return fieldName.evaluateString(evaluationContext, field);
	}

	@Override
	public String getFieldValue(Element field) {
		return fieldValue.evaluateString(evaluationContext, field);
	}

	@Override
	public AlvisIRIndexedTokens<Element,Element> getIndexedTokens(Element field) {
		indexedTokens.setField(field);
		return indexedTokens;
	}

	@Override
	public List<AlvisIRIndexedTokens<Element,Element>> getIndexedAnnotations(Element field) {
		for (IndexedTokensEvaluator ia : indexedAnnotations) {
			ia.setField(field);
		}
		return Collections.unmodifiableList((List<? extends AlvisIRIndexedTokens<Element,Element>>) indexedAnnotations);
	}

	@Override
	public NormalizationOptions getNormalizationOptions() {
		return normalizationOptions;
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		instances.collectUsedNames(nameUsage, defaultType);
		fieldName.collectUsedNames(nameUsage, defaultType);
		fieldValue.collectUsedNames(nameUsage, defaultType);
		indexedTokens.collectUsedNames(nameUsage, defaultType);
		for (IndexedTokensEvaluator ite : indexedAnnotations) {
			ite.collectUsedNames(nameUsage, defaultType);
		}
	}

	void setRoleNames(Map<String,Integer> roleNames) {
		indexedTokens.setRoleNames(roleNames);
		for (IndexedTokensEvaluator ann : indexedAnnotations) {
			ann.setRoleNames(roleNames);
		}
	}
	
	void setPropertyKeys(Map<String,Integer> propertyKeys) {
		indexedTokens.setPropertyKeys(propertyKeys);
		for (IndexedTokensEvaluator ann : indexedAnnotations) {
			ann.setPropertyKeys(propertyKeys);
		}
	}
}
