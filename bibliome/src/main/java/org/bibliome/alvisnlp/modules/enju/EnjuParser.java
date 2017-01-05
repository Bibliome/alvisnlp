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
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(obsoleteUseInstead=EnjuParser2.class)
public abstract class EnjuParser extends SectionModule<SectionResolvedObjects> implements TupleCreator {
	private String wordFormFeatureName = Annotation.FORM_FEATURE_NAME;
	private String posFeatureName = DefaultNames.getPosTagFeature();
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String wordLayerName = DefaultNames.getWordLayer();
	private Expression sentenceFilter = ConstantsLibrary.TRUE;
	private String enjuEncoding = "UTF-8";
	private ExecutableFile enjuExecutable;
	private Boolean biology = false;
	private Integer nBest = 1;
	private String dependenciesRelationName = DefaultNames.getDependencyRelationName();
	private String parseNumberFeatureName = DefaultNames.getParseNumberFeatureName();
	private String sentenceRole = DefaultNames.getDependencySentenceRole();
	private String dependencyLabelFeatureName = DefaultNames.getDependencyLabelFeatureName();
	private String dependencyHeadRole = DefaultNames.getDependencyHeadRole();
	private String parseStatusFeature = "parse-status";

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { sentenceLayerName, wordLayerName };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		LibraryResolver resolver = getLibraryResolver(ctx);
		Evaluator sentenceFilter = resolver.resolveNullable(this.sentenceFilter);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		File tempDir = getTempDir(ctx);
		
		Collection<Layer> sentences = getSentences(corpus, evalCtx, sentenceFilter);
		
		try {
			EnjuExternal external = new EnjuExternal(this, ctx, sentences, tempDir);
			ctx.callExternal(external);
			external.readSentences();
		}
		catch (IOException e) {
			rethrow(e);
		}
	}

	private Collection<Layer> getSentences(Corpus corpus, EvaluationContext ctx, Evaluator sentenceFilter) {
		Collection<Layer> result = new ArrayList<Layer>();
		for (Section sec : Iterators.loop(sectionIterator(ctx, corpus)))
			for (Layer sent : sec.getSentences(wordLayerName, sentenceLayerName))
				if (sentenceFilter.evaluateBoolean(ctx, sent.getSentenceAnnotation()))
					result.add(sent);
		return result;
	}
	
	@Param(nameType=NameType.FEATURE)
	public String getWordFormFeatureName() {
		return wordFormFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getPosFeatureName() {
		return posFeatureName;
	}

	@Param(nameType=NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayerName;
	}

	@Param(nameType=NameType.LAYER)
	public String getWordLayerName() {
		return wordLayerName;
	}

	@Param
	public Expression getSentenceFilter() {
		return sentenceFilter;
	}

	@Param
	public String getEnjuEncoding() {
		return enjuEncoding;
	}

	@Param
	public ExecutableFile getEnjuExecutable() {
		return enjuExecutable;
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

	@Param(nameType=NameType.ARGUMENT)
	public String getSentenceRole() {
		return sentenceRole;
	}

	@Param(nameType=NameType.FEATURE)
	public String getDependencyLabelFeatureName() {
		return dependencyLabelFeatureName;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getDependencyHeadRole() {
		return dependencyHeadRole;
	}

	@Param(nameType=NameType.FEATURE)
	public String getParseStatusFeature() {
		return parseStatusFeature;
	}

	public void setParseStatusFeature(String parseStatusFeature) {
		this.parseStatusFeature = parseStatusFeature;
	}

	public void setDependencyHeadRole(String dependencyHeadRole) {
		this.dependencyHeadRole = dependencyHeadRole;
	}

	public void setWordFormFeatureName(String wordFormFeatureName) {
		this.wordFormFeatureName = wordFormFeatureName;
	}

	public void setPosFeatureName(String posFeatureName) {
		this.posFeatureName = posFeatureName;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}

	public void setWordLayerName(String wordLayerName) {
		this.wordLayerName = wordLayerName;
	}

	public void setSentenceFilter(Expression sentenceFilter) {
		this.sentenceFilter = sentenceFilter;
	}

	public void setEnjuEncoding(String enjuEncoding) {
		this.enjuEncoding = enjuEncoding;
	}

	public void setEnjuExecutable(ExecutableFile enjuExecutable) {
		this.enjuExecutable = enjuExecutable;
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

	public void setSentenceRole(String sentenceRole) {
		this.sentenceRole = sentenceRole;
	}

	public void setDependencyLabelFeatureName(String dependencyLabelFeatureName) {
		this.dependencyLabelFeatureName = dependencyLabelFeatureName;
	}
}
