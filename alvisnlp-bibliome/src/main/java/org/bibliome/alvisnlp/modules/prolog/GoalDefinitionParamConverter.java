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


package org.bibliome.alvisnlp.modules.prolog;

import org.w3c.dom.Element;

import alice.tuprolog.Term;
import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;
import alvisnlp.corpus.expressions.Expression;

@Converter(targetType=GoalDefinition.class)
public class GoalDefinitionParamConverter extends AbstractParamConverter<GoalDefinition> {

	@Override
	protected GoalDefinition convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "unavailable string conversion for " + GoalDefinition.class.getCanonicalName());
		return null;
	}

	@Override
	protected GoalDefinition convertXML(Element xmlValue) throws ConverterException {
		if (!xmlValue.hasAttribute("goal"))
			cannotConvertXML(xmlValue, "missing attribute @goal");
		Term goal = Term.createTerm(xmlValue.getAttribute("goal"));
		Expression action = convertComponent(Expression.class, xmlValue);
		return new GoalDefinition(goal, action, null);
	}
}
