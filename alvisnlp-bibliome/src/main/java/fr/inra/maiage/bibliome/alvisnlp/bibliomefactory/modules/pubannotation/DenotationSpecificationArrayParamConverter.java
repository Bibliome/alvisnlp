package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pubannotation;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.ArrayParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;

@Converter(targetType=DenotationSpecification[].class)
public class DenotationSpecificationArrayParamConverter extends ArrayParamConverter<DenotationSpecification> {
	@Override
	protected String[] getAlternateElementTags() {
		return new String[] { "denotation", "denot" };
	}
}
