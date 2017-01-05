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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.bibliome.util.Iterators;
import org.bibliome.util.streams.TargetStream;

import weka.classifiers.Classifier;
import weka.core.Instance;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

@AlvisNLPModule
public class TaggingElementClassifier extends PredictionElementClassifier {
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ProcessingException {
		try {
			Classifier classifier = loadClassifier(ctx);
			ElementClassifierResolvedObjects resObj = getResolvedObjects();
			IdentifiedInstances<Element> devSet = resObj.getRelationDefinition().createInstances();
			predictExamples(ctx, classifier, devSet, corpus);
		}
		catch (IOException ioe) {
			rethrow(ioe);
		}
		catch (ClassNotFoundException cnfe) {
			rethrow(cnfe);
		}
		catch (Exception e) {
			rethrow(e);
		}
	}

	@TimeThis(task="prediction")
	protected void predictExamples(ProcessingContext<Corpus> ctx, Classifier classifier, IdentifiedInstances<Element> devSet, Corpus corpus) throws Exception {
		ElementClassifierResolvedObjects resObj = getResolvedObjects();
		RelationDefinition relationDefinition = resObj.getRelationDefinition();
		Evaluator examples = resObj.getExamples();
		String predictedClassFeatureKey = getPredictedClassFeatureKey();
		TargetStream evaluationFile = getEvaluationFile();
		boolean withId = evaluationFile != null;
		String[] classes = getClasses(devSet);
		getLogger(ctx).info("predicting class for each example");
        EvaluationContext evalCtx = new EvaluationContext(getLogger(ctx));
		for (Element example : Iterators.loop(getExamples(corpus, examples, evalCtx))) {
			Instance inst = relationDefinition.addExample(devSet, evalCtx, example, withId, withId);
			double prediction = classifier.classifyInstance(inst);
			example.addFeature(predictedClassFeatureKey, classes[(int) prediction]);
			if (!withId)
				devSet.delete();
		}
	}

	@TimeThis(task="load-classifier", category=TimerCategory.LOAD_RESOURCE)
	protected Classifier loadClassifier(ProcessingContext<Corpus> ctx) throws IOException, ClassNotFoundException {
		File classifierFile = getClassifierFile();
        getLogger(ctx).info("reading classifier from " + classifierFile.getCanonicalPath());
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(classifierFile));
        Classifier result = (Classifier)ois.readObject();
        ois.close();
        return result;
	}

	@Override
	@Param(nameType=NameType.FEATURE)
	public String getPredictedClassFeatureKey() {
		return super.getPredictedClassFeatureKey();
	}

	@Override
	@Param
	public File getClassifierFile() {
		return super.getClassifierFile();
	}
}
