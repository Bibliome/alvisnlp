package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.converters;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.ArrayParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.util.files.InputFile;

@Converter(targetType=InputFile[].class)
public class InputFileArrayParamConverter extends ArrayParamConverter<InputFile> {
	@Override
	protected String[] getAlternateElementTags() {
		return new String[] { "file", "files" };
	}
}
