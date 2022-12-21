package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.ArrayParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;

@Converter(targetType = WapitiAttribute[].class)
public class WapitiAttributeArrayParamConverter extends ArrayParamConverter<WapitiAttribute> {
	@Override
	protected String[] getAlternateElementTags() {
		return null;
	}
}
