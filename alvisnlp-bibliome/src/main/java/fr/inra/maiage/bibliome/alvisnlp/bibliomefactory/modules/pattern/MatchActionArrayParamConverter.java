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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pattern;

import java.util.ArrayList;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.converters.expression.parser.ExpressionParser;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pattern.action.AddToLayer;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pattern.action.CreateAnnotation;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pattern.action.CreateTuple;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pattern.action.MatchAction;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pattern.action.RemoveAnnotations;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pattern.action.SetAnnotationFeatures;
import org.w3c.dom.Element;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.AbstractParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

@Converter(targetType=MatchAction[].class)
public class MatchActionArrayParamConverter extends AbstractParamConverter<MatchAction[]> {
	private static final String GROUP_ATTRIBUTE = "group";
	private static final String TARGET_LAYER_NAME_ATTRIBUTE = "layer";
	private static final String FEATURES_ATTRIBUTE = "features";
	private static final String RELATION_ATTRIBUTE = "relation";
	private static final String ARGUMENTS_ATTRIBUTE = "arguments";

	@Override
	protected MatchAction[] convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "string conversion is not available for this type");
		return null;
	}

	@Override
	protected MatchAction[] convertXML(Element xmlValue) throws ConverterException {
		List<MatchAction> actions = new ArrayList<MatchAction>();
		for (Element elt : XMLUtils.childrenElements(xmlValue))
			actions.add(convertMatchAction(elt));
		return actions.toArray(new MatchAction[actions.size()]);
	}

	private String[] convertLayerNames(Element elt) throws ConverterException {
		if (elt.hasAttribute(TARGET_LAYER_NAME_ATTRIBUTE))
			return convertComponent(String[].class, elt.getAttribute(TARGET_LAYER_NAME_ATTRIBUTE));
		cannotConvertXML(elt, "missing attribute " + TARGET_LAYER_NAME_ATTRIBUTE);
		return null;
	}
	
	private ExpressionMapping convertFeatures(Element elt) throws ConverterException {
		if (elt.hasAttribute(FEATURES_ATTRIBUTE))
			return convertComponent(ExpressionMapping.class, elt.getAttribute(FEATURES_ATTRIBUTE));
		return new ExpressionMapping();
	}
	
	private MatchAction convertMatchAction(Element elt) throws ConverterException {
		String tagName = elt.getTagName();
		String targetGroup = "match";
		if (elt.hasAttribute(GROUP_ATTRIBUTE))
			targetGroup = elt.getAttribute(GROUP_ATTRIBUTE);
		Expression target = ExpressionParser.parseUnsafe("group:" + targetGroup);
		if ("addToLayer".equals(tagName))
			return new AddToLayer(target, null, convertLayerNames(elt));
		if ("createAnnotation".equals(tagName))
			return new CreateAnnotation(target, null, convertFeatures(elt), null, convertLayerNames(elt));
		if ("createTuple".equals(tagName)) {
			String relationName = null;
			if (elt.hasAttribute(RELATION_ATTRIBUTE))
				relationName = elt.getAttribute(RELATION_ATTRIBUTE);
			else
				cannotConvertXML(elt, "missing attribute " + RELATION_ATTRIBUTE);
			ExpressionMapping arguments = null;
			if (elt.hasAttribute(ARGUMENTS_ATTRIBUTE))
				arguments = convertComponent(ExpressionMapping.class, elt.getAttribute(ARGUMENTS_ATTRIBUTE));
			else
				cannotConvertXML(elt, "missing attribute " + ARGUMENTS_ATTRIBUTE);
			return new CreateTuple(target, null, convertFeatures(elt), null, relationName, arguments, null);
		}
		if ("removeAnnotations".equals(tagName))
			return new RemoveAnnotations(target, null, convertLayerNames(elt));
		if ("setFeatures".equals(tagName)) {
			return new SetAnnotationFeatures(target, null, convertFeatures(elt), null);
		}
		cannotConvertXML(elt, "unsupported match action: " + tagName);
		return null;
	}
}
