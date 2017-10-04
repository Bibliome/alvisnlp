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

import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

public class FeatureSimilarity implements ElementSimilarity {
	private final String key;
	private double ifDifferent;
	
	public FeatureSimilarity(String key, double ifDifferent) {
		super();
		this.key = key;
		this.ifDifferent = ifDifferent;
	}

	public FeatureSimilarity(String key) {
		this(key, 0);
	}

	@Override
	public double similarity(Element a, Element b) {
		if (a.hasFeature(key) && b.hasFeature(key) && a.getLastFeature(key).equals(b.getLastFeature(key)))
			return 1;
		return ifDifferent;
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		nameUsage.addNames(NameType.FEATURE, key);
	}
}
