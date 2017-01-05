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

import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.xml.transform.TransformerConfigurationException;

import org.bibliome.util.service.AmbiguousAliasException;
import org.bibliome.util.service.UnsupportedServiceException;
import org.w3c.dom.Document;

import alvisnlp.corpus.Corpus;
import alvisnlp.factory.CompoundCorpusModuleFactory;
import alvisnlp.factory.CorpusModuleFactory;
import alvisnlp.module.Module;

@Path("/modules")
public class ModuleResource extends DocumentableResource<Class<? extends Module<Corpus>>,Module<Corpus>> {
	private final CompoundCorpusModuleFactory moduleFactory;
	
	public ModuleResource(@Context ServletContext servletContext, @Context UriInfo uriInfo) {
		super(servletContext, uriInfo, "alvisnlp-supported-modules", "module-item", "module", "modules");
		this.moduleFactory = new CompoundCorpusModuleFactory();
		this.moduleFactory.loadServiceFactories(CorpusModuleFactory.class, null, null, Logger.getLogger("alvisnlp4rest"));
	}

	@Override
	protected Iterable<Class<? extends Module<Corpus>>> getKeyList() throws ClassNotFoundException, TransformerConfigurationException {
		return moduleFactory.supportedServices();
	}

	@Override
	protected String getShortName(Class<? extends Module<Corpus>> key) {
		return key.getSimpleName();
	}

	@Override
	protected String getFullName(Class<? extends Module<Corpus>> key) {
		return key.getCanonicalName();
	}

	@Override
	protected Module<Corpus> getItem(String key) throws ClassNotFoundException, TransformerConfigurationException, AmbiguousAliasException {
		try {
			return moduleFactory.getServiceByAlias(key);
		}
		catch (UnsupportedServiceException e) {
			return null;
		}
	}

	@Override
	protected Module<Corpus> getItem(Class<? extends Module<Corpus>> key) throws Exception {
		return moduleFactory.getService(key);
	}

	@Override
	protected void doSupplement(Document doc, Module<Corpus> item) throws Exception {
	}
}
