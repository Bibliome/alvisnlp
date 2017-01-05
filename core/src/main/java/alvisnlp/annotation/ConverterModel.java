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


package alvisnlp.annotation;

import javax.lang.model.element.TypeElement;

import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import alvisnlp.converters.lib.Converter;

/**
 * Converter model.
 * @author rbossy
 *
 */
class ConverterModel {
	private final String fullName;
	private final String targetType;
	private final String bundleName;
	
	/**
	 * Creates a new converter model.
	 * @param ctx
	 * @param converterElement type element representing the converter class declaration
	 * @throws ModelException
	 */
	ConverterModel(ModelContext ctx, TypeElement converterElement) throws ModelException {
		fullName = converterElement.getQualifiedName().toString();
		targetType = ctx.getTargetType(converterElement);
		Converter annotation = converterElement.getAnnotation(Converter.class);
		bundleName = annotation.docResourceBundle().isEmpty() ? fullName + "Doc" : annotation.docResourceBundle();
	}

	/**
	 * Return the converter class full name.
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Return the converter target type full name.
	 */
	public String getTargetType() {
		return targetType;
	}

	Node getDOM(Document doc) {
		org.w3c.dom.Element result = doc.createElement("converter");
		result.setAttribute("name", fullName);
		result.setAttribute("target-type", targetType);
		return result;
	}
	
	Document generateDoc() {
		Document result = XMLUtils.docBuilder.newDocument();
		
		Element root = XMLUtils.createRootElement(result, "alvisnlp-doc");
		root.setAttribute("target", fullName);
		root.setAttribute("author", "");
		root.setAttribute("date", "");

		Element synopsis = XMLUtils.createElement(result, root, 1, "synopsis");
		XMLUtils.createElement(result, synopsis, 2, "p", "synopsis");

		Element converter = XMLUtils.createElement(result, root, 1, "converter-doc");
		Element stringConversion = XMLUtils.createElement(result, converter, 2, "string-conversion");
		XMLUtils.createElement(result, stringConversion, 3, "p", "string conversion");
		Element xmlConversion = XMLUtils.createElement(result, converter, 2, "xml-conversion");
		XMLUtils.createElement(result, xmlConversion, 3, "p", "XML conversion");

		return result;
	}
	
	String getBundleName() {
		return bundleName;
	}
}
