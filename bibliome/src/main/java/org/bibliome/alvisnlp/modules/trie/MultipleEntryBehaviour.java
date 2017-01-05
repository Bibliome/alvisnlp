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


package org.bibliome.alvisnlp.modules.trie;

import java.util.List;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.trie.Match;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Layer;

public enum MultipleEntryBehaviour {
	ALL {
		@Override
		public String toString() {
			return "all";
		}

		@Override
		<S extends SectionResolvedObjects,T> void handle(TrieProjector<S,T> projector, Layer layer, Match<T> match) {
			for (T value : match.getValues()) {
				Annotation a = getAnnotation(projector, layer, match);
				projector.handleMatch(value, a);
			}
		}
	},
	
	MERGE {
		@Override
		public String toString() {
			return "merge";
		}

		@Override
		<S extends SectionResolvedObjects,T> void handle(TrieProjector<S,T> projector, Layer layer, Match<T> match) {
			Annotation a = getAnnotation(projector, layer, match);
			for (T value : match.getValues())
				projector.handleMatch(value, a);
		}
	},
	
	FIRST {
		@Override
		public String toString() {
			return "first";
		}

		@Override
		<S extends SectionResolvedObjects,T> void handle(TrieProjector<S,T> projector, Layer layer, Match<T> match) {
			Annotation a = getAnnotation(projector, layer, match);
			List<T> values = match.getValues();
			projector.handleMatch(values.get(0), a);
		}
	},
	
	LAST {
		@Override
		public String toString() {
			return "last";
		}

		@Override
		<S extends SectionResolvedObjects,T> void handle(TrieProjector<S,T> projector, Layer layer, Match<T> match) {
			Annotation a = getAnnotation(projector, layer, match);
			List<T> values = match.getValues();
			projector.handleMatch(values.get(values.size() - 1), a);
		}
	};
	
	abstract <S extends SectionResolvedObjects,T> void handle(TrieProjector<S,T> projector, Layer layer, Match<T> match);
	
	protected static <S extends SectionResolvedObjects,T> Annotation getAnnotation(TrieProjector<S,T> projector, Layer layer, Match<T> match) {
		return new Annotation(projector, layer, match.getStart(), match.getEnd());
	}
}
