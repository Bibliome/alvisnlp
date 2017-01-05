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

package fr.jouy.inra.maiage.bibliome.alvis.web;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

import alvisnlp.documentation.Documentable;
import alvisnlp.documentation.Documentation;

public abstract class DocumentableResource<K,D extends Documentable> extends AbstractResource {
	private final String listTag;
	private final String itemTag;
	private final String resourceType;
	private final String resourceList;

	protected DocumentableResource(ServletContext servletContext, UriInfo uriInfo, String listTag, String itemTag, String resourceType, String resourceList) {
		super(servletContext, uriInfo);
		this.listTag = listTag;
		this.itemTag = itemTag;
		this.resourceType = resourceType;
		this.resourceList = resourceList;
	}

	protected abstract Iterable<K> getKeyList() throws Exception;
	
	protected abstract String getShortName(K key);
	
	protected abstract String getFullName(K key);

	protected abstract D getItem(K key) throws Exception;

	private Document getKeyListAsXML(boolean synopsis) throws Exception {
		Document result = createListDocument();
		Element root = XMLUtils.createRootElement(result, listTag);
		addURLBase(root);
		for (K key : getKeyList()) {
			Element converterElt = createKeyElement(result, root, key);
			if (synopsis) {
				D item = getItem(key);
				Document doc = getDocument(item);
				Element docElt = doc.getDocumentElement();
				cloneAttributes(converterElt, docElt);
				transferSynopsisElement(result, converterElt, docElt);
			}
		}
		return result;
	}
	
	private static void transferSynopsisElement(Document doc, Element converterElt, Element docElt) {
		for (Element child : XMLUtils.childrenElements(docElt)) {
			String tagName = child.getTagName();
			if (tagName.equals("synopsis")) {
				converterElt.appendChild(doc.adoptNode(child.cloneNode(true)));
			}
		}
	}
	
	private static void cloneAttributes(Element converterElt, Element docElt) {
		NamedNodeMap docAttr = docElt.getAttributes();
		for (int i = 0; i < docAttr.getLength(); ++i) {
			Node a = docAttr.item(i);
			String n = a.getNodeName();
			String v = a.getNodeValue();
			converterElt.setAttribute(n, v);
		}
	}
	
	private Element createKeyElement(Document doc, Element parent, K key) {
		String name = getShortName(key);
		String fullName = getFullName(key);
		Element result = XMLUtils.createElement(doc, parent, 4, itemTag);
		result.setAttribute("short-target", name);
		result.setAttribute("target", fullName);
		return result;
	}
	
	private Document createListDocument() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		Document result = docBuilder.newDocument();
		addStylesheet(result);
		return result;
	}
	
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_XML)
	public Document getSimpleKeyList(@DefaultValue("false") @QueryParam("synopsis") boolean synopsis) throws Exception {
		return getKeyListAsXML(synopsis);
	}

	protected abstract D getItem(String key) throws Exception;

	private void supplement(Document doc, D item) throws Exception {
		Element root = doc.getDocumentElement();
		root.setAttribute("resource-type", resourceType);
		root.setAttribute("resource-list", resourceList);
		doSupplement(doc, item);
	}
	
	protected abstract void doSupplement(Document doc, D item) throws Exception;

	private void addStylesheet(Document doc) {
		for (Node node : XMLUtils.childrenNodes(doc)) {
			if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
				ProcessingInstruction pi = (ProcessingInstruction) node;
				if (pi.getTarget().equals("xml-stylesheet")) {
					return;
				}
			}
		}
		ProcessingInstruction stylesheetPI = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\""+getURLBase()+"/static/style/alvisnlp-doc2xhtml.xslt\"");
		Element root = doc.getDocumentElement();
		doc.insertBefore(stylesheetPI, root);
	}
	
	private Document getDocument(D item) throws Exception {
		Documentation documentation = item.getDocumentation();
		Document doc = documentation.getDocument();
		addURLBase(doc);
		addStylesheet(doc);
		supplement(doc, item);
		return doc;
	}
	
	private void addURLBase(Element elt) {
		elt.setAttribute("url-base", getURLBase());
	}

	private void addURLBase(Document doc) {
		Element elt = doc.getDocumentElement();
		addURLBase(elt);
	}

	@GET
	@Path("/{key}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getDocumentation(@PathParam("key") String key) throws Exception {
		D item = getItem(key);
		if (item == null) {
			return Response.status(404).build();
		}
		Document doc = getDocument(item);
		return Response.ok(doc).build();
	}
}
