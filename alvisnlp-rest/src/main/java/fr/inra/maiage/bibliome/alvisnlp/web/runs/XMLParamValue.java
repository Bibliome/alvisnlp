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

package fr.inra.maiage.bibliome.alvisnlp.web.runs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ParameterException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Sequence;
import fr.inra.maiage.bibliome.alvisnlp.core.plan.PlanException;
import fr.inra.maiage.bibliome.alvisnlp.core.plan.PlanLoader;
import fr.inra.maiage.bibliome.util.service.UnsupportedServiceException;

public class XMLParamValue extends ParamValue<Element> {
	protected XMLParamValue(String name, Element value) {
		super(ParamValue.METHOD_XML, name, value);
	}

	@Override
	public void setParam(Logger logger, PlanLoader<Corpus> planLoader, Sequence<Corpus> plan) throws ParameterException, UnsupportedServiceException, PlanException, ConverterException, SAXException, IOException, URISyntaxException {
		planLoader.setParam(logger, getValue(), plan);
	}

	@Override
	protected void fillXMLValue(Element elt) {
		Document doc = elt.getOwnerDocument();
		Node ev = doc.importNode(getValue(), true);
		elt.appendChild(ev);
	}
}
