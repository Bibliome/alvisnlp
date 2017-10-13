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


package org.bibliome.alvisnlp.converters.expression;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.MapParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping;

@Converter(targetType=ExpressionMapping.class)
public class ExpressionMappingParamConverter extends MapParamConverter<String,Expression,ExpressionMapping> {
	@Override
	public Class<String> keysType() {
		return String.class;
	}

	@Override
	public Class<Expression> valuesType() {
		return Expression.class;
	}

	@Override
	public ExpressionMapping newEmptyMap() {
		return new ExpressionMapping();
	}
}
