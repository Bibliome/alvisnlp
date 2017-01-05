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


package org.bibliome.alvisnlp.modules.tomap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bibliome.util.Checkable;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.tomap.Candidate;
import org.bibliome.util.tomap.StringNormalization;
import org.bibliome.util.tomap.Token;
import org.bibliome.util.tomap.TokenNormalization;
import org.bibliome.util.tomap.classifiers.CandidateClassifier;
import org.bibliome.util.tomap.classifiers.CandidateDistanceFactory;
import org.bibliome.util.tomap.classifiers.DefaultCandidateClassifier;
import org.bibliome.util.tomap.classifiers.ExactTermProxyCandidateClassifier;
import org.bibliome.util.tomap.classifiers.FallbackCandidateClassifier;
import org.bibliome.util.tomap.classifiers.HeadBasedTermProxyCandidateClassifier;
import org.bibliome.util.tomap.classifiers.NullCandidateClassifier;
import org.bibliome.util.tomap.readers.TreeTaggerReader;
import org.bibliome.util.tomap.readers.TreeTaggerReader.TreeTaggerResult;
import org.bibliome.util.tomap.readers.XMLCandidateReader;
import org.bibliome.util.tomap.readers.XMLCandidateReader.XMLResult;
import org.xml.sax.SAXException;

public class TomapClassifier implements Checkable {
	private final boolean noExactClassifier;
	private final SourceStream tomapFile;
	private final SourceStream headGraylistFile;
	private final String defaultConcept;
	private final CandidateDistanceFactory candidateDistanceFactory;
	private final SourceStream emptyWordsFile;
	private final boolean wholeCandidateDistance;
	private final boolean wholeProxyDistance;
	private final boolean candidateHeadPriority;
	private final boolean proxyHeadPriority;

	TomapClassifier(boolean noExactClassifier, SourceStream tomapFile, SourceStream headGraylistFile, String defaultConcept, CandidateDistanceFactory candidateDistanceFactory, SourceStream emptyWordsFile, boolean wholeCandidateDistance, boolean wholeProxyDistance, boolean candidateHeadPriority, boolean proxyHeadPriority) {
		super();
		this.noExactClassifier = noExactClassifier;
		this.tomapFile = tomapFile;
		this.headGraylistFile = headGraylistFile;
		this.defaultConcept = defaultConcept;
		this.candidateDistanceFactory = candidateDistanceFactory;
		this.emptyWordsFile = emptyWordsFile;
		this.wholeCandidateDistance = wholeCandidateDistance;
		this.wholeProxyDistance = wholeProxyDistance;
		this.candidateHeadPriority = candidateHeadPriority;
		this.proxyHeadPriority = proxyHeadPriority;
	}

	@Override
	public boolean check(Logger logger) {
		boolean result = true;
		if (tomapFile == null) {
			if (headGraylistFile != null) {
				logger.warning("head graylist specified but no tomap classifier");
				result = false;
			}
			if (defaultConcept == null) {
				logger.warning("no tomap classifier, nor default concept");
				result = false;
			}
		}
		return result;
	}

	CandidateClassifier readClassifier(TomapProjector owner, Logger logger) throws SAXException, IOException {
		if (tomapFile == null) {
			return createDefaultClassifier();
		}
		try (InputStream is = tomapFile.getInputStream()) {
			XMLCandidateReader xmlReader = new XMLCandidateReader(logger, owner.getTokenNormalization(), owner.getStringNormalization());
			XMLResult xmlResult = xmlReader.parseStream(is);
			Map<Candidate,List<String>> proxies = xmlResult.getConceptIDs();
			FallbackCandidateClassifier result = new FallbackCandidateClassifier("alvisnlp");
			if (!noExactClassifier) {
				result.addClassifier(createExactClassifier(proxies));
			}
			result.addClassifier(createCoreClassifier(owner, logger, proxies));
			result.addClassifier(createDefaultClassifier());
			return result;
		}
	}

	private static CandidateClassifier createExactClassifier(Map<Candidate,List<String>> proxies) {
		return new ExactTermProxyCandidateClassifier("exact", proxies);
	}

	private CandidateClassifier createDefaultClassifier() {
		if (defaultConcept == null) {
			return NullCandidateClassifier.INSTANCE;
		}
		return new DefaultCandidateClassifier("default", defaultConcept);
	}
	
	private CandidateClassifier createCoreClassifier(TomapProjector owner, Logger logger, Map<Candidate,List<String>> proxies) throws IOException {
		Set<Token> headGraylist = readTokenList(owner, logger, headGraylistFile);
		Set<Token> emptyWords = readTokenList(owner, logger, emptyWordsFile);
		return new HeadBasedTermProxyCandidateClassifier("head", proxies, headGraylist, candidateDistanceFactory, emptyWords, wholeCandidateDistance, wholeProxyDistance, candidateHeadPriority, proxyHeadPriority);
	}

	private static Set<Token> readTokenList(TomapProjector owner, Logger logger, SourceStream source) throws IOException {
		if (source == null) {
			return Collections.emptySet();
		}
		StringNormalization stringNormalization = owner.getStringNormalization();
		TokenNormalization tokenNormalization = owner.getTokenNormalization();
		TreeTaggerReader ttReader = new TreeTaggerReader(logger, tokenNormalization, stringNormalization);
		TreeTaggerResult ttResult = ttReader.parseSource(source);
		return ttResult.getEmptyWords();
	}

	public boolean isNoExactClassifier() {
		return noExactClassifier;
	}

	public SourceStream getTomapFile() {
		return tomapFile;
	}

	public SourceStream getHeadGraylistFile() {
		return headGraylistFile;
	}

	public String getDefaultConcept() {
		return defaultConcept;
	}

	public CandidateDistanceFactory getCandidateDistanceFactory() {
		return candidateDistanceFactory;
	}

	public SourceStream getEmptyWordsFile() {
		return emptyWordsFile;
	}

	public boolean isWholeCandidateDistance() {
		return wholeCandidateDistance;
	}

	public boolean isWholeProxyDistance() {
		return wholeProxyDistance;
	}

	public boolean isCandidateHeadPriority() {
		return candidateHeadPriority;
	}

	public boolean isProxyHeadPriority() {
		return proxyHeadPriority;
	}
}
