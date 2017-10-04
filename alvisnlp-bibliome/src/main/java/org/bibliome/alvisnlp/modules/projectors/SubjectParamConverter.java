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


package org.bibliome.alvisnlp.modules.projectors;

import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;
import alvisnlp.converters.lib.SimpleParamConverter;
import alvisnlp.corpus.Annotation;

@Converter(targetType = Subject.class)
public class SubjectParamConverter extends AbstractParamConverter<Subject> {

	@Override
	protected ContentsSubject convertTrimmed(String stringValue) throws ConverterException {
		switch (stringValue) {
			case "plain": return ContentsSubject.PLAIN;
			case "prefix": return ContentsSubject.PREFIX;
			case "suffix": return ContentsSubject.SUFFIX;
			case "words": return ContentsSubject.WORD;
		}
		cannotConvertString(stringValue, "unknown contents projection mode");
		return null;
	}

	@Override
	protected Subject convertXML(Element xmlValue) throws ConverterException {
		String contentsSubject = XMLUtils.attributeOrValue(xmlValue, SimpleParamConverter.VALUE_ATTRIBUTE, "contents");
		if (xmlValue.hasAttribute("layer")) {
//			if (contentsSubject != null)
//				cannotConvertXML(xmlValue, "contents and layer subjects are exclusive");
			String layerName = xmlValue.getAttribute("layer");
			String featureName = Annotation.FORM_FEATURE_NAME;
			if (xmlValue.hasAttribute("feature"))
				featureName = xmlValue.getAttribute("feature");
			char separator = ' ';
			if (xmlValue.hasAttribute("separator")) {
				String sep = xmlValue.getAttribute("separator");
				if (!sep.isEmpty())
					separator = sep.charAt(0);
			}
			return new LayerSubject(layerName, featureName, separator);
		}
		return convertTrimmed(contentsSubject.trim());
	}
}
