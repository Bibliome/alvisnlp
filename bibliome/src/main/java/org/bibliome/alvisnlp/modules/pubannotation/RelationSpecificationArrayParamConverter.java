package org.bibliome.alvisnlp.modules.pubannotation;

import alvisnlp.converters.lib.ArrayParamConverter;
import alvisnlp.converters.lib.Converter;

@Converter(targetType=RelationSpecification[].class)
public class RelationSpecificationArrayParamConverter extends ArrayParamConverter<RelationSpecification> {
	@Override
	protected String[] getAlternateElementTags() {
		return new String[] { "relations", "rels" };
	}
}
