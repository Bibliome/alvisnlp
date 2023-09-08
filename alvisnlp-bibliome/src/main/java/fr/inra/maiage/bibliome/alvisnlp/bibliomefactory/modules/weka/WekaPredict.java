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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.weka;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import weka.classifiers.Classifier;
import weka.core.Instance;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

@AlvisNLPModule
public class WekaPredict extends PredictionElementClassifier {
	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ProcessingException {
		try {
			Classifier classifier = loadClassifier(ctx);
			ElementClassifierResolvedObjects resObj = getResolvedObjects();
			IdentifiedInstances<Element> devSet = resObj.getRelationDefinition().createInstances();
			predictExamples(ctx, classifier, devSet, corpus);
		}
		catch (Exception e) {
			throw new ProcessingException(e);
		}
	}

	@TimeThis(task="prediction")
	protected void predictExamples(ProcessingContext ctx, Classifier classifier, IdentifiedInstances<Element> devSet, Corpus corpus) throws Exception {
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
	protected Classifier loadClassifier(ProcessingContext ctx) throws IOException, ClassNotFoundException {
		File classifierFile = getClassifierFile();
        getLogger(ctx).info("reading classifier from " + classifierFile.getCanonicalPath());
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(classifierFile));
        Classifier result = (Classifier)ois.readObject();
        ois.close();
        return result;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getPredictedClassFeatureKey() {
		return super.getPredictedClassFeature();
	}

	public void setPredictedClassFeatureKey(String predictedClassFeature) {
		super.setPredictedClassFeature(predictedClassFeature);
	}

	@Override
	@Param(nameType=NameType.FEATURE)
	public String getPredictedClassFeature() {
		return super.getPredictedClassFeature();
	}

	@Override
	@Param
	public File getClassifierFile() {
		return super.getClassifierFile();
	}
}
