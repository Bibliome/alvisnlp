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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tomap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.trie.TrieProjector;
import org.xml.sax.SAXException;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.marshall.Decoder;
import fr.inra.maiage.bibliome.util.marshall.Encoder;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.tomap.Candidate;
import fr.inra.maiage.bibliome.util.tomap.StringNormalization;
import fr.inra.maiage.bibliome.util.tomap.Token;
import fr.inra.maiage.bibliome.util.tomap.TokenNormalization;
import fr.inra.maiage.bibliome.util.tomap.classifiers.Attribution;
import fr.inra.maiage.bibliome.util.tomap.classifiers.CandidateClassifier;
import fr.inra.maiage.bibliome.util.tomap.readers.YateaCandidateReader;
import fr.inra.maiage.bibliome.util.tomap.readers.YateaCandidateReader.YateaResult;
import fr.inra.maiage.bibliome.util.trie.Trie;

@AlvisNLPModule
public abstract class TomapProjector extends TrieProjector<SectionResolvedObjects,Attribution> {
	private SourceStream yateaFile;
	private Boolean lemmaKeys = false;
	private Boolean onlyMNP = false;
	private String conceptFeature;
	private String explanationFeaturePrefix;
	private TomapClassifier tomapClassifier;
	private String scoreFeature;

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	protected void fillTrie(Logger logger, Trie<Attribution> trie, Corpus corpus) throws IOException, ModuleException {
		try {
			CandidateClassifier classifier = tomapClassifier.readClassifier(this, logger);
			Collection<Candidate> candidates = readCandidates(logger);
			for (Candidate cand : candidates) {
				List<Attribution> attributions = classifier.classify(cand);
				if (attributions.isEmpty()) {
					continue;
				}
				CharSequence key = getCandidateKey(cand);
				for (Attribution attr : attributions) {
					trie.addEntry(key, attr);
				}
			}
		}
		catch (SAXException|ParserConfigurationException e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	protected void finish() {
	}

	private CharSequence getCandidateKey(Candidate cand) {
		StringBuilder result = new StringBuilder();
		boolean notFirst = false;
		for (Token t : cand.getTokens()) {
			if (notFirst) {
				result.append(' ');
			}
			else {
				notFirst = true;
			}
			result.append(lemmaKeys ? t.getLemma() : t.getForm());
		}
		return result;
	}

	private Collection<Candidate> readCandidates(Logger logger) throws SAXException, ParserConfigurationException, IOException {
		YateaCandidateReader yateaReader = new YateaCandidateReader(logger, getTokenNormalization(), getStringNormalization());
		try (InputStream is = yateaFile.getInputStream()) {
			YateaResult yateaResult = yateaReader.parseStream(is);
			if (onlyMNP) {
				return yateaResult.getMNPCandidates();
			}
			return yateaResult.getCandidates();
		}
	}

	TokenNormalization getTokenNormalization() {
		if (getLemmaKeys()) {
			return TokenNormalization.LEMMA;
		}
		return TokenNormalization.FORM;
	}

	StringNormalization getStringNormalization() {
		return StringNormalization.get(getCaseInsensitive(), getIgnoreDiacritics());
	}

	@Override
	protected boolean marshallingSupported() {
		return false;
	}

	@Override
	protected Decoder<Attribution> getDecoder() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Encoder<Attribution> getEncoder() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void handleMatch(Attribution value, Annotation a) {
		a.addFeature(conceptFeature, value.getConceptID());
		if (explanationFeaturePrefix != null) {
			for (String key : value.getExplanationKeys()) {
				a.addFeature(explanationFeaturePrefix + key, value.getExplanation(key));
			}
		}
		if (scoreFeature != null) {
			a.addFeature(scoreFeature, Double.toString(value.getScore()));
		}
	}

	@Override
	@TimeThis(task="create-trie", category=TimerCategory.LOAD_RESOURCE)
	protected Trie<Attribution> getTrie(ProcessingContext ctx, Logger logger, Corpus corpus) throws IOException, ModuleException {
		return super.getTrie(ctx, logger, corpus);
	}

	@Param
	public SourceStream getYateaFile() {
		return yateaFile;
	}

	@Param
	public Boolean getLemmaKeys() {
		return lemmaKeys;
	}

	@Param
	public Boolean getOnlyMNP() {
		return onlyMNP;
	}

	@Param(nameType=NameType.FEATURE)
	public String getConceptFeature() {
		return conceptFeature;
	}

	@Param(mandatory=false)
	public String getExplanationFeaturePrefix() {
		return explanationFeaturePrefix;
	}

	@Param
	public TomapClassifier getTomapClassifier() {
		return tomapClassifier;
	}

	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String getScoreFeature() {
		return scoreFeature;
	}

	public void setScoreFeature(String scoreFeature) {
		this.scoreFeature = scoreFeature;
	}

	public void setTomapClassifier(TomapClassifier tomapClassifier) {
		this.tomapClassifier = tomapClassifier;
	}

	public void setYateaFile(SourceStream yateaFile) {
		this.yateaFile = yateaFile;
	}

	public void setLemmaKeys(Boolean lemmaKeys) {
		this.lemmaKeys = lemmaKeys;
	}

	public void setOnlyMNP(Boolean onlyMNP) {
		this.onlyMNP = onlyMNP;
	}

	public void setConceptFeature(String conceptFeature) {
		this.conceptFeature = conceptFeature;
	}

	public void setExplanationFeaturePrefix(String explanationFeaturePrefix) {
		this.explanationFeaturePrefix = explanationFeaturePrefix;
	}
}
