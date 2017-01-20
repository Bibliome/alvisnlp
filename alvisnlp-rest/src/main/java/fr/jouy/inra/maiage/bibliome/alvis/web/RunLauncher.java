package fr.jouy.inra.maiage.bibliome.alvis.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import alvisnlp.corpus.Corpus;
import alvisnlp.module.Sequence;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import fr.jouy.inra.maiage.bibliome.alvis.web.executor.AlvisNLPExecutor;
import fr.jouy.inra.maiage.bibliome.alvis.web.runs.ParamValue;
import fr.jouy.inra.maiage.bibliome.alvis.web.runs.PlanBuilder;
import fr.jouy.inra.maiage.bibliome.alvis.web.runs.Run;

public abstract class RunLauncher extends AbstractResource {
	protected final File rootProcessingDir;
	protected final PlanBuilder planBuilder;

	protected RunLauncher(ServletContext servletContext, UriInfo uriInfo) throws SecurityException, IllegalArgumentException {
		super(servletContext, uriInfo);
		this.rootProcessingDir = AlvisNLPContextParameter.ROOT_PROCESSING_DIR.getFileValue(servletContext);
		planBuilder = new PlanBuilder(servletContext);
	}

	protected Run createRun(Sequence<Corpus> plan, HttpContext httpContext, MultivaluedMap<String,String> formParams, FormDataMultiPart formData, AlvisNLPExecutor executor, String... excludedParams) throws IOException {
		Run result = new Run(rootProcessingDir, plan, executor);
		if (formData != null) {
			setFormParams(formData, result, excludedParams);
		}
		if (formParams != null) {
			setMultivaluedMapParams(formParams, result, excludedParams);
		}
		if (httpContext != null) {
			HttpRequestContext requestContext = httpContext.getRequest();
			MultivaluedMap<String,String> params = requestContext.getQueryParameters();
			setMultivaluedMapParams(params, result, excludedParams);
		}
		result.write();
		return result;
	}

	private static void setFormParams(FormDataMultiPart formData, Run run, String... excluded) throws IOException {
		Collection<String> ex = new HashSet<String>(Arrays.asList(excluded));
		Map<String,List<FormDataBodyPart>> formFields = formData.getFields();
		for (Map.Entry<String,List<FormDataBodyPart>> e : formFields.entrySet()) {
			String name = e.getKey();
			if (ex.contains(name)) {
				continue;
			}
			List<FormDataBodyPart> fields = e.getValue();
			if (fields.isEmpty()) {
				continue;
			}
			FormDataBodyPart field = fields.get(fields.size() - 1);
			if (name.startsWith(ParamValue.METHOD_UPLOAD + "-")) {
				name = name.substring(7);
				ContentDisposition cd = field.getContentDisposition();
				
				run.addUploadParamValue(name, cd.getFileName(), field.getValueAs(InputStream.class));
			}
			else {
				String value = field.getValue();
				setParam(run, name, value);
			}
		}
	}
	
	private static void setMultivaluedMapParams(MultivaluedMap<String,String> params, Run run, String... excluded) throws IOException {
		Collection<String> ex = new HashSet<String>(Arrays.asList(excluded));
		for (Map.Entry<String,List<String>> e : params.entrySet()) {
			String name = e.getKey();
			if (ex.contains(name)) {
				continue;
			}
			List<String> values = e.getValue();
			if (values.isEmpty()) {
				continue;
			}
			String value = values.get(values.size() - 1);
			setParam(run, name, value);
		}
	}

	protected static void setParam(Run run, String name, String value) throws IOException {
		if (name.startsWith(ParamValue.METHOD_XML + "-")) {
			name = name.substring(4);
			run.addXMLParamValue(name, value);
		}
		else if (name.startsWith(ParamValue.METHOD_TEXT + "-")) {
			name = name.substring(5);
			run.addTextParamValue(name, value);
		}
		else {
			run.addStringParamValue(name, value);
		}
	}
	
	protected static AlvisNLPExecutor getExecutor(ServletContext servletContext) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String executorClassName = AlvisNLPContextParameter.EXECUTOR_CLASS.getStringValue(servletContext);
		Class<?> klass = Class.forName(executorClassName);
		if (!AlvisNLPExecutor.class.isAssignableFrom(klass)) {
			throw new RuntimeException("the class " + executorClassName + " provided by " + AlvisNLPContextParameter.EXECUTOR_CLASS.key + " is not a sub-class of " + AlvisNLPExecutor.class.getName());
		}
		return (AlvisNLPExecutor) klass.newInstance();
	}
}
