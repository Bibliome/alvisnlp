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


package org.bibliome.alvisnlp.modules.trie;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;
import alvisnlp.corpus.Annotation;

@Converter(targetType=Subject.class)
public class SubjectParamConverter extends AbstractParamConverter<Subject> {
	@Override
	protected Subject convertTrimmed(String stringValue) throws ConverterException {
		return convertTrimmed(stringValue, null);
	}
	
	private static Subject convertTrimmed(String stringValue, Collection<String> features) {
		switch (stringValue) {
			case "plain": return ContentsSubject.PLAIN;
			case "word": return ContentsSubject.WORD;
			case "prefix": return ContentsSubject.PREFIX;
			case "suffix": return ContentsSubject.SUFFIX;
			default: {
				if (features == null)
					return new LayerSubject(stringValue);
				return new LayerSubject(stringValue, features);
			}
		}
	}

	@Override
	protected Subject convertXML(Element xmlValue) throws ConverterException {
		Collection<String> features = null;
		if (xmlValue.hasAttribute("feature")) {
			String[] fa = convertComponent(String[].class, xmlValue.getAttribute("feature"));
			if (fa == null || fa.length == 0) {
				features = Collections.singleton(Annotation.FORM_FEATURE_NAME);
			}
			else {
				features = Arrays.asList(fa);
			}
		}
		if (xmlValue.hasAttribute("value")) {
			return convertTrimmed(xmlValue.getAttribute("value"), features);
		}
		if (xmlValue.hasAttribute("layer")) {
			return convertTrimmed(xmlValue.getAttribute("layer"), features);
		}
		String contents = xmlValue.getTextContent().trim();
		if (contents.isEmpty()) {
			cannotConvertXML(xmlValue, "missing subject specification");
		}
		return convertTrimmed(contents, features);
	}
}
