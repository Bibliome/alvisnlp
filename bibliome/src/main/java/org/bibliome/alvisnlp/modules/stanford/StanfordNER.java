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


package org.bibliome.alvisnlp.modules.stanford;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.files.InputFile;
import org.bibliome.util.mappers.Mapper;
import org.bibliome.util.mappers.Mappers;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

@AlvisNLPModule(beta=true)
public abstract class StanfordNER extends SectionModule<SectionResolvedObjects> implements AnnotationCreator {
	private InputFile classifierFile;
	private Boolean searchInContents = false;
	private String wordLayerName = DefaultNames.getWordLayer();
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String formFeatureName = Annotation.FORM_FEATURE_NAME;
	private String targetLayerName;
	private String labelFeatureName;
	
	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			Logger logger = getLogger(ctx);
			EvaluationContext evalCtx = new EvaluationContext(logger );
			AbstractSequenceClassifier<CoreLabel> classifier = createClassifier();
			AtomicInteger n = new AtomicInteger();
			for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
				Layer targetLayer = sec.ensureLayer(targetLayerName);
				if (searchInContents) {
					String contents = sec.getContents();
					List<List<CoreLabel>> sentenceList = classifier.classify(contents);
					for (List<CoreLabel> tokenList : sentenceList) {
						readResultInContents(targetLayer, tokenList, n);
					}
				}
				else {
					for (Layer sentence : sec.getSentences(wordLayerName, sentenceLayerName)) {
						List<CoreLabel> tokenList = createTokenList(sentence);
						classifier.classify(tokenList);
						readResultInSentence(targetLayer, sentence, tokenList, n);
					}
				}
			}
			logger.info("created " + n.intValue() + " annotations");
		}
		catch (ClassCastException|ClassNotFoundException|IOException e) {
			rethrow(e);
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
			result.setWord(x.getLastFeature(formFeatureName));
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
			a.addFeature(labelFeatureName, label);
			n.incrementAndGet();
		}
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		if (searchInContents) {
			return null;
		}
		return new String[] { wordLayerName, sentenceLayerName };
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
	public String getWordLayerName() {
		return wordLayerName;
	}

	@Param(nameType=NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayerName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getFormFeatureName() {
		return formFeatureName;
	}

	@Param(nameType=NameType.LAYER)
	public String getTargetLayerName() {
		return targetLayerName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getLabelFeatureName() {
		return labelFeatureName;
	}

	@Param
	public Boolean getSearchInContents() {
		return searchInContents;
	}

	public void setSearchInContents(Boolean searchInContents) {
		this.searchInContents = searchInContents;
	}

	public void setTargetLayerName(String targetLayerName) {
		this.targetLayerName = targetLayerName;
	}

	public void setLabelFeatureName(String labelFeatureName) {
		this.labelFeatureName = labelFeatureName;
	}

	public void setClassifierFile(InputFile classifierFile) {
		this.classifierFile = classifierFile;
	}

	public void setWordLayerName(String wordLayerName) {
		this.wordLayerName = wordLayerName;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}

	public void setFormFeatureName(String formFeatureName) {
		this.formFeatureName = formFeatureName;
	}
}
