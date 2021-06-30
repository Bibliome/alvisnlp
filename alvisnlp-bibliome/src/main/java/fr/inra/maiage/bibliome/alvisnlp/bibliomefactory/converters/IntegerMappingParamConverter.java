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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.converters;

import java.util.Map;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.MapParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.IntegerMapping;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;

@Converter(targetType = IntegerMapping.class)
public class IntegerMappingParamConverter extends MapParamConverter<String,Integer,IntegerMapping> {
    @Override
    public IntegerMapping newEmptyMap() {
        return new IntegerMapping();
    }

    @Override
    public Class<Integer> valuesType() {
        return Integer.class;
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
