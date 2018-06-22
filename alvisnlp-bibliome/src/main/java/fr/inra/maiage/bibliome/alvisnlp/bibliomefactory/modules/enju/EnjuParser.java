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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.enju;

import java.io.IOException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.enju.EnjuParser.EnjuParserResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ConstantsLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;

@AlvisNLPModule
public abstract class EnjuParser extends SectionModule<EnjuParserResolvedObjects> implements TupleCreator {
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private Expression sentenceFilter = ConstantsLibrary.TRUE;
	private String wordLayerName = DefaultNames.getWordLayer();
	private String wordFormFeatureName = Annotation.FORM_FEATURE_NAME;
	private String posFeatureName = DefaultNames.getPosTagFeature();
	private String lemmaFeatureName = DefaultNames.getCanonicalFormFeature();
	
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
	
	class EnjuParserResolvedObjects extends SectionResolvedObjects {
		@SuppressWarnings("hiding")
		private final Evaluator sentenceFilter;
		
		private EnjuParserResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
			super(ctx, EnjuParser.this);
			sentenceFilter = EnjuParser.this.sentenceFilter.resolveExpressions(rootResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			getSentenceFilter().collectUsedNames(nameUsage, defaultType);
		}

		Evaluator getSentenceFilter() {
			return sentenceFilter;
		}
	}
	
	@Override
	protected EnjuParserResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new EnjuParserResolvedObjects(ctx);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			new EnjuParserExternalHandler(ctx, this, corpus).start();;
		}
		catch (IOException | InterruptedException e) {
			rethrow(e);
		}
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

	@Param(nameType=NameType.FEATURE)
	public String getLemmaFeatureName() {
		return lemmaFeatureName;
	}

	public void setLemmaFeatureName(String lemmaFeatureName) {
		this.lemmaFeatureName = lemmaFeatureName;
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
