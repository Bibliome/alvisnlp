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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisir2;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.inra.maiage.bibliome.alvisir.core.index.AlvisIRIndexedDocuments;
import fr.inra.maiage.bibliome.alvisir.core.index.AlvisIRIndexedFields;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUser;
import fr.inra.maiage.bibliome.util.Iterators;

class IndexedDocumentsEvaluator implements AlvisIRIndexedDocuments<Element,Element,Element,Element>, NameUser {
	private final Evaluator instances;
	private final List<IndexedFieldsEvaluator> indexedFields;
	
	private EvaluationContext evaluationContext;
	private Corpus corpus;

	IndexedDocumentsEvaluator(Evaluator instances, List<IndexedFieldsEvaluator> indexedFields) {
		super();
		this.instances = instances;
		this.indexedFields = indexedFields;
	}

	void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}
	
	void setEvaluationContext(EvaluationContext evaluationContext) {
		this.evaluationContext = evaluationContext;
		for (IndexedFieldsEvaluator field : indexedFields)
			field.setEvaluationContext(evaluationContext);
	}
	
	@Override
	public Iterator<Element> getDocumentInstances() {
		return instances.evaluateElements(evaluationContext, corpus);
	}

	@Override
	public Iterator<AlvisIRIndexedFields<Element,Element,Element>> getIndexedFields(Element doc) {
		for (IndexedFieldsEvaluator ife : indexedFields) {
			ife.setDocument(doc);
		}
		return Iterators.upcast(indexedFields.iterator());
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		instances.collectUsedNames(nameUsage, defaultType);
		for (IndexedFieldsEvaluator ife : indexedFields) {
			ife.collectUsedNames(nameUsage, defaultType);
		}
	}

	void setRoleNames(Map<String,Integer> roleNames) {
		for (IndexedFieldsEvaluator field : indexedFields) {
			field.setRoleNames(roleNames);
		}
	}
	
	void setPropertyKeys(Map<String,Integer> propertyKeys) {
		for (IndexedFieldsEvaluator field : indexedFields) {
			field.setPropertyKeys(propertyKeys);
		}
	}
}
