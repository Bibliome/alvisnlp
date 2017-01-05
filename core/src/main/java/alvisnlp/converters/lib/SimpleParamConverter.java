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

import org.bibliome.util.service.UnsupportedServiceException;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;


/**
 * Base class for converters whose XML conversion is directly based on the string conversion.
 * 
 * @author rbossy
 */
public abstract class SimpleParamConverter<T> extends AbstractParamConverter<T> {
	/**
	 * XML attribute specifying the parameter value.
	 */
    public static final String VALUE_ATTRIBUTE = "value";

    /**
     * Creates a new simple parameter converter.
     */
    public SimpleParamConverter() {
        super();
    }

    @Override
    public T convertXML(Element xmlValue) throws ConverterException {
    	String stringValue = XMLUtils.attributeOrValue(xmlValue, VALUE_ATTRIBUTE, getAlternateAttributes());
    	if (stringValue == null)
    		cannotConvertXML(xmlValue, "no value provided");
    	return convert(stringValue);
    }

    @Override
    public Element getXMLValue(Document doc, String tagName, Object value) throws ConverterException, UnsupportedServiceException {
    	return XMLUtils.createElement(doc, null, 0, tagName, getStringValue(value));
    }
    
    /**
     * Returns the attribute names supported to recognize the parameter value during XML conversion.
     */
    public abstract String[] getAlternateAttributes();
}
