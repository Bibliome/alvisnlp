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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.Converter;
import alvisnlp.converters.lib.MapParamConverter;
import alvisnlp.module.types.MultiMapping;

@Converter(targetType=MultiMapping.class)
public class MultiMappingParamConverter extends MapParamConverter<String,String[],MultiMapping> {
	@Override
	public Class<String> keysType() {
		return String.class;
	}

	@Override
	public Class<String[]> valuesType() {
		return String[].class;
	}

	@Override
	public MultiMapping newEmptyMap() {
		return new MultiMapping();
	}

	@Override
	public Element getXMLValue(Document doc, String tagName, Object value) throws ConverterException {
		// TODO Auto-generated method stub
		return super.getXMLValue(doc, tagName, value);
	}

	@Override
	public String getStringValue(Object value) throws ConverterException {
		// TODO Auto-generated method stub
		return super.getStringValue(value);
	}
}
