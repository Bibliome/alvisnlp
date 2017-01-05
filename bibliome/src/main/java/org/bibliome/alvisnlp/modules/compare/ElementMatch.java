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


package org.bibliome.alvisnlp.modules.compare;

import java.util.Iterator;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.Element;

class ElementMatch {
	private final Element first;
	private Element second = null;
	private double score = 0;
	
	ElementMatch(Element first) {
		this.first = first;
	}

	void searchMatch(Iterator<Element> candidates, ElementSimilarity sim) {
		for (Element candidate : Iterators.loop(candidates)) {
			double score = sim.similarity(first, candidate);
			if (score > this.score) {
				this.score = score;
				second = candidate;
			}
		}
	}

	Element getFirst() {
		return first;
	}

	Element getSecond() {
		return second;
	}

	double getScore() {
		return score;
	}
}
