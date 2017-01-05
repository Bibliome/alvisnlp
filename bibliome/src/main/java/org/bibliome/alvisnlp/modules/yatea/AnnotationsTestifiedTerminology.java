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


package org.bibliome.alvisnlp.modules.yatea;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.Strings;
import org.bibliome.util.files.InputFile;
import org.bibliome.util.streams.FileTargetStream;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;

public class AnnotationsTestifiedTerminology implements TestifiedTerminology {
	private final String termsLayerName;

	public AnnotationsTestifiedTerminology(String termsLayerName) {
		super();
		this.termsLayerName = termsLayerName;
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		nameUsage.addNames(NameType.LAYER, termsLayerName);
	}

	@Override
	public <S extends SectionResolvedObjects> InputFile ensureFile(AbstractYateaExtractor<S> module, ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException, IOException {
		File tmpDir = module.getTempDir(ctx);
		InputFile result = new InputFile(tmpDir, "attested-terms.ttg");
		TargetStream ts = new FileTargetStream("ISO-8859-1", result.getAbsolutePath());
		try (PrintStream out = ts.getPrintStream()) {
			String wordLayerName = module.getWordLayerName();
			String formFeature = module.getFormFeature();
			String posFeature = module.getPosFeature();
			String lemmaFeature = module.getLemmaFeature();
			EvaluationContext evalCtx = new EvaluationContext(module.getLogger(ctx));
			for (Section sec : Iterators.loop(module.sectionIterator(evalCtx, corpus))) {
				if (!sec.hasLayer(termsLayerName)) {
					continue;
				}
				if (!sec.hasLayer(wordLayerName)) {
					continue;
				}
				Layer words = sec.getLayer(wordLayerName);
				for (Annotation term : sec.getLayer(termsLayerName)) {
					for (Annotation word : words.between(term)) {
						String form = Strings.normalizeSpace(word.getLastFeature(formFeature));
						String pos = Strings.normalizeSpace(word.getLastFeature(posFeature));
						String lemma = Strings.normalizeSpace(word.getLastFeature(lemmaFeature));
						out.println(form + '\t' + pos + '\t' + lemma);
					}
					out.println(".\tSENT\t.");
				}
			}
		}
		return result;
	}

	@Override
	public boolean check(Logger logger) {
		return true;
	}
}