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

import java.util.Collection;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;
import fr.inra.mig_bibliome.alvisir.core.index.AlvisIRTokenFragments;

public class TokenFragmentsEvaluator implements AlvisIRTokenFragments<Element>, NameUser {
	private final Evaluator instances;
	private final Evaluator start;
	private final Evaluator end;
	
	private EvaluationContext evaluationContext;
	private Element token;

	TokenFragmentsEvaluator(Evaluator instances, Evaluator start, Evaluator end) {
		super();
		this.instances = instances;
		this.start = start;
		this.end = end;
	}
	
	void setToken(Element token) {
		this.token = token;
	}

	void setEvaluationContext(EvaluationContext evaluationContext) {
		this.evaluationContext = evaluationContext;
	}

	@Override
	public Collection<Element> getTokenFragments() {
		return instances.evaluateList(evaluationContext, token);
	}
	
	@Override
	public int getStart(Element fragment) {
		return start.evaluateInt(evaluationContext, fragment);
	}
	
	@Override
	public int getEnd(Element fragment) {
		return end.evaluateInt(evaluationContext, fragment);
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		instances.collectUsedNames(nameUsage, defaultType);
		start.collectUsedNames(nameUsage, defaultType);
		end.collectUsedNames(nameUsage, defaultType);
	}
}
