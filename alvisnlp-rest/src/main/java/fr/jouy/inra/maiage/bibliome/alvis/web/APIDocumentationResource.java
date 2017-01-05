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

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

@Path("")
public class APIDocumentationResource extends AbstractResource {
	public APIDocumentationResource(@Context ServletContext servletContext, @Context UriInfo uriInfo) {
		super(servletContext, uriInfo);
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/doc")
	public Document getAPIDocumentation() throws SAXException, IOException {
		InputStream is = APIDocumentationResource.class.getResourceAsStream("apidoc.xml");
		Document doc = XMLUtils.docBuilder.parse(is);
		Element root = doc.getDocumentElement();
		root.setAttribute("url-base", getURLBase());
		ProcessingInstruction stylesheetPI = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\""+getURLBase()+"/static/style/apidoc2xhtml.xslt\"");
		doc.insertBefore(stylesheetPI, root);
		return doc;
	}
}
