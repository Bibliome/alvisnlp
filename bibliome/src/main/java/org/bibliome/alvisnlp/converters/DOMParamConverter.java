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

import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;

@Converter(targetType=DocumentFragment.class)
public class DOMParamConverter extends AbstractParamConverter<DocumentFragment> {
	@Override
	protected DocumentFragment convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "string conversion is not available for " + Node.class.getCanonicalName());
		return null;
	}

	@Override
	protected DocumentFragment convertXML(Element xmlValue) throws ConverterException {
		Document doc = xmlValue.getOwnerDocument();
		DocumentFragment result = doc.createDocumentFragment();
		for (Node child : XMLUtils.childrenNodes(xmlValue)) {
			Node clone = child.cloneNode(true);
			result.appendChild(clone);
		}
		return result;
	}
}
