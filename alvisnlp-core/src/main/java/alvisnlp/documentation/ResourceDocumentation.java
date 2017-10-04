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


package alvisnlp.documentation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Documentation object represents the XML user documentation of a module or converter class.
 * @author rbossy
 *
 */
public class ResourceDocumentation implements Documentation {
	private final String baseName;
	
	/**
	 * Creates a documentation for a class with the specified name.
	 * @param baseName
	 */
	public ResourceDocumentation(String baseName) {
		super();
		this.baseName = baseName;
	}

	private ResourceBundle getResourceBundle() throws SAXException, IOException {
		//return ResourceBundle.getBundle(baseName, XMLDocumentationResourceBundleControl.INSTANCE);
		Locale locale = Locale.getDefault();
		return getResourceBundle(locale);
	}

	private ResourceBundle getResourceBundle(Locale locale) throws SAXException, IOException {
		try {
			ClassLoader loader = getClass().getClassLoader();
			return ResourceBundle.getBundle(baseName, locale, loader, XMLDocumentationResourceBundleControl.INSTANCE);
		}
		catch (MissingResourceException e) {
			InputStream empty = new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<alvisnlp-doc/>".getBytes());
			Document document = XMLUtils.docBuilder.parse(empty);
			return new XMLDocumentationResourceBundle(document);
		}
//		return ResourceBundle.getBundle(baseName, locale, XMLDocumentationResourceBundleControl.INSTANCE);
	}

	/**
	 * Returns the base name of the documented class.
	 */
	public String getBaseName() {
		return baseName;
	}

	/**
	 * Returns the documentation contents as a DOM document.
	 */
	@Override
	public Document getDocument() {
		try {
			return (Document) getResourceBundle().getObject(XMLDocumentationResourceBundle.DOCUMENTATION);
		}
		catch (SAXException|IOException e) {
			throw new RuntimeException(e);
		}
	}
	

	/**
	 * Returns the documentation contents for the specified locale as a DOM document.
	 */
	@Override
	public Document getDocument(Locale locale) {
		try {
			return (Document) getResourceBundle(locale).getObject(XMLDocumentationResourceBundle.DOCUMENTATION);
		}
		catch (SAXException|IOException e) {
			throw new RuntimeException(e);
		}
	}
}
