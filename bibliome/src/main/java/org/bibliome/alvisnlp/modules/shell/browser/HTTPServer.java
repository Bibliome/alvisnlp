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


package org.bibliome.alvisnlp.modules.shell.browser;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import fi.iki.elonen.NanoHTTPD;

@SuppressWarnings("unused")
class HTTPServer extends NanoHTTPD {
	private final Logger logger;
	private final Corpus corpus;
	private final Map<String,Element> elementIndex;
	private final Map<String,Layer> layerIndex;
	private final BrowserShellResolvedObjects resObj;
	
	public HTTPServer(int port, Logger logger, Corpus corpus, BrowserShellResolvedObjects resObj) {
		super(port);
		this.logger = logger;
		this.corpus = corpus;
		this.elementIndex = indexElements(corpus);
		this.layerIndex = indexLayers(corpus);
		this.resObj = resObj;
	}
	
	private static Map<String,Layer> indexLayers(Corpus corpus) {
		Map<String,Layer> result = new HashMap<String,Layer>();
		for (Document doc : Iterators.loop(corpus.documentIterator())) {
			for (Section sec : Iterators.loop(doc.sectionIterator())) {
				for (Layer layer : sec.getAllLayers()) {
					String id = Integer.toHexString(System.identityHashCode(layer));
					result.put(id, layer);
				}
			}
		}
		return result;
	}
	
	private static Map<String,Element> indexElements(Corpus corpus) {
		Map<String,Element> result = new HashMap<String,Element>();
		indexElement(corpus, result);
		for (Document doc : Iterators.loop(corpus.documentIterator())) {
			indexElement(doc, result);
			for (Section sec : Iterators.loop(doc.sectionIterator())) {
				indexElement(sec, result);
				for (Annotation a : sec.getAllAnnotations()) {
					indexElement(a, result);
				}
				for (Relation rel : sec.getAllRelations()) {
					indexElement(rel, result);
					for (Tuple t : rel.getTuples()) {
						indexElement(t, result);
					}
				}
			}
		}
		return result;
	}
	
	private static void indexElement(Element elt, Map<String,Element> map) {
		String id = elt.getStringId();
		map.put(id, elt);
	}
	
	@Override
	public Response serve(IHTTPSession session) {
		return null;
	}
}
