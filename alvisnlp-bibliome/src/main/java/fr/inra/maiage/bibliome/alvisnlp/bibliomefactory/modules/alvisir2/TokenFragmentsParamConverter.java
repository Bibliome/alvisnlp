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
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

@Converter(targetType=TokenFragments.class)
public class TokenFragmentsParamConverter extends AbstractParamConverter<TokenFragments> {
	@Override
	protected TokenFragments convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "no string conversion for " + TokenFragments.class.getCanonicalName());
		return null;
	}

	@Override
	protected TokenFragments convertXML(Element xmlValue) throws ConverterException {
		Expression instances = null;
		Expression start = getDefaultFragmentStart();
		Expression end = getDefaultFragmentEnd();
		for (Element elt : XMLUtils.childrenElements(xmlValue)) {
			String tag = elt.getTagName();
			switch (tag) {
				case "instances":
					instances = convertComponent(Expression.class, elt);
					break;
				case "start":
					start = convertComponent(Expression.class, elt);
					break;
				case "end":
					end = convertComponent(Expression.class, elt);
					break;
				default:
					cannotConvertXML(xmlValue, "unexpected element '" + tag + "'");
			}
		}
		if (instances == null) {
			cannotConvertXML(xmlValue, "missing element 'instances'");
		}
		return new TokenFragments(instances, start, end);
	}

	static Expression getDefaultFragmentStart() {
		return DefaultExpressions.ANNOTATION_START;
	}

	static Expression getDefaultFragmentEnd() {
		return DefaultExpressions.ANNOTATION_END;
	}
}
