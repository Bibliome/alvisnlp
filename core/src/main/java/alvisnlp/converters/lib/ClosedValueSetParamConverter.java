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


package alvisnlp.converters.lib;

import java.util.HashMap;
import java.util.Map;

import org.bibliome.util.service.UnsupportedServiceException;

import alvisnlp.converters.ConverterException;

/**
 * Base class for converters to a closed set of values.
 * @author rbossy
 */
public abstract class ClosedValueSetParamConverter<T> extends SimpleParamConverter<T> {
    private final Map<String,T> values = new HashMap<String,T>();

    /**
     * Create a parameter for a closed values type.
     * @throws ConverterException
     * @throws UnsupportedServiceException 
     */
    public ClosedValueSetParamConverter() throws ConverterException, UnsupportedServiceException {
        super();
        for (T v : allowedValues()) {
            values.put(getStringValue(v), v);
        }
    }

    /**
     * Returns all allowed values. Each value should be handled properly by
     * getStringValue().
     * 
     * @return the t[]
     */
    public abstract T[] allowedValues();

    @Override
    public T convertTrimmed(String stringValue) throws ConverterException {
        if (values.containsKey(stringValue))
            return values.get(stringValue);
        return fallBack(stringValue);
    }

    /**
     * Default string conversion.
     * This method is called when stringValue could not be converted to any of the allowedValues() values.
     * @param stringValue
     * @return the converted value
     * @throws ConverterException
     */
    public abstract T fallBack(String stringValue) throws ConverterException;
}
