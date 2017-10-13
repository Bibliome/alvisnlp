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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.trie;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.ClosedValueSetParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.util.service.UnsupportedServiceException;

@Converter(targetType=MultipleEntryBehaviour.class)
public class MultipleEntryBehaviourParamConverter extends ClosedValueSetParamConverter<MultipleEntryBehaviour> {
	public MultipleEntryBehaviourParamConverter() throws ConverterException, UnsupportedServiceException {
		super();
	}

	@Override
	public MultipleEntryBehaviour[] allowedValues() {
		return MultipleEntryBehaviour.values();
	}

	@Override
	public MultipleEntryBehaviour fallBack(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "unsupported behaviour: " + stringValue);
		return null;
	}

	@Override
	public String[] getAlternateAttributes() {
		return null;
	}
}
