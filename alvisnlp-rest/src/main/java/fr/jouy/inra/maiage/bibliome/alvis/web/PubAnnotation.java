package fr.jouy.inra.maiage.bibliome.alvis.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.xml.sax.SAXException;

import alvisnlp.corpus.Corpus;
import alvisnlp.module.Sequence;

import com.sun.jersey.api.core.HttpContext;

import fr.jouy.inra.maiage.bibliome.alvis.web.executor.AlvisNLPExecutor;
import fr.jouy.inra.maiage.bibliome.alvis.web.runs.PlanBuilder;
import fr.jouy.inra.maiage.bibliome.alvis.web.runs.Run;

@Path("/pubannotation")
public class PubAnnotation extends AbstractResource {
	public static final String JSON_OUTPUT_FILE = "pubannotation.json";

	private final File rootProcessingDir;
	private final RunResource runResource;
	
	public PubAnnotation(@Context ServletContext servletContext, @Context UriInfo uriInfo) throws SecurityException, IllegalArgumentException {
		super(servletContext, uriInfo);
		this.rootProcessingDir = AlvisNLPContextParameter.ROOT_PROCESSING_DIR.getFileValue(servletContext);
		this.runResource = new RunResource(servletContext, uriInfo);
	}

	@GET
	@Path("/plans/{plan}")
	public Response annotate(
			@Context ServletContext servletContext,
			@Context HttpContext httpContext,
			@PathParam("plan") String planName,
			@QueryParam("text") @DefaultValue("") String text,
			@QueryParam("sourcedb") @DefaultValue("") String sourcedb,
			@QueryParam("sourceid") @DefaultValue("") String sourceid
			) throws Exception {
		PlanBuilder planBuilder = runResource.getPlanBuilder();
		Sequence<Corpus> plan = planBuilder.buildPlan(planName);
		AlvisNLPExecutor executor = RunResource.getExecutor(servletContext);
		Run run = runResource.createRun(plan, httpContext, null, executor);
		injectInputText(run, text, sourcedb, sourceid);
		planBuilder.setParams(run, plan);
		planBuilder.check(plan);
		run.execute(servletContext, planBuilder, false);
		return createRunResponse(run);
	}

	private Response createRunResponse(Run run) throws MalformedURLException {
		URL url = new URL(getURLBase() + "/api/pubannotation/annotations/" + run.getId());
		return Response
				.status(Status.SEE_OTHER)
				.header("Location", url)
				.build();
	}

	private static void injectInputText(Run run, String text, String sourcedb, String sourceid) throws IOException {
		if (!sourcedb.isEmpty() && !sourceid.isEmpty()) {
			URL url = new URL(String.format("http://pubannotation.org/docs/sourcedb/%s/sourceid/%s.txt", sourcedb, sourceid));
			URLConnection conn = url.openConnection();
			conn.connect();
			try (InputStream is = conn.getInputStream()) {
				run.addUploadParamValue("text", String.format("%s:%s", sourcedb, sourceid), is);
			}
		}
		else {
			run.addTextParamValue("text", text);
		}
	}

	@GET
	@Path("/annotations/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetch(@PathParam("id") String id) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SAXException, IOException {
		Run run = Run.read(rootProcessingDir, id);
		if (run == null || run.isCancelled()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		if (!run.isFinished()) {
			return Response
					.status(Status.SERVICE_UNAVAILABLE)
					.header("Retry-After", 10)
					.build();
		}
		File outputDir = run.getOutputDir();
		File f = new File(outputDir, JSON_OUTPUT_FILE);
		if (!f.exists()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		if (f.isFile()) {
			InputStream is = new FileInputStream(f);
			return Response.ok(is).build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}
}
