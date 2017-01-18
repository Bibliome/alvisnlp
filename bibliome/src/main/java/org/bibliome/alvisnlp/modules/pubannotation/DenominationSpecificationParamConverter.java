package org.bibliome.alvisnlp.modules.pubannotation;

import java.util.List;

import org.bibliome.alvisnlp.modules.DefaultExpressions;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;
import alvisnlp.corpus.expressions.Expression;

@Converter(targetType=DenominationSpecification.class)
public class DenominationSpecificationParamConverter extends AbstractParamConverter<DenominationSpecification> {
	@Override
	protected DenominationSpecification convertTrimmed(String stringValue) throws ConverterException {
		Expression obj = convertComponent(Expression.class, stringValue);
		return new DenominationSpecification(obj);
	}

	@Override
	protected DenominationSpecification convertXML(Element xmlValue) throws ConverterException {
		List<Element> children = XMLUtils.childrenElements(xmlValue);
		if (children.isEmpty()) {
			return convert(xmlValue.getTextContent());
		}
		Expression instances = DefaultExpressions.SECTION_ANNOTATIONS;
		Expression begin = DefaultExpressions.ANNOTATION_START;
		Expression end = DefaultExpressions.ANNOTATION_END;
		Expression obj = DefaultExpressions.feature("ref");
		for (Element child : children) {
			Expression expr = convertComponent(Expression.class, child);
			switch (child.getTagName()) {
				case "instances":
				case "inst":
				case "denominations":
				case "denom":
					instances = expr;
					break;
				case "begin":
				case "start":
					begin = expr;
					break;
				case "end":
					end = expr;
					break;
				case "obj":
				case "object":
				case "ref":
					obj = expr;
					break;
				default:
					cannotConvertXML(xmlValue, "unexpected tag " + child.getTagName());
			}
		}
		return new DenominationSpecification(instances, begin, end, obj);
	}
}
