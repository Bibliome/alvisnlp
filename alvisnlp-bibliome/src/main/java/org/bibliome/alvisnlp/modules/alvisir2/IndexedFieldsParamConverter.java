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


package org.bibliome.alvisnlp.modules.alvisir2;

import java.util.ArrayList;
import java.util.List;

import org.bibliome.alvisnlp.converters.expression.parser.ExpressionParser;
import org.bibliome.alvisnlp.library.StringLibrary;
import org.bibliome.alvisnlp.modules.DefaultExpressions;
import org.bibliome.util.Strings;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.module.types.ExpressionMapping;
import fr.inra.mig_bibliome.alvisir.core.index.NormalizationOptions;

@Converter(targetType=IndexedFields.class)
public class IndexedFieldsParamConverter extends AbstractParamConverter<IndexedFields> {
	@Override
	protected IndexedFields convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "no string conversion for " + IndexedFields.class.getCanonicalName());
		return null;
	}

	@Override
	protected IndexedFields convertXML(Element xmlValue) throws ConverterException {
		Expression instances = getDefaultFieldInstances();
		Expression fieldName = getDefaultFieldName();
		Expression fieldValue = getDefaultFieldValue();
		IndexedTokens indexedTokens = getDefaultTokens();
		NormalizationOptions normalizationOptions = NormalizationOptions.DEFAULT;
		if (xmlValue.hasAttribute("normalization")) {
			normalizationOptions = NormalizationOptions.NONE;
			String normsStr = xmlValue.getAttribute("normalization");
			List<String> norms = Strings.splitAndTrim(normsStr, ',', -1);
			for (String norm : norms) {
				normalizationOptions = NormalizationOptions.getFilter(norm, normalizationOptions);
				if (normalizationOptions == null) {
					cannotConvertXML(xmlValue, "unknown normalization option: " + norm);
				}
			}
		}
		List<IndexedTokens> indexedAnnotations = new ArrayList<IndexedTokens>();
		for (Element elt : XMLUtils.childrenElements(xmlValue)) {
			String tag = elt.getTagName();
			switch (tag) {
				case "instances":
					instances = convertComponent(Expression.class, elt);
					break;
				case "field-name":
					fieldName = convertComponent(Expression.class, elt);
					break;
				case "field-value":
					fieldValue = convertComponent(Expression.class, elt);
					break;
				case "tokens":
					indexedTokens = convertComponent(IndexedTokens.class, elt);
					break;
				case "annotations":
					indexedAnnotations.add(convertComponent(IndexedTokens.class, elt));
					break;
				case "keyword":
					fieldValue = convertComponent(Expression.class, elt);
					Expression self = DefaultExpressions.SELF;
					Expression zero = ExpressionParser.parseUnsafe("0");
					Expression strlen = new Expression(StringLibrary.NAME, "len", fieldValue);
					TokenFragments kwFragment = new TokenFragments(self, zero, strlen);
					Expression identifier = DefaultExpressions.UNIQUE_ID;
					ExpressionMapping arguments = new ExpressionMapping();
					ExpressionMapping properties = new ExpressionMapping();
					indexedTokens = new IndexedTokens(self, fieldValue, kwFragment, identifier, arguments, properties);
					break;
				default:
					cannotConvertXML(xmlValue, "unexpected element '" + tag + "'");
			}
		}
		return new IndexedFields(instances, fieldName, fieldValue, indexedTokens, indexedAnnotations, normalizationOptions);
	}

	private static IndexedTokens getDefaultTokens() {
		Expression instances = DefaultExpressions.SECTION_WORDS;
		Expression text = IndexedTokensParamConverter.getDefaultTokenText();
		TokenFragments tokenFragments = IndexedTokensParamConverter.getDefaultTokenFragments();
		Expression identifier = DefaultExpressions.UNIQUE_ID;
		ExpressionMapping arguments = new ExpressionMapping();
		ExpressionMapping properties = new ExpressionMapping();
		return new IndexedTokens(instances, text, tokenFragments, identifier, arguments, properties);
	}

	private static Expression getDefaultFieldValue() {
		return DefaultExpressions.SECTION_CONTENTS;
	}

	private static Expression getDefaultFieldName() {
		return DefaultExpressions.SECTION_NAME;
	}

	private static Expression getDefaultFieldInstances() {
		return DefaultExpressions.DOCUMENT_SECTIONS;
	}
}
