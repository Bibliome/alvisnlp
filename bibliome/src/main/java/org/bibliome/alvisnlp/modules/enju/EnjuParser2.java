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


package org.bibliome.alvisnlp.modules.enju;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.enju.EnjuParser2.EnjuParserResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.files.ExecutableFile;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.ConstantsLibrary;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

@AlvisNLPModule(beta=true)
public abstract class EnjuParser2 extends SectionModule<EnjuParserResolvedObjects> implements TupleCreator {
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private Expression sentenceFilter = ConstantsLibrary.TRUE;
	private String wordLayerName = DefaultNames.getWordLayer();
	private String wordFormFeatureName = Annotation.FORM_FEATURE_NAME;
	private String posFeatureName = DefaultNames.getPosTagFeature();
	
	private ExecutableFile enjuExecutable;
	private String enjuEncoding = "UTF-8";
	private Boolean biology = false;
	private Integer nBest = 1;
	
	private String dependenciesRelationName = DefaultNames.getDependencyRelationName();
	private String parseNumberFeatureName = DefaultNames.getParseNumberFeatureName();
	private String dependencyLabelFeatureName = DefaultNames.getDependencyLabelFeatureName();
	private String sentenceRole = DefaultNames.getDependencySentenceRole();
	private String dependencyHeadRole = DefaultNames.getDependencyHeadRole();
	private String dependencyDependentRole = DefaultNames.getDependencyDependentRole();
	private String parseStatusFeatureName = "parse-status";
	private String dependentTypeFeatureName = "arg-type";
	
	@SuppressWarnings("hiding")
	class EnjuParserResolvedObjects extends SectionResolvedObjects {
		private final Evaluator sentenceFilter;
		
		private EnjuParserResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
			super(ctx, EnjuParser2.this);
			sentenceFilter = EnjuParser2.this.sentenceFilter.resolveExpressions(rootResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			sentenceFilter.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Override
	protected EnjuParserResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new EnjuParserResolvedObjects(ctx);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			Logger logger = getLogger(ctx);
			Collection<Layer> sentences = getSentences(logger, corpus);
			EnjuExternal2 ext = prepareCorpus(ctx, sentences);
			logger.info("running enju");
			callExternal(ctx, "enju", ext, enjuEncoding, "enju.sh");
			readParse(ctx, ext, sentences);
		}
		catch (IOException e) {
			rethrow(e);
		}
	}

	@TimeThis(task="read-enju", category=TimerCategory.COLLECT_DATA)
	protected void readParse(ProcessingContext<Corpus> ctx, EnjuExternal2 ext, Collection<Layer> sentences) throws ProcessingException, IOException {
		Logger logger = getLogger(ctx);
		logger.info("reading enju output");
		ext.readEnjuOut(sentences);
	}
	
	private Collection<Layer> getSentences(Logger logger, Corpus corpus) {
		EnjuParserResolvedObjects resObj = getResolvedObjects();
		EvaluationContext evalCtx = new EvaluationContext(logger);
		return getSentences(evalCtx, corpus, resObj.sentenceFilter);
	}
	
	@TimeThis(task="prepare-corpus", category=TimerCategory.PREPARE_DATA)
	protected EnjuExternal2 prepareCorpus(ProcessingContext<Corpus> ctx, Collection<Layer> sentences) throws IOException {
		Logger logger = getLogger(ctx);
		File tempDir = getTempDir(ctx);
		logger.info("preparing corpus for enju");
		return new EnjuExternal2(this, tempDir, logger, sentences);
	}

	private Collection<Layer> getSentences(EvaluationContext ctx, Corpus corpus, Evaluator sentenceFilter) {
		Collection<Layer> result = new ArrayList<Layer>();
		for (Section sec : Iterators.loop(sectionIterator(ctx, corpus)))
			for (Layer sent : sec.getSentences(wordLayerName, sentenceLayerName))
				if (sentenceFilter.evaluateBoolean(ctx, sent.getSentenceAnnotation()))
					result.add(sent);
		return result;
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { sentenceLayerName, wordLayerName };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Param(nameType=NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayerName;
	}

	@Param
	public Expression getSentenceFilter() {
		return sentenceFilter;
	}

	@Param(nameType=NameType.LAYER)
	public String getWordLayerName() {
		return wordLayerName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getWordFormFeatureName() {
		return wordFormFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getPosFeatureName() {
		return posFeatureName;
	}

	@Param
	public ExecutableFile getEnjuExecutable() {
		return enjuExecutable;
	}

	@Param
	public String getEnjuEncoding() {
		return enjuEncoding;
	}

	@Param
	public Boolean getBiology() {
		return biology;
	}

	@Param
	public Integer getnBest() {
		return nBest;
	}

	@Param(nameType=NameType.RELATION)
	public String getDependenciesRelationName() {
		return dependenciesRelationName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getParseNumberFeatureName() {
		return parseNumberFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getDependencyLabelFeatureName() {
		return dependencyLabelFeatureName;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getSentenceRole() {
		return sentenceRole;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getDependencyHeadRole() {
		return dependencyHeadRole;
	}

	@Param(nameType=NameType.FEATURE)
	public String getParseStatusFeatureName() {
		return parseStatusFeatureName;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getDependencyDependentRole() {
		return dependencyDependentRole;
	}

	@Param(nameType=NameType.FEATURE)
	public String getDependentTypeFeatureName() {
		return dependentTypeFeatureName;
	}

	public void setDependentTypeFeatureName(String dependentTypeFeatureName) {
		this.dependentTypeFeatureName = dependentTypeFeatureName;
	}

	public void setDependencyDependentRole(String dependencyDependentRole) {
		this.dependencyDependentRole = dependencyDependentRole;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}

	public void setSentenceFilter(Expression sentenceFilter) {
		this.sentenceFilter = sentenceFilter;
	}

	public void setWordLayerName(String wordLayerName) {
		this.wordLayerName = wordLayerName;
	}

	public void setWordFormFeatureName(String wordFormFeatureName) {
		this.wordFormFeatureName = wordFormFeatureName;
	}

	public void setPosFeatureName(String posFeatureName) {
		this.posFeatureName = posFeatureName;
	}

	public void setEnjuExecutable(ExecutableFile enjuExecutable) {
		this.enjuExecutable = enjuExecutable;
	}

	public void setEnjuEncoding(String enjuEncoding) {
		this.enjuEncoding = enjuEncoding;
	}

	public void setBiology(Boolean biology) {
		this.biology = biology;
	}

	public void setnBest(Integer nBest) {
		this.nBest = nBest;
	}

	public void setDependenciesRelationName(String dependenciesRelationName) {
		this.dependenciesRelationName = dependenciesRelationName;
	}

	public void setParseNumberFeatureName(String parseNumberFeatureName) {
		this.parseNumberFeatureName = parseNumberFeatureName;
	}

	public void setDependencyLabelFeatureName(String dependencyLabelFeatureName) {
		this.dependencyLabelFeatureName = dependencyLabelFeatureName;
	}

	public void setSentenceRole(String sentenceRole) {
		this.sentenceRole = sentenceRole;
	}

	public void setDependencyHeadRole(String dependencyHeadRole) {
		this.dependencyHeadRole = dependencyHeadRole;
	}

	public void setParseStatusFeatureName(String parseStatusFeatureName) {
		this.parseStatusFeatureName = parseStatusFeatureName;
	}
}
