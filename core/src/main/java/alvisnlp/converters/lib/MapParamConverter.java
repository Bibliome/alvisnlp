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

import java.util.List;
import java.util.Map;

import org.bibliome.util.Strings;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.ParamConverter;

/**
 * Base class for mapping converters.
 * 
 * @author rbossy
 */
public abstract class MapParamConverter<K,V,M extends Map<K,V>> extends SimpleParamConverter<M> {
	/**
	 * XML tag for a map entry.
	 */
    public static final String ENTRY_TAG      = "entry";
    
    /**
     * XML attribute specifying an entry key.
     */
    public static final String KEY_ATTRIBUTE  = "key";
    
    /**
     * XML attribute specifying the map entries.
     */
    public static final String ENTRIES_ATTRIBUTE = "entries";

    /**
     * Returns the type of the entry keys.
     * @return the type of the entry keys
     */
    public abstract Class<K> keysType();

    /**
     * Returns the type of the entry values.
     * @return the type of the entry values
     */
    public abstract Class<V> valuesType();

    /**
     * Returns an new empty mapping instance.
     * @return an new empty mapping instance
     */
    public abstract M newEmptyMap();

    @Override
    public M convertTrimmed(String stringValue) throws ConverterException {
        M result = newEmptyMap();
        for (String s : Strings.split(stringValue, getSeparator(), 0)) {
            int q = s.indexOf(getQualifier());
            if (q < 0)
            	cannotConvertString(s, "mapping requires a " + getQualifier());
            K key = convertComponent(keysType(), s.substring(0, q));
            V value = convertComponent(valuesType(), s.substring(q + 1));
            result.put(key, value);
        }
        return result;
    }

    @Override
    public M convertXML(Element xmlValue) throws ConverterException {
        M result = newEmptyMap();
        if (xmlValue.hasAttribute(VALUE_ATTRIBUTE))
            result.putAll(convert(xmlValue.getAttribute(VALUE_ATTRIBUTE)));
        List<Element> children = XMLUtils.childrenElements(xmlValue);
        if (children.isEmpty()) {
        	String stringValue = xmlValue.getTextContent();
        	if (!stringValue.trim().isEmpty())
                result.putAll(convert(stringValue));
        }
        else {
        	for (Element child : children) {
        		String stringKey;
        		if (child.hasAttribute(KEY_ATTRIBUTE))
        			stringKey = child.getAttribute(KEY_ATTRIBUTE);
        		else
        			stringKey = child.getTagName();
        		K key = convertComponent(keysType(), stringKey);
        		String stringValue;
        		if (child.hasAttribute(VALUE_ATTRIBUTE))
        			stringValue = child.getAttribute(VALUE_ATTRIBUTE);
        		else
        			stringValue = child.getTextContent();
        		V value = convertComponent(valuesType(), stringValue);
        		result.put(key, value);
        	}
        }
        return result;
    }

    @Override
    public String[] getAlternateAttributes() {
        return new String[] { ENTRIES_ATTRIBUTE };
    }

	@Override
	public String getStringValue(Object value) throws ConverterException {
		if (!(value instanceof Map)) {
			throw new RuntimeException();
		}
		try {
			@SuppressWarnings("unchecked")
			Map<K,V> map = (Map<K,V>) value;
			StringBuilder sb = new StringBuilder();
			ParamConverter kc = getComponentConverter(keysType());
			ParamConverter vc = getComponentConverter(valuesType());
			boolean notFirst = false;
			for (Map.Entry<K,V> e : map.entrySet()) {
				if (notFirst) {
					sb.append(',');
				}
				else {
					notFirst = true;
				}
				String k = kc.getStringValue(e.getKey());
				String v = vc.getStringValue(e.getValue());
				sb.append(k);
				sb.append('=');
				sb.append(v);
			}
			return sb.toString();
		}
		catch (Exception e) {
			throw new ConverterException(e);
		}
	}

	@Override
	public Element getXMLValue(Document doc, String tagName, Object value) throws ConverterException {
		if (!(value instanceof Map)) {
			throw new RuntimeException();
		}
		try {
			@SuppressWarnings("unchecked")
			Map<K,V> map = (Map<K,V>) value;
			Element result = XMLUtils.createElement(doc, null, 0, tagName);
			Class<?> kt = keysType();
			boolean stringKeys = kt.equals(String.class);
			ParamConverter kc = getComponentConverter(kt);
			ParamConverter vc = getComponentConverter(valuesType());
			for (Map.Entry<K,V> e : map.entrySet()) {
				String k = kc.getStringValue(e.getKey());
				Element elt;
				if (stringKeys) {
					elt = vc.getXMLValue(doc, k, e.getValue());
				}
				else {
					elt = vc.getXMLValue(doc, ENTRY_TAG, e.getValue());
					elt.setAttribute(KEY_ATTRIBUTE, k);
				}
				result.appendChild(elt);
			}
			return result;
		}
		catch (Exception e) {
			throw new ConverterException(e);
		}
	}
}
