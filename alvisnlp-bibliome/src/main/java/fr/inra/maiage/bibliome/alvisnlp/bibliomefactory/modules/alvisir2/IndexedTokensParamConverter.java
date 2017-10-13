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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisir2;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DefaultExpressions;
import org.w3c.dom.Element;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.AbstractParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

@Converter(targetType=IndexedTokens.class)
public class IndexedTokensParamConverter extends AbstractParamConverter<IndexedTokens> {
	@Override
	protected IndexedTokens convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "no string conversion for " + IndexedTokens.class.getCanonicalName());
		return null;
	}

	@Override
	protected IndexedTokens convertXML(Element xmlValue) throws ConverterException {
		Expression instances = null;
		Expression text = getDefaultTokenText();
		TokenFragments fragments = getDefaultTokenFragments();
		Expression identifier = DefaultExpressions.UNIQUE_ID;
		ExpressionMapping arguments = new ExpressionMapping();
		ExpressionMapping properties = new ExpressionMapping();
		for (Element elt : XMLUtils.childrenElements(xmlValue)) {
			String tag = elt.getTagName();
			switch (tag) {
				case "instances":
					instances = convertComponent(Expression.class, elt);
					break;
				case "text":
					text = convertComponent(Expression.class, elt);
					break;
				case "fragments":
					fragments = convertComponent(TokenFragments.class, elt);
					break;
				case "identifier":
					identifier = convertComponent(Expression.class, elt);
					break;
				case "arguments":
					arguments = convertComponent(ExpressionMapping.class, elt);
					break;
				case "properties":
					properties = convertComponent(ExpressionMapping.class, elt);
					break;
				default:
					cannotConvertXML(xmlValue, "unexpected element '" + tag + "'");
			}
		}
		if (instances == null) {
			cannotConvertXML(xmlValue, "missing element 'instances'");
		}
		return new IndexedTokens(instances, text, fragments, identifier, arguments, properties);
	}

	static TokenFragments getDefaultTokenFragments() {
		Expression instances = DefaultExpressions.SELF;
		Expression start = TokenFragmentsParamConverter.getDefaultFragmentStart();
		Expression end = TokenFragmentsParamConverter.getDefaultFragmentEnd();
		return new TokenFragments(instances, start, end);
	}

	static Expression getDefaultTokenText() {
		return DefaultExpressions.ANNOTATION_FORM;
	}
}
