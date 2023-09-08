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


package fr.inra.maiage.bibliome.alvisnlp.core.app.cli;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ArgumentElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.FeatureElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.dump.CorpusDumper;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.util.Timer;

/**
 * Processing context for the alvisnlp CLI using the legacy corpus annotable model.
 * @author rbossy
 *
 */
public class CorpusCommandLineProcessingContext extends CommandLineProcessingContext {
	/**
	 * Creates an new processing context.
	 * @param timer
	 */
	public CorpusCommandLineProcessingContext(Timer<TimerCategory> timer) {
		super(timer);
	}

	@Override
	public boolean checkPlan(Logger logger, Module mainModule) throws ModuleException {
		return super.checkPlan(logger, mainModule);
	}

	@Override
	protected Collection<String> getNameTypes() {
		return Arrays.asList(NameType.getAllNameTypes());
	}

	@Override
	protected Collection<String> getIgnoreNameTypes(String nameType) {
		switch (nameType) {
			case NameType.FEATURE:
				return new LinkedHashSet<String>(Arrays.asList(
						"",
						ArgumentElement.ROLE_FEATURE_KEY,
						FeatureElement.KEY_FEATURE_KEY,
						FeatureElement.VALUE_FEATURE_KEY,
						Document.ID_FEATURE_NAME,
						Section.NAME_FEATURE_NAME,
						Annotation.FORM_FEATURE_NAME,
						Relation.NAME_FEATURE_NAME
						));
			default:
				return Collections.emptySet();
		}
	}

	@Override
	public CorpusDumper getDumper(Logger logger, File file) throws IOException {
		return new CorpusDumper(logger, file);
	}
}
