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


package org.bibliome.alvisnlp.modules.yatea;

import org.bibliome.util.files.InputFile;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;

@Converter(targetType=TestifiedTerminology.class)
public class TestifiedTerminologyParamConverter extends AbstractParamConverter<TestifiedTerminology> {
	@Override
	protected TestifiedTerminology convertTrimmed(String stringValue) throws ConverterException {
		InputFile file = new InputFile(stringValue);
		return new FileTestifiedTerminology(file);
	}

	@Override
	protected TestifiedTerminology convertXML(Element xmlValue) throws ConverterException {
		if (xmlValue.hasAttribute("termsLayer")) {
			String termsLN = xmlValue.getAttribute("termsLayer");
			return new AnnotationsTestifiedTerminology(termsLN);
		}
		String stringValue = XMLUtils.attributeOrValue(xmlValue, "value", "file");
		return convertTrimmed(stringValue);
	}
}
