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

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.alvisnlp.modules.classifiers.ElementClassifier.ElementClassifierResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

public abstract class ElementClassifier extends CorpusModule<ElementClassifierResolvedObjects> {
	private RelationDefinition relationDefinition;
	private Expression examples;
	private TargetStream evaluationFile;

	static class ElementClassifierResolvedObjects extends ResolvedObjects {
		private final RelationDefinition relationDefinition;
		private final Evaluator examples;
		
		private ElementClassifierResolvedObjects(ProcessingContext<Corpus> ctx, ElementClassifier module) throws ResolverException, IOException {
			super(ctx, module);
			examples = module.examples.resolveExpressions(rootResolver);
			relationDefinition = rootResolver.resolveNullable(module.relationDefinition);
			relationDefinition.initializeBags();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			relationDefinition.collectUsedNames(nameUsage, defaultType);
			examples.collectUsedNames(nameUsage, defaultType);
		}

		RelationDefinition getRelationDefinition() {
			return relationDefinition;
		}

		Evaluator getExamples() {
			return examples;
		}
	}

	@Override
	protected ElementClassifierResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		try {
			return new ElementClassifierResolvedObjects(ctx, this);
		}
		catch (IOException e) {
			throw new ResolverException(e);
		}
	}

	@TimeThis(task="create-training-set", category=TimerCategory.PREPARE_DATA)
	protected IdentifiedInstances<Element> getTrainingSet(ProcessingContext<Corpus> ctx, Corpus corpus, EvaluationContext evalCtx, boolean withId) throws IOException {
		ElementClassifierResolvedObjects resObj = getResolvedObjects();
		RelationDefinition relationDefinition = resObj.getRelationDefinition();
		Evaluator examples = resObj.getExamples();
		IdentifiedInstances<Element> result = relationDefinition.createInstances();
		Logger logger = getLogger(ctx);
    	logger.info("computing training set");
		for (Element example : Iterators.loop(getExamples(corpus, examples, evalCtx)))
			relationDefinition.addExample(result, evalCtx, example, true, withId);
		logger.info("# attributes = " + result.numAttributes() + ", # instances = " + result.numInstances());
		return result;
	}

	static Iterator<Element> getExamples(Corpus corpus, Evaluator resolvedExamples, EvaluationContext ctx) {
		return resolvedExamples.evaluateElements(ctx, corpus);
	}
	
	@Deprecated
	@Param
	public RelationDefinition getRelationDefinition() {
		return relationDefinition;
	}
	
	@Param
	public Expression getExamples() {
		return examples;
	}

	@Param(mandatory=false)
	public TargetStream getEvaluationFile() {
		return evaluationFile;
	}
	
	public void setRelationDefinition(RelationDefinition relationDefinition) {
		this.relationDefinition = relationDefinition;
	}
	
	public void setExamples(Expression examples) {
		this.examples = examples;
	}
	
	public void setEvaluationFile(TargetStream evaluationFile) {
		this.evaluationFile = evaluationFile;
	}
}
