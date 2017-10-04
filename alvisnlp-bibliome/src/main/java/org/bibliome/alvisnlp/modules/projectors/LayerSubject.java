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


package org.bibliome.alvisnlp.modules.projectors;

import org.bibliome.util.newprojector.CharFilter;
import org.bibliome.util.newprojector.Dictionary;
import org.bibliome.util.newprojector.Matcher;
import org.bibliome.util.newprojector.chars.Filters;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

public class LayerSubject implements Subject {
	private final String layerName;
	private final String featureName;
	private final char separator;

	public LayerSubject(String layerName, String featureName, char separator) {
		super();
		this.layerName = layerName;
		this.featureName = featureName;
		this.separator = separator;
	}

	@Override
	public CharFilter getEndFilter() {
		return Filters.REJECT_ALL;
	}

	@Override
	public CharFilter getStartFilter() {
		return Filters.REJECT_ALL;
	}

	@Override
	public boolean isCharPos() {
		return false;
	}

	@Override
	public <T> void match(Section sec, Dictionary<T> dict, Matcher<T> matcher) {
		if (!sec.hasLayer(layerName))
			return;
		boolean notFirst = false;
		for (Annotation a : sec.getLayer(layerName)) {
			if (!a.hasFeature(featureName))
				continue;
			if (notFirst)
				matcher.matchChar(separator);
			else
				notFirst = true;
			matcher.setPosition(a.getStart());
			matcher.startMatch();
			dict.match(matcher, a.getLastFeature(featureName));
			matcher.setPosition(a.getEnd());
			matcher.endMatches();
		}
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		nameUsage.addNames(NameType.LAYER, layerName);
		nameUsage.addNames(NameType.FEATURE, featureName);
	}
}
