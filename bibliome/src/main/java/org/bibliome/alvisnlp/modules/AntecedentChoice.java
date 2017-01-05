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


package org.bibliome.alvisnlp.modules;

import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;

@AlvisNLPModule
public class AntecedentChoice extends SectionModule<SectionResolvedObjects> {
	private String relation = "coreferences";

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		int nVisited = 0;
		int nAnte = 0;
		int nMissed = 0;
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			if (!sec.hasRelation(relation))
				continue;
			Layer bicoref = sec.ensureLayer("bi-anaphora");
			for (Tuple t : sec.getRelation(relation).getTuples()) {
				Annotation anaphora = DownCastElement.toAnnotation(t.getArgument("Anaphora"));
				nVisited++;
				nAnte++;
				if (bicoref.contains(anaphora)) {
					nAnte++;
					Annotation ante = null;
					Annotation anteTwo = null;
					for (Element ea : t.getAllArguments()) {
						Annotation a = DownCastElement.toAnnotation(ea);
						if (a == anaphora)
							continue;
						if ((ante == null) || (a.getStart() > ante.getStart())) {
							anteTwo = ante;
							ante = a;
							continue;
						}
						if ((anteTwo == null) || (a.getStart() > anteTwo.getStart()))
							anteTwo = a;
					}
					if (ante == null) {
						nMissed += 2;
						logger.finer("could not find ante for " + anaphora + " in " + sec);
					}
					else if (anteTwo == null) {
						nMissed ++;
						logger.finer("could not find second ante for " + anaphora + " in " + sec);
					}
					else {
						t.setArgument("Ante", ante);
						t.setArgument("AnteTwo", anteTwo);
					}
					continue;
				}
				Annotation ante = null;
				if (t.hasArgument("AntePreviousLowerTaxon"))
					ante = DownCastElement.toAnnotation(t.getArgument("AntePreviousLowerTaxon"));
				else {
					for (Element ea : t.getAllArguments()) {
						Annotation a = DownCastElement.toAnnotation(ea);
						if (a == anaphora)
							continue;
						if ((ante == null) || (a.getStart() > ante.getStart()))
							ante = a;
					}
				}
				if (ante == null) {
					nMissed++;
					logger.finer("could not find ante for " + anaphora + " in " + sec);
				}
				t.setArgument("Ante", ante);
			}
		}
		if (nVisited == 0) {
			logger.warning("no anaphora");
		}
		else {
			String msg = String.format("visited %d anaphora, expecting %d ante, missed %d", nVisited, nAnte, nMissed);
			if (nMissed > 0) {
				logger.warning(msg);
			}
			else {
				logger.info(msg);
			}
		}
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		nameUsage.addNames(NameType.RELATION, "coreferences");
		nameUsage.addNames(NameType.LAYER, "bi-anaphora");
		nameUsage.addNames(NameType.ARGUMENT, "Anaphora", "Ante", "AnteTwo", "AntePreviousLowerTaxon");
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}
}
