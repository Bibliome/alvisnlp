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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.geniatagger;

import java.io.File;
import java.io.IOException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.geniatagger.GeniaTagger.GeniaTaggerResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ConstantsLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;

@AlvisNLPModule
public class GeniaTagger extends SectionModule<GeniaTaggerResolvedObjects> {
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String wordLayer = DefaultNames.getWordLayer();
	private String wordFormFeature = Annotation.FORM_FEATURE_NAME;
	private String posFeature = DefaultNames.getPosTagFeature();
	private String lemmaFeature = DefaultNames.getCanonicalFormFeature();
	private String chunkFeature;
	private String entityFeature;
	private File geniaDir;
	private File geniaTaggerExecutable = new File("./geniatagger");
	private String geniaCharset = "UTF-8";
	private Expression sentenceFilter = ConstantsLibrary.TRUE;
	private Boolean treeTaggerTagset = false;

	static class GeniaTaggerResolvedObjects extends SectionResolvedObjects {
		final Evaluator sentenceFilter;

		private GeniaTaggerResolvedObjects(ProcessingContext<Corpus> ctx, GeniaTagger module) throws ResolverException {
			super(ctx, module);
			sentenceFilter = module.sentenceFilter.resolveExpressions(rootResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			sentenceFilter.collectUsedNames(nameUsage, defaultType);
		}
	}

	@Override
	protected GeniaTaggerResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new GeniaTaggerResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			new GeniaTaggerExternalHandler(ctx, this, corpus).start();
		}
		catch (IOException|InterruptedException e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { sentenceLayer, wordLayer };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing sentence annotations.")
	public String getSentenceLayer() {
	    return this.sentenceLayer;
	};

	public void setSentenceLayer(String sentenceLayer) {
	    this.sentenceLayer = sentenceLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing sentence annotations.")
	public String getSentenceLayerName() {
		return sentenceLayer;
	}

	@Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing word annotations.")
	public String getWordLayer() {
	    return this.wordLayer;
	};

	public void setWordLayer(String wordLayer) {
	    this.wordLayer = wordLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing word annotations.")
	public String getWordLayerName() {
		return wordLayer;
	}

	@Param(nameType=NameType.FEATURE, defaultDoc = "Feature containing the word surface form.")
	public String getWordFormFeature() {
		return wordFormFeature;
	}

	@Param(nameType=NameType.FEATURE, defaultDoc = "Feature where to put the POS tag.")
	public String getPosFeature() {
		return posFeature;
	}

	@Param(nameType=NameType.FEATURE, defaultDoc = "Feature where to put the word lemma.")
	public String getLemmaFeature() {
		return lemmaFeature;
	}

	@Param(nameType=NameType.FEATURE, defaultDoc = "Feature where to put the chunk status.", mandatory = false)
	public String getChunkFeature() {
		return chunkFeature;
	}

	@Param(nameType=NameType.FEATURE, defaultDoc = "Feature where to put the entity status.", mandatory = false)
	public String getEntityFeature() {
		return entityFeature;
	}

	@Param(defaultDoc = "Directory where geniatagger is installed.")
	public File getGeniaDir() {
		return geniaDir;
	}

	@Param(defaultDoc = "Name of the geniatagger executable file.")
	public File getGeniaTaggerExecutable() {
		return geniaTaggerExecutable;
	}

	@Param(defaultDoc = "Character encoding of geniatagger input and output.")
	public String getGeniaCharset() {
		return geniaCharset;
	}

	@Param
	public Expression getSentenceFilter() {
		return sentenceFilter;
	}

	@Param
	public Boolean getTreeTaggerTagset() {
		return treeTaggerTagset;
	}

	public void setTreeTaggerTagset(Boolean treeTaggerTagset) {
		this.treeTaggerTagset = treeTaggerTagset;
	}

	public void setSentenceFilter(Expression sentenceFilter) {
		this.sentenceFilter = sentenceFilter;
	}

	public void setSentenceLayerName(String sentences) {
		this.sentenceLayer = sentences;
	}

	public void setWordLayerName(String words) {
		this.wordLayer = words;
	}

	public void setWordFormFeature(String wordForm) {
		this.wordFormFeature = wordForm;
	}

	public void setPosFeature(String pos) {
		this.posFeature = pos;
	}

	public void setLemmaFeature(String lemma) {
		this.lemmaFeature = lemma;
	}

	public void setChunkFeature(String chunk) {
		this.chunkFeature = chunk;
	}

	public void setEntityFeature(String entity) {
		this.entityFeature = entity;
	}

	public void setGeniaDir(File geniaDir) {
		this.geniaDir = geniaDir;
	}

	public void setGeniaTaggerExecutable(File geniaTaggerExecutable) {
		this.geniaTaggerExecutable = geniaTaggerExecutable;
	}

	public void setGeniaCharset(String geniaCharset) {
		this.geniaCharset = geniaCharset;
	}
}
