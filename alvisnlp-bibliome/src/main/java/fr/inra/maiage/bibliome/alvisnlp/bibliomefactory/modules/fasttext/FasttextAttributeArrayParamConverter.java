package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.fasttext;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.ArrayParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;

@Converter(targetType = FasttextAttribute[].class)
public class FasttextAttributeArrayParamConverter extends ArrayParamConverter<FasttextAttribute> {
	@Override
	protected String[] getAlternateElementTags() {
		return new String[] { "attribute", "attr" };
	}
}
