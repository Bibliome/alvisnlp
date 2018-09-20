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


package fr.inra.maiage.bibliome.alvisnlp.core.documentation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.inra.maiage.bibliome.util.Pair;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

class XMLDocumentationResourceBundleControl extends ResourceBundle.Control {
	static final String FORMAT_DOCUMENTATION = "alvisnlp.documentation";
	static final XMLDocumentationResourceBundleControl INSTANCE = new XMLDocumentationResourceBundleControl();

	@Override
	public List<String> getFormats(String arg0) {
		return Collections.unmodifiableList(Arrays.asList(FORMAT_DOCUMENTATION));
	}
	
	private static InputStream openInputStream(String resName, ClassLoader loader, boolean reload) throws IOException {
		if (reload) {
			URL url = loader.getResource(resName);
			if (url != null) {
				URLConnection urlCx = url.openConnection();
				urlCx.setUseCaches(false);
				return urlCx.getInputStream();
			}
			return null;
		}
		return loader.getResourceAsStream(resName);
	}
	
	private static InputStream safeOpenInputStream(String resName, ClassLoader loader, boolean reload) throws IOException {
		return openInputStream(resName, loader, reload);
	}
	
	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
		if (!FORMAT_DOCUMENTATION.equals(format))
			throw new IllegalArgumentException("unknown format: " + format);
		String bundleName = toBundleName(baseName, locale);
		String resName = toResourceName(bundleName, "xml");
		try (InputStream is = safeOpenInputStream(resName, loader, reload)) {
			if (is == null) {
				return null;
			}
			Document document = XMLUtils.docBuilder.parse(is);
			replaceIncludes(document, locale, loader);
			return new XMLDocumentationResourceBundle(document);
		}
		catch (SAXException saxe) {
			throw new IllegalArgumentException(saxe);
		}
	}
	
	private static void replaceIncludes(Document doc, Locale locale, ClassLoader loader) {
		NodeList includes = doc.getElementsByTagName("include-doc");
		Collection<Pair<Element,Collection<Node>>> inserts = new ArrayList<>();
		for (int i = 0; i < includes.getLength(); ++i) {
			Element include = (Element) includes.item(i);
			if (isElementInPre(include)) {
				continue;
			}
			String baseName = include.getTextContent().trim();
			ResourceBundle rb = null;
			try {
				rb = ResourceBundle.getBundle(baseName, locale, loader, XMLDocumentationResourceBundleControl.INSTANCE);
			}
			catch (MissingResourceException e) {
				System.err.println("ERROR: could not find included document: " + baseName);
				return;
			}
			Document included = (Document) rb.getObject(XMLDocumentationResourceBundle.DOCUMENTATION);
			Collection<Node> replacements = new ArrayList<Node>();
			for (Node n : XMLUtils.childrenNodes(included.getDocumentElement())) {
				Node cpy = doc.importNode(n, true);
				replacements.add(cpy);
			}
			inserts.add(new Pair<Element,Collection<Node>>(include, replacements));
		}
		for (Pair<Element,Collection<Node>> p : inserts) {
			Node parent = p.first.getParentNode();
			parent.removeChild(p.first);
			for (Node n : p.second) {
				parent.appendChild(n);
			}
		}
	}

	private static boolean isElementInPre(Element include) {
		for (Node e = include; e != null; e = e.getParentNode()) {
			String name = e.getNodeName();
			if (name.equals("pre") || name.equals("code")) {
				return true;
			}
		}
		return false;
	}
}
