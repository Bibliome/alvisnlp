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



package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.segmig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;

// TODO: Auto-generated Javadoc
/**
 * The Class SeSMig.
 */
@AlvisNLPModule
public abstract class SeSMig extends SectionModule<SectionResolvedObjects> implements AnnotationCreator {
    /** The word annotations. */
    private String                       wordLayer      = DefaultNames.getWordLayer();

    /** The target layer name. */
    private String targetLayer    = DefaultNames.getSentenceLayer();

    private String noBreakLayer = null;

    /** The eos status feature. */
    private String                       eosStatusFeature   = DefaultNames.getEndOfSentenceStatusFeature();

    /** The strong punctuations. */
    private String                       strongPunctuations = "?.!";

    /** The form feature. */
    private String                       formFeature        = Annotation.FORM_FEATURE_NAME;

    /** The type feature. */
    private String                       typeFeature        = DefaultNames.getWordTypeFeature();

    /** The sentence words. */
    private final List<Annotation> sentenceWords      = new ArrayList<Annotation>();

    /** The sentence layer. */
    private Layer                        sentenceLayer;

    /*
     * (non-Javadoc)
     *
     * @see fr.inra.maiage.bibliome.alvisnlp.core.module.lib.SectionModule#addLayersToSectionFilter()
     */
    @Override
    public String[] addLayersToSectionFilter() {
        return new String[] {
            wordLayer
        };
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.inra.maiage.bibliome.alvisnlp.core.module.lib.SectionModule#addFeaturesToSectionFilter()
     */
    @Override
    public String[] addFeaturesToSectionFilter() {
        return new String[] {};
    }

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ModuleBase#process(alvisnlp.module.ProcessingContext,
     * fr.inra.maiage.bibliome.alvisnlp.core.document.Corpus)
     */
    @Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
    	int n = 0;
        for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
//        	Logger logger = getLogger(ctx);
            sentenceLayer = sec.ensureLayer(targetLayer);
            sentenceWords.clear();
            Iterator<Annotation> noBreakIt;
            if ((noBreakLayer != null) && sec.hasLayer(noBreakLayer))
            	noBreakIt = sec.getLayer(noBreakLayer).iterator();
            else
            	noBreakIt = Iterators.emptyIterator();
            Annotation noBreak = noBreakIt.hasNext() ? noBreakIt.next() : null;
            Annotation pending = null;
            for (Annotation w : sec.getLayer(wordLayer)) {
//            	logger.finer(w.toString());
                if (pending != null) {
//                	logger.fine("pending = " + pending);
                    if (Character.isUpperCase(w.getLastFeature(formFeature).charAt(0))) {
                        pending.addFeature(eosStatusFeature, "eos");
                        newSentence(pending);
                        n++;
//                        logger.fine("we decided that '" + pending + "' is a sentence boundary");
                    }
                    else {
                        pending.addFeature(eosStatusFeature, "not-eos");
                        sentenceWords.add(pending);
//                        logger.fine("we decided that '" + pending + "' is NOT a sentence boundary");
                    }
                    pending = null;
                }
                while (noBreak != null) {
                	if ((noBreak.getEnd() > w.getEnd()) || (noBreak.getStart() > w.getEnd()))
                		break;
                	noBreak = noBreakIt.hasNext() ? noBreakIt.next() : null;
                }
                String eos = w.getLastFeature(eosStatusFeature);
                if ("not-eos".equals(eos) || ((noBreak != null) && (w.getEnd() > noBreak.getStart()))) {
//                	logger.fine("NOT EOS w = " + w);
                    sentenceWords.add(w);
                    continue;
                }
                if ("eos".equals(eos)) {
//                	logger.fine("EOS w = " + w);
                    newSentence(w);
                    n++;
                    continue;
                }
                if ("maybe-eos".equals(eos)) {
//                	logger.fine("MAYBE EOS w = " + w);
                    pending = w;
                    continue;
                }
                if ("punctuation".equals(w.getLastFeature(typeFeature)) && (strongPunctuations.indexOf(w.getForm().charAt(0)) >= 0)) {
//                	logger.fine("STRONG w = " + w);
                    pending = w;
                    continue;
                }
//            	logger.fine("DEFAULT w = " + w + ", " + w.getLastFeature(typeFeature));
                w.addFeature(eosStatusFeature, "not-eos");
                sentenceWords.add(w);
            }
            if (pending != null) {
//            	logger.fine("TRAIL PENDING: " + pending);
                pending.addFeature(eosStatusFeature, "eos");
                newSentence(pending);
                n++;
            }
            else if (!sentenceWords.isEmpty()) {
                Annotation last = sentenceWords.remove(sentenceWords.size() - 1);
//            	logger.fine("TRAILING: " + sentenceWords.get(0) + " - " + last);
                last.addFeature(eosStatusFeature, "eos");
                newSentence(last);
                n++;
            }
        }
        getLogger(ctx).info("created " + n + " sentence annotations");
    }

    /**
     * New sentence.
     *
     * @param lastWord
     *            the last word
     */
    private void newSentence(Annotation lastWord) {
        int start = sentenceWords.isEmpty() ? lastWord.getStart() : sentenceWords.get(0).getStart();
        int end = lastWord.getEnd();
        lastWord.addFeature(eosStatusFeature, "eos");
        new Annotation(this, sentenceLayer, start, end);
        sentenceWords.clear();
    }

    /**
     * Gets the word layer name.
     *
     * @return the wordLayer
     */
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

    /**
     * Gets the target layer name.
     *
     * @return the targetLayer
     */
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer where to store sentence annotations.")
    public String getTargetLayer() {
        return this.targetLayer;
    };

    public void setTargetLayer(String targetLayer) {
        this.targetLayer = targetLayer;
    };

    @Deprecated
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer where to store sentence annotations.")
    public String getTargetLayerName() {
        return targetLayer;
    }

    /**
     * Gets the eos status feature.
     *
     * @return the eosStatusFeature
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Name of the feature (in words) containing the end-of-sentence status (not-eos, maybe-eos).")
    public String getEosStatusFeature() {
        return eosStatusFeature;
    }

    /**
     * Gets the strong punctuations.
     *
     * @return the strongPunctuations
     */
    @Param(defaultDoc = "List of strong punctuations.")
    public String getStrongPunctuations() {
        return strongPunctuations;
    }

    /**
     * Gets the form feature.
     *
     * @return the formFeature
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Name of the feature containing the word surface form.")
    public String getFormFeature() {
        return formFeature;
    }

    /**
     * Gets the type feature.
     *
     * @return the typeFeature
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Name of the feature where to read word annotation type.")
    public String getTypeFeature() {
        return typeFeature;
    }

    @Param(nameType=NameType.LAYER, mandatory=false, defaultDoc = "Name of the layer containing annotations within which there cannot be sentence boundaries.")
    public String getNoBreakLayer() {
        return this.noBreakLayer;
    };

    public void setNoBreakLayer(String noBreakLayer) {
        this.noBreakLayer = noBreakLayer;
    };

    @Deprecated
    @Param(nameType=NameType.LAYER, mandatory=false, defaultDoc = "Name of the layer containing annotations within which there cannot be sentence boundaries.")
    public String getNoBreakLayerName() {
		return noBreakLayer;
	}

	public void setNoBreakLayerName(String noBreakLayer) {
		this.noBreakLayer = noBreakLayer;
	}

	/**
     * Sets the word layer name.
     *
     * @param wordLayer
     *            the wordLayer to set
     */
    public void setWordLayerName(String wordLayer) {
        this.wordLayer = wordLayer;
    }

    /**
     * Sets the target layer name.
     *
     * @param targetLayer
     *            the targetLayer to set
     */
    public void setTargetLayerName(String targetLayer) {
        this.targetLayer = targetLayer;
    }

    /**
     * Sets the eos status feature.
     *
     * @param eosStatusFeature
     *            the eosStatusFeature to set
     */
    public void setEosStatusFeature(String eosStatusFeature) {
        this.eosStatusFeature = eosStatusFeature;
    }

    /**
     * Sets the strong punctuations.
     *
     * @param strongPunctuations
     *            the strongPunctuations to set
     */
    public void setStrongPunctuations(String strongPunctuations) {
        this.strongPunctuations = strongPunctuations;
    }

    /**
     * Sets the form feature.
     *
     * @param formFeature
     *            the formFeature to set
     */
    public void setFormFeature(String formFeature) {
        this.formFeature = formFeature;
    }

    /**
     * Sets the type feature.
     *
     * @param typeFeature
     *            the typeFeature to set
     */
    public void setTypeFeature(String typeFeature) {
        this.typeFeature = typeFeature;
    }
}
