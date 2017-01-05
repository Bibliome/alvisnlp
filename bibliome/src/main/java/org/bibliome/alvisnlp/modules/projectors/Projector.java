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


package org.bibliome.alvisnlp.modules.projectors;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.Timer;
import org.bibliome.util.newprojector.CharFilter;
import org.bibliome.util.newprojector.CharMapper;
import org.bibliome.util.newprojector.Dictionary;
import org.bibliome.util.newprojector.Match;
import org.bibliome.util.newprojector.Matcher;
import org.bibliome.util.newprojector.State;
import org.bibliome.util.newprojector.chars.Filters;
import org.bibliome.util.newprojector.chars.Mappers;

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

public abstract class Projector<S extends SectionResolvedObjects,T,D extends Dictionary<T>> extends SectionModule<S> implements AnnotationCreator {
	private String targetLayerName = null;
	private Subject subject = ContentsSubject.WORD;
	private Boolean ignoreCase = false;
	private Boolean ignoreDiacritics = false;
	private Boolean normalizeSpace = false;
	private Boolean ignoreWhitespace = false;
	private Boolean errorDuplicateValues = false;
	private MultipleValueAction multipleValueAction = MultipleValueAction.ADD;

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);

		D dict = createDictionary(ctx);
		try {
			Timer<TimerCategory> fillTimer = getTimer(ctx, "load-dictionary", TimerCategory.LOAD_RESOURCE, true);
			fillDictionary(ctx, corpus, dict);
			fillTimer.stop();
			logger.finer("dictionary weight: " + dict.countKeys() + " keys, " + dict.keyLength() + " key length, " + dict.countValues() + " values, " + dict.countStates() + " states");
			Matcher<T> matcher = new Matcher<T>(subject.isCharPos(), logger.isLoggable(Level.FINER), dict, subject.getStartFilter(), subject.getEndFilter());

			logger.info("searching");
			int n = 0;
			for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
				matcher.reset();
				subject.match(sec, dict, matcher);
				Layer targetLayer = sec.ensureLayer(targetLayerName );
				for (Match<T> m : matcher.getMatches()) {
					Annotation a = new Annotation(this, targetLayer, m.getStart(), m.getEnd());
					for (T entry : m.getState().getValues())
						handleEntryValues(ctx, dict, a, entry);
					n++;
				}
			}
			if (n == 0) {
				logger.warning("found no matches");
			}
			else {
				logger.info("found " + n + " matches");
			}
			logger.finer("match weight: " + matcher.getVisitedStatesCount() + " visited states");
		}
		catch (Exception e) {
			rethrow(e);
		}
	}

	private D createDictionary(ProcessingContext<Corpus> ctx) {
		CharFilter charFilter = Filters.ACCEPT_ALL;
		CharMapper charMapper = Mappers.IDENTITY;
		if (ignoreCase)
			charMapper = charMapper.combine(Mappers.TO_LOWER);
		if (ignoreDiacritics )
			charMapper = charMapper.combine(Mappers.NO_DIACRITICS);
		if (ignoreWhitespace) {
			charFilter = charFilter.combine(Filters.NO_SPACE);
			if (normalizeSpace)
				getLogger(ctx).warning("space normalization will be ignored since spaces are ignored");
		}
		else if (normalizeSpace) {
			charMapper = charMapper.combine(Mappers.NORM_SPACE);
			charFilter = charFilter.combine(Filters.NORM_SPACE);
		}
		return newDictionary(getRootState(), charFilter, charMapper);

	}
	
	protected abstract D newDictionary(State<T> root, CharFilter charFilter, CharMapper charMapper);

	protected abstract void fillDictionary(ProcessingContext<Corpus> ctx, Corpus corpus, D dict) throws Exception;

	protected abstract void handleEntryValues(ProcessingContext<Corpus> ctx, D dict, Annotation a, T entry) throws Exception;
	
	@Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer where to put match annotations.")
	public String getTargetLayerName() {
		return targetLayerName;
	}

	@Param(defaultDoc = "Subject on which to project the dictionary.")
	public Subject getSubject() {
		return subject;
	}

	@Param(defaultDoc = "Match ignoring case.")
	public Boolean getIgnoreCase() {
		return ignoreCase;
	}

	@Param(defaultDoc = "Match ignoring diacritics.")
	public Boolean getIgnoreDiacritics() {
		return ignoreDiacritics;
	}

	@Param(defaultDoc = "Match normalizing whitespace.")
	public Boolean getNormalizeSpace() {
		return normalizeSpace;
	}

	@Param(defaultDoc = "Match ignoring whitespace characters.")
	public Boolean getIgnoreWhitespace() {
		return ignoreWhitespace;
	}
	
	@Param(defaultDoc = "Either to stop when a duplicate entry is seen.")
	public Boolean getErrorDuplicateValues() {
		return errorDuplicateValues;
	}

	@Param(defaultDoc = "Either to stop when multiple entries with the same key is seen.")
	public MultipleValueAction getMultipleValueAction() {
		return multipleValueAction;
	}

	public void setErrorDuplicateValues(Boolean errorDuplicateValues) {
		this.errorDuplicateValues = errorDuplicateValues;
	}

	public void setMultipleValueAction(MultipleValueAction multipleValueAction) {
		this.multipleValueAction = multipleValueAction;
	}

	public void setIgnoreCase(Boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public void setIgnoreDiacritics(Boolean ignoreDiacritics) {
		this.ignoreDiacritics = ignoreDiacritics;
	}

	public void setNormalizeSpace(Boolean normalizeSpace) {
		this.normalizeSpace = normalizeSpace;
	}

	public void setIgnoreWhitespace(Boolean ignoreWhitespace) {
		this.ignoreWhitespace = ignoreWhitespace;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public void setTargetLayerName(String targetLayerName) {
		this.targetLayerName = targetLayerName;
	}
	
	private State<T> getRootState() {
		if (multipleValueAction == MultipleValueAction.ADD)
			return new AlvisNLPMultipleValueState();
		return new AlvisNLPSingleValueState();
	}
	
	private class AlvisNLPSingleValueState extends State<T> {
		private T value = null;

		private AlvisNLPSingleValueState() {
			super();
		}

		private AlvisNLPSingleValueState(char c, State<T> parent) {
			super(c, parent);
		}

		@Override
		protected void addValue(T value, CharSequence key) {
			if (this.value == null) {
				this.value = value;
				return;
			}
			if (this.value.equals(value)) {
				if (errorDuplicateValues)
					throw new ProjectorException("duplicate entry for: " + key);
				return;
			}
			switch (multipleValueAction) {
			case ERROR:
				throw new ProjectorException("multiple entry for: " + key);
			case NOP:
				break;
			case REPLACE:
				this.value = value;
				break;
			case ADD:
				throw new RuntimeException();
			}
		}

		@Override
		public Collection<T> getValues() {
			if (value == null)
				return Collections.emptyList();
			return Collections.singleton(value);
		}

		@Override
		public boolean hasValue() {
			return value != null;
		}

		@Override
		protected State<T> newState(char c, State<T> parent) {
			return new AlvisNLPSingleValueState(c, parent);
		}
	}
	
	private class AlvisNLPMultipleValueState extends State<T> {
		private Collection<T> values = null;
		
		private AlvisNLPMultipleValueState() {
			super();
		}

		private AlvisNLPMultipleValueState(char c, State<T> parent) {
			super(c, parent);
		}

		@Override
		protected void addValue(T value, CharSequence key) {
			if (values == null) {
				values = new HashSet<T>(2);
				values.add(value);
				return;
			}
			if (values.contains(value)) {
				if (errorDuplicateValues)
					throw new ProjectorException("duplicate entry for: " + key);
				return;
			}
			values.add(value);
		}

		@Override
		public Collection<T> getValues() {
			if (values == null)
				return Collections.emptyList();
			return Collections.unmodifiableCollection(values);
		}

		@Override
		public boolean hasValue() {
			return values != null;
		}

		@Override
		protected State<T> newState(char c, State<T> parent) {
			return new AlvisNLPMultipleValueState(c, parent);
		}
	}
}
