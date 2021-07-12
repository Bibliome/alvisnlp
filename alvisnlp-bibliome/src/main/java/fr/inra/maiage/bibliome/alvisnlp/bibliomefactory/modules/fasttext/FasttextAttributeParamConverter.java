package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.fasttext;

import org.w3c.dom.Element;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DefaultExpressions;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.AbstractParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

@Converter(targetType = FasttextAttribute.class)
public class FasttextAttributeParamConverter extends AbstractParamConverter<FasttextAttribute> {
	@Override
	protected FasttextAttribute convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "no string conversion for " + FasttextAttribute.class.getCanonicalName());
		return null;
	}

	@Override
	protected FasttextAttribute convertXML(Element xmlValue) throws ConverterException {
		Expression tokens = null;
		Expression form = null;
		if (xmlValue.hasAttribute("tokens")) {
			tokens = convertComponent(Expression.class, xmlValue.getAttribute("tokens"));
		}
		if (xmlValue.hasAttribute("form")) {
			form = convertComponent(Expression.class, xmlValue.getAttribute("form"));
		}
		for (Element elt : XMLUtils.childrenElements(xmlValue)) {
			String tagName = elt.getTagName();
			switch (tagName) {
				case "tokens": {
					if (tokens != null) {
						cannotConvertXML(xmlValue, "duplicate tokens specification");
					}
					tokens = convertComponent(Expression.class, elt.getTextContent());
					break;
				}
				case "form": {
					if (form != null) {
						cannotConvertXML(xmlValue, "duplicate form specification");
					}
					form = convertComponent(Expression.class, elt.getTextContent());
					break;
				}
				default: {
					cannotConvertXML(xmlValue, "unhandled tag: " + tagName);
				}
			}
		}
		if (tokens == null) {
			cannotConvertXML(xmlValue, "missing tokens specification");
		}
		if (form == null) {
			form = DefaultExpressions.ANNOTATION_FORM;
		}
		return new FasttextAttribute(tokens, form);
	}
}
