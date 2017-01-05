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
