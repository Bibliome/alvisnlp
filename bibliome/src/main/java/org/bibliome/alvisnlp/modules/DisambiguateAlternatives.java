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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.DisambiguateAlternatives.DisambiguateAlternativesResolvedObjects;
import org.bibliome.alvisnlp.modules.DocumentModule.DocumentResolvedObjects;
import org.bibliome.util.Iterators;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule
public class DisambiguateAlternatives extends DocumentModule<DisambiguateAlternativesResolvedObjects> {
	private Expression target;
	private String ambiguousFeature;
	private Boolean warnIfAmbiguous = false;
	
	@SuppressWarnings("hiding")
	class DisambiguateAlternativesResolvedObjects extends DocumentResolvedObjects {
		private final Evaluator target;
		
		private DisambiguateAlternativesResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
			super(ctx, DisambiguateAlternatives.this);
			target = rootResolver.resolveNullable(DisambiguateAlternatives.this.target);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			target.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Override
	protected DisambiguateAlternativesResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new DisambiguateAlternativesResolvedObjects(ctx);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		DisambiguateAlternativesResolvedObjects resObj = getResolvedObjects();
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		int nRemoved = 0;
		int nDisambiguated = 0;
		int nNonAmbiguous = 0;
		int nStillAmbiguous = 0;
		int nCannotDisambiguate = 0;
		for (Document doc : Iterators.loop(documentIterator(evalCtx, corpus))) {
			Set<String> seen = new HashSet<String>();
			for (Element elt : Iterators.loop(resObj.target.evaluateElements(evalCtx, doc))) {
				if (!elt.hasFeature(ambiguousFeature))
					continue;
				List<String> values = new ArrayList<String>(elt.getFeature(ambiguousFeature));
				if (values.size() == 1) {
					seen.add(values.get(0));
					nNonAmbiguous++;
					continue;
				}
				values.retainAll(seen);
				if (values.size() == 0) {
					if (warnIfAmbiguous)
						logger.warning("in " + doc + ", " + elt + " could not be disambiguated");
					nCannotDisambiguate++;
					continue;
				}
				values.clear();
				values.addAll(elt.getFeature(ambiguousFeature));
				for (String v : values)
					if (!seen.contains(v)) {
						nRemoved++;
						elt.removeFeature(ambiguousFeature, v);
					}
				if (elt.getFeature(ambiguousFeature).size() > 1) {
					if (warnIfAmbiguous)
						logger.warning("in " + doc + ", " + elt + " is still ambiguous");
					nStillAmbiguous++;
				}
				else
					nDisambiguated++;
			}
		}
		logger.info("removed values: " + nRemoved);
		logger.info("non ambiguous: " + nNonAmbiguous);
		logger.info("disambiguated: " + nDisambiguated);
		logger.info("could not disambiguate: " + nCannotDisambiguate);
		logger.info("still ambiguous: " + nStillAmbiguous);
	}

	@Param(nameType=NameType.FEATURE)
	public String getAmbiguousFeature() {
		return ambiguousFeature;
	}

	@Param
	public Boolean getWarnIfAmbiguous() {
		return warnIfAmbiguous;
	}

	@Param
	public Expression getTarget() {
		return target;
	}

	public void setTarget(Expression target) {
		this.target = target;
	}

	public void setWarnIfAmbiguous(Boolean warnIfAmbiguous) {
		this.warnIfAmbiguous = warnIfAmbiguous;
	}

	public void setAmbiguousFeature(String ambiguousFeature) {
		this.ambiguousFeature = ambiguousFeature;
	}
}
