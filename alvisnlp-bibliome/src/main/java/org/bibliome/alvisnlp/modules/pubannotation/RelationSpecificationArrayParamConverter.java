package org.bibliome.alvisnlp.modules.pubannotation;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.ArrayParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;

@Converter(targetType=RelationSpecification[].class)
public class RelationSpecificationArrayParamConverter extends ArrayParamConverter<RelationSpecification> {
	@Override
	protected String[] getAlternateElementTags() {
		return new String[] { "relations", "rels" };
	}
}
