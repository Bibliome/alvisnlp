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


package org.bibliome.alvisnlp.modules.alvisdb;

import org.bibliome.alvisnlp.converters.expression.parser.ExpressionParser;
import org.bibliome.alvisnlp.modules.DefaultExpressions;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;
import alvisnlp.corpus.expressions.Expression;

@Converter(targetType=ADBElements.class)
public class ADBElementsParamConverter extends AbstractParamConverter<ADBElements> {
	@Override
	protected ADBRelations convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "string conversion not available");
		return null;
	}

	@Override
	protected ADBElements convertXML(Element xmlValue) throws ConverterException {
		String tag = xmlValue.getTagName();
		switch (tag) {
			case "entities": {
				return convertADBEntities(xmlValue);
			}
			case "relations": {
				return convertADBRelations(xmlValue);
			}
			default:
				cannotConvertXML(xmlValue, "unknown element: " + tag);
				return null;
		}
	}
	
	private void checkNotNull(Expression expr, Element elt, String tag) throws ConverterException {
		if (expr == null) {
			cannotConvertXML(elt, "missing tag: " + tag);
		}
	}
	
	private ADBRelations convertADBRelations(Element xmlValue) throws ConverterException {
		Expression items = null;
		Expression id = null;
		Expression name = null;
		Expression type = null;
		Expression args = null;
		Expression argId = null;
		Expression argName = null;
		Expression argAncestors = null;
		Expression argAncestorId = null;
		Expression argDoc = ExpressionParser.parseUnsafe("document.@id");
		Expression argSec = ExpressionParser.parseUnsafe("section.@name");
		Expression argStart = DefaultExpressions.ANNOTATION_START;
		Expression argEnd = DefaultExpressions.ANNOTATION_END;
		
		for (Element elt : XMLUtils.childrenElements(xmlValue)) {
			String tag = elt.getTagName();
			switch (tag) {
				case "items": {
					items = convertComponent(Expression.class, elt);
					break;
				}
				case "id": {
					id = convertComponent(Expression.class, elt);
					break;
				}
				case "name": {
					name = convertComponent(Expression.class, elt);
					break;
				}
				case "type": {
					type = convertComponent(Expression.class, elt);
					break;
				}
				case "args": {
					args = convertComponent(Expression.class, elt);
					break;
				}
				case "arg-id": {
					argId = convertComponent(Expression.class, elt);
					break;
				}
				case "arg-name": {
					argName = convertComponent(Expression.class, elt);
					break;
				}
				case "ancestors": {
					argAncestors = convertComponent(Expression.class, elt);
					break;
				}
				case "ancestor-id": {
					argAncestorId = convertComponent(Expression.class, elt);
					break;
				}
				case "arg-doc": {
					argDoc = convertComponent(Expression.class, elt);
					break;
				}
				case "arg-sec": {
					argSec = convertComponent(Expression.class, elt);
					break;
				}
				case "arg-start": {
					argStart = convertComponent(Expression.class, elt);
					break;
				}
				case "arg-end": {
					argEnd = convertComponent(Expression.class, elt);
					break;
				}
				default: {
					cannotConvertXML(xmlValue, "unknown option tag: " + tag);
				}
			}
		}
		checkNotNull(items, xmlValue, "items");
		checkNotNull(id, xmlValue, "id");
		checkNotNull(name, xmlValue, "name");
		checkNotNull(type, xmlValue, "type");
		checkNotNull(args, xmlValue, "args");
		checkNotNull(argId, xmlValue, "arg-id");
		if (argAncestors != null || argAncestorId != null) {
			checkNotNull(argAncestors, xmlValue, "ancestors");
			checkNotNull(argAncestorId, xmlValue, "ancestor-id");
		}
		return new ADBRelations(items, id, name, type, args, argId, argName, argAncestors, argAncestorId, argDoc, argSec, argStart, argEnd);
	}
	
	private ADBEntities convertADBEntities(Element xmlValue) throws ConverterException {
		Expression items = null;
		Expression id = null;
		Expression name = null;
		Expression type = null;
		Expression path = null;
		Expression pathItemId = null;
		Expression ancestors = null;
		Expression ancestorId = null;
		Expression children = null;
		Expression childId = null;
		Expression synonyms = null;
		Expression synonymForm = null;
		for (Element elt : XMLUtils.childrenElements(xmlValue)) {
			String tag = elt.getTagName();
			switch (tag) {
				case "items": {
					items = convertComponent(Expression.class, elt);
					break;
				}
				case "id": {
					id = convertComponent(Expression.class, elt);
					break;
				}
				case "name": {
					name = convertComponent(Expression.class, elt);
					break;
				}
				case "type": {
					type = convertComponent(Expression.class, elt);
					break;
				}
				case "path": {
					path = convertComponent(Expression.class, elt);
					break;
				}
				case "path-id": {
					pathItemId = convertComponent(Expression.class, elt);
					break;
				}
				case "ancestors": {
					ancestors = convertComponent(Expression.class, elt);
					break;
				}
				case "ancestor-id": {
					ancestorId = convertComponent(Expression.class, elt);
					break;
				}
				case "children": {
					children = convertComponent(Expression.class, elt);
					break;
				}
				case "child-id": {
					childId = convertComponent(Expression.class, elt);
					break;
				}
				case "synonyms": {
					synonyms = convertComponent(Expression.class, elt);
					break;
				}
				case "synonym-form": {
					synonymForm = convertComponent(Expression.class, elt);
					break;
				}
				default: {
					cannotConvertXML(xmlValue, "unknown option tag: " + tag);
				}
			}
		}
		checkNotNull(items, xmlValue, "items");
		checkNotNull(id, xmlValue, "id");
		checkNotNull(name, xmlValue, "name");
		checkNotNull(type, xmlValue, "type");
		checkNotNull(synonyms, xmlValue, "synonyms");
		checkNotNull(synonymForm, xmlValue, "synonym-form");
		if (path != null || pathItemId != null || ancestors != null || ancestorId != null || children != null || childId != null) {
			checkNotNull(path, xmlValue, "path");
			checkNotNull(pathItemId, xmlValue, "path-id");
			checkNotNull(ancestors, xmlValue, "ancestors");
			checkNotNull(ancestorId, xmlValue, "ancestor-id");
			checkNotNull(children, xmlValue, "children");
			checkNotNull(childId, xmlValue, "child-id");
		}
		return new ADBEntities(items, id, name, type, path, pathItemId, ancestors, ancestorId, children, childId, synonyms, synonymForm);
	}
}
