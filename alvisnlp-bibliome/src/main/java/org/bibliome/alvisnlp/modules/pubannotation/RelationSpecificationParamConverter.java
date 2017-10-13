package org.bibliome.alvisnlp.modules.pubannotation;

import org.bibliome.alvisnlp.converters.expression.parser.ExpressionParser;
import org.bibliome.alvisnlp.modules.DefaultExpressions;
import org.w3c.dom.Element;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.AbstractParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

@Converter(targetType=RelationSpecification.class)
public class RelationSpecificationParamConverter extends AbstractParamConverter<RelationSpecification> {
	@Override
	protected RelationSpecification convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "string conversion unavailable");
		return null;
	}

	@Override
	protected RelationSpecification convertXML(Element xmlValue) throws ConverterException {
		Expression instances = DefaultExpressions.SECTION_TUPLES;
		Expression pred = DefaultExpressions.feature("type");
		Expression subj = ExpressionParser.parseUnsafe("args{0}");
		Expression obj = ExpressionParser.parseUnsafe("args{1}");
		for (Element child : XMLUtils.childrenElements(xmlValue)) {
			Expression expr = convertComponent(Expression.class, child);
			switch (child.getTagName()) {
				case "instances":
				case "inst":
				case "relations":
				case "rels":
					instances = expr;
					break;
				case "pred":
				case "predicate":
				case "type":
					pred = expr;
					break;
				case "subj":
				case "subject":
					subj = expr;
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
		return new RelationSpecification(instances, pred, subj, obj);
	}

}
