/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


package org.bibliome.alvisnlp.modules.classifiers;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.xpath.XPathExpressionException;

import org.bibliome.util.Strings;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;
import alvisnlp.corpus.expressions.Expression;

@Converter(targetType=RelationDefinition.class)
public class RelationDefinitionParamConverter extends AbstractParamConverter<RelationDefinition> {
	private static final String WEKA_RELATION_NAME = "alvisnlp_weka";

	@Override
	protected RelationDefinition convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "string conversion is not available for " + RelationDefinition.class.getCanonicalName());
		return null;
	}

	@Override
	protected RelationDefinition convertXML(Element xmlValue) throws ConverterException {
		RelationDefinition result = new RelationDefinition(WEKA_RELATION_NAME);
		for (Element child : XMLUtils.childrenElements(xmlValue)) {
			String tagName = child.getTagName();
			if (tagName.startsWith("attr")) {
				result.addAttribute(convertAttributeDefinition(child));
				continue;
			}
			if ("bag".equals(tagName)) {
				result.addBag(convertBagDefinition(child));
				continue;
			}
			cannotConvertXML(child, "unsupported tag <" + tagName + '>');
		}
		return result;
	}

	private BagDefinition convertBagDefinition(Element elt) throws ConverterException {
		if (!elt.hasAttribute("prefix"))
			cannotConvertXML(elt, "missing @prefix");
		if (!elt.hasAttribute("feature"))
			cannotConvertXML(elt, "missing @feature");
		String prefix = elt.getAttribute("prefix");
		String featureKey = elt.getAttribute("feature");
		Expression expression = convertExpression(XMLUtils.attributeOrValue(elt, "expr"));
		boolean count = elt.hasAttribute("count") ? Strings.getBoolean(elt.getAttribute("count")) : false;
		BagDefinition result = new BagDefinition(prefix, featureKey, count, expression, null);
		if (elt.hasAttribute("loadValues")) {
			//			result.setValuesFile(new FileSourceStream("UTF-8", elt.getAttribute("loadValues")));
			String valuesPath = elt.getAttribute("loadValues");
			SourceStream valuesSource = convertComponent(SourceStream.class, valuesPath);
			result.setValuesFile(valuesSource);
		}
		else {
			try {
				for (Element v : XMLUtils.childrenElements(elt))
					result.addValue(XMLUtils.evaluateString(XMLUtils.CONTENTS, v));
			}
			catch (XPathExpressionException xpee) {
				cannotConvertXML(elt, xpee.getMessage());
			}
		}
		return result;
	}

	private AttributeDefinition convertAttributeDefinition(Element elt) throws ConverterException {
		if (!elt.hasAttribute("name"))
			cannotConvertXML(elt, "missing @name");
		String name = elt.getAttribute("name");
		Expression value = convertExpression(XMLUtils.attributeOrValue(elt, "value"));
		boolean classAttribute = elt.hasAttribute("class") ? Strings.getBoolean(elt.getAttribute("class")) : false;
		String type = elt.hasAttribute("type") ? elt.getAttribute("type") : "boolean";
		if (type.startsWith("bool"))
			return new BooleanExpressionAttributeDefinition(classAttribute, name, value, null);
		if (type.startsWith("int"))
			return new IntExpressionAttributeDefinition(classAttribute, name, value, null);
		if ("nominal".equals(type) || type.startsWith("str")) {
			Collection<String> values = new ArrayList<String>();
			try {
				for (Element v : XMLUtils.childrenElements(elt))
					values.add(XMLUtils.evaluateString(XMLUtils.CONTENTS, v));
			}
			catch (XPathExpressionException xpee) {
				cannotConvertXML(elt, xpee.getMessage());
			}
			return new NominalExpressionAttributeDefinition(classAttribute, name, values, value, null);
		}
		cannotConvertXML(elt, "unsupported attribute type " + type);
		return null;
	}

	private Expression convertExpression(String s) throws ConverterException {
		return convertComponent(Expression.class, s);
	}
}
