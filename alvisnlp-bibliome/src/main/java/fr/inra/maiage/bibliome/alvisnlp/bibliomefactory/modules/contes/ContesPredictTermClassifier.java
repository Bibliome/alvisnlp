package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.util.files.InputFile;

public class ContesPredictTermClassifier extends ContesTermClassifier<InputFile> {
	public ContesPredictTermClassifier(Expression documentFilter, Expression sectionFilter, Double factor, String termLayerName, String conceptFeatureName, String similarityFeatureName, InputFile regressionMatrixFile) {
		super(documentFilter, sectionFilter, factor, termLayerName, conceptFeatureName, similarityFeatureName, regressionMatrixFile);
	}
}
