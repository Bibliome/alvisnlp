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


package org.bibliome.alvisnlp.modules.trie;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.files.InputFile;
import org.bibliome.util.files.OutputFile;
import org.bibliome.util.marshall.Decoder;
import org.bibliome.util.marshall.Encoder;
import org.bibliome.util.trie.Match;
import org.bibliome.util.trie.Matcher;
import org.bibliome.util.trie.StandardMatchControl;
import org.bibliome.util.trie.Trie;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

public abstract class TrieProjector<S extends SectionResolvedObjects,T> extends SectionModule<S> implements AnnotationCreator {
	private String targetLayerName;
	private Subject subject = ContentsSubject.WORD;
	private InputFile trieSource;
	private OutputFile trieSink;
	private Boolean allowJoined = false;
	private Boolean allUpperCaseInsensitive = false;
	private Boolean caseInsensitive = false;
	private Boolean ignoreDiacritics = false;
	private Boolean joinDash = false;
	private Boolean matchStartCaseInsensitive = false;
	private Boolean skipConsecutiveWhitespaces = false;
	private Boolean skipWhitespace = false;
	private Boolean wordStartCaseInsensitive = false;
	private MultipleEntryBehaviour multipleEntryBehaviour = MultipleEntryBehaviour.ALL;

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		try (Trie<T> trie = getTrie(ctx, logger, corpus)) {
			StandardMatchControl control = getControl();
			subject.correctControl(control);
			Matcher<T> matcher = new Matcher<T>(trie, control);
			logger.info("searching...");
			int nMatches = 0;
			for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
				matcher.init();
				List<Match<T>> matches = subject.search(matcher, sec);
				nMatches += matches.size();
				Layer targetLayer = sec.ensureLayer(targetLayerName);
				for (Match<T> match : matches)
					multipleEntryBehaviour.handle(this, targetLayer, match);
			}
			
			if (nMatches == 0) {
				logger.warning("found no matches");
			}
			else {
				logger.info("found " + nMatches + " matches");
			}
			finish();

			if (trieSink != null && trieSource == null) {
				Encoder<T> encoder = getEncoder();
				logger.info("saving trie into " + trieSink);
				trie.save(trieSink, encoder);
			}
		}
		catch (IOException e) {
			rethrow(e);
		}
	}
	
	private StandardMatchControl getControl() {
		StandardMatchControl result = new StandardMatchControl();
		result.setAllowJoined(allowJoined);
		result.setAllUpperCaseInsensitive(allUpperCaseInsensitive);
		result.setCaseInsensitive(caseInsensitive);
		result.setIgnoreDiacritics(ignoreDiacritics);
		result.setJoinDash(joinDash);
		result.setMatchStartCaseInsensitive(matchStartCaseInsensitive);
		result.setSkipConsecutiveWhitespaces(skipConsecutiveWhitespaces);
		result.setSkipWhitespace(skipWhitespace);
		result.setWordStartCaseInsensitive(wordStartCaseInsensitive);
		return result;
	}

	@TimeThis(task="create-trie", category=TimerCategory.LOAD_RESOURCE)
	protected Trie<T> getTrie(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, Logger logger, Corpus corpus) throws IOException, ModuleException {
		if (trieSource == null) {
			Trie<T> result = new Trie<T>();
			fillTrie(logger, result, corpus);
			return result;
		}
		logger.info("loading trie from " + trieSource);
		Decoder<T> valueDecoder = getDecoder();
		return new Trie<T>(trieSource, valueDecoder);
	}

	/**
	 * Fills the specified trie with entries.
	 * @param logger
	 * @param trie
	 * @param corpus TODO
	 * @throws ModuleException 
	 * @throws IOException 
	 */
	protected abstract void fillTrie(Logger logger, Trie<T> trie, Corpus corpus) throws IOException, ModuleException;

	protected abstract void finish();
	
	protected abstract boolean marshallingSupported();
	
	/**
	 * Returns a marshalling decoder for type <T>.
	 */
	protected abstract Decoder<T> getDecoder();

	/**
	 * Returns a marshalling encoder for type <T>.
	 * @return
	 */
	protected abstract Encoder<T> getEncoder();

	protected abstract void handleMatch(T value, Annotation a);
	
	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Param(nameType=NameType.LAYER)
	public String getTargetLayerName() {
		return targetLayerName;
	}

	@Param
	public Subject getSubject() {
		return subject;
	}

	@Param(mandatory=false)
	public InputFile getTrieSource() {
		return trieSource;
	}

	@Param(mandatory=false)
	public OutputFile getTrieSink() {
		return trieSink;
	}

	@Param
	public Boolean getAllowJoined() {
		return allowJoined;
	}

	@Param
	public Boolean getAllUpperCaseInsensitive() {
		return allUpperCaseInsensitive;
	}

	@Param
	public Boolean getCaseInsensitive() {
		return caseInsensitive;
	}

	@Param
	public Boolean getIgnoreDiacritics() {
		return ignoreDiacritics;
	}

	@Param
	public Boolean getJoinDash() {
		return joinDash;
	}

	@Param
	public Boolean getMatchStartCaseInsensitive() {
		return matchStartCaseInsensitive;
	}

	@Param
	public Boolean getSkipConsecutiveWhitespaces() {
		return skipConsecutiveWhitespaces;
	}

	@Param
	public Boolean getSkipWhitespace() {
		return skipWhitespace;
	}

	@Param
	public Boolean getWordStartCaseInsensitive() {
		return wordStartCaseInsensitive;
	}

	@Param
	public MultipleEntryBehaviour getMultipleEntryBehaviour() {
		return multipleEntryBehaviour;
	}

	public void setMultipleEntryBehaviour(MultipleEntryBehaviour multipleEntryBehaviour) {
		this.multipleEntryBehaviour = multipleEntryBehaviour;
	}

	public void setTargetLayerName(String targetLayerName) {
		this.targetLayerName = targetLayerName;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public void setTrieSource(InputFile trieSource) {
		this.trieSource = trieSource;
	}

	public void setTrieSink(OutputFile trieSink) {
		this.trieSink = trieSink;
	}

	public void setAllowJoined(Boolean allowJoined) {
		this.allowJoined = allowJoined;
	}

	public void setAllUpperCaseInsensitive(Boolean allUpperCaseInsensitive) {
		this.allUpperCaseInsensitive = allUpperCaseInsensitive;
	}

	public void setCaseInsensitive(Boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}

	public void setIgnoreDiacritics(Boolean ignoreDiacritics) {
		this.ignoreDiacritics = ignoreDiacritics;
	}

	public void setJoinDash(Boolean joinDash) {
		this.joinDash = joinDash;
	}

	public void setMatchStartCaseInsensitive(Boolean matchStartCaseInsensitive) {
		this.matchStartCaseInsensitive = matchStartCaseInsensitive;
	}

	public void setSkipConsecutiveWhitespaces(Boolean skipConsecutiveWhitespaces) {
		this.skipConsecutiveWhitespaces = skipConsecutiveWhitespaces;
	}

	public void setSkipWhitespace(Boolean skipWhitespace) {
		this.skipWhitespace = skipWhitespace;
	}

	public void setWordStartCaseInsensitive(Boolean wordStartCaseInsensitive) {
		this.wordStartCaseInsensitive = wordStartCaseInsensitive;
	}
}
