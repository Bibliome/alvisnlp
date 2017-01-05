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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import alvisnlp.corpus.Corpus;
import alvisnlp.module.Sequence;
import alvisnlp.plan.PlanLoader;
import fr.jouy.inra.maiage.bibliome.alvis.web.runs.PlanBuilder;

@Path("/plans")
public class PlanResource extends DocumentableResource<String,Sequence<Corpus>> {
	private final PlanBuilder planBuilder;
	private PlanLoader<Corpus> planLoader;

	public PlanResource(@Context ServletContext servletContext, @Context UriInfo uriInfo) throws SecurityException, IllegalArgumentException {
		super(servletContext, uriInfo, "alvisnlp-supported-plans", "plan-item", "plan", "plans");
		planBuilder = new PlanBuilder(servletContext);
	}

	private static enum PlanFilter implements FilenameFilter {
		INTANCE;

		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".plan");
		}
	}
	
	@Override
	protected Iterable<String> getKeyList() throws Exception {
		List<String> result = new ArrayList<String>();
		for (File file : planBuilder.getPlanDir().listFiles(PlanFilter.INTANCE)) {
			String fname = file.getName();
			String planName = fname.substring(0, fname.lastIndexOf('.'));
			result.add(planName);
		}
		return result;
	}

	@Override
	protected String getShortName(String key) {
		return key;
	}

	@Override
	protected String getFullName(String key) {
		return key;
	}

	@Override
	protected Sequence<Corpus> getItem(String key) throws Exception {
		File file = new File(planBuilder.getPlanDir(), key + ".plan");
		if (!file.exists()) {
			return null;
		}
		if (planLoader == null) {
			planLoader = planBuilder.createPlanLoader();
		}
		return planBuilder.buildPlan(planLoader, key);
	}

	@Override
	protected void doSupplement(Document doc, Sequence<Corpus> item) throws XPathExpressionException {
		Element docElt = doc.getDocumentElement();
		docElt.setAttribute("target", item.getId());
		docElt.setAttribute("short-target", item.getId());
	}
}
