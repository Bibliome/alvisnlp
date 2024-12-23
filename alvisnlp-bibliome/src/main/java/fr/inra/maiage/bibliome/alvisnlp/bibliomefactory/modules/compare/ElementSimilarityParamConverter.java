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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.compare;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.ClosedValueSetParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.service.UnsupportedServiceException;

@Converter(targetType=ElementSimilarity.class)
public class ElementSimilarityParamConverter extends ClosedValueSetParamConverter<ElementSimilarity> {
	public ElementSimilarityParamConverter() throws ConverterException, UnsupportedServiceException {
		super();
	}

	@Override
	public ElementSimilarity[] allowedValues() {
		return StandardSimilarity.values();
	}

	@Override
	public ElementSimilarity fallBack(String stringValue) throws ConverterException {
		List<String> split = Strings.splitAndTrim(stringValue, ',', -1);
		if (split.size() == 1)
			return featureSimilarity(split.get(0));
		ElementSimilarity[] similarities = new ElementSimilarity[split.size()];
		for (int i = 0; i < split.size(); ++i)
			similarities[i] = convertTrimmed(split.get(i));
		return new MultiplicativeSimilarity(similarities);
	}

	private static Pattern FEATURE_SIMILARITY_PATTERN = Pattern.compile("(\\S+)\\s*\\(\\s*(\\S+)\\s*\\)");
	
	private static ElementSimilarity featureSimilarity(String s) {
		switch (s) {
			case "annotation-strict":
				return StandardSimilarity.ANNOTATION_STRICT;
			case "annotation-relaxed":
				return StandardSimilarity.ANNOTATION_LAX;
			case "annotation-jaccard":
			case "jaccard":
				return StandardSimilarity.ANNOTATION_JACCARD;
			default: {
				Matcher m = FEATURE_SIMILARITY_PATTERN.matcher(s);
				if (!m.matches())
					return new FeatureSimilarity(s);
				if ("edit".equalsIgnoreCase(m.group(2)))
					return new FeatureEdit(m.group(1));
				return new FeatureSimilarity(m.group(1), Double.parseDouble(m.group(2)));
			}
		}
	}

	@Override
	public String[] getAlternateAttributes() {
		return null;
	}
}
