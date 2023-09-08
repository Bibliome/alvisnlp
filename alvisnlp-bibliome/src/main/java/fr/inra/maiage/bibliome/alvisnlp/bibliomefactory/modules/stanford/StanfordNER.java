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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.stanford;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
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
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.LoggingUtils;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.mappers.Mapper;
import fr.inra.maiage.bibliome.util.mappers.Mappers;

@AlvisNLPModule
public abstract class StanfordNER extends SectionModule<SectionResolvedObjects> implements AnnotationCreator {
	private InputFile classifierFile;
	private Boolean searchInContents = false;
	private String wordLayer = DefaultNames.getWordLayer();
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String formFeature = Annotation.FORM_FEATURE_NAME;
	private String targetLayer;
	private String labelFeature;

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		try {
			LoggingUtils.configureSilentLog4J();
			Logger logger = getLogger(ctx);
			EvaluationContext evalCtx = new EvaluationContext(logger );
			AbstractSequenceClassifier<CoreLabel> classifier = createClassifier();
			AtomicInteger n = new AtomicInteger();
			for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
				Layer targetLayer = sec.ensureLayer(this.targetLayer);
				if (searchInContents) {
					String contents = sec.getContents();
					List<List<CoreLabel>> sentenceList = classifier.classify(contents);
					for (List<CoreLabel> tokenList : sentenceList) {
						readResultInContents(targetLayer, tokenList, n);
					}
				}
				else {
					for (Layer sentence : sec.getSentences(wordLayer, sentenceLayer)) {
						List<CoreLabel> tokenList = createTokenList(sentence);
						classifier.classify(tokenList);
						readResultInSentence(targetLayer, sentence, tokenList, n);
					}
				}
			}
			logger.info("created " + n.intValue() + " annotations");
		}
		catch (ClassCastException|ClassNotFoundException|IOException e) {
			throw new ProcessingException(e);
		}
	}

	private AbstractSequenceClassifier<CoreLabel> createClassifier() throws ClassCastException, ClassNotFoundException, IOException {
		String classifierPath = classifierFile.getAbsolutePath();
		return CRFClassifier.getClassifier(classifierPath);
	}

	private List<CoreLabel> createTokenList(Layer sentence) {
		List<CoreLabel> tokenList = new ArrayList<CoreLabel>(sentence.size());
		return Mappers.apply(ANNOTATION_TO_CORE_LABEL, sentence, tokenList);
	}

	private final Mapper<Annotation,CoreLabel> ANNOTATION_TO_CORE_LABEL = new Mapper<Annotation,CoreLabel>() {
		@Override
		public CoreLabel map(Annotation x) {
			CoreLabel result = new CoreLabel();
			result.setWord(x.getLastFeature(formFeature));
			return result;
		}
	};

	private void readResultInContents(Layer targetLayer, List<CoreLabel> tokenList, AtomicInteger n) {
		int start = 0;
		int end = 0;
		String prevLabel = "O";
		for (int i = 0; i < tokenList.size(); ++i) {
			CoreLabel token = tokenList.get(i);
			String label = token.get(CoreAnnotations.AnswerAnnotation.class);
			if (!label.equals(prevLabel)) {
				createAnnotation(targetLayer, start, end, prevLabel, n);
				start = token.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
				prevLabel = label;
			}
			end = token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
		}
		createAnnotation(targetLayer, start, end, prevLabel, n);
	}

	private void readResultInSentence(Layer targetLayer, Layer sentence, List<CoreLabel> tokenList, AtomicInteger n) {
		int start = 0;
		int end = 0;
		String prevLabel = "O";
		for (int i = 0; i < tokenList.size(); ++i) {
			Annotation w = sentence.get(i);
			CoreLabel token = tokenList.get(i);
			String label = token.get(CoreAnnotations.AnswerAnnotation.class);
			if (!label.equals(prevLabel)) {
				createAnnotation(targetLayer, start, end, prevLabel, n);
				start = w.getStart();
				prevLabel = label;
			}
			end = w.getEnd();
		}
		createAnnotation(targetLayer, start, end, prevLabel, n);
	}

	private void createAnnotation(Layer targetLayer, int start, int end, String label, AtomicInteger n) {
		if (!label.equals("O")) {
			Annotation a = new Annotation(this, targetLayer, start, end);
			a.addFeature(labelFeature, label);
			n.incrementAndGet();
		}
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		if (searchInContents) {
			return null;
		}
		return new String[] { wordLayer, sentenceLayer };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Param
	public InputFile getClassifierFile() {
		return classifierFile;
	}

	@Param(nameType=NameType.LAYER)
	public String getWordLayer() {
	    return this.wordLayer;
	};

	public void setWordLayer(String wordLayer) {
	    this.wordLayer = wordLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getWordLayerName() {
		return wordLayer;
	}

	@Param(nameType=NameType.LAYER)
	public String getSentenceLayer() {
	    return this.sentenceLayer;
	};

	public void setSentenceLayer(String sentenceLayer) {
	    this.sentenceLayer = sentenceLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayer;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getFormFeatureName() {
		return formFeature;
	}

	@Param(nameType=NameType.LAYER)
	public String getTargetLayer() {
	    return this.targetLayer;
	};

	public void setTargetLayer(String targetLayer) {
	    this.targetLayer = targetLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getTargetLayerName() {
		return targetLayer;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getLabelFeatureName() {
		return labelFeature;
	}

	@Param
	public Boolean getSearchInContents() {
		return searchInContents;
	}

	@Param(nameType=NameType.FEATURE)
	public String getFormFeature() {
		return formFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getLabelFeature() {
		return labelFeature;
	}

	public void setFormFeature(String formFeature) {
		this.formFeature = formFeature;
	}

	public void setLabelFeature(String labelFeature) {
		this.labelFeature = labelFeature;
	}

	public void setSearchInContents(Boolean searchInContents) {
		this.searchInContents = searchInContents;
	}

	public void setTargetLayerName(String targetLayer) {
		this.targetLayer = targetLayer;
	}

	public void setLabelFeatureName(String labelFeatureName) {
		this.labelFeature = labelFeatureName;
	}

	public void setClassifierFile(InputFile classifierFile) {
		this.classifierFile = classifierFile;
	}

	public void setWordLayerName(String wordLayer) {
		this.wordLayer = wordLayer;
	}

	public void setSentenceLayerName(String sentenceLayer) {
		this.sentenceLayer = sentenceLayer;
	}

	public void setFormFeatureName(String formFeatureName) {
		this.formFeature = formFeatureName;
	}
}
