package fr.jouy.inra.maiage.bibliome.alvis.web;

import java.util.ServiceLoader;

import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.w3c.dom.Document;

import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;

@Path("/libraries")
public class LibraryResource extends DocumentableResource<FunctionLibrary,FunctionLibrary> {
	public LibraryResource(@Context ServletContext servletContext, @Context UriInfo uriInfo) {
		super(servletContext, uriInfo, "alvisnlp-supported-libraries", "library-item", "library", "libraries");
	}

	@Override
	protected Iterable<FunctionLibrary> getKeyList() {
		Class<FunctionLibrary> klass = FunctionLibrary.class;
		return ServiceLoader.load(klass, klass.getClassLoader());
	}

	@Override
	protected String getShortName(FunctionLibrary key) {
		return key.getName();
	}

	@Override
	protected String getFullName(FunctionLibrary key) {
		return key.getName();
	}

	@Override
	protected FunctionLibrary getItem(String key) throws ResolverException {
		LibraryResolver resolver = new LibraryResolver();
		Class<FunctionLibrary> klass = FunctionLibrary.class;
		for (FunctionLibrary lib : ServiceLoader.load(klass, klass.getClassLoader())) {
			resolver.addLibrary(lib);
		}
		try {
			return resolver.resolveLibrary(key);
		}
		catch (ResolverException e) {
			return null;
		}
	}

	@Override
	protected FunctionLibrary getItem(FunctionLibrary key) throws Exception {
		return key;
	}

	@Override
	protected void doSupplement(Document doc, FunctionLibrary item) {
	}
}
