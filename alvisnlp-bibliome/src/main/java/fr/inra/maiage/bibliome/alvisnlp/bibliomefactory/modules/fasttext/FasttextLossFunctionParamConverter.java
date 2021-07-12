package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.fasttext;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.SimpleParamConverter;

@Converter(targetType = FasttextLossFunction.class)
public class FasttextLossFunctionParamConverter extends SimpleParamConverter<FasttextLossFunction> {
	@Override
	public String[] getAlternateAttributes() {
		return new String[] { "loss", "loss-function" };
	}

	@Override
	protected FasttextLossFunction convertTrimmed(String stringValue) throws ConverterException {
		switch (stringValue) {
			case "softmax":
				return FasttextLossFunction.SOFTMAX;
			case "skipgram-negative-sampling":
			case "negative-sampling":
			case "ns":
				return FasttextLossFunction.SKIPGRAM_NEGATICE_SAMPLING;
			case "skipgram-hierarchical-softmax":
			case "hierarchical-softmax":
			case "hs":
				return FasttextLossFunction.SKIPGRAM_HIERARCHICAL_SOFTMAX;
			default:
				cannotConvertString(stringValue, "unknown loss function: " + stringValue);
				return null;
		}
	}

}
