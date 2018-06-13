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
import java.util.Iterator;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.geniatagger.GeniaTagger.GeniaTaggerResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ConstantsLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.filters.Filters;
import fr.inra.maiage.bibliome.util.mappers.Mapper;
import fr.inra.maiage.bibliome.util.mappers.Mappers;

@AlvisNLPModule
public class GeniaTagger extends SectionModule<GeniaTaggerResolvedObjects> {
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String wordLayerName = DefaultNames.getWordLayer();
	private String wordFormFeature = Annotation.FORM_FEATURE_NAME;
	private String posFeature = DefaultNames.getPosTagFeature();
	private String lemmaFeature = DefaultNames.getCanonicalFormFeature();
	private String chunkFeature;
	private String entityFeature;
	private File geniaDir;
	private File geniaTaggerExecutable = new File("geniatagger");
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
			GeniaTaggerExternal ext = prepare(ctx, corpus);
			callExternal(ctx, "genia", ext, geniaCharset, "genia-command.sh");
			collect(ctx, corpus, ext);
		}
		catch (IOException ioe) {
			rethrow(ioe);
		}
	}
	
	@TimeThis(task="prepare-data", category=TimerCategory.PREPARE_DATA)
	public GeniaTaggerExternal prepare(ProcessingContext<Corpus> ctx, Corpus corpus) throws IOException {
		return new GeniaTaggerExternal(this, ctx, corpus, getTempDir(ctx));
	}

	@SuppressWarnings("static-method")
	@TimeThis(task="read-genia", category=TimerCategory.COLLECT_DATA)
	protected void collect(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, Corpus corpus, GeniaTaggerExternal ext) throws ProcessingException, IOException {
		ext.readOutput(corpus);
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { sentenceLayerName, wordLayerName };
	}
	
	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}
    
    private static final class AnnotationCollector implements Mapper<Section,Iterator<Annotation>> {
		private final EvaluationContext ctx;
        private final String layerName;
        private final Evaluator filter;
        
        private AnnotationCollector(EvaluationContext ctx, String layerName, Evaluator filter) {
            super();
            this.ctx = ctx;
            this.layerName = layerName;
            this.filter = filter;
        }

        @Override
        public Iterator<Annotation> map(Section sec) {
            return annotationIterator(ctx, sec, layerName, filter);
        }
    }
    
    private static Iterator<Annotation> annotationIterator(EvaluationContext ctx, Section sec, String name, Evaluator filter) {
        if (sec.hasLayer(name)) {
            if (filter == null)
                return sec.getLayer(name).iterator();
            return Filters.apply(filter.getFilter(ctx), sec.getLayer(name).iterator());
        }
        return Iterators.emptyIterator();
    }

	Iterator<Annotation> getSentenceIterator(EvaluationContext ctx, Corpus corpus, Evaluator resolvedDocumentFilter, Evaluator resolvedSectionFilter, Evaluator resolvedSentenceFilter) {
        return Iterators.flatten(Mappers.apply(new AnnotationCollector(ctx, sentenceLayerName, resolvedSentenceFilter), corpus.sectionIterator(ctx, resolvedDocumentFilter, resolvedSectionFilter)));
	}

	@Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing sentence annotations.")
	public String getSentenceLayerName() {
		return sentenceLayerName;
	}

	@Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing word annotations.")
	public String getWordLayerName() {
		return wordLayerName;
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
		this.sentenceLayerName = sentences;
	}

	public void setWordLayerName(String words) {
		this.wordLayerName = words;
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
