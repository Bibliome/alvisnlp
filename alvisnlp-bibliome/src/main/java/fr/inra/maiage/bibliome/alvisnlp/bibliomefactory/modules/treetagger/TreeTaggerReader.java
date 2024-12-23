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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.treetagger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.DocumentCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.StringCat;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.filelines.RecordFileLines;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule
public abstract class TreeTaggerReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator {
	private String sectionName = DefaultNames.getDefaultSectionName();
	private String wordLayer = DefaultNames.getWordLayer();
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String posFeature = null;
	private String lemmaFeature = null;
	private String charset = "UTF-8";
	private SourceStream source;

	private static final RecordFileLines recordFileLines = new RecordFileLines();

	private static final String getSectionContents(List<List<String>> tokens) {
		StringCat sb = new StringCat();
		for (List<String> t : tokens) {
			if (!sb.isEmpty())
				sb.append(" ");
			sb.append(t.get(0));
		}
		return sb.toString();
	}

	private void fillLayers(Layer wordLayer, Layer sentenceLayer, List<List<String>> tokens) throws ProcessingException {
		int wordStart = 0;
		int sentenceStart = 0;
		for (int i = 0; i < tokens.size(); ++i) {
			List<String> t = tokens.get(i);
			if (t.size() != 3)
				throw new ProcessingException("malformed line in: " + wordLayer.getSection().getDocument().getId());
			String surface = t.get(0);
			int end = wordStart + surface.length();
			String pos = t.get(1);
			if ("SENT".equals(pos)) {
				new Annotation(this, sentenceLayer, sentenceStart, end);
				sentenceStart = end + 1;
			}
			Annotation a = new Annotation(this, wordLayer, wordStart, end);
			if (posFeature != null)
				a.addFeature(posFeature, pos);
			if (lemmaFeature != null)
				a.addFeature(lemmaFeature, t.get(2));
			wordStart = end + 1;
			tokens.set(i, null);
		}
	}

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		try {
			for (BufferedReader r : Iterators.loop(source.getBufferedReaders())) {
				processFile(logger, corpus, r);
				r.close();
			}
		}
		catch (IOException|InvalidFileLineEntry e) {
			throw new ProcessingException(e);
		}
	}

	private void processFile(Logger logger, Corpus corpus, BufferedReader reader) throws ModuleException, IOException, InvalidFileLineEntry {
		String name = source.getStreamName(reader);
		logger.fine("reading: " + name);

		List<List<String>> tokens = new ArrayList<List<String>>();
		recordFileLines.process(reader, tokens);
		reader.close();

		Document doc = Document.getDocument(this, corpus, name);
		Section sec = new Section(this, doc, sectionName, getSectionContents(tokens));
		fillLayers(sec.ensureLayer(wordLayer), sec.ensureLayer(sentenceLayer), tokens);
	}

	@Param
	public String getSectionName() {
		return sectionName;
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
	@Param(mandatory=false)
	public String getPosFeatureKey() {
		return posFeature;
	}

	@Deprecated
	@Param(mandatory=false)
	public String getLemmaFeatureKey() {
		return lemmaFeature;
	}

	@Param
	public String getCharset() {
		return charset;
	}

	@Deprecated
	@Param
	public SourceStream getSourcePath() {
		return source;
	}

	@Param
	public SourceStream getSource() {
		return source;
	}

	@Param(mandatory = false, nameType = NameType.FEATURE)
	public String getPosFeature() {
		return posFeature;
	}

	@Param(mandatory = false, nameType = NameType.FEATURE)
	public String getLemmaFeature() {
		return lemmaFeature;
	}

	public void setPosFeature(String posFeature) {
		this.posFeature = posFeature;
	}

	public void setLemmaFeature(String lemmaFeature) {
		this.lemmaFeature = lemmaFeature;
	}

	public void setSource(SourceStream source) {
		this.source = source;
	}

	public void setSourcePath(SourceStream sourcePath) {
		this.source = sourcePath;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setPosFeatureKey(String posFeatureKey) {
		this.posFeature = posFeatureKey;
	}

	public void setLemmaFeatureKey(String lemmaFeatureKey) {
		this.lemmaFeature = lemmaFeatureKey;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public void setWordLayerName(String wordLayer) {
		this.wordLayer = wordLayer;
	}

	public void setSentenceLayerName(String sentenceLayer) {
		this.sentenceLayer = sentenceLayer;
	}
}
