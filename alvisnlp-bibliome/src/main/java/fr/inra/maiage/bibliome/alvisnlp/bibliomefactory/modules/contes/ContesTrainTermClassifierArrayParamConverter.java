package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.ArrayParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;

@Converter(targetType=ContesPredictTermClassifier[].class)
public class ContesTrainTermClassifierArrayParamConverter extends ArrayParamConverter<ContesPredictTermClassifier>{
	@Override
	protected String[] getAlternateElementTags() {
		return new String[] { "classifier" , "termClassifier" , "term-classifier" };
	}
}
