package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.util.files.OutputFile;

public class ContesTrainTermClassifier extends ContesTermClassifier<OutputFile> {
	public ContesTrainTermClassifier(Expression documentFilter, Expression sectionFilter, Double factor, String termLayerName, String conceptFeatureName, OutputFile regressionMatrixFile) {
		super(documentFilter, sectionFilter, factor, termLayerName, conceptFeatureName, null, regressionMatrixFile);
	}
}
