package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.opennlp;

import opennlp.tools.ml.maxent.GISTrainer;
import opennlp.tools.ml.maxent.quasinewton.QNTrainer;
import opennlp.tools.ml.naivebayes.NaiveBayesTrainer;
import opennlp.tools.ml.perceptron.PerceptronTrainer;

public enum OpenNLPAlgorithm {
	NAIVE_BAYES(NaiveBayesTrainer.NAIVE_BAYES_VALUE),
	GENERALIZED_ITERATIVE_SCALING(GISTrainer.MAXENT_VALUE),
	PERCEPTRON(PerceptronTrainer.PERCEPTRON_VALUE),
	QUASI_NEWTON(QNTrainer.MAXENT_QN_VALUE);
	
	public final String paramValue;
	
	private OpenNLPAlgorithm(String paramValue) {
		this.paramValue = paramValue;
	}
}
