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

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;

@AlvisNLPModule(obsoleteUseInstead=Action.class)
public abstract class OntoReif extends SectionModule<SectionResolvedObjects> implements TupleCreator {
	private static final Pattern MBTO_PATTERN = Pattern.compile("https://bibliome.jouy.inra.fr/tydirws/ontobiotope2013/projects/23260/semClass/(\\d+)(?:/canonic/\\d+)?");
	
	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			Relation textBound = sec.ensureRelation(this, "textBound");
			Relation onto = sec.ensureRelation(this, "onto");
			for (Tuple t : textBound.getTuples()) {
				String type = t.getLastFeature("type");
				if (type.equals("p") || type.equals("Irrelevant") || type.equals("Bacteria") || type.equals("Geographical"))
					continue;
				List<String> mbto;
				if (t.hasFeature("MBTO-link"))
					mbto = t.getFeature("MBTO-link");
				else {
					if (t.hasFeature("MBTO-create"))
						mbto = t.getFeature("MBTO-create");
					else
						continue;
				}
				for (String v : mbto) {
					Matcher m = MBTO_PATTERN.matcher(v);
					if (m.matches()) {
						Tuple ot = new Tuple(this, onto);
						ot.addFeature("MBTO", m.group(1));
						ot.setArgument("annot", t);
					}
				}
			}
		}
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
