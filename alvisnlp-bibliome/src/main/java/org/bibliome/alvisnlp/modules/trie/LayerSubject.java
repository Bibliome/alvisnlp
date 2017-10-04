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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bibliome.util.trie.Match;
import org.bibliome.util.trie.Matcher;
import org.bibliome.util.trie.StandardMatchControl;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

class LayerSubject implements Subject {
	private final String layerName;
	private final Collection<String> features; // XXX multiple features not work
	
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
	
	private boolean hasOneFeature(Annotation a) {
		for (String key : features) {
			if (a.hasFeature(key)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public <T> List<Match<T>> search(Matcher<T> matcher, Section sec) {
		List<Match<T>> result = Collections.emptyList();
		if (sec.hasLayer(layerName)) {
			boolean notFirst = false;
			int reach = 0;
			for (Annotation a : sec.getLayer(layerName)) {
				if (!hasOneFeature(a))
					continue;
				if (notFirst)
					matcher.matchChar(reach, ' ');
				else
					notFirst = true;
				int start = a.getStart();
				for (String key : features) {
					matcher.start(start);
					matcher.search(a.getLastFeature(key), start);
				}
				reach = a.getEnd();
				result = matcher.finish(reach);
			}
		}
		return result;
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
