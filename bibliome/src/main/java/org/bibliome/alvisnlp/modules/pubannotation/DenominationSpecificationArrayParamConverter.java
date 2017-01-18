package org.bibliome.alvisnlp.modules.pubannotation;

import alvisnlp.converters.lib.ArrayParamConverter;
import alvisnlp.converters.lib.Converter;

@Converter(targetType=DenominationSpecification[].class)
public class DenominationSpecificationArrayParamConverter extends ArrayParamConverter<DenominationSpecification> {
	@Override
	protected String[] getAlternateElementTags() {
		return new String[] { "denominations", "denom" };
	}
}
