package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.AbstractParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.util.Strings;

@Converter(targetType = WapitiAttribute.class)
public class WapitiAttributeParamConverter extends AbstractParamConverter<WapitiAttribute> {
	@Override
	protected WapitiAttribute convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "no string conversion");
		return null;
	}

	@Override
	protected WapitiAttribute convertXML(Element xmlValue) throws ConverterException {
		Expression value = convertComponent(Expression.class, xmlValue.getTextContent());
		int[] window = getWindow(xmlValue);
		return new WapitiAttribute(value, window);
	}

	private int[] getWindow(Element xmlValue) throws ConverterException {
		if (xmlValue.hasAttribute("offsets")) {
			List<Integer> offsets = new ArrayList<Integer>();
			List<String> sOffsets = Strings.splitAndTrim(xmlValue.getAttribute("offsets"), ',', 0);
			for (String sOff : sOffsets) {
				List<String> sRange = Strings.splitAndTrim(sOff, ':', 0);
				if (sRange.size() > 2) {
					cannotConvertXML(xmlValue, "range must have at most 2 values: " + sOff);
				}
				if (sRange.size() == 1) {
					offsets.add(convertComponent(Integer.class, sRange.get(0)));
				}
				if (sRange.size() == 2) {
					Integer from = convertComponent(Integer.class, sRange.get(0));
					Integer to = convertComponent(Integer.class, sRange.get(1));
					if (to < from) {
						cannotConvertXML(xmlValue, "range upside down: " + sRange);
					}
					for (int off = from; off <= to; ++off) {
						offsets.add(off);
					}
				}
			}
			int[] result = new int[offsets.size()];
			for (int iOff = 0; iOff < result.length; ++iOff) {
				result[iOff] = offsets.get(iOff);
			}
			return result;
		}
		if (xmlValue.hasAttribute("window")) {
			int size = convertComponent(Integer.class, xmlValue.getAttribute("window"));
			int[] result = new int[size * 2];
			int off = -size;
			for (int iOff = 0; iOff < result.length; ++iOff) {
				result[iOff] = off;
				off++;
				if (off == 0) {
					off++;
				}
			}
			return result;
		}
		return new int[] {0};
	}
}
