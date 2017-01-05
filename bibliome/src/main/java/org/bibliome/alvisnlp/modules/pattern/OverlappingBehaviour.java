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


package org.bibliome.alvisnlp.modules.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.AnnotationComparator;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;

public enum OverlappingBehaviour {
	IGNORE {
		@Override
		Collection<List<Element>> getSequences(Logger logger, Layer layer, AnnotationComparator comp) {
			logger.warning("matching on overlapping sequence");
			return Collections.singleton(layer.asElementList());
		}

		@Override
		public String toString() {
			return "ignore";
		}
	},
	
	REJECT {
		@Override
		Collection<List<Element>> getSequences(Logger logger, Layer layer, AnnotationComparator comp) {
			logger.warning("refusing to match overlapping sequences");
			return Collections.emptyList();
		}

		@Override
		public String toString() {
			return "reject";
		}
	},
	
	REMOVE_OVERLAPS {
		@Override
		Collection<List<Element>> getSequences(Logger logger, Layer layer, AnnotationComparator comp) {
			logger.warning("removing overlapping annotations");
			Layer cleanLayer = layer.getAnonymousCopy();
			cleanLayer.removeOverlaps(comp);
			return Collections.singleton(cleanLayer.asElementList());
		}

		@Override
		public String toString() {
			return "remove";
		}
	},
	
	MULTIPLEX {
		@Override
		Collection<List<Element>> getSequences(Logger logger, Layer layer, AnnotationComparator comp) {
			if (layer.isEmpty())
				return Collections.emptyList();
			Map<Element,Collection<Element>> order = getOrder(layer);
			Collection<List<Element>> result = new ArrayList<List<Element>>();
			List<Element> seed = new ArrayList<Element>();
			result.add(seed);
			while (true) {
				boolean stop = true;
				Collection<List<Element>> toAdd = new ArrayList<List<Element>>(2);
				for (List<Element> list : result)
					stop = nextStep(order, list, toAdd) && stop;
				result.addAll(toAdd);
				if (stop)
					break;
			}
			return result;
		}

		@Override
		public String toString() {
			return "multiplex";
		}
	};
	
	abstract Collection<List<Element>> getSequences(Logger logger, Layer layer, AnnotationComparator comp);

	private static Collection<Element> getSuccessors(Layer layer) {
		if (layer.isEmpty())
			return Collections.emptyList();
		int maxStart = layer.getSection().getContents().length();
		for (int i = 0; i < layer.size(); ++i) {
			Annotation a = layer.get(i);
			if (a.getStart() >= maxStart) {
				return layer.subLayer(0, i).asElementList();
			}
			if (a.getEnd() < maxStart) {
				maxStart = a.getEnd();
			}
		}
		return layer.asElementList();
	}
	
	private static Map<Element,Collection<Element>> getOrder(Layer layer) {
		Map<Element,Collection<Element>> result = new HashMap<Element,Collection<Element>>();
		result.put(null, getSuccessors(layer));
		for (Annotation a : layer)
			result.put(a, getSuccessors(layer.after(a.getEnd())));
		return result;
	}

	private static boolean nextStep(Map<Element,Collection<Element>> order, List<Element> list, Collection<List<Element>> toAdd) {
		Element last = list.isEmpty() ? null : list.get(list.size() - 1);
		Collection<Element> successors = order.get(last);
		if (successors.isEmpty())
			return true;
		Iterator<Element> it = successors.iterator();
		Element next = it.next();
		while (it.hasNext()) {
			List<Element> more = new ArrayList<Element>(list);
			more.add(it.next());
			toAdd.add(more);
		}
		list.add(next);
		return false;
	}
}
