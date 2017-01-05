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

import org.bibliome.alvisnlp.modules.DefaultExpressions;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;
import alvisnlp.corpus.expressions.Expression;

@Converter(targetType=AlvisRETokens.class)
public class AlvisRETokensParamConverter extends AbstractParamConverter<AlvisRETokens> {
	@Override
	protected AlvisRETokens convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "string conversion not available for " + AlvisRETokens.class.getCanonicalName());
		return null;
	}

	@Override
	protected AlvisRETokens convertXML(Element xmlValue) throws ConverterException {
		Expression items = null;
		Expression type = null;
		Expression form = DefaultExpressions.ANNOTATION_FORM;
		Expression[] layers = null;
		Expression start = DefaultExpressions.ANNOTATION_START;
		Expression end = DefaultExpressions.ANNOTATION_END;
		for (Element child : XMLUtils.childrenElements(xmlValue)) {
			String tag = child.getTagName();
			switch (tag) {
				case "items": {
					items = convertComponent(Expression.class, child);
					break;
				}
				case "type": {
					type = convertComponent(Expression.class, child);
					break;
				}
				case "form": {
					form = convertComponent(Expression.class, child);
					break;
				}
				case "layers": {
					layers = convertComponent(Expression[].class, child);
					break;
				}
				case "start": {
					start = convertComponent(Expression.class, child);
					break;
				}
				case "end": {
					end = convertComponent(Expression.class, child);
					break;
				}
				default: {
					cannotConvertXML(xmlValue, "unexpected tag " + tag);
				}
			}
		}
		if (items == null) {
			cannotConvertXML(xmlValue, "missing tag items");
		}
		if (type == null) {
			cannotConvertXML(xmlValue, "missing tag type");
		}
		if (layers == null) {
			cannotConvertXML(xmlValue, "missing tag layers");
		}
		return new AlvisRETokens(items, type, form, layers, start, end);
	}
}
