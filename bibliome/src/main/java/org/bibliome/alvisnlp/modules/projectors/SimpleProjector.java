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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.trie.SimpleProjector2;
import org.bibliome.util.Iterators;
import org.bibliome.util.filelines.FileLines;
import org.bibliome.util.filelines.InvalidFileLineEntry;
import org.bibliome.util.newprojector.CharFilter;
import org.bibliome.util.newprojector.CharMapper;
import org.bibliome.util.newprojector.Dictionary;
import org.bibliome.util.newprojector.State;
import org.bibliome.util.streams.SourceStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(obsoleteUseInstead=SimpleProjector2.class)
public abstract class SimpleProjector extends Projector<SectionResolvedObjects,StringArray,Dictionary<StringArray>> {
	private SourceStream dictFile;
	private String[] entryFeatureNames = new String[0];
	private Boolean strictColumnNumber = false;
	private Boolean trimColumns = true;
	private Boolean skipBlankLines = true;
	private Character separator = '\t';

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	protected Dictionary<StringArray> newDictionary(State<StringArray> root, CharFilter charFilter, CharMapper charMapper) {
		return new Dictionary<StringArray>(root, charFilter, charMapper);
	}

	@Override
	protected void fillDictionary(ProcessingContext<Corpus> ctx, Corpus corpus, Dictionary<StringArray> dict) throws IOException, InvalidFileLineEntry {
		EntryKeyFileLines fl = new EntryKeyFileLines(ctx);
		for (BufferedReader r : Iterators.loop(dictFile.getBufferedReaders())) {
			getLogger(ctx).info("reading dictionary from " + dictFile.getStreamName(r));
			fl.process(r, dict);
			r.close();
		}
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected void handleEntryValues(ProcessingContext<Corpus> ctx, Dictionary<StringArray> dict, Annotation a, StringArray entry) throws InvalidFileLineEntry {
		if (entry.string == null)
			return;
		if (entry.array == null) {
			EntryValueFileLines fl = new EntryValueFileLines(ctx);
			fl.process(entry.string, entry, entry.lineno);
		}
		for (int i = 0; i < entryFeatureNames.length && i < entry.array.size(); ++i)
			handleEntryColumn(a, entryFeatureNames[i], entry.array.get(i));
	}

	private static void handleEntryColumn(Annotation a, String key, String value) {
		if (key == null)
			return;
		if (value == null)
			return;
		if (key.isEmpty())
			return;
		if (value.isEmpty())
			return;
		a.addFeature(key, value);
	}

	@Param(nameType=NameType.FEATURE, defaultDoc = "Feature names for the entry columns.")
	public String[] getEntryFeatureNames() {
		return entryFeatureNames;
	}

	@Param(defaultDoc = "Check that all lines in the dictionary file contains the same number of columns than entryFeatureNames.")
	public Boolean getStrictColumnNumber() {
		return strictColumnNumber;
	}

	@Param(defaultDoc = "Either to trim column values from leading and trailing whitespaces.")
	public Boolean getTrimColumns() {
		return trimColumns;
	}

	@Param(defaultDoc = "Either to skip empty lines and lines that contains only whitespace.")
	public Boolean getSkipBlankLines() {
		return skipBlankLines;
	}

	@Param
	public SourceStream getDictFile() {
		return dictFile;
	}

	@Param(defaultDoc = "Character that separates entry columns.")
	public Character getSeparator() {
		return separator;
	}

	public void setSeparator(Character separator) {
		this.separator = separator;
	}

	public void setDictFile(SourceStream dictFile) {
		this.dictFile = dictFile;
	}

	public void setStrictColumnNumber(Boolean strictColumnNumber) {
		this.strictColumnNumber = strictColumnNumber;
	}

	public void setTrimColumns(Boolean trimColumns) {
		this.trimColumns = trimColumns;
	}

	public void setSkipBlankLines(Boolean skipBlankLines) {
		this.skipBlankLines = skipBlankLines;
	}

	public void setEntryFeatureNames(String[] entryFeatureNames) {
		this.entryFeatureNames = entryFeatureNames;
	}
	
	private final class EntryKeyFileLines extends FileLines<Dictionary<StringArray>> {
		private EntryKeyFileLines(ProcessingContext<Corpus> ctx) {
			super();
			getFormat().setColumnLimit(2);
			setLogger(SimpleProjector.this.getLogger(ctx));
			getFormat().setNullifyEmpty(false);
			setSeparator(separator);
			getFormat().setSkipBlank(skipBlankLines);
			setTrimColumns(trimColumns);
		}

		@Override
		public void processEntry(Dictionary<StringArray> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
			data.addEntry(entry.get(0), new StringArray(entry.size() == 2 ? entry.get(1) : null, lineno));
		}
	}
	
	private final class EntryValueFileLines extends FileLines<StringArray> {
		private EntryValueFileLines(ProcessingContext<Corpus> ctx) {
			super();
			setLogger(SimpleProjector.this.getLogger(ctx));
			getFormat().setNullifyEmpty(false);
			setSeparator(separator);
			getFormat().setSkipBlank(skipBlankLines);
			setTrimColumns(trimColumns);
			setStrictColumnNumber(strictColumnNumber);
			getFormat().setNumColumns(entryFeatureNames.length);
		}

		@Override
		public void processEntry(StringArray data, int lineno, List<String> entry) throws InvalidFileLineEntry {
			data.array = entry;
		}
	}
}
