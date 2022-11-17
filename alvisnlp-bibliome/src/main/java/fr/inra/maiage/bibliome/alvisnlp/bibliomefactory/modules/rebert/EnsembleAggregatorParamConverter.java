package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.ClosedValueSetParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.util.service.UnsupportedServiceException;

@Converter(targetType = EnsembleAggregator.class)
public class EnsembleAggregatorParamConverter extends ClosedValueSetParamConverter<EnsembleAggregator> {
	public EnsembleAggregatorParamConverter() throws ConverterException, UnsupportedServiceException {
		super();
	}

	@Override
	public EnsembleAggregator[] allowedValues() {
		return EnsembleAggregator.values();
	}

	@Override
	public EnsembleAggregator fallBack(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "unsupported aggregator: " + stringValue);
		return null;
	}

	@Override
	public String[] getAlternateAttributes() {
		return null;
	}

}
