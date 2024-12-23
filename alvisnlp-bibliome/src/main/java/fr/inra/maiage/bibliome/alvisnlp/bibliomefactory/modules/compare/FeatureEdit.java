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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.compare;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.util.Strings;

public class FeatureEdit implements ElementSimilarity {
	private final String key;

	public FeatureEdit(String key) {
		super();
		this.key = key;
	}

	@Override
	public double similarity(Element a, Element b) {
		String va = a.hasFeature(key) ? a.getLastFeature(key) : "";
		String vb = b.hasFeature(key) ? b.getLastFeature(key) : "";
		double d = Strings.levenshtein(va, vb);
		return 1 - (d / Math.max(va.length(), vb.length()));
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		nameUsage.addNames(NameType.FEATURE, key);
	}
}
