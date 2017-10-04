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

import java.util.ArrayList;
import java.util.List;

import org.bibliome.util.Pair;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;
import alvisnlp.corpus.expressions.EvaluationType;
import alvisnlp.corpus.expressions.Expression;

@Converter(targetType=FactDefinition.class)
public class FactDefinitionParamConverter extends AbstractParamConverter<FactDefinition> {

	@Override
	protected FactDefinition convertTrimmed(String stringValue)	throws ConverterException {
		cannotConvertString(stringValue, "string conversion is not available for " + FactDefinition.class.getCanonicalName());
		return null;
	}

	@Override
	protected FactDefinition convertXML(Element xmlValue) throws ConverterException {
		Expression facts = null;
		Expression ctor = null;
		List<Pair<Expression,EvaluationType>> args = new ArrayList<Pair<Expression,EvaluationType>>();
		for (Element e : XMLUtils.childrenElements(xmlValue)) {
			String name = e.getTagName();
			switch (name) {
			case "facts":
				facts = convertComponent(Expression.class, e);
				break;
			case "functor":
				ctor = convertComponent(Expression.class, e);
				break;
			case "arg":
				EvaluationType type = getExpressionType(e, XMLUtils.getAttribute(e, "type", "string"));
				Expression expr = convertComponent(Expression.class, e);
				Pair<Expression,EvaluationType> p = new Pair<Expression,EvaluationType>(expr,type);
				args.add(p);
				break;
			default:
				cannotConvertXML(e, "unhandled element: " + name);
			}
		}
		return new FactDefinition(facts, ctor, args, null, null, null);
	}

	private EvaluationType getExpressionType(Element e, String s) throws ConverterException {
		switch (s) {
		case "int":
		case "integer":
			return EvaluationType.INT;
		case "double":
			return EvaluationType.DOUBLE;
		case "str":
		case "string":
		case "atom":
			return EvaluationType.STRING;
		case "element":
		case "elt":
		case "ref":
			return EvaluationType.ELEMENTS;
		case "bool":
		case "boolean":
			return EvaluationType.BOOLEAN;
		}
		cannotConvertXML(e, "unknown type: " + s);
		return null;
	}
}
