/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

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


package org.bibliome.alvisnlp.modules.rdf;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

public class ElementResourceMap implements ResourceBuilder {
	private final Model model;
	private final Map<Element,Resource> resources = new HashMap<Element,Resource>();

	public ElementResourceMap(Model model) {
		super();
		this.model = model;
	}
	
	public void set(Element elt, Resource res) {
		resources.put(elt, res);
	}

	private Resource getResource(String uri) {
		if (uri == null || uri.isEmpty()) {
			return model.createResource();
		}
		return model.createResource(uri);
	}

	public void set(Element elt, String uri) {
		set(elt, getResource(uri));
	}
	
	public Resource get(Element elt) {
		if (resources.containsKey(elt)) {
			return resources.get(elt);
		}
		throw new RuntimeException("no resource associated with " + elt);
	}

	@Override
	public Resource createNode(EvaluationContext ctx, Element elt) {
		return get(elt);
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
	}
}
