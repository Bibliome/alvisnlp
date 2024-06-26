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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Random;
import java.util.logging.Logger;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

@AlvisNLPModule
public class WekaTrain extends PredictionElementClassifier {
	private String algorithm;
	private String[] classifierOptions;
	private Integer crossFolds;
	private Long randomSeed = 1L;
	private String foldFeature;
	private TargetStream classifierInfoFile;
	private TargetStream arffFile;

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ProcessingException {
		try {
	    	Logger logger = getLogger(ctx);
	        EvaluationContext evalCtx = new EvaluationContext(logger);
			Classifier classifier = createClassifier();
			boolean withId = (foldFeature != null) || (getPredictedClassFeatureKey() != null);
			IdentifiedInstances<Element> trainingSet = getTrainingSet(ctx, corpus, evalCtx, withId);
			writeArff(ctx, trainingSet);
			trainClassifier(ctx, classifier, trainingSet);
			writeClassifier(ctx, classifier);
			writeClassifierInfo(ctx, classifier);
			crossValidate(ctx, classifier, trainingSet);
		}
		catch (Exception e) {
			throw new ProcessingException(e);
		}
	}

	@TimeThis(task="train", category=TimerCategory.EXTERNAL)
	protected void trainClassifier(ProcessingContext ctx, Classifier classifier, IdentifiedInstances<Element> trainingSet) throws Exception {
		getLogger(ctx).info("training classifier");
		classifier.buildClassifier(trainingSet);
	}

	@TimeThis(task="write-arff", category=TimerCategory.EXPORT)
	protected void writeArff(ProcessingContext ctx, Instances instances) throws IOException {
		if (arffFile == null)
			return;
    	getLogger(ctx).info("writing training set into " + arffFile.getName());
    	Writer out = arffFile.getWriter();
    	out.write(instances.toString());
    	out.close();
	}

	@TimeThis(task="save-classifier", category=TimerCategory.EXPORT)
	protected void writeClassifier(ProcessingContext ctx, Classifier classifier) throws IOException {
        getLogger(ctx).info("writing classifier");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getClassifierFile()));
        oos.writeObject(classifier);
        oos.flush();
        oos.close();
	}

	@TimeThis(task="classifier-info", category=TimerCategory.EXPORT)
	protected void writeClassifierInfo(ProcessingContext ctx, Classifier classifier) throws IOException {
		if (classifierInfoFile == null)
			return;
    	getLogger(ctx).info("writing classifier info to " + classifierInfoFile.getName());
    	Writer out = classifierInfoFile.getWriter();
    	out.write(classifier.toString());
    	out.close();
	}

	private Classifier createClassifier() throws Exception {
    	Classifier baseClassifier = Classifier.forName(getAlgorithm(), getClassifierOptions());
    	FilteredClassifier result = new FilteredClassifier();
    	result.setClassifier(baseClassifier);
    	Remove filter = new Remove();
    	filter.setAttributeIndicesArray(new int[] { 0 });
    	result.setFilter(filter);
    	return result;
	}

	private void crossValidate(ProcessingContext ctx, Classifier classifier, IdentifiedInstances<Element> instances) throws Exception {
		TargetStream evaluationFile = getEvaluationFile();
    	String foldFeatureKey = getFoldFeatureKey();
    	String predictedClassFeatureKey = getPredictedClassFeatureKey();
    	boolean withId = (foldFeatureKey != null) || (predictedClassFeatureKey != null);
		if ((evaluationFile == null) && !withId)
			return;
    	String[] classes = getClasses(instances);
    	Evaluation evaluation = crossValidate(ctx, classifier, instances, classes, withId);
    	writeCrossValidationResults(ctx, evaluationFile, evaluation, classes);
	}

	@TimeThis(task="cross-validation")
	protected Evaluation crossValidate(ProcessingContext ctx, Classifier classifier, IdentifiedInstances<Element> instances, String[] classes, boolean withId) throws Exception {
		Logger logger = getLogger(ctx);
        logger.info("cross-validating " + crossFolds + " folds");
    	Random random = new Random(getRandomSeed());
    	instances.randomize(random);
    	instances.stratify(crossFolds);
    	Evaluation evaluation = new Evaluation(instances);
		Integer requiredCrossFolds = getCrossFolds();
		int crossFolds = requiredCrossFolds == null ? instances.numInstances() : requiredCrossFolds;
    	for (int i = 0; i < crossFolds; i++) {
    		logger.fine("fold " + (i+1));
    		Instances crossTrainingSet = instances.trainCV(crossFolds, i, random);
    		evaluation.setPriors(crossTrainingSet);
    		classifier.buildClassifier(crossTrainingSet);
    		Instances crossTestSet = instances.testCV(crossFolds, i);
    		double[] predictions = evaluation.evaluateModel(classifier, crossTestSet);
    		if (withId) {
    			String s = Integer.toString(i);
    			for (int j = 0; j < predictions.length; ++j) {
    				int predictedClass = (int)predictions[j];
    				int elementId = (int) crossTestSet.instance(j).value(0);
    				Element elt = instances.getElement(elementId);
    				elt.addFeature(foldFeature, s);
    				elt.addFeature(getPredictedClassFeatureKey(), classes[predictedClass]);
    			}
    		}
    	}
    	return evaluation;
	}

	@TimeThis(task="write-results", category=TimerCategory.EXPORT)
	protected void writeCrossValidationResults(ProcessingContext ctx, TargetStream evaluationFile, Evaluation evaluation, String[] classes) throws Exception {
		Logger logger = getLogger(ctx);
        logger.info("writing test results into " + evaluationFile.getName());
        try (PrintStream out = evaluationFile.getPrintStream()) {
        	for (int i = 0; i < classes.length; ++i) {
        		out.printf("Results for class %d (%s):\n", i, classes[i]);
        		out.printf("  True positives : %8.0f\n", evaluation.numTruePositives(i));
        		out.printf("  False positives: %8.0f\n", evaluation.numFalsePositives(i));
        		out.printf("  True negatives : %8.0f\n", evaluation.numTrueNegatives(i));
        		out.printf("  False negatives: %8.0f\n", evaluation.numFalseNegatives(i));
        		out.printf("  Recall:    %6.4f\n", evaluation.recall(i));
        		out.printf("  Precision: %6.4f\n", evaluation.precision(i));
        		out.printf("  F-Measure: %6.4f\n", evaluation.fMeasure(i));
        		out.println();
        	}
        	out.println(evaluation.toMatrixString("Confusion matrix:"));
        }
	}

	@Deprecated
	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String getPredictedClassFeatureKey() {
		return super.getPredictedClassFeature();
	}

	@Override
	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String getPredictedClassFeature() {
		return super.getPredictedClassFeature();
	}

	@Override
	@Param
	public File getClassifierFile() {
		return super.getClassifierFile();
	}

	@Param
	public String getAlgorithm() {
		return algorithm;
	}

	@Param(mandatory=false)
	public String[] getClassifierOptions() {
		return classifierOptions;
	}

	@Param(mandatory=false)
	public Integer getCrossFolds() {
		return crossFolds;
	}

	@Param
	public Long getRandomSeed() {
		return randomSeed;
	}

	@Deprecated
	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String getFoldFeatureKey() {
		return foldFeature;
	}

	@Param(mandatory=false)
	public TargetStream getClassifierInfoFile() {
		return classifierInfoFile;
	}

	@Param(mandatory=false)
	public TargetStream getArffFile() {
		return arffFile;
	}

	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String getFoldFeature() {
		return foldFeature;
	}

	public void setFoldFeature(String foldFeature) {
		this.foldFeature = foldFeature;
	}

	public void setPredictedClassFeatureKey(String predictedClassFeature) {
		super.setPredictedClassFeature(predictedClassFeature);
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public void setClassifierOptions(String[] classifierOptions) {
		this.classifierOptions = classifierOptions;
	}

	public void setCrossFolds(Integer crossFolds) {
		this.crossFolds = crossFolds;
	}

	public void setRandomSeed(Long randomSeed) {
		this.randomSeed = randomSeed;
	}

	public void setFoldFeatureKey(String foldFeatureKey) {
		this.foldFeature = foldFeatureKey;
	}

	public void setClassifierInfoFile(TargetStream classifierInfoFile) {
		this.classifierInfoFile = classifierInfoFile;
	}

	public void setArffFile(TargetStream arffFile) {
		this.arffFile = arffFile;
	}
}
