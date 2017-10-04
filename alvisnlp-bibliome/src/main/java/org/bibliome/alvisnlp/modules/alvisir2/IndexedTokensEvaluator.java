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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;
import fr.inra.mig_bibliome.alvisir.core.index.AlvisIRIndexedTokens;
import fr.inra.mig_bibliome.alvisir.core.index.AlvisIRTokenFragments;

public class IndexedTokensEvaluator implements AlvisIRIndexedTokens<Element,Element>, NameUser {
	private final Evaluator instances;
	private final Evaluator text;
	private final TokenFragmentsEvaluator tokenFragments;
	private final Evaluator identifier;
	private Map<String,Integer> roleNames;
	private Map<String,Integer> propertyKeys;
	private final Map<String,Evaluator> arguments;
	private final Map<String,Evaluator> properties;

	private EvaluationContext evaluationContext;
	private Element field;

	IndexedTokensEvaluator(Evaluator instances, Evaluator text, TokenFragmentsEvaluator tokenFragments, Evaluator identifier, Map<String,Evaluator> arguments, Map<String,Evaluator> properties) {
		super();
		this.instances = instances;
		this.text = text;
		this.tokenFragments = tokenFragments;
		this.identifier = identifier;
		this.arguments = arguments;
		this.properties = properties;
	}

	void setField(Element field) {
		this.field = field;
	}
	
	void setEvaluationContext(EvaluationContext evaluationContext) {
		this.evaluationContext = evaluationContext;
		tokenFragments.setEvaluationContext(evaluationContext);
	}

	@Override
	public Iterator<Element> getTokenInstances() {
		return instances.evaluateElements(evaluationContext, field);
	}
	
	@Override
	public AlvisIRTokenFragments<Element> getTokenFragments(Element token) {
		tokenFragments.setToken(token);
		return tokenFragments;
	}
	
	@Override
	public String getTokenText(Element token) {
		return text.evaluateString(evaluationContext, token);
	}

	@Override
	public int getTokenIdentifier(Element token) {
		return identifier.evaluateInt(evaluationContext, token);
	}

	@Override
	public Map<Integer,Integer> getRelationArguments(Element token) {
		Map<Integer,Integer> result = new LinkedHashMap<Integer,Integer>();
		for (Map.Entry<String,Evaluator> e : arguments.entrySet()) {
			String role = e.getKey();
			Evaluator argEvaluator = e.getValue();
			if (!roleNames.containsKey(role)) {
				throw new RuntimeException("unknown role: " + role);
			}
			int roleId = roleNames.get(role);
			int argId = argEvaluator.evaluateInt(evaluationContext, token);
			result.put(roleId, argId);
		}
		return result;
	}

	@Override
	public Map<Integer,String> getProperties(Element token) {
		Map<Integer,String> result = new LinkedHashMap<Integer,String>();
		for (Map.Entry<String,Evaluator> e : properties.entrySet()) {
			String key = e.getKey();
			Evaluator eval = e.getValue();
			if (!propertyKeys.containsKey(key)) {
				throw new RuntimeException("unknown property: " + key);
			}
			int keyId = propertyKeys.get(key);
			String value = eval.evaluateString(evaluationContext, token);
			result.put(keyId, value);
		}
		return result;
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		instances.collectUsedNames(nameUsage, defaultType);
		text.collectUsedNames(nameUsage, defaultType);
		tokenFragments.collectUsedNames(nameUsage, defaultType);
		identifier.collectUsedNames(nameUsage, defaultType);
		for (Evaluator e : arguments.values()) {
			e.collectUsedNames(nameUsage, defaultType);
		}
		for (Evaluator e : properties.values()) {
			e.collectUsedNames(nameUsage, defaultType);
		}
	}

	void setRoleNames(Map<String,Integer> roleNames) {
		this.roleNames = roleNames;
	}
	
	void setPropertyKeys(Map<String,Integer> propertyKeys) {
		this.propertyKeys = propertyKeys;
	}
}
