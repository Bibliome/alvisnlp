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

import java.util.Map;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.Converter;
import alvisnlp.converters.lib.MapParamConverter;
import alvisnlp.module.types.Mapping;

@Converter(targetType = Mapping.class)
public class MappingParamConverter extends MapParamConverter<String,String,Mapping> {
    @Override
    public Mapping newEmptyMap() {
        return new Mapping();
    }

    @Override
    public Class<String> valuesType() {
        return String.class;
    }

    @Override
    public Class<String> keysType() {
        return String.class;
    }

	@Override
	public String getStringValue(Object value) throws ConverterException {
		if (!(value instanceof Mapping)) {
			throw new RuntimeException();
		}
		Mapping mapping = (Mapping) value;
		StringBuilder sb = new StringBuilder();
		boolean notFirst = false;
		for (Map.Entry<String,String> e : mapping.entrySet()) {
			if (notFirst) {
				sb.append(',');
			}
			else {
				notFirst = true;
			}
			sb.append(e.getKey());
			sb.append('=');
			sb.append(e.getValue());
		}
		return sb.toString();
	}
}
