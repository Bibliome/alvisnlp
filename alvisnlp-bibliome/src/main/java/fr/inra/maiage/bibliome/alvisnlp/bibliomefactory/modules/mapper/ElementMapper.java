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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.mapper.ElementMapper.ElementMapperResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.mapper.Mapper.MapperResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.defaultmap.DefaultMap;

@AlvisNLPModule(beta=true)
public class ElementMapper extends Mapper<ElementMapperResolvedObjects,List<String>> {
	private Expression entries;
	private Expression key;
	private Expression[] values;
    private String[] targetFeatures;

    static class ElementMapperResolvedObjects extends MapperResolvedObjects {
    	private final Evaluator entries;
		private final Evaluator key;
		private final Evaluator[] values;

		private ElementMapperResolvedObjects(ProcessingContext<Corpus> ctx, ElementMapper module) throws ResolverException {
			super(ctx, module);
			entries = module.entries.resolveExpressions(rootResolver);
			key = module.key.resolveExpressions(rootResolver);
			values = rootResolver.resolveArray(module.values, Evaluator.class);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			entries.collectUsedNames(nameUsage, defaultType);
			key.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesArray(values, defaultType);
		}
    }
    
	@Override
	protected ElementMapperResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ElementMapperResolvedObjects(ctx, this);
	}

	@Override
	public void fillMapping(DefaultMap<String, List<List<String>>> mapping, ProcessingContext<Corpus> ctx, Corpus corpus) throws ProcessingException {
		ElementMapperResolvedObjects resObj = getResolvedObjects();
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		for (Element entry : Iterators.loop(resObj.entries.evaluateElements(evalCtx, corpus))) {
			String sKey = resObj.key.evaluateString(evalCtx, entry);
			if (ignoreCase)
				sKey = sKey.toLowerCase();
			List<String> sValues = new ArrayList<String>(values.length);
			for (Evaluator e : resObj.values) {
				String v = e.evaluateString(evalCtx, entry);
				sValues.add(v);
			}
			mapping.safeGet(sKey).add(sValues);
		}
	}

	@Override
	protected void handleMatch(Element target, List<String> value) {
		for (int i = 0; i < value.size() && i < targetFeatures.length; i++) {
			if (targetFeatures[i].isEmpty())
				continue;
			String s = value.get(i);
			if (s == null)
				continue;
			if (s.isEmpty())
				continue;
			target.addFeature(targetFeatures[i], s);
		}
	}

	@Param
	public Expression getEntries() {
		return entries;
	}

	@Param
	public Expression getKey() {
		return key;
	}

	@Param
	public Expression[] getValues() {
		return values;
	}

    @Param(nameType=NameType.FEATURE) 
	public String[] getTargetFeatures() {
		return targetFeatures;
	}

	public void setTargetFeatures(String[] targetFeatures) {
		this.targetFeatures = targetFeatures;
	}

	public void setEntries(Expression entries) {
		this.entries = entries;
	}

	public void setKey(Expression key) {
		this.key = key;
	}

	public void setValues(Expression[] values) {
		this.values = values;
	}
}
