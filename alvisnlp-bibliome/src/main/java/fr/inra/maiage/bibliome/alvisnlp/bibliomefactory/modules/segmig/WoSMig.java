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

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.AnnotationComparator;
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

@AlvisNLPModule
public abstract class WoSMig extends SectionModule<SectionResolvedObjects> implements AnnotationCreator {
	private static final Pattern WORD_PATTERN = Pattern.compile("\\S+");
	private String targetLayer = DefaultNames.getWordLayer();
	private String fixedFormLayer = null;
	private String punctuations = "?.!;,:-";
	private String balancedPunctuations = "()[]{}\"\"";
	private String annotationTypeFeature = DefaultNames.getWordTypeFeature();
	private String punctuationType = "punctuation";
	private String wordType = "word";
	private String fixedType = "fixed";
	private AnnotationComparator annotationComparator = AnnotationComparator.byLength;

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		if (balancedPunctuations.length() % 2 != 0) {
			getLogger(ctx).warning("balancedPunctuations has odd number of characters, the last one will be ignored");
			balancedPunctuations = balancedPunctuations.substring(0, balancedPunctuations.length() - 1);
		}
		Pattern punctPattern = Pattern.compile("[" + Pattern.quote(punctuations) + "]");
		int n = 0;
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			String contents = sec.getContents();
			Layer layer = sec.ensureLayer(targetLayer);
			int start = 0;
			if ((fixedFormLayer != null) && sec.hasLayer(fixedFormLayer)) {
				Layer fixedLayer = sec.getLayer(fixedFormLayer);
				if (fixedLayer.hasOverlaps()) {
					getLogger(ctx).warning("fixed layer has overlapping annotations");
					fixedLayer = fixedLayer.getAnonymousCopy();
					fixedLayer.removeOverlaps(annotationComparator);
				}
				for (Annotation fixed : fixedLayer) {
					n += searchPunctuations(layer, contents.substring(start, fixed.getStart()), start, punctPattern);
					layer.add(fixed);
					if (annotationTypeFeature != null)
						fixed.addFeature(annotationTypeFeature, fixedType);
					start = fixed.getEnd();
				}
			}
			n += searchPunctuations(layer, contents.substring(start), start, punctPattern);
		}
		getLogger(ctx).info("created " + n + " word annotations");
	}

	private int searchPunctuations(Layer layer, String s, int offset, Pattern punctPattern) {
		Matcher m = punctPattern.matcher(s);
		int start = 0;
		int result = 0;
		while (m.find()) {
			result += searchWords(layer, s.substring(start, m.start()), offset + start);
			addWord(layer, offset + m.start(), offset + m.end(), punctuationType);
			result++;
			start = m.end();
		}
		result += searchWords(layer, s.substring(start), offset + start);
		return result;
	}

	private int searchWords(Layer layer, String s, int offset) {
		Matcher m = WORD_PATTERN.matcher(s);
		int result = 0;
		while (m.find())
			result += processWord(layer, s.substring(m.start(), m.end()), offset + m.start(), offset + m.end());
		return result;
	}

	private int processWord(Layer layer, String s, int start, int end) {
		int len = end - start;
		if (len <= 0)
			throw new Error();
		char firstChar = s.charAt(0);
		int firstBalancedIndex = balancedPunctuations.indexOf(firstChar);
		if (len == 1) {
			if (firstBalancedIndex >= 0)
				addWord(layer, start, end, punctuationType);
			else
				addWord(layer, start, end, wordType);
			return 1;
		}
		String middle = s.substring(1, len - 1);
		boolean cutFirst = false;
		if (firstBalancedIndex >= 0) {
			if (firstBalancedIndex % 2 == 1)
				cutFirst = true;
			else {
				char closing = balancedPunctuations.charAt(firstBalancedIndex + 1);
				int midBalancedIndex = middle.indexOf(closing);
				if (midBalancedIndex < 0)
					cutFirst = true;
			}
		}
		char lastChar = s.charAt(s.length() - 1);
		int lastBalancedIndex = balancedPunctuations.indexOf(lastChar);
		boolean cutLast = false;
		if (lastBalancedIndex >= 0) {
			if (lastBalancedIndex % 2 == 0)
				cutLast = true;
			else {
				char opening = balancedPunctuations.charAt(lastBalancedIndex - 1);
				int midBalancedIndex = middle.indexOf(opening);
				if (midBalancedIndex < 0)
					cutLast = true;
			}
		}
		if (cutFirst) {
			addWord(layer, start, start + 1, punctuationType);
			if (cutLast) {
				if (!middle.isEmpty())
					addWord(layer, start + 1, end - 1, wordType);
				addWord(layer, end - 1, end, punctuationType);
				return 3;
			}
			addWord(layer, start + 1, end, wordType);
			return 2;
		}
		if (cutLast) {
			addWord(layer, start, end - 1, wordType);
			addWord(layer, end - 1, end, punctuationType);
			return 2;
		}
		addWord(layer, start, end, wordType);
		return 1;
	}

	private void addWord(Layer layer, int start, int end, String type) {
		Annotation a = new Annotation(this, layer, start, end);
		if (annotationTypeFeature != null)
			a.addFeature(annotationTypeFeature, type);
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

	@Param(nameType=NameType.LAYER, mandatory = false)
	public String getFixedFormLayer() {
	    return this.fixedFormLayer;
	};

	public void setFixedFormLayer(String fixedFormLayer) {
	    this.fixedFormLayer = fixedFormLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER, mandatory = false)
	public String getFixedFormLayerName() {
		return fixedFormLayer;
	}

	@Param
	public String getPunctuations() {
		return punctuations;
	}

	@Param
	public String getBalancedPunctuations() {
		return balancedPunctuations;
	}

	@Param(nameType=NameType.FEATURE)
	public String getAnnotationTypeFeature() {
		return annotationTypeFeature;
	}

	@Param
	public String getPunctuationType() {
		return punctuationType;
	}

	@Param
	public String getWordType() {
		return wordType;
	}

	@Param
	public String getFixedType() {
		return fixedType;
	}

	@Param
	public AnnotationComparator getAnnotationComparator() {
		return annotationComparator;
	}

	public void setAnnotationComparator(AnnotationComparator annotationComparator) {
		this.annotationComparator = annotationComparator;
	}

	public void setTargetLayerName(String targetLayer) {
		this.targetLayer = targetLayer;
	}

	public void setFixedFormLayerName(String fixedFormsLayer) {
		this.fixedFormLayer = fixedFormsLayer;
	}

	public void setPunctuations(String punctuations) {
		this.punctuations = punctuations;
	}

	public void setBalancedPunctuations(String balancedPunctuations) {
		this.balancedPunctuations = balancedPunctuations;
	}

	public void setAnnotationTypeFeature(String typeFeature) {
		this.annotationTypeFeature = typeFeature;
	}

	public void setPunctuationType(String punctuationType) {
		this.punctuationType = punctuationType;
	}

	public void setWordType(String wordType) {
		this.wordType = wordType;
	}

	public void setFixedType(String fixedType) {
		this.fixedType = fixedType;
	}
}
