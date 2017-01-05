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


package org.bibliome.alvisnlp.converters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bibliome.alvisnlp.library.UserFunction;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;
import alvisnlp.corpus.expressions.Expression;

@Converter(targetType=UserFunction.class)
public class UserFunctionParamConverter extends AbstractParamConverter<UserFunction> {
	@Override
	protected UserFunction convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "string conversion is not available for " + UserFunction.class.getName());
		return null;
	}

	@Override
	protected UserFunction convertXML(Element xmlValue) throws ConverterException {
		List<String> ftors = null;
		List<String> params = null;
		Expression body = null;
		if (xmlValue.hasAttribute("ftors")) {
			String[] a = convertComponent(String[].class, xmlValue.getAttribute("ftors"));
			ftors = Arrays.asList(a);
		}
		if (xmlValue.hasAttribute("params")) {
			String[] a = convertComponent(String[].class, xmlValue.getAttribute("params"));
			ftors = Arrays.asList(a);
		}
		if (xmlValue.hasAttribute("body")) {
			body = convertComponent(Expression.class, xmlValue.getAttribute("body"));
		}
		for (Element elt : XMLUtils.childrenElements(xmlValue)) {
			String name = elt.getTagName();
			switch (name) {
				case "ftors": {
					String[] a = convertComponent(String[].class, elt);
					ftors = Arrays.asList(a);
					break;
				}
				case "params": {
					String[] a = convertComponent(String[].class, elt);
					params = Arrays.asList(a);
					break;
				}
				case "body": {
					body = convertComponent(Expression.class, elt);
					break;
				}
				default: {
					cannotConvertXML(xmlValue, "unexpected tag: " + name);
				}
			}
		}
		if (ftors == null) {
			ftors = Collections.emptyList();
		}
		if (params == null) {
			params = Collections.emptyList();
		}
		if (body == null) {
			cannotConvertXML(xmlValue, "missing function body");
		}
		return new UserFunction(ftors, params, body);
	}
}
