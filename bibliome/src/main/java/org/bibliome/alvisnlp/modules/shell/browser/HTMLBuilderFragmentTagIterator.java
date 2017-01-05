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


package org.bibliome.alvisnlp.modules.shell.browser;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bibliome.util.fragments.FragmentTag;
import org.bibliome.util.fragments.FragmentTagIterator;
import org.bibliome.util.fragments.FragmentTagType;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import alvisnlp.corpus.Annotation;

class HTMLBuilderFragmentTagIterator implements FragmentTagIterator<String,Annotation> {
	private final Map<Element,Annotation> elementAnnotations = new HashMap<Element,Annotation>();
	private final Document document;
	private Element currentElement;
	
	HTMLBuilderFragmentTagIterator(Document document) {
		super();
		this.document = document;
		currentElement = XMLUtils.createRootElement(document, "div");
		addClass(currentElement, "section-contents");
	}
	
	HTMLBuilderFragmentTagIterator(DocumentBuilder docBuilder) {
		this(docBuilder.newDocument());
	}
	
	private static void addClass(Element elt, String klass) {
		if (elt.hasAttribute("class")) {
			elt.setAttribute("class", elt.getAttribute("class") + " " + klass);
		}
		else {
			elt.setAttribute("class", klass);
		}
	}
	
	private Element createElement(Annotation a, String klass) {
		Element result = XMLUtils.createElement(document, currentElement, 0, "span");
		addClass(result, "fragment");
		elementAnnotations.put(result, a);
		String id = a.getStringId();
		addClass(result, "fragment-" + id);
		result.setAttribute("annotation-id", id);
		if (klass != null) {
			addClass(result, klass);
		}
		return result;
	}

	@Override
	public void handleTag(String param, FragmentTag<Annotation> tag) {
		Annotation a = tag.getFragment();
		FragmentTagType tagType = tag.getTagType();
		switch (tagType) {
			case EMPTY: {
				createElement(a, "empty-fragment");
				break;
			}
			case OPEN: {
				currentElement = createElement(a, "first-fragment");
				break;
			}
			case CLOSE: {
				List<Annotation> split = new LinkedList<Annotation>();
				while (elementAnnotations.containsKey(currentElement)) {
					Annotation ca = elementAnnotations.get(currentElement);
					currentElement = (Element) currentElement.getParentNode();
					if (a.equals(ca)) {
						addClass(currentElement, "last-fragment");
						break;
					}
					split.add(0, ca);
				}
				for (Annotation sa : split) {
					currentElement = createElement(sa, null);
				}
				break;
			}
		}
	}

	private void addText(String s, int from, int to) {
		XMLUtils.createText(document, currentElement, s.substring(from, to));
	}
	
	@Override
	public void handleGap(String param, int from, int to) {
		addText(param, from, to);
	}

	@Override
	public void handleHead(String param, int to) {
		addText(param, 0, to);
	}

	@Override
	public void handleTail(String param, int from) {
		addText(param, from, param.length());
		stratify();
	}
	
	private int stratify(Map<Annotation,AtomicInteger> strates, Element elt) {
		int result = -1;
		for (Element child : XMLUtils.childrenElements(elt)) {
			int s = stratify(strates, child);
			if (s > result) {
				result = s;
			}
		}
		result++;
		if (elementAnnotations.containsKey(elt)) {
			Annotation a = elementAnnotations.get(elt);
			if (strates.containsKey(a)) {
				AtomicInteger strate = strates.get(a);
				if (result > strate.get()) {
					strate.set(result);
				}
			}
			else {
				strates.put(a, new AtomicInteger(result));
			}
		}
		return result;
	}
	
	private void stratify() {
		Map<Annotation,AtomicInteger> strates = new HashMap<Annotation,AtomicInteger>();
		stratify(strates, document.getDocumentElement());
		for (Map.Entry<Element,Annotation> e : elementAnnotations.entrySet()) {
			Element elt = e.getKey();
			Annotation a = e.getValue();
			AtomicInteger s = strates.get(a);
			addClass(elt, "strate-" + s);
		}
	}
	
	String serialize() throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		return writer.getBuffer().toString();
	}
}
