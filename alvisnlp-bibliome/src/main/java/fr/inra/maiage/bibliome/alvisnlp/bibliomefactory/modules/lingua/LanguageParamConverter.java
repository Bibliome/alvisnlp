package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.lingua;

import com.github.pemistahl.lingua.api.IsoCode639_1;
import com.github.pemistahl.lingua.api.IsoCode639_3;
import com.github.pemistahl.lingua.api.Language;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.SimpleParamConverter;

@Converter(targetType=Language.class)
public class LanguageParamConverter extends SimpleParamConverter<Language> {

	@Override
	public String[] getAlternateAttributes() {
		return new String[] {
			"lang",
			"language"
		};
	}

	@Override
	protected Language convertTrimmed(String stringValue) throws ConverterException {
		String upperValue = stringValue.toUpperCase();
		try {
			return Language.valueOf(upperValue);
		}
		catch (IllegalArgumentException e1) {
			try {
				IsoCode639_1 iso = IsoCode639_1.valueOf(upperValue);
				return Language.getByIsoCode639_1(iso);
			}
			catch (IllegalArgumentException e2) {
				try {
					IsoCode639_3 iso = IsoCode639_3.valueOf(upperValue);
					return Language.getByIsoCode639_3(iso);
				}
				catch (IllegalArgumentException e3) {
					cannotConvertString(stringValue, "unrecognized language");
				}
			}
		}
		return null;
	}
}
