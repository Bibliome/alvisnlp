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


package org.bibliome.alvisnlp.modules.aggregate;

import java.text.DecimalFormat;

import org.bibliome.util.ArithmeticOperator;
import org.bibliome.util.SuperiorOperator;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;
import alvisnlp.corpus.expressions.ConstantsLibrary;
import alvisnlp.corpus.expressions.Expression;

@Converter(targetType=Aggregator.class)
public class AggregatorParamConverter extends AbstractParamConverter<Aggregator> {

	@Override
	protected Aggregator convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "no string conversion for " + Aggregator.class.getCanonicalName());
		return null;
	}
	
	private Expression getItem(Element xmlValue) throws DOMException, ConverterException {
		return convertComponent(Expression.class, xmlValue.getTextContent());
	}
	
	private static DecimalFormat getFormat(Element xmlValue) {
		if (xmlValue.hasAttribute("format")) {
			String pattern = xmlValue.getAttribute("format");
			return new DecimalFormat(pattern);
		}
		return new DecimalFormat();
	}
	
	private static char getSeparator(Element xmlValue) {
		String result = xmlValue.getAttribute("separator").trim();
		if (result.isEmpty()) {
			return ',';
		}
		return result.charAt(0);
	}

	@Override
	protected Aggregator convertXML(Element xmlValue) throws ConverterException {
		String type = xmlValue.getTagName();
		switch (type) {
			case "count": {
				Expression item = ConstantsLibrary.create(1);
				return new IntAggregator(item, ArithmeticOperator.PLUS);
			}
			case "sum": {
				Expression item = getItem(xmlValue);
				return new IntAggregator(item, ArithmeticOperator.PLUS);
			}
			case "dsum": {
				Expression item = getItem(xmlValue);
				DecimalFormat format = getFormat(xmlValue);
				return new DoubleAggregator(item, ArithmeticOperator.PLUS, format);
			}
			case "min": {
				Expression item = getItem(xmlValue);
				return new IntAggregator(item, SuperiorOperator.MIN);
			}
			case "dmin": {
				Expression item = getItem(xmlValue);
				DecimalFormat format = getFormat(xmlValue);
				return new DoubleAggregator(item, SuperiorOperator.MIN, format);
			}
			case "max": {
				Expression item = getItem(xmlValue);
				return new IntAggregator(item, SuperiorOperator.MAX);
			}
			case "dmax": {
				Expression item = getItem(xmlValue);
				DecimalFormat format = getFormat(xmlValue);
				return new DoubleAggregator(item, SuperiorOperator.MAX, format);
			}
			case "list": {
				Expression item = getItem(xmlValue);
				char separator = getSeparator(xmlValue);
				return new CollectionAggregator(item, CollectionFactory.LIST, separator, false);
			}
			case "set": {
				Expression item = getItem(xmlValue);
				char separator = getSeparator(xmlValue);
				return new CollectionAggregator(item, CollectionFactory.SET, separator, false);
			}
			case "ordered": {
				Expression item = getItem(xmlValue);
				char separator = getSeparator(xmlValue);
				return new CollectionAggregator(item, CollectionFactory.ORDERED, separator, false);
			}
			case "freq": {
				Expression item = getItem(xmlValue);
				return new CollectionAggregator(item, CollectionFactory.SET, ' ', true);
			}
			default:
				cannotConvertXML(xmlValue, "unknown aggregate " + type);
		}
		return null;
	}
}
