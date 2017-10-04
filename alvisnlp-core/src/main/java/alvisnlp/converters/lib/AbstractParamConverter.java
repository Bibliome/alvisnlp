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
import java.util.Locale;

import org.bibliome.util.Strings;
import org.bibliome.util.service.UnsupportedServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.ParamConverter;
import alvisnlp.converters.ParamConverterFactory;
import alvisnlp.documentation.Documentation;
import alvisnlp.documentation.ResourceDocumentation;

/**
 * Base class for parameter converters. The targetType() method assumes that
 * there is a Converter annotation present.
 * 
 * @author rbossy
 */
public abstract class AbstractParamConverter<T> implements ParamConverter {
    private char                  separator                 = ',';
    private char                  qualifier                 = '=';
    private boolean               trimValue                 = true;
    private ParamConverterFactory componentConverterFactory = null;
    private List<String> inputDirs;
    private String outputDir;
    
    /**
     * XML attribute that specifies the separator character for array elements and map entries.
     */
    public static final String    SEPARATOR_ATTRIBUTE       = "separator";
    
    /**
     * XML attribute that specifies the separator character between an entry key and value.
     */
    public static final String    QUALIFIER_ATTRIBUTE       = "qualifier";

    /**
     * XML attribute that specifies either if values should be trimmed.
     */
    public static final String    TRIM_VALUE_ATTRIBUTE      = "trim";

    /**
     * Creates a parameter converter.
     */
    protected AbstractParamConverter() {
        super();
    }

    @Override
    public String getStringValue(Object value) throws ConverterException, UnsupportedServiceException {
        return value.toString();
    }

    @Override
    public Element getXMLValue(Document doc, String tagName, Object value) throws ConverterException, UnsupportedServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final T convert(String stringValue) throws ConverterException {
        return convertTrimmed(trimValue ? stringValue.trim() : stringValue);
    }

    /**
     * Effective string conversion.
     * @return the converted value
     * @throws ConverterException
     */
    protected abstract T convertTrimmed(String stringValue) throws ConverterException;

    /**
     * Throws a "cannot convert string" exception with the specified message.
     * @param s
     * @param msg
     * @throws ConverterException
     */
    protected void cannotConvertString(String s, String msg) throws ConverterException {
        throw new ConverterException(String.format("cannot convert \"%s\" into %s: %s", s, targetType().getCanonicalName(), msg));
    }

    protected void cannotConvertString(String s, String msg, Throwable cause) throws ConverterException {
        throw new ConverterException(String.format("cannot convert \"%s\" into %s: %s", s, targetType().getCanonicalName(), msg), cause);
    }

    @Override
    public final T convert(Element xmlValue) throws ConverterException {
        char prevSeparator = separator;
        char prevQualifier = qualifier;
        boolean prevTrimValue = trimValue;
        setConverterProperties(xmlValue);
        T result = convertXML(xmlValue);
        setSeparator(prevSeparator);
        setQualifier(prevQualifier);
        setTrimValue(prevTrimValue);
        return result;
    }

    /**
     * Effective XML node conversion.
     * @param xmlValue
     * @return the converted value
     * @throws ConverterException
     */
    abstract protected T convertXML(Element xmlValue) throws ConverterException;

    /**
     * Sets converter properties. Attribute "separator" -> setSeparator()
     * Attribute "qualifier" -> setQualifier() Attribute "trim" ->
     * setTrimValue()
     * @param xmlValue
     */
    private void setConverterProperties(Element xmlValue) {
        if (xmlValue.hasAttribute(SEPARATOR_ATTRIBUTE)) {
            String separator = xmlValue.getAttribute(SEPARATOR_ATTRIBUTE);
            if (!separator.isEmpty())
                setSeparator(separator.charAt(0));
        }
        if (xmlValue.hasAttribute(QUALIFIER_ATTRIBUTE)) {
            String qualifier = xmlValue.getAttribute(QUALIFIER_ATTRIBUTE);
            if (!qualifier.isEmpty())
                setSeparator(qualifier.charAt(0));
        }
        if (xmlValue.hasAttribute(TRIM_VALUE_ATTRIBUTE)) {
            try {
                setTrimValue(Strings.getBoolean(xmlValue.getAttribute(TRIM_VALUE_ATTRIBUTE)));
            }
            catch (IllegalArgumentException iae) {
                iae.printStackTrace();
            }
        }
    }

    /**
     * Throws a "cannot convert element" exception.
     * @param elt
     * @param msg
     * @throws ConverterException
     */
    protected void cannotConvertXML(Element elt, String msg) throws ConverterException {
        throw new ConverterException(String.format("cannot convert element %s into %s: %s", elt.getNodeName(), targetType().getCanonicalName(), msg));
    }

    @Override
    public final Class<?> targetType() {
        Converter converterAnnotation = getClass().getAnnotation(Converter.class);
        if (converterAnnotation == null)
            return Object.class;
        return converterAnnotation.targetType();
    }
    
    private String getDocResourceBundleName() {
    	Class<?> klass = getClass();
        Converter converterAnnotation = klass.getAnnotation(Converter.class);
        if ((converterAnnotation == null) || (converterAnnotation.docResourceBundle().isEmpty()))
        	return klass.getCanonicalName() + "Doc";
        return converterAnnotation.docResourceBundle();
    }

    @Override
    public char getSeparator() {
        return separator;
    }

    @Override
    public char getQualifier() {
        return qualifier;
    }

    @Override
    public void setSeparator(char separator) {
        this.separator = separator;
    }

    @Override
    public void setQualifier(char qualifier) {
        this.qualifier = qualifier;
    }

    @Override
    public void setTrimValue(boolean trimValue) {
        this.trimValue = trimValue;
    }

    @Override
    public boolean isTrimValue() {
        return trimValue;
    }

	@Override
    public void setComponentConverterFactory(ParamConverterFactory componentFactory) {
        this.componentConverterFactory = componentFactory;
    }

    @Override
    public ParamConverterFactory getComponentConverterFactory() {
        return componentConverterFactory;
    }

	@Override
	public Documentation getDocumentation() {
		return new ParamConverterDocumentation();
	}
	
	private class ParamConverterDocumentation extends ResourceDocumentation {
		private ParamConverterDocumentation() {
			super(getDocResourceBundleName());
		}
		
		private void supplement(Document doc) {
			Element root = doc.getDocumentElement();
			Class<?> type = targetType();
			root.setAttribute("target", type.getCanonicalName());
			root.setAttribute("short-target", type.getSimpleName());
		}

		@Override
		public Document getDocument() {
			Document result = super.getDocument();
			supplement(result);
			return result;
		}

		@Override
		public Document getDocument(Locale locale) {
			Document result = super.getDocument(locale);
			supplement(result);
			return result;
		}
	}

	@Override
	public List<String> getInputDirs() {
		return inputDirs;
	}

	@Override
	public String getOutputDir() {
		return outputDir;
	}

	@Override
	public void setInputDirs(List<String> inputDirs) {
		this.inputDirs = inputDirs;
	}

	@Override
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	protected <C> ParamConverter getComponentConverter(Class<C> klass) throws UnsupportedServiceException {
		ParamConverter result = componentConverterFactory.getService(klass);
		result.setInputDirs(inputDirs);
		result.setOutputDir(outputDir);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	protected <C> C convertComponent(Class<C> klass, Element elt) throws ConverterException {
		try {
			return (C) getComponentConverter(klass).convert(elt);
		}
		catch (UnsupportedServiceException use) {
			cannotConvertXML(elt, use.getMessage());
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected <C> C convertComponent(Class<C> klass, String s) throws ConverterException {
		try {
			return (C) getComponentConverter(klass).convert(s);
		}
		catch (UnsupportedServiceException use) {
			cannotConvertString(s, use.getMessage());
		}
		return null;
	}
	
	protected <C> C convertAttribute(Class<C> klass, Element elt, String attribute, C defaultValue) throws ConverterException {
		if (!elt.hasAttribute(attribute)) {
			return defaultValue;
		}
		String sValue = elt.getAttribute(attribute);
		return convertComponent(klass, sValue);
	}

	protected <C> C convertAttribute(Class<C> klass, Element elt, String attribute) throws ConverterException {
		if (!elt.hasAttribute(attribute)) {
			cannotConvertXML(elt, "missing attribute " + attribute);
		}
		String sValue = elt.getAttribute(attribute);
		return convertComponent(klass, sValue);
	}
}
