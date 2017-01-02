package fr.jouy.inra.maiage.bibliome.alvis.web.runs;

import java.io.IOException;
import java.net.URISyntaxException;

import org.bibliome.util.service.UnsupportedServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import alvisnlp.converters.ConverterException;
import alvisnlp.corpus.Corpus;
import alvisnlp.module.ParameterException;
import alvisnlp.module.Sequence;
import alvisnlp.plan.PlanException;
import alvisnlp.plan.PlanLoader;

public class XMLParamValue extends ParamValue<Element> {
	protected XMLParamValue(String name, Element value) {
		super(ParamValue.METHOD_XML, name, value);
	}

	@Override
	public void setParam(PlanLoader<Corpus> planLoader, Sequence<Corpus> plan) throws ParameterException, UnsupportedServiceException, PlanException, ConverterException, SAXException, IOException, URISyntaxException {
		planLoader.setParam(getValue(), plan);
	}

	@Override
	protected void fillXMLValue(Element elt) {
		Document doc = elt.getOwnerDocument();
		Node ev = doc.importNode(getValue(), true);
		elt.appendChild(ev);
	}
}
