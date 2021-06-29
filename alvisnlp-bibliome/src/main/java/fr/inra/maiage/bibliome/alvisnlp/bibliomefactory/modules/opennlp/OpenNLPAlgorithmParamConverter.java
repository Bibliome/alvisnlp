package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.opennlp;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.SimpleParamConverter;

@Converter(targetType=OpenNLPAlgorithm.class)
public class OpenNLPAlgorithmParamConverter extends SimpleParamConverter<OpenNLPAlgorithm> {
	@Override
	public String[] getAlternateAttributes() {
		return null;
	}

	@Override
	protected OpenNLPAlgorithm convertTrimmed(String stringValue) throws ConverterException {
		switch (stringValue.toLowerCase()) {
			case "naive-bayes":
			case "nb":
				return OpenNLPAlgorithm.NAIVE_BAYES;
			case "generalized-iterative-scaling":
			case "gis":
				return OpenNLPAlgorithm.GENERALIZED_ITERATIVE_SCALING;
			case "perceptron":
				return OpenNLPAlgorithm.PERCEPTRON;
			case "quasi-newton":
			case "qn":
			case "l-bfgs":
			case "lbfgs":
			case "bfgs":
				return OpenNLPAlgorithm.QUASI_NEWTON;
		}
		cannotConvertString(stringValue, "unknown algorithm");
		return null;
	}
}
