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
import org.bibliome.util.service.UnsupportedServiceException;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.ClosedValueSetParamConverter;
import alvisnlp.converters.lib.Converter;

@Converter(targetType = MultipleValueAction.class)
public class MultipleValueActionParamConverter extends ClosedValueSetParamConverter<MultipleValueAction> {

	public MultipleValueActionParamConverter() throws ConverterException, UnsupportedServiceException {
		super();
	}

	@Override
	public MultipleValueAction[] allowedValues() {
		return MultipleValueAction.values();
	}

	@Override
	public MultipleValueAction fallBack(String stringValue)	throws ConverterException {
		cannotConvertString(stringValue, "invalid action, expected nop, add, replace or error");
		return null;
	}

	@Override
	public String[] getAlternateAttributes() {
		return new String[] { "action" };
	}
}
