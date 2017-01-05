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


package org.bibliome.alvisnlp.modules.ccg;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.DefaultExpressions;
import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.ccg.CCGBase.CCGResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.Strings;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

public abstract class CCGBase<T extends CCGResolvedObjects> extends SectionModule<T> {
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String wordLayerName = DefaultNames.getWordLayer();
	private String formFeatureName = Annotation.FORM_FEATURE_NAME;
	private String posFeatureName = DefaultNames.getPosTagFeature();
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

	protected static int getMaxLength(List<Layer> sentences) {
		int result = 0;
		for (Layer sent : sentences)
			if (sent.size() > result)
				result = sent.size();
		return result;
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
	
	protected void printSentence(PrintStream out, StringBuilder sb, Layer sentence, boolean withPos) {
		boolean notFirst = false;
		for (Annotation word : sentence) {
			String form = word.getLastFeature(formFeatureName);
			if (form.isEmpty())
				continue;
			sb.setLength(0);
			if (notFirst)
				sb.append(' ');
			else
				notFirst = true;
			Strings.escapeWhitespaces(sb, form, '|', '.');
			if (withPos) {
				sb.append('|');
				sb.append(word.getLastFeature(posFeatureName));
			}
			out.print(sb);
		}
		out.println();
	}

	@TimeThis(task="prepare-sentences", category=TimerCategory.PREPARE_DATA)
	protected void printSentences(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, PrintStream out, List<Layer> sentences, boolean withPos) {
		StringBuilder sb = new StringBuilder();
		for (Layer sent : sentences)
			printSentence(out, sb, sent, withPos);
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

	@Param(nameType=NameType.FEATURE)
	public String getFormFeatureName() {
		return formFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getPosFeatureName() {
		return posFeatureName;
	}

	@Param
	public Expression getSentenceFilter() {
		return sentenceFilter;
	}

	@Param
	public Integer getMaxRuns() {
		return maxRuns;
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
		this.formFeatureName = formFeatureName;
	}

	public void setPosFeatureName(String posFeatureName) {
		this.posFeatureName = posFeatureName;
	}
}
