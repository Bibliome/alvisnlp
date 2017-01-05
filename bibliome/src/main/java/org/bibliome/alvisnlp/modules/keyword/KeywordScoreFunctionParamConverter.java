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


package org.bibliome.alvisnlp.modules.keyword;

import java.util.HashMap;
import java.util.Map;

import org.bibliome.util.Strings;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;

@Converter(targetType=KeywordScoreFunction.class)
public class KeywordScoreFunctionParamConverter extends AbstractParamConverter<KeywordScoreFunction> {
	private static final Map<String,KeywordScoreFunction> standardFunctions = new HashMap<String,KeywordScoreFunction>();
	
	static {
		standardFunctions.put("freq", Frequency.ABSOLUTE);
		standardFunctions.put("frequency", Frequency.ABSOLUTE);
		standardFunctions.put("count", Frequency.ABSOLUTE);
		standardFunctions.put("absolute", Frequency.ABSOLUTE);
		standardFunctions.put("absolute-frequency", Frequency.ABSOLUTE);
		standardFunctions.put("relative", Frequency.RELATIVE_TO_DOCUMENT_LENGTH);
		standardFunctions.put("relative-frequency", Frequency.RELATIVE_TO_DOCUMENT_LENGTH);
		standardFunctions.put("tfidf", TFIDF.RAW);
		standardFunctions.put("tfidf-raw", TFIDF.RAW);
		standardFunctions.put("tfidf-bool", TFIDF.BOOLEAN);
		standardFunctions.put("tfidf-boolean", TFIDF.BOOLEAN);
		standardFunctions.put("tfidf-log", TFIDF.LOGARITHMIC);
		standardFunctions.put("tfidf-logarithmic", TFIDF.LOGARITHMIC);
		standardFunctions.put("tfidf-augmented", TFIDF.AUGMENTED);
	}
	
	@Override
	protected KeywordScoreFunction convertTrimmed(String stringValue) throws ConverterException {
		if (standardFunctions.containsKey(stringValue))
			return standardFunctions.get(stringValue);
		cannotConvertString(stringValue, "unknown scoring function " + stringValue + ", I know: " + Strings.join(standardFunctions.keySet(), ", "));
		return null;
	}

	@Override
	protected KeywordScoreFunction convertXML(Element xmlValue) throws ConverterException {
		String contents = XMLUtils.attributeOrValue(xmlValue, "value", "score", "function");
		if (!contents.isEmpty())
			return convertTrimmed(contents);
		if (!xmlValue.hasAttribute("type"))
			cannotConvertXML(xmlValue, "missing attribute @type");
		String type = xmlValue.getAttribute("type").trim().toLowerCase();
		switch (type) {
		case "freq":
		case "frequency":
			return convertXMLToFrequency(xmlValue);
		case "tfidf":
		case "tf-idf":
			return convertXMLToTFIDF(xmlValue);
		case "okapi":
		case "bm25":
			return convertXMLToOkapiBM25(xmlValue);
		}
		cannotConvertXML(xmlValue, "unknown function type " + type + ", I know about: freq, frequency, tfidf, tf-idf, okapi, bm25");
		return null;
	}

	private static OkapiBM25 convertXMLToOkapiBM25(Element xmlValue) {
		double k1 = XMLUtils.getDoubleAttribute(xmlValue, "k1", 1.2);
		double b = XMLUtils.getDoubleAttribute(xmlValue, "b", 0.75);
		return new OkapiBM25(k1, b);
	}

	private TFIDF convertXMLToTFIDF(Element xmlValue) throws ConverterException {
		String tf = XMLUtils.getAttribute(xmlValue, "tf", "raw").trim().toLowerCase();
		switch (tf) {
		case "raw":
			return TFIDF.RAW;
		case "boolean":
		case "bool":
			return TFIDF.BOOLEAN;
		case "log":
		case "logarithmic":
			return TFIDF.LOGARITHMIC;
		case "augmented":
			return TFIDF.AUGMENTED;
		}
		cannotConvertXML(xmlValue, "unknown term frequency function " + tf + ", I know about: raw, bool, boolean, log, logarithmic, augmented");
		return null;
	}

	private static Frequency convertXMLToFrequency(Element xmlValue) {
		if (XMLUtils.getBooleanAttribute(xmlValue, "relative", false))
			return Frequency.RELATIVE_TO_DOCUMENT_LENGTH;
		return Frequency.ABSOLUTE;
	}
}
