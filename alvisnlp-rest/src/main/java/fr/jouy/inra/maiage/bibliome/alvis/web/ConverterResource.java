package fr.jouy.inra.maiage.bibliome.alvis.web;

import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.w3c.dom.Document;

import alvisnlp.app.cli.AbstractAlvisNLP;
import alvisnlp.converters.ParamConverter;
import alvisnlp.converters.ParamConverterFactory;

@Path("/converters")
public class ConverterResource extends DocumentableResource<Class<?>,ParamConverter> {
	public ConverterResource(@Context ServletContext servletContext, @Context UriInfo uriInfo) {
		super(servletContext, uriInfo, "alvisnlp-supported-converters", "converter-item", "converter", "converters");
	}

	@Override
	protected Iterable<Class<?>> getKeyList() throws Exception {
		ParamConverterFactory factory = AbstractAlvisNLP.getParamConverterFactory();
		return factory.supportedServices();
	}

	@Override
	protected String getShortName(Class<?> key) {
		return key.getSimpleName();
	}

	@Override
	protected String getFullName(Class<?> key) {
		return key.getCanonicalName();
	}

	@Override
	protected ParamConverter getItem(Class<?> key) throws Exception {
		ParamConverterFactory factory = AbstractAlvisNLP.getParamConverterFactory();
		return factory.getService(key);
	}

	@Override
	protected ParamConverter getItem(String key) throws Exception {
		ParamConverterFactory factory = AbstractAlvisNLP.getParamConverterFactory();
		return factory.getServiceByAlias(key);
	}

	@Override
	protected void doSupplement(Document doc, ParamConverter item) {
	}
}
