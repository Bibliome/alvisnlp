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


package alvisnlp.converters;

import java.util.List;

import org.bibliome.util.service.UnsupportedServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import alvisnlp.documentation.Documentable;

/**
 * The ParamConverter interface is the parameter conversion service. The objects
 * returned by the convert() methods should be instances of the class returned
 * by targetType().
 * 
 * @author rbossy
 */
public interface ParamConverter extends Documentable {

    /**
     * Type to which strings and/or XML nodes are converted to.
     * 
     * @return the class<?>
     */
    public Class<?> targetType();

    /**
     * Convert a string into an object.
     * 
     * @param stringValue
     *            the string value
     * 
     * @return the object
     * 
     * @throws ConverterException
     *             the converter exception
     */
    public Object convert(String stringValue) throws ConverterException;

    /**
     * Convert an XML node into an object.
     * 
     * @param xmlValue
     *            the xml value
     * 
     * @return the object
     * 
     * @throws ConverterException
     *             the converter exception
     */
    public Object convert(Element xmlValue) throws ConverterException;

    /**
     * Converts an object into a string.
     * convert(getStringValue(obj)).equals(obj) should be true.
     * 
     * @param value
     *            the value
     * 
     * @return the string value
     * 
     * @throws ConverterException
     *             the converter exception
     * @throws UnsupportedServiceException 
     */
    public String getStringValue(Object value) throws ConverterException, UnsupportedServiceException;

    /**
     * Converts an object into an xml node.
     * convert(getXMLValue(obj)).equals(obj) should be true.
     * 
     * @param doc
     *            the doc
     * @param tagName
     *            the tag name
     * @param value
     *            the value
     * 
     * @return the xML value
     * 
     * @throws ConverterException
     *             the converter exception
     * @throws UnsupportedServiceException 
     */
    public Element getXMLValue(Document doc, String tagName, Object value) throws ConverterException, UnsupportedServiceException;

    /**
     * Returns the separator character for compound types.
     * 
     * @return the separator
     */
    public char getSeparator();

    /**
     * Returns the qualifier character for mapping types.
     * 
     * @return the qualifier
     */
    public char getQualifier();

    /**
     * Sets the separator character for compound types.
     * 
     * @param sep
     *            the new separator
     */
    public void setSeparator(char sep);

    /**
     * Sets the qualifier character for mapping types.
     * 
     * @param qual
     *            the new qualifier
     */
    public void setQualifier(char qual);

    /**
     * Returns either if string trimming is enabled.
     * 
     * @return true, if is trim value
     */
    public boolean isTrimValue();

    /**
     * Sets either if string trimming is enabled.
     * 
     * @param trimValue
     *            the new trim value
     */
    public void setTrimValue(boolean trimValue);

    /**
     * Sets the component converter factory.
     * 
     * @param componentFactory
     *            the new component converter factory
     */
    public void setComponentConverterFactory(ParamConverterFactory componentFactory);

    /**
     * Gets the component converter factory.
     * 
     * @return the component converter factory
     */
    public ParamConverterFactory getComponentConverterFactory();

//    public void setProperties(Properties props);
//    
//    public Properties getProperties();
    List<String> getInputDirs();
    void setInputDirs(List<String> inputDirs);
    String getOutputDir();
    void setOutputDir(String outputDir);
}
