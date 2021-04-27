package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.lingua;

import com.github.pemistahl.lingua.api.Language;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.ArrayParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;

@Converter(targetType=Language[].class)
public class LanguageArrayParamConverter extends ArrayParamConverter<Language> {
	@Override
	protected String[] getAlternateElementTags() {
		return new String[] {
			"lang",
			"language",
			"languages"
		};
	}
}
