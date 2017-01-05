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


package org.bibliome.alvisnlp.modules.cadixe;

import org.bibliome.alvisnlp.converters.expression.parser.ExpressionParser;
import org.bibliome.alvisnlp.modules.DefaultExpressions;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.module.types.ExpressionMapping;

@Converter(targetType=AnnotationSet.class)
public class AnnotationSetParamConverter extends AbstractParamConverter<AnnotationSet> {

	@Override
	protected AnnotationSet convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "string conversion is not available for type " + AnnotationSet.class);
		return null;
	}

	@Override
	protected AnnotationSet convertXML(Element xmlValue) throws ConverterException {
		AnnotationSet result = new AnnotationSet();
		result.setDescription(XMLUtils.getAttribute(xmlValue, "description", ""));
		result.setRevision(XMLUtils.getIntegerAttribute(xmlValue, "revision", 0));
		result.setType(XMLUtils.getAttribute(xmlValue, "type", "AlvisNLP"));
		result.setOwner(XMLUtils.getIntegerAttribute(xmlValue, "owner", 0));
		result.setTaskId(XMLUtils.getIntegerAttribute(xmlValue, "task", 0));
		result.setId(XMLUtils.getIntegerAttribute(xmlValue, "id", 0));
		for (Element elt : XMLUtils.childrenElements(xmlValue))
			getSpecificAnnotationKindConverter(elt).process(result, elt);
		return result;
	}
	
	private ConvertSpecificAnnotationKind getSpecificAnnotationKindConverter(Element elt) throws ConverterException {
		String name = elt.getNodeName();
		if (name.equals("text"))
			return new ConvertTextAnnotationDefinition();
		if (name.equals("group"))
			return new ConvertGroupAnnotationDefinition();
		if (name.equals("relation"))
			return new ConvertRelationAnnotationDefinition();
		cannotConvertXML(elt, "unknown annotation kind " + name);
		return null;
	}

	private abstract class ConvertSpecificAnnotationKind {
		protected Expression instances = null;
		protected Expression type = null;
		protected ExpressionMapping propsMap = new ExpressionMapping();
		protected Expression properties = null;
		protected Expression propKey = DefaultExpressions.feature("key");
		protected Expression propValue = DefaultExpressions.feature("value");
		protected Expression sources = ExpressionParser.parseUnsafe("nav:arguments[@role ^= \"source\"]");
		protected Expression sourceId = DefaultExpressions.feature("id");
		protected Expression sourceAnnotationSet = DefaultExpressions.feature("annotation-set");
		
		void process(AnnotationSet set, Element elt) throws ConverterException {
			for (Element e : XMLUtils.childrenElements(elt)) {
				String name = e.getNodeName();
				if (name.equals("properties")) {
					propsMap = convertComponent(ExpressionMapping.class, e);
					continue;
				}
				if (name.equals("propdef")) {
					properties = convertComponent(Expression.class, e);
					continue;
				}
				if (name.equals("key")) {
					propKey = convertComponent(Expression.class, e);
					continue;
				}
				if (name.equals("value")) {
					propValue = convertComponent(Expression.class, e);
					continue;
				}
				if (name.equals("instances")) {
					if (instances != null)
						cannotConvertXML(elt, "duplicate tag 'instances'");
					instances = convertComponent(Expression.class, e);
					continue;
				}
				if (name.equals("type")) {
					if (type != null)
						cannotConvertXML(elt, "duplicate tag 'type'");
					type = convertComponent(Expression.class, e);
					continue;
				}
				specificProcess(e, name);
			}
			if (instances == null)
				cannotConvertXML(elt, "missing tag 'instances'");
			if (type == null)
				cannotConvertXML(elt, "missing tag 'type'");
			finish(set, elt);
		}
		
		protected abstract void specificProcess(Element elt, String name) throws ConverterException;
		
		protected abstract void finish(AnnotationSet set, Element elt) throws ConverterException;
	}
	
	private class ConvertTextAnnotationDefinition extends ConvertSpecificAnnotationKind {
		private Expression fragments;
		
		@Override
		protected void specificProcess(Element elt, String name) throws ConverterException {
			if (name.equals("fragments")) {
				if (fragments != null)
					cannotConvertXML(elt, "duplicate tag 'fragments'");
				fragments = convertComponent(Expression.class, elt);
				return;
			}
			cannotConvertXML(elt, "unexpected tag '" + name + "'");
		}

		@Override
		protected void finish(AnnotationSet set, Element elt) {
			set.addTextAnnotationDefinition(new TextAnnotationDefinition(instances, propsMap, properties, propKey, propValue, sources, sourceId, sourceAnnotationSet, type, fragments == null ? DefaultExpressions.SELF : fragments));
		}
	}
	
	private class ConvertGroupAnnotationDefinition extends ConvertSpecificAnnotationKind {
		private Expression items = null;
		
		@Override
		protected void specificProcess(Element elt, String name) throws ConverterException {
			if (name.equals("items")) {
				if (items != null)
					cannotConvertXML(elt, "duplicate tag 'items'");
				items = convertComponent(Expression.class, elt);
				return;
			}
			cannotConvertXML(elt, "unexpected tag '" + name + "'");
		}

		@Override
		protected void finish(AnnotationSet set, Element elt) throws ConverterException {
			if (items == null)
				cannotConvertXML(elt, "missing tag 'items'");
			set.addGroupDefinition(new GroupDefinition(instances, propsMap, properties, propKey, propValue, sources, sourceId, sourceAnnotationSet, type, items));
		}
	}
	
	private class ConvertRelationAnnotationDefinition extends ConvertSpecificAnnotationKind {
		private ExpressionMapping argsMap = null;
		private Expression args = null;
		private Expression role = null;

		@Override
		protected void specificProcess(Element elt, String name) throws ConverterException {
			switch (name) {
				case "args": {
					if (argsMap != null) {
						cannotConvertXML(elt, "duplicate tag 'args'");
					}
					argsMap = convertComponent(ExpressionMapping.class, elt);
					return;
				}
				case "argdef": {
					if (args != null) {
						cannotConvertXML(elt, "duplicate tag 'argdef'");
					}
					args = convertComponent(Expression.class, elt);
					return;
				}
				case "role": {
					if (role != null) {
						cannotConvertXML(elt, "duplicate tag 'role'");
					}
					role = convertComponent(Expression.class, elt);
					return;
				}
			}
			cannotConvertXML(elt, "unexpected tag '" + name + "'");
		}

		@Override
		protected void finish(AnnotationSet set, Element elt) throws ConverterException {
			if (argsMap == null && args == null) {
				cannotConvertXML(elt, "missing tag 'args' or 'argdef'");
			}
			if (args != null && role == null) {
				cannotConvertXML(elt, "missing tag 'role'");
			}
			if (role != null && args == null) {
				cannotConvertXML(elt, "missing tag 'argdef'");
			}
			if (argsMap == null) {
				argsMap = new ExpressionMapping();
			}
			set.addRelationDefinition(new RelationDefinition(instances, propsMap, properties, propKey, propValue, sources, sourceId, sourceAnnotationSet, type, argsMap, args, role));
		}
	}
}
