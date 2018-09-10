package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.util.files.OutputFile;

@Converter(targetType=ContesTrainTermClassifier.class)
public class ContesTrainTermClassifierParamConverter extends AbstractContesTermClassifierParamConverter<OutputFile,ContesTrainTermClassifier> {
	@Override
	protected Class<OutputFile> getMatrixFileClass() {
		return OutputFile.class;
	}

	@Override
	protected ContesTrainTermClassifier createContesTermClassifier(Expression documentFilter, Expression sectionFilter, String termLayerName, String conceptFeatureName, OutputFile regressionMatrixFile) {
		return new ContesTrainTermClassifier(documentFilter, sectionFilter, termLayerName, conceptFeatureName, regressionMatrixFile);
	}
}
