/*
Copyright 2017 Institut National de la Recherche Agronomique

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

package fr.jouy.inra.maiage.bibliome.alvis.web.runs;

import java.io.IOException;
import java.net.URISyntaxException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import alvisnlp.converters.ConverterException;
import alvisnlp.corpus.Corpus;
import alvisnlp.module.ParameterException;
import alvisnlp.module.Sequence;
import alvisnlp.plan.PlanException;
import alvisnlp.plan.PlanLoader;
import fr.inra.maiage.bibliome.util.service.UnsupportedServiceException;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

public abstract class ParamValue<T> {
	public static final String METHOD_XML = "xml";
	public static final String METHOD_UPLOAD = "upload";
	public static final String METHOD_TEXT = "text";
	public static final String METHOD_STRING = "string";
	
	private final String method;
	private final String name;
	private final T value;
	
	protected ParamValue(String method, String name, T value) {
		super();
		this.method = method;
		this.name = name;
		this.value = value;
	}
	
	public static ParamValue<?> create(Element elt) {
		String name = elt.getAttribute("name");
		String method = elt.getAttribute("method");
		switch (method) {
			case METHOD_STRING: {
				String value = elt.getTextContent();
				return new StringParamValue(name, value);
			}
			case METHOD_TEXT: {
				String value = elt.getTextContent();
				return new TextParamValue(name, value);
			}
			case METHOD_UPLOAD: {
				String value = elt.getTextContent();
				return new UploadParamValue(name, value);
			}
			case METHOD_XML: {
				Element value = XMLUtils.childrenElements(elt).get(0);
				return new XMLParamValue(name, value);
			}
		}
		throw new RuntimeException();
	}
	
	public String getMethod() {
		return method;
	}


	public String getName() {
		return name;
	}


	public T getValue() {
		return value;
	}
	
	public Element toXML(Document doc, Element parent) {
		Element result = XMLUtils.createElement(doc, parent, -1, "param-value");
		result.setAttribute("method", method);
		result.setAttribute("name", name);
		fillXMLValue(result);
		return result;
	}

	public abstract void setParam(PlanLoader<Corpus> planLoader, Sequence<Corpus> plan) throws ParameterException, UnsupportedServiceException, PlanException, ConverterException, SAXException, IOException, URISyntaxException;
	
	protected abstract void fillXMLValue(Element elt);
}
