package org.bibliome.alvisnlp.modules.pubannotation;

import java.util.List;

import org.bibliome.alvisnlp.modules.DefaultExpressions;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;
import alvisnlp.corpus.expressions.Expression;

@Converter(targetType=DenotationSpecification.class)
public class DenotationSpecificationParamConverter extends AbstractParamConverter<DenotationSpecification> {
	@Override
	protected DenotationSpecification convertTrimmed(String stringValue) throws ConverterException {
		Expression obj = convertComponent(Expression.class, stringValue);
		return new DenotationSpecification(obj);
	}

	@Override
	protected DenotationSpecification convertXML(Element xmlValue) throws ConverterException {
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
				case "denotations":
				case "denot":
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
		return new DenotationSpecification(instances, begin, end, obj);
	}
}
