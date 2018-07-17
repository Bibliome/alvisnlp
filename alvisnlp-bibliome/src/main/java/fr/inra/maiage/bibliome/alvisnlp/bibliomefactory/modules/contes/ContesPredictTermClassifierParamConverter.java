package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.util.files.InputFile;

@Converter(targetType=ContesPredictTermClassifier.class)
public class ContesPredictTermClassifierParamConverter extends AbstractContesTermClassifierParamConverter<InputFile,ContesPredictTermClassifier> {
	@Override
	protected Class<InputFile> getMatrixFileClass() {
		return InputFile.class;
	}

	@Override
	protected ContesPredictTermClassifier createContesTermClassifier(Expression documentFilter, Expression sectionFilter, String termLayerName, String conceptFeatureName, InputFile regressionMatrixFile) {
		return new ContesPredictTermClassifier(documentFilter, sectionFilter, termLayerName, conceptFeatureName, regressionMatrixFile);
	}
}
