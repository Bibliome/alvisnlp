package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.AbstractParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

@Converter(targetType = JsonValue.class)
public class JsonValueParamConverter extends AbstractParamConverter<JsonValue> {
	@Override
	protected JsonValue convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "no string conversion");
		return null;
	}

	@Override
	protected JsonValue convertXML(Element xmlValue) throws ConverterException {
		return convertXML(xmlValue, true);
	}

	private JsonValue convertXML(Element xmlValue, boolean allowTypeAttribute) throws ConverterException {
		if (allowTypeAttribute && xmlValue.hasAttribute("type")) {
			return convertXML(xmlValue.getAttribute("type"), xmlValue);
		}
		List<Element> children = XMLUtils.childrenElements(xmlValue);
		if (children.isEmpty()) {
			return convertToJsonString(xmlValue);
		}
		if (children.size() > 1) {
			cannotConvertXML(xmlValue, "extra element " + children.get(1).getTagName());
		}
		Element child = children.get(0);
		return convertXML(child.getTagName(), child);
	}
	
	private JsonValue convertXML(String selector, Element xmlValue) throws ConverterException {
		switch (selector) {
			case "integer":
			case "int": {
				return convertToJsonInt(xmlValue);
			}
			case "string":
			case "str": {
				return convertToJsonString(xmlValue);
			}
			case "list":
			case "array": {
				return convertToJsonArray(xmlValue);
			}
			case "map":
			case "obj":
			case "dict":
			case "object": {
				return convertToJsonObject(xmlValue);
			}
			case "offsets":
			case "offset":
			case "off": {
				return AnnotationOffset.INSTANCE;
			}
			case "args":
			case "arguments": {
				return convertToTupleArgs(xmlValue);
			}
			case "feat":
			case "feats":
			case "feature":
			case "features": {
				return convertToFeatures(xmlValue);
			}
			default: {
				cannotConvertXML(xmlValue, "unknown selector " + selector);
				return null;
			}
		}
	}

	private JsonValue convertToJsonInt(Element xmlValue) throws ConverterException {
		String content = xmlValue.getTextContent();
		Expression value = convertComponent(Expression.class, content);
		return new JsonInt(value);
	}

	private JsonValue convertToJsonString(Element xmlValue) throws ConverterException {
		String content = xmlValue.getTextContent();
		Expression value = convertComponent(Expression.class, content);
		return new JsonString(value);
	}

	private JsonValue convertToJsonArray(Element xmlValue) throws ConverterException {
		Element itemsElt = null;
		Element valueElt = null;
		for (Element child : XMLUtils.childrenElements(xmlValue)) {
			String tag = child.getTagName();
			switch (tag) {
				case "items": {
					if (itemsElt != null) {
						cannotConvertXML(xmlValue, "duplicate items");
					}
					itemsElt = child;
					break;
				}
				case "value": {
					if (valueElt != null) {
						cannotConvertXML(xmlValue, "duplicate value");
					}
					valueElt = child;
					break;
				}
				default: {
					cannotConvertXML(xmlValue, "unexpected " + tag);
				}
			}
		}
		if (itemsElt == null) {
			cannotConvertXML(xmlValue, "missing items");
		}
		if (valueElt == null) {
			cannotConvertXML(xmlValue, "missing value");
		}
		Expression items = convertComponent(Expression.class, itemsElt);
		JsonValue value = convertXML(valueElt, true);
		return new JsonArray(items, value);
	}

	private JsonValue convertToJsonObject(Element xmlValue) throws ConverterException {
		Map<String,JsonValue> entries = new HashMap<String,JsonValue>();
		for (Element child : XMLUtils.childrenElements(xmlValue)) {
			String key = child.getTagName();
			JsonValue value = convertXML(child, true);
			entries.put(key, value);
		}
		return new JsonObject(entries );
	}

	private JsonValue convertToTupleArgs(Element xmlValue) throws ConverterException {
		JsonValue value = convertXML(xmlValue, false);
		return new TupleArgs(value);
	}

	private JsonValue convertToFeatures(Element xmlValue) throws ConverterException {
		String[] features = convertComponent(String[].class, xmlValue);
		return new Features(features);
	}
}
