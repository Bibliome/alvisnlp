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


package alvisnlp.app.cli;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Logger;

import org.bibliome.util.Timer;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.ArgumentElement;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.FeatureElement;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.dump.CorpusDumper;
import alvisnlp.module.Annotable.Dumper;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.TimerCategory;

/**
 * Processing context for the alvisnlp CLI using the legacy corpus annotable model.
 * @author rbossy
 *
 */
public class CorpusCommandLineProcessingContext extends CommandLineProcessingContext<Corpus> {
	/**
	 * Creates an new processing context.
	 * @param timer
	 */
	public CorpusCommandLineProcessingContext(Timer<TimerCategory> timer) {
		super(timer);
	}

	@Override
	public boolean checkPlan(Logger logger, Module<Corpus> mainModule) throws ModuleException {
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
				return new HashSet<String>(Arrays.asList(
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
	public Dumper<Corpus> getDumper(File file) throws IOException {
		return new CorpusDumper(file);
	}
}
