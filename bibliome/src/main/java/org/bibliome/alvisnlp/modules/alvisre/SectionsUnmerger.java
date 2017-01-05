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


package org.bibliome.alvisnlp.modules.alvisre;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.AnnotationCreator;

public class SectionsUnmerger {
	private final NavigableMap<Integer,Section> offsets = new TreeMap<Integer,Section>();
	private final Map<String,Element> elementIndex = new HashMap<String,Element>();
	private final Map<Element,String> idIndex = new HashMap<Element,String>();
	private int nextTokenId = 0;
	private int nextRelationId = 0;
	
	SectionsUnmerger() {
		super();
	}
	
	void setOffset(int offset, Section sec) {
		offsets.put(offset, sec);
	}
	
	String addToken(Element elt) {
		elt = elt.getOriginal();
		String id = "T" + (++nextTokenId);
		elementIndex.put(id, elt);
		idIndex.put(elt, id);
		return id;
	}

	String addRelation(Element elt) {
		elt = elt.getOriginal();
		String id = "R" + (++nextRelationId);
		elementIndex.put(id, elt);
		idIndex.put(elt, id);
		return id;
	}

	public Element getElement(String id) {
		if (elementIndex.containsKey(id)) {
			return elementIndex.get(id);
		}
		throw new IllegalArgumentException("unknown element with id: " + id);
	}

	public String getId(Element elt) {
		elt = elt.getOriginal();
		if (idIndex.containsKey(elt)) {
			return idIndex.get(elt);
		}
		throw new IllegalArgumentException("unknown id for element: " + elt);
	}

	public Layer getAnnotation(AnnotationCreator ac, String layerName, int start, int end, SectionsUnmerger.SearchOperation op) {
		Map.Entry<Integer,Section> e = offsets.floorEntry(start);
		if (e == null) {
			
		}
		int offset = e.getKey();
		int trueStart = start - offset;
		int trueEnd = end - offset;
		Section section = e.getValue();
		int sectionEnd = section.getContents().length();
		if (trueStart > sectionEnd) {
			throw new RuntimeException();
		}
		if (trueEnd > sectionEnd) {
			throw new RuntimeException();
		}
		return op.getAnnotation(ac, section, layerName, trueStart, trueEnd);
	}
	
	public static enum SearchOperation {
		SEARCH {
			@Override
			Layer getAnnotation(AnnotationCreator ac, Section section, String layerName, int start, int end) {
				return searchAnnotation(section, layerName, start, end);
			}
		},
		
		ENSURE {
			@Override
			Layer getAnnotation(AnnotationCreator ac, Section section, String layerName, int start, int end) {
				Layer result = searchAnnotation(section, layerName, start, end);
				if (result.isEmpty()) {
					createAnnotation(ac, section, layerName, start, end, result);
				}
				return result;
			}
		},
		
		CREATE {
			@Override
			Layer getAnnotation(AnnotationCreator ac, Section section, String layerName, int start, int end) {
				Layer result = new Layer(section);
				createAnnotation(ac, section, layerName, start, end, result);
				return result;
			}
		};

		abstract Layer getAnnotation(AnnotationCreator ac, Section section, String layerName, int start, int end);

		private static Annotation createAnnotation(AnnotationCreator ac, Section section, String layerName, int start, int end) {
			if (layerName == null) {
				return new Annotation(ac, section, start, end);
			}
			Layer layer = section.ensureLayer(layerName);
			return new Annotation(ac, layer, start, end);
		}
		
		private static void createAnnotation(AnnotationCreator ac, Section section, String layerName, int start, int end, Layer result) {
			Annotation a = createAnnotation(ac, section, layerName, start, end);
			result.add(a);
		}
		
		private static Layer searchAnnotation(Section section, String layerName, int start, int end) {
			Layer result = new Layer(section);
			for (Layer layer : getLayers(section, layerName)) {
				Layer found = layer.span(start, end);
				result.addAll(found);
			}
			return result;
		}
		
		private static Collection<Layer> getLayers(Section section, String layerName) {
			if (layerName == null) {
				return section.getAllLayers();
			}
			if (section.hasLayer(layerName)) {
				Layer layer = section.getLayer(layerName);
				return Collections.singleton(layer);
			}
			return Collections.emptyList();
		}
	}
}
