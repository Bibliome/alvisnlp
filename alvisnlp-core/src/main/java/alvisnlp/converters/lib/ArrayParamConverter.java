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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bibliome.util.Strings;
import org.bibliome.util.service.UnsupportedServiceException;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.ParamConverter;

/**
 * Base class for converters into array objects. This implementation assumes
 * that the target type is an array and that there is a converter for the component type.
 * 
 * @author rbossy
 */
public abstract class ArrayParamConverter<T> extends SimpleParamConverter<T[]> {
	/**
	 * XML tag containing an array element.
	 */
    public static final String ELEMENT_TAG      = "element";
    
    /**
     * XML attribute specifying the array values.
     */
    public static final String VALUES_ATTRIBUTE = "values";

    private ParamConverter     elementConverter = null;

    /**
     * Creates an array parameter converter.
     */
    public ArrayParamConverter() {
        super();
    }

    /**
     * Returns the converter for elements.
     * @return the element converter
     * @throws ConverterException
     */
    private ParamConverter getElementConverter() throws ConverterException {
        try {
            if (elementConverter == null)
                elementConverter = getComponentConverter(targetType().getComponentType());
        }
        catch (UnsupportedServiceException use) {
            throw new ConverterException(use);
        }
        return elementConverter;
    }

    /**
     * Returns a new array instance with the specified length.
     * @param length
     * @return a fresh array instance
     */
    @SuppressWarnings("unchecked")
    private T[] newInstance(int length) {
        return (T[]) Array.newInstance(targetType().getComponentType(), length); // unchecked cast
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] convertTrimmed(String stringValue) throws ConverterException {
        List<String> values = Strings.split(stringValue, getSeparator(), 0);
        T[] result = newInstance(values.size());
        for (int i = 0; i < values.size(); ++i)
        	result[i] = (T) convertComponent(targetType().getComponentType(), values.get(i));
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] convertXML(Element xmlValue) throws ConverterException {
        Collection<T> result = new ArrayList<T>();
        if (xmlValue.hasAttribute(VALUE_ATTRIBUTE)) {
        	String stringValue = xmlValue.getAttribute(VALUE_ATTRIBUTE);
            result.addAll(Arrays.asList(convert(stringValue)));
        }
        List<Element> children = XMLUtils.childrenElements(xmlValue);
        if (children.isEmpty()) {
        	String stringValue = xmlValue.getTextContent();
        	if (!stringValue.trim().isEmpty())
        		result.addAll(Arrays.asList(convert(stringValue)));
        }
        else {
        	for (Element child : children) {
        		String name = child.getTagName();
        		if (VALUE_ATTRIBUTE.equals(name)) {
        			String stringValue = child.getTextContent();
        			result.addAll(Arrays.asList(convert(stringValue)));
        		}
        		if (ELEMENT_TAG.equals(name)) {
        			T item = (T) convertComponent(targetType().getComponentType(), child);
        			result.add(item);
        		}
        		String[] alternateTags = getAlternateElementTags();
        		if (alternateTags != null) {
        			for (String alt : alternateTags) {
                		if (alt.equals(name)) {
                			T item = (T) convertComponent(targetType().getComponentType(), child);
                			result.add(item);
                		}
        			}
        		}
        	}
        }
        return result.toArray(newInstance(result.size()));
    }
    
    protected abstract String[] getAlternateElementTags();

    @SuppressWarnings("unchecked")
    @Override
    public String getStringValue(Object value) throws ConverterException, UnsupportedServiceException {
        T[] typedValue = (T[])value; // unchecked cast
        ParamConverter conv = getElementConverter();
        String[] stringValues = new String[typedValue.length];
        for (int i = 0; i < stringValues.length; ++i)
            stringValues[i] = conv.getStringValue(typedValue[i]);
        return Strings.join(stringValues, getSeparator());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Element getXMLValue(Document doc, String tagName, Object value) throws ConverterException, DOMException, UnsupportedServiceException {
        ParamConverter conv = getElementConverter();
        Element result = doc.createElement(tagName);
        for (T e : (T[])value)
            // unchecked cast
            result.appendChild(conv.getXMLValue(doc, ELEMENT_TAG, e));
        return result;
    }

    @Override
    public String[] getAlternateAttributes() {
        return new String[] { VALUES_ATTRIBUTE };
    }
}
