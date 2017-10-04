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

import java.util.List;

import org.bibliome.util.Strings;
import org.bibliome.util.service.UnsupportedServiceException;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.ClosedValueSetParamConverter;
import alvisnlp.converters.lib.Converter;
import alvisnlp.corpus.AnnotationComparator;

@Converter(targetType = AnnotationComparator.class)
public class AnnotationComparatorParamConverter extends ClosedValueSetParamConverter<AnnotationComparator> {
    private static final String REVERSE = "reverse-";
    private static final String NUMERIC = "numeric-";
    private static final String COMPARATOR_ATTRIBUTE = "comparator";

    public AnnotationComparatorParamConverter() throws ConverterException, UnsupportedServiceException {
        super();
    }

    @Override
    public AnnotationComparator[] allowedValues() {
        return new AnnotationComparator[] {
                AnnotationComparator.byEnd,
                AnnotationComparator.byStart,
                AnnotationComparator.byLength,
                AnnotationComparator.byOrder
        };
    }

    @Override
    public AnnotationComparator fallBack(String stringValue) throws ConverterException {
    	List<String> split = Strings.splitAndTrim(stringValue, ',', -1);
    	if (split.size() == 1)
    		return reverse(stringValue);
    	AnnotationComparator[] multiple = new AnnotationComparator[split.size()];
    	for (int i = 0; i < split.size(); ++i)
    		multiple[i] = reverse(split.get(i));
    	return AnnotationComparator.multiple(multiple);
    }
    
    private AnnotationComparator reverse(String stringValue) throws ConverterException {
		if (stringValue.startsWith(REVERSE))
			return AnnotationComparator.reverse(convert(stringValue.substring(REVERSE.length())));
		return numeric(stringValue);
    }
    
    private static AnnotationComparator numeric(String stringValue) {
    	if (stringValue.startsWith(NUMERIC)) {
    		return AnnotationComparator.byDoubleFeature(stringValue);
    	}
		return AnnotationComparator.byFeature(stringValue);
    }

    @Override
    public String[] getAlternateAttributes() {
        return new String[] { COMPARATOR_ATTRIBUTE };
    }
}
