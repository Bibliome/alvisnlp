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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.trie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.util.trie.Match;
import fr.inra.maiage.bibliome.util.trie.Matcher;
import fr.inra.maiage.bibliome.util.trie.StandardMatchControl;
import fr.inra.maiage.bibliome.util.trie.State;

class LayerSubject implements Subject {
	private final String layerName;
	private final Collection<String> features;
	
	LayerSubject(String layerName, Collection<String> features) {
		super();
		this.layerName = layerName;
		if (features == null) {
			throw new NullPointerException();
		}
		this.features = features;
	}
	
	LayerSubject(String layerName, String feature) {
		this(layerName, Collections.singleton(feature));
	}
	
	LayerSubject(String layerName) {
		this(layerName, Annotation.FORM_FEATURE_NAME);
	}
	
	@Override
	public <T> List<Match<T>> search(Matcher<T> matcher, Section sec) {
		List<Match<T>> result = Collections.emptyList();
		if (sec.hasLayer(layerName)) {
			boolean notFirst = false;
			int reach = 0;
			for (Annotation a : sec.getLayer(layerName)) {
				if (notFirst)
					matcher.matchChar(reach, ' ');
				else
					notFirst = true;
				int start = a.getStart();
				matcher.start(start);
				SavedStates<T> savedStates = new SavedStates<T>(matcher);
				for (String key : features) {
					String value = a.getLastFeature(key);
					if (value == null) {
						continue;
					}
					matcher.search(value, start);
					savedStates.checkSuccesses();
				}
				savedStates.restoreSuccesses();
				reach = a.getEnd();
				result = matcher.finish(reach);
			}
		}
		return result;
	}
	
	private static class SavedStates<T> {
		private final Matcher<T> matcher;
		private final Map<Match<T>,State<T>> initialStates = new HashMap<Match<T>,State<T>>();
		private final Collection<Match<T>> followupMatches = new ArrayList<Match<T>>();
		
		private SavedStates(Matcher<T> matcher) {
			this.matcher = matcher;
			for (Match<T> m : matcher.getCandidates()) {
				State<T> s = m.getState();
				initialStates.put(m, s);
				m.setOriginal(m);
			}
		}
		
		private void checkSuccesses() {
			for (Match<T> m : matcher.getCandidates()) {
				initialStates.remove(m.getOriginal());
				followupMatches.add(m);
			}
			for (Map.Entry<Match<T>,State<T>> e : initialStates.entrySet()) {
				e.getKey().setState(e.getValue());
			}
			matcher.setCandidates(initialStates.keySet());
		}
		
		private void restoreSuccesses() {
			matcher.setCandidates(followupMatches);
		}
	}

	@Override
	public void correctControl(StandardMatchControl control) {
		control.setNeverStart(true);
		control.setNeverEnd(true);
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		nameUsage.addNames(NameType.LAYER, layerName);
		nameUsage.addNames(NameType.FEATURE, features);
	}
}
