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
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.filelines.FileLines;
import org.bibliome.util.filelines.InvalidFileLineEntry;
import org.bibliome.util.filelines.TabularFormat;
import org.bibliome.util.marshall.Decoder;
import org.bibliome.util.marshall.Encoder;
import org.bibliome.util.marshall.StringCodec;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.trie.Trie;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

@AlvisNLPModule
public abstract class SimpleProjector2 extends TrieProjector<SectionResolvedObjects,List<String>> {
	private Integer[] keyIndex = new Integer[] { 0 };
	private SourceStream dictFile;
	private String[] valueFeatures;
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
	protected void fillTrie(Logger logger, Trie<List<String>> trie, Corpus corpus) throws IOException, ModuleException {
		logger.info("reading dictionary from: " + dictFile);
		TabularFormat format = new TabularFormat();
		format.setNumColumns(valueFeatures.length);
		logger.info("we expect lines with " + valueFeatures.length + " columns");
		format.setSeparator(separator);
		if (!strictColumnNumber)
			logger.warning("you deliberately choose to ignore malformed dictionary lines");
		format.setStrictColumnNumber(strictColumnNumber);
		if (skipEmpty)
			logger.warning("skipping empty lines");
		format.setSkipEmpty(skipEmpty);
		if (skipBlank)
			logger.warning("skipping lines with only whitespace");
		format.setSkipBlank(skipBlank);
		if (trimColumns)
			logger.warning("columns will be trimmed from leading and trailing whitespace");
		format.setTrimColumns(trimColumns);
		FileLines<Trie<List<String>>> fl = new EntryFileLines(format, keyIndex);
		try {
			fl.process(dictFile, trie);
		}
		catch (InvalidFileLineEntry e) {
			rethrow(e);
		}
	}

	@Override
	protected void finish() {
	}

	private static final class EntryFileLines extends FileLines<Trie<List<String>>> {
		private final Integer[] keyIndex;
		
		private EntryFileLines(TabularFormat format, Integer[] keyIndex) {
			super(format);
			this.keyIndex = keyIndex;
		}

		@Override
		public void processEntry(Trie<List<String>> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
			for (int i : keyIndex) {
				String key = entry.get(i);
				data.addEntry(key, entry);
			}
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
		return stringListDecoder;
	}
	
	private static final Decoder<List<String>> stringListDecoder = new Decoder<List<String>>() {
		@Override
		public List<String> decode1(ByteBuffer buffer) {
			final int len = buffer.getInt();
			String[] result = new String[len];
			for (int i = 0; i < len; ++i)
				result[i] = StringCodec.INSTANCE.decode1(buffer);
			return Arrays.asList(result);
		}

		@Override
		public void decode2(ByteBuffer buffer, List<String> object) {
		}
	};

	@Override
	protected Encoder<List<String>> getEncoder() {
		return stringListEncoder;
	}
	
	private static final Encoder<List<String>> stringListEncoder = new Encoder<List<String>>() {
		@Override
		public int getSize(List<String> object) {
			int result = 4;
			for (String s : object)
				result += StringCodec.INSTANCE.getSize(s);
			return result;
		}

		@Override
		public void encode(List<String> object, ByteBuffer buf) throws IOException {
			buf.putInt(object.size());
			for (String s : object)
				StringCodec.INSTANCE.encode(s, buf);
		}
	};

	@Param
	public Integer[] getKeyIndex() {
		return keyIndex;
	}

	@Param
	public SourceStream getDictFile() {
		return dictFile;
	}

	@Param(nameType=NameType.FEATURE)
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
