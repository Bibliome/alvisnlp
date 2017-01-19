package org.bibliome.alvisnlp.modules.pubannotation;

import alvisnlp.converters.lib.ArrayParamConverter;
import alvisnlp.converters.lib.Converter;

@Converter(targetType=DenotationSpecification[].class)
public class DenotationSpecificationArrayParamConverter extends ArrayParamConverter<DenotationSpecification> {
	@Override
	protected String[] getAlternateElementTags() {
		return new String[] { "denotations", "denot" };
	}
}
