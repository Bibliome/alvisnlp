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

import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.projectors.ElementProjector.ElementProjectorResolvedObjects;
import org.bibliome.alvisnlp.modules.trie.ElementProjector2;
import org.bibliome.util.Iterators;
import org.bibliome.util.newprojector.CharFilter;
import org.bibliome.util.newprojector.CharMapper;
import org.bibliome.util.newprojector.Dictionary;
import org.bibliome.util.newprojector.State;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
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
import alvisnlp.module.types.EvaluatorMapping;
import alvisnlp.module.types.ExpressionMapping;

@AlvisNLPModule(obsoleteUseInstead=ElementProjector2.class)
public abstract class ElementProjector extends Projector<ElementProjectorResolvedObjects,Element,Dictionary<Element>> {
	private Expression values;
	private Expression key;
	private ExpressionMapping features;

	static class ElementProjectorResolvedObjects extends SectionResolvedObjects {
		private final Evaluator values;
		private final Evaluator key;
		private final EvaluatorMapping features;
		
		private ElementProjectorResolvedObjects(ProcessingContext<Corpus> ctx, ElementProjector module) throws ResolverException {
			super(ctx, module);
			values = module.values.resolveExpressions(rootResolver);
			key = module.key.resolveExpressions(rootResolver);
			features = module.features.resolveExpressions(rootResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			values.collectUsedNames(nameUsage, defaultType);
			key.collectUsedNames(nameUsage, defaultType);
			features.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Override
	protected ElementProjectorResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ElementProjectorResolvedObjects(ctx, this);
	}

	@Override
	protected Dictionary<Element> newDictionary(State<Element> root, CharFilter charFilter, CharMapper charMapper) {
		return new Dictionary<Element>(root, charFilter, charMapper);
	}

	@Override
	protected void fillDictionary(ProcessingContext<Corpus> ctx, Corpus corpus, Dictionary<Element> dict) throws Exception {
		ElementProjectorResolvedObjects resObj = getResolvedObjects();
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		for (Element v : Iterators.loop(resObj.values.evaluateElements(evalCtx, corpus))) {
			String k = resObj.key.evaluateString(evalCtx, v);
			dict.addEntry(k, v);
		}
	}

	@Override
	protected void handleEntryValues(ProcessingContext<Corpus> ctx, Dictionary<Element> dict, Annotation a, Element entry) throws Exception {
		ElementProjectorResolvedObjects resObj = getResolvedObjects();
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		for (Map.Entry<String,Evaluator> e : resObj.features.entrySet())
			a.addFeature(e.getKey(), e.getValue().evaluateString(evalCtx, entry));
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Param
	public Expression getValues() {
		return values;
	}

	@Param
	public Expression getKey() {
		return key;
	}

	@Param(nameType=NameType.FEATURE)
	public ExpressionMapping getFeatures() {
		return features;
	}

	public void setValues(Expression values) {
		this.values = values;
	}

	public void setKey(Expression key) {
		this.key = key;
	}

	public void setFeatures(ExpressionMapping features) {
		this.features = features;
	}
}
