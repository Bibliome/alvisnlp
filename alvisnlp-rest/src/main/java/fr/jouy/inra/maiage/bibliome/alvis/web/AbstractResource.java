package fr.jouy.inra.maiage.bibliome.alvis.web;

import javax.servlet.ServletContext;
import javax.ws.rs.core.UriInfo;

public abstract class AbstractResource {
	static {
		// XXX
		// Force the JVM to load a class from the Module Factory.
		// Otherwise META-INF/services is not loaded
		forceLoadClass(org.bibliome.alvisnlp.BibliomeModuleFactory.class);
		forceLoadClass(alvisnlp.corpus.expressions.ConstantsLibrary.class);
	}
	
	private static void forceLoadClass(Class<?> klass) {
		System.err.println("Force load: " + klass.getCanonicalName());
	}

	private final String urlBase;

	protected AbstractResource(ServletContext servletContext, UriInfo uriInfo) {
		String urlBase = AlvisNLPContextParameter.URL_BASE.getStringValue(servletContext);
		if (urlBase == null) {
			this.urlBase = uriInfo.getBaseUri().resolve("..").toString();
		}
		else {
			this.urlBase = urlBase;
		}
	}

	protected String getURLBase() {
		return urlBase;
	}
}
