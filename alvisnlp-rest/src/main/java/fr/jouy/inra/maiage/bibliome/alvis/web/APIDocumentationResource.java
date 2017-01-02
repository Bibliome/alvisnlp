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
