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


package org.bibliome.alvisnlp.modules.alvisre;

import org.bibliome.alvisnlp.converters.expression.parser.ExpressionParser;
import org.bibliome.alvisnlp.modules.DefaultExpressions;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;
import alvisnlp.corpus.ArgumentElement;
import alvisnlp.corpus.expressions.Expression;

@Converter(targetType=AlvisRERelations.class)
public class AlvisRERelationsParamConverter extends AbstractParamConverter<AlvisRERelations> {
	@Override
	protected AlvisRERelations convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "string conversion not available for " + AlvisRERelations.class);
		return null;
	}

	@Override
	protected AlvisRERelations convertXML(Element xmlValue) throws ConverterException {
		Expression items = null;
		Expression type = null;
		Expression arguments = ExpressionParser.parseUnsafe("nav:arguments");
		Expression role = DefaultExpressions.feature(ArgumentElement.ROLE_FEATURE_KEY);
		Expression label = null;
		for (Element child : XMLUtils.childrenElements(xmlValue)) {
			Expression e = convertComponent(Expression.class, child);
			String tag = child.getTagName();
			switch (tag) {
				case "items": items = e; break;
				case "type": type = e; break;
				case "arguments": arguments = e; break;
				case "role": role = e; break;
				case "label": label = e; break;
				default:
					cannotConvertXML(xmlValue, "unexpected tag " + tag);
			}
		}
		if (items == null) {
			cannotConvertXML(xmlValue, "missing tag items");
		}
		if (type == null) {
			cannotConvertXML(xmlValue, "missing tag type");
		}
		return new AlvisRERelations(items, type, arguments, role, label);
	}
}
