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

import java.util.regex.Pattern;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.Converter;
import alvisnlp.converters.lib.SimpleParamConverter;

@Converter(targetType = Pattern.class)
public class PatternParamConverter extends SimpleParamConverter<Pattern> {
    private static final String PATTERN_ATTRIBUTE = "pattern";
    private static final String REGEX_ATTRIBUTE = "regex";
    private static final String REGEXP_ATTRIBUTE = "regexp";

    @Override
    public Pattern convertTrimmed(String stringValue) {
        return Pattern.compile(stringValue);
    }

    @Override
    public String[] getAlternateAttributes() {
        return new String[] { PATTERN_ATTRIBUTE, REGEX_ATTRIBUTE, REGEXP_ATTRIBUTE };
    }

	@Override
	public String getStringValue(Object value) throws ConverterException {
		if (value instanceof Pattern) {
			return ((Pattern) value).pattern();
		}
		throw new RuntimeException();
	}
}
