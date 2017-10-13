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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.sql;

import org.w3c.dom.Element;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.AbstractParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

@Converter(targetType=SQLParameter.class)
public class SQLParameterParamConverter extends AbstractParamConverter<SQLParameter> {
	@Override
	protected SQLParameter convertTrimmed(String stringValue) throws ConverterException {
		return convertTrimmed(stringValue, EvaluationType.STRING);
	}
	
	private SQLParameter convertTrimmed(String stringValue, EvaluationType type) throws ConverterException {
		Expression expr = convertComponent(Expression.class, stringValue);
		return new SQLParameter(type, expr);
	}
	
	private static EvaluationType getEvaluationType(String s) throws ConverterException {
		switch (s.trim().toLowerCase()) {
			case "bool":
			case "boolean":
				return EvaluationType.BOOLEAN;
			case "int":
			case "integer":
				return EvaluationType.INT;
			case "double":
			case "float":
				return EvaluationType.DOUBLE;
			case "string":
				return EvaluationType.STRING;
			default:
				throw new ConverterException("unhandled type " + s);
		}
	}

	@Override
	protected SQLParameter convertXML(Element xmlValue) throws ConverterException {
		String sType = XMLUtils.getAttribute(xmlValue, "type", "string");
		EvaluationType type = getEvaluationType(sType);
		String value = xmlValue.getTextContent();
		return convertTrimmed(value.trim(), type);
	}
}
