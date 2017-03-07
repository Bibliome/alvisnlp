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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.bibliome.util.Strings;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

import alvisnlp.converters.ConverterException;
import alvisnlp.corpus.Corpus;
import alvisnlp.module.MissingParameterException;
import alvisnlp.module.ParameterValueConstraintException;
import alvisnlp.module.Sequence;
import alvisnlp.module.UnexpectedParameterException;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.multipart.FormDataMultiPart;

import fr.jouy.inra.maiage.bibliome.alvis.web.executor.AlvisNLPExecutor;
import fr.jouy.inra.maiage.bibliome.alvis.web.runs.Run;

@Path("/runs")
public class RunResource extends RunLauncher {
	public RunResource(@Context ServletContext servletContext, @Context UriInfo uriInfo) throws SecurityException, IllegalArgumentException {
		super(servletContext, uriInfo);
	}

	@POST
	@Path("")
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@Produces(MediaType.APPLICATION_XML)
	public Response run(
			@Context ServletContext servletContext,
			@Context HttpContext httpContext,
			@QueryParam("plan") String planName,
			@DefaultValue("false") @QueryParam("async") boolean async,
			FormDataMultiPart formData) throws Exception {
		Sequence<Corpus> plan = planBuilder.buildPlan(planName);;
		if (plan == null) {
			return createErrorRespose("missing plan name");
		}
		AlvisNLPExecutor executor = getExecutor(servletContext);
		Run run = createRun(plan, httpContext, null, formData, executor, "plan");
		try {
			planBuilder.setParams(run, plan);
			planBuilder.check(plan);
		}
		catch (ConverterException e) {
			return createErrorRespose("missing parameter " + e.getMessage());
		}
		catch (MissingParameterException e) {
			return createErrorRespose("missing parameter: " + e.getParameter());
		}
		catch (UnexpectedParameterException e) {
			return createErrorRespose("unexpected parameter: " + e.getParameter());
		}
		catch (ParameterValueConstraintException e) {
			return createErrorRespose("parameter out of range: " + e.getParameter());
		}
		if (async) {
			String planAsyncProp = plan.getProperty("async");
			boolean planAsync = planAsyncProp != null && Strings.getBoolean(planAsyncProp);
			if (!planAsync) {
				return createErrorRespose("asynchronous not allowed");
			}
		}
		run.execute(servletContext, planBuilder, async);
		return createRunResponse(run, async);
	}

	private Response createRunResponse(Run run, boolean async) throws MalformedURLException {
		Document doc = run.toXML(true);
		supplementDocument(doc);
		URL url = new URL(getURLBase() + "/api/runs/" + run.getId());
		Status status = async ? Status.OK : Status.ACCEPTED;
		return Response
				.status(status)
				.entity(doc)
				.header("Location", url)
				.build();
	}
	
	private static Response createErrorRespose(String message) {
		Document doc = XMLUtils.docBuilder.newDocument();
		Element root = XMLUtils.createRootElement(doc, "alvisnlp-error");
		root.setTextContent(message);
		return Response.status(422).entity(doc).build();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response status(@PathParam("id") String id) throws SAXException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Run run = Run.read(rootProcessingDir, id);
		if (run == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		if (run.isCancelled()) {
			return Response.status(Status.GONE).build();
		}
		Document doc = run.toXML(true);
		supplementDocument(doc);
		return Response.ok(doc).build();
	}
	
	private void supplementDocument(Document doc) {
		Element root = doc.getDocumentElement();
		root.setAttribute("url-base", getURLBase());
		ProcessingInstruction stylesheetPI = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\""+getURLBase()+"/static/style/alvisnlp-run2xhtml.xslt\"");
		doc.insertBefore(stylesheetPI, root);
	}

	@GET
	@Path("/{id}/output")
	public Response output(
			@PathParam("id") String id,
			@DefaultValue("false") @QueryParam("recdir") boolean recdir
			) throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		return output(id, "", recdir ? Integer.MAX_VALUE : 1);
	}

	@GET
	@Path("/{id}/output/{path:.*}")
	public Response output(
			@PathParam("id") String id,
			@PathParam("path") String path,
			@DefaultValue("false") @QueryParam("recdir") boolean recdir
			) throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		return output(id, path, recdir ? Integer.MAX_VALUE : 1);
	}

	private Response output(String id, String path, int depthLimit) throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Run run = Run.read(rootProcessingDir, id);
		if (run == null || !run.isFinished()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		if (run.isCancelled()) {
			return Response.status(Status.GONE).build();
		}
		File outputDir = run.getOutputDir();
		File f = new File(outputDir, path);
		if (!f.exists()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		if (f.isFile()) {
			InputStream is = new FileInputStream(f);
			return Response.ok(is).build();
		}
		if (f.isDirectory()) {
			if (depthLimit >= 0) {
				Document doc = run.toXML(true);
				supplementDocument(doc);
				Element runElt = doc.getDocumentElement();
				buildDirElement(doc, runElt, outputDir.toPath(), f, depthLimit);
				return Response
						.ok(doc, MediaType.APPLICATION_XML)
						.build();
			}
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	}

	private static Element createFileElement(Document dirDoc, Node parent, java.nio.file.Path outputDir, File f, String tag) throws IOException {
		Element result = XMLUtils.createElement(dirDoc, parent, -1, tag);
		result.setAttribute("name", f.getName());
		result.setAttribute("path", outputDir.relativize(f.toPath()).toString());
		long size = Files.size(f.toPath());
		result.setAttribute("size", Long.toString(size));
		result.setAttribute("human-size", getHumanSize(size));
		return result;
	}

	private static final String[] SIZE_UNITS = {
		"bytes",
		"KB",
		"MB",
		"GB"
	};

	private static String getHumanSize(long size) {
		long n = size;
		int u;
		for (u = 0; u < SIZE_UNITS.length && n > 1024; ++u) {
			n = n / 1024;
		}
		return String.format("%d %s", n, SIZE_UNITS[u]);
	}

	private void buildDirElement(Document dirDoc, Node parent, java.nio.file.Path outputDir, File f, int depthLimit) throws IOException {
		if (!f.exists()) {
			return;
		}
		if (!f.canRead()) {
			return;
		}
		if (f.isFile()) {
			createFileElement(dirDoc, parent, outputDir, f, "file");
			return;
		}
		if (f.isDirectory()) {
			Element dirElt = createFileElement(dirDoc, parent, outputDir, f, "dir");
			File[] children = f.listFiles();
			dirElt.setAttribute("empty", Boolean.toString(children.length == 0));
			if (depthLimit > 0) {
				int newDepthLimit = depthLimit - 1;
				if (children.length == 1 && newDepthLimit == 0) {
					newDepthLimit = 1;
				}
				for (File ch : children) {
					buildDirElement(dirDoc, dirElt, outputDir, ch, newDepthLimit);
				}
			}
		}
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response delete(@PathParam("id") String id) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SAXException, IOException {
		Run run = Run.read(rootProcessingDir, id);
		if (run == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		if (run.isCancelled()) {
			return Response.status(Status.GONE).build();
		}
		run.cancel();
		return Response.ok().build();
	}
}
