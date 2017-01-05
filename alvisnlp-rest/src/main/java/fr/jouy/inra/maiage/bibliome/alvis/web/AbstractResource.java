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
import javax.ws.rs.core.UriInfo;

public abstract class AbstractResource {
	static {
		// XXX
		// Force the JVM to load a class from the Module Factory.
		// Otherwise META-INF/services is not loaded
		try {
			forceLoadClass("org.bibliome.alvisnlp.BibliomeModuleFactory");
			forceLoadClass("alvisnlp.corpus.expressions.ConstantsLibrary");
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void forceLoadClass(String name) throws ClassNotFoundException {
		System.err.println("Force load: " + name);
		Class.forName(name);
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
