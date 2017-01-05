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


package org.bibliome.alvisnlp.modules.wok.readers;

import org.bibliome.alvisnlp.modules.wok.FieldReader;
import org.bibliome.alvisnlp.modules.wok.WoKReaderStatus;
import org.bibliome.util.Strings;

import alvisnlp.corpus.Element;

public class FeatureSetter implements FieldReader {
	private final Target target;
	private final String featureName;
	private boolean keywords;

	public FeatureSetter(Target target, String featureName, boolean keywords) {
		super();
		this.target = target;
		this.featureName = featureName;
		this.keywords = keywords;
	}

	@Override
	public void start(WoKReaderStatus status) {
	}

	@Override
	public void addLine(WoKReaderStatus status, String line) {
		if (!line.isEmpty()) {
			if (line.charAt(0) == '"') {
				line = line.substring(1);
			}
			if (line.charAt(line.length() - 1) == '"') {
				line = line.substring(0, line.length() - 1);
			}
		}
		Element elt = target.getElement(status);
		if (keywords) {
			for (String kw : Strings.splitAndTrim(line, ';', 0)) {
				if (!kw.isEmpty()) {
					elt.addFeature(featureName, kw);
				}
			}
		}
		else {
			elt.addFeature(featureName, line);
		}
	}

	@Override
	public void finish(WoKReaderStatus status) {
	}
	
	public static enum Target {
		CORPUS {
			@Override
			Element getElement(WoKReaderStatus status) {
				return status.getCorpus();
			}
		},
		
		DOCUMENT {
			@Override
			Element getElement(WoKReaderStatus status) {
				return status.getDocument();
			}
		};
		
		abstract Element getElement(WoKReaderStatus status);
	}
}
