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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.trie;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
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
import fr.inra.maiage.bibliome.util.Checkable;
import fr.inra.maiage.bibliome.util.filelines.FileLines;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.filelines.TabularFormat;
import fr.inra.maiage.bibliome.util.marshall.Decoder;
import fr.inra.maiage.bibliome.util.marshall.Encoder;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.trie.Trie;

@AlvisNLPModule
public abstract class TabularProjector extends TrieProjector<SectionResolvedObjects,List<String>> implements Checkable {
	private Integer[] keyIndex = new Integer[] { 0 };
	private SourceStream dictFile;
	private String[] valueFeatures;
	private Boolean headerLine = false;
	private Character separator = '\t';
	private Boolean strictColumnNumber = true;
	private Boolean skipEmpty = false;
	private Boolean skipBlank = false;
	private Boolean trimColumns = false;

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}
	
	@Override
	public boolean check(Logger logger) {
		boolean result = true;
		if (getTrieSource() == null) {
			if ((valueFeatures == null) && !headerLine) {
				logger.severe("at least one of valueFeatures or headerLine must be set");
				result = false;
			}
			if ((valueFeatures != null) && headerLine) {
				logger.warning("valueFeatures will be overriden by column names since headerLines is set");
			}
		}
		else {
			if (valueFeatures == null) {
				logger.severe("valueFeatures is mandatory if trieSource is set");
				result = false;
			}
			if (headerLine) {
				logger.warning("headerLine will be ignored since no dictionary will be read");
			}
		}
		return result;
	}

	@Override
	protected void fillTrie(Logger logger, Trie<List<String>> trie, Corpus corpus) throws IOException, ModuleException {
		logger.info("reading dictionary from: " + dictFile);
		TabularFormat format = new TabularFormat();
		if (!headerLine) {
			format.setNumColumns(valueFeatures.length);
			logger.info("we expect lines with " + valueFeatures.length + " columns");
			if (!strictColumnNumber)
				logger.warning("you deliberately choose to ignore malformed dictionary lines");
			format.setStrictColumnNumber(strictColumnNumber);
		}
		format.setSeparator(separator);
		if (skipEmpty)
			logger.warning("skipping empty lines");
		format.setSkipEmpty(skipEmpty);
		if (skipBlank)
			logger.warning("skipping lines with only whitespace");
		format.setSkipBlank(skipBlank);
		if (trimColumns)
			logger.warning("columns will be trimmed from leading and trailing whitespace");
		format.setTrimColumns(trimColumns);
		EntryFileLines fl = new EntryFileLines(format, keyIndex, headerLine);
		try {
			fl.process(dictFile, trie);
			if (headerLine) {
				valueFeatures = fl.getHeaders();
			}
		}
		catch (InvalidFileLineEntry e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	protected void finish() {
	}

	private static final class EntryFileLines extends FileLines<Trie<List<String>>> {
		private final Integer[] keyIndex;
		private boolean headerLine;
		private String[] headers = null;
		
		private EntryFileLines(TabularFormat format, Integer[] keyIndex, boolean headerLine) {
			super(format);
			this.keyIndex = keyIndex;
			this.headerLine = headerLine;
		}

		@Override
		public void processEntry(Trie<List<String>> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
			if (headerLine && (headers == null)) {
				headers = entry.toArray(new String[entry.size()]);
			}
			else {
				for (int i : keyIndex) {
					String key = entry.get(i);
					data.addEntry(key, entry);
				}
			}
		}

		public String[] getHeaders() {
			return headers;
		}
	}

	@Override
	@TimeThis(task="create-trie", category=TimerCategory.LOAD_RESOURCE)
	protected Trie<List<String>> getTrie(ProcessingContext<Corpus> ctx, Logger logger, Corpus corpus) throws IOException, ModuleException {
		return super.getTrie(ctx, logger, corpus);
	}

	@Override
	protected void handleMatch(List<String> value, Annotation a) {
		final int len = Math.min(valueFeatures.length, value.size());
		for (int i = 0; i < len; ++i)
			a.addFeature(valueFeatures[i], value.get(i));
	}

	@Override
	protected boolean marshallingSupported() {
		return true;
	}

	@Override
	protected Decoder<List<String>> getDecoder() {
		return StringListCodex.INSANCE;
	}

	@Override
	protected Encoder<List<String>> getEncoder() {
		return StringListCodex.INSANCE;
	}

	@Param
	public Integer[] getKeyIndex() {
		return keyIndex;
	}

	@Param
	public SourceStream getDictFile() {
		return dictFile;
	}

	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String[] getValueFeatures() {
		return valueFeatures;
	}

	@Param
	public Character getSeparator() {
		return separator;
	}

	@Param
	public Boolean getStrictColumnNumber() {
		return strictColumnNumber;
	}

	@Param
	public Boolean getSkipEmpty() {
		return skipEmpty;
	}

	@Param
	public Boolean getSkipBlank() {
		return skipBlank;
	}

	@Param
	public Boolean getTrimColumns() {
		return trimColumns;
	}

	@Param
	public Boolean getHeaderLine() {
		return headerLine;
	}

	public void setHeaderLine(Boolean headerLine) {
		this.headerLine = headerLine;
	}

	public void setKeyIndex(Integer[] keyIndex) {
		this.keyIndex = keyIndex;
	}

	public void setDictFile(SourceStream dictFile) {
		this.dictFile = dictFile;
	}

	public void setValueFeatures(String[] valueFeatures) {
		this.valueFeatures = valueFeatures;
	}

	public void setSeparator(Character separator) {
		this.separator = separator;
	}

	public void setStrictColumnNumber(Boolean strictColumnNumber) {
		this.strictColumnNumber = strictColumnNumber;
	}

	public void setSkipEmpty(Boolean skipEmpty) {
		this.skipEmpty = skipEmpty;
	}

	public void setSkipBlank(Boolean skipBlank) {
		this.skipBlank = skipBlank;
	}

	public void setTrimColumns(Boolean trimColumns) {
		this.trimColumns = trimColumns;
	}
}
