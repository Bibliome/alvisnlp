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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DefaultExpressions;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg.CCGBase.CCGResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;

public abstract class CCGBase<T extends CCGResolvedObjects> extends SectionModule<T> {
	private String internalEncoding = "UTF-8";
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String wordLayerName = DefaultNames.getWordLayer();
	private String formFeature = Annotation.FORM_FEATURE_NAME;
	private String posFeature = DefaultNames.getPosTagFeature();
	private Expression sentenceFilter = DefaultExpressions.TRUE;
	private Integer maxRuns = 1;

	public static class CCGResolvedObjects extends SectionResolvedObjects {
		private final Evaluator sentenceFilter;

		public CCGResolvedObjects(ProcessingContext<Corpus> ctx, CCGBase<? extends CCGResolvedObjects> module) throws ResolverException {
			super(ctx, module);
			this.sentenceFilter = module.sentenceFilter.resolveExpressions(rootResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			sentenceFilter.collectUsedNames(nameUsage, defaultType);
		}
	}

	protected List<List<Layer>> getSentences(Logger logger, EvaluationContext evalCtx, Corpus corpus) {
		CCGResolvedObjects resObj = getResolvedObjects();
		List<Layer> sentences = new ArrayList<Layer>();
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			Layer words = sec.getLayer(wordLayerName);
			for (Annotation sent : sec.getLayer(sentenceLayerName)) {
				if (resObj.sentenceFilter.evaluateBoolean(evalCtx, sent)) {
					Layer sentLayer = words.between(sent);
					sentLayer.setSentenceAnnotation(sent);
					sentences.add(sentLayer);
				}
			}
		}
		if (sentences.isEmpty()) {
			logger.warning("no sentences");
			return Collections.emptyList();
		}
		int spr = sentences.size() / maxRuns;
		if (sentences.size() % maxRuns > 0) {
			spr++;
		}
		List<List<Layer>> result = new ArrayList<List<Layer>>(maxRuns);
		for (int i = 0; i < sentences.size(); i += spr) {
			List<Layer> run = sentences.subList(i, Math.min(i + spr, sentences.size()));
			result.add(run);
		}
		logger.info("sentences: " + sentences.size() + ", per run: " + spr + ", runs: " +result.size());
		return result;
	}
	
	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { sentenceLayerName , wordLayerName };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Param(nameType=NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayerName;
	}

	@Param(nameType=NameType.LAYER)
	public String getWordLayerName() {
		return wordLayerName;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getFormFeatureName() {
		return formFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getPosFeatureName() {
		return posFeature;
	}

	@Param
	public Expression getSentenceFilter() {
		return sentenceFilter;
	}

	@Param
	public Integer getMaxRuns() {
		return maxRuns;
	}

	@Param
	public String getInternalEncoding() {
		return internalEncoding;
	}

	@Param(nameType=NameType.FEATURE)
	public String getFormFeature() {
		return formFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getPosFeature() {
		return posFeature;
	}

	public void setFormFeature(String formFeature) {
		this.formFeature = formFeature;
	}

	public void setPosFeature(String posFeature) {
		this.posFeature = posFeature;
	}

	public void setInternalEncoding(String internalEncoding) {
		this.internalEncoding = internalEncoding;
	}

	public void setMaxRuns(Integer maxRuns) {
		this.maxRuns = maxRuns;
	}

	public void setSentenceFilter(Expression sentenceFilter) {
		this.sentenceFilter = sentenceFilter;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}

	public void setWordLayerName(String wordLayerName) {
		this.wordLayerName = wordLayerName;
	}

	public void setFormFeatureName(String formFeatureName) {
		this.formFeature = formFeatureName;
	}

	public void setPosFeatureName(String posFeatureName) {
		this.posFeature = posFeatureName;
	}
}
