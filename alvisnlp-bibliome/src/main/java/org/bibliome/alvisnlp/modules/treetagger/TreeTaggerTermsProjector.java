package org.bibliome.alvisnlp.modules.treetagger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.trie.TrieProjector;
import org.bibliome.util.StringCat;
import org.bibliome.util.filelines.FileLines;
import org.bibliome.util.filelines.InvalidFileLineEntry;
import org.bibliome.util.marshall.Decoder;
import org.bibliome.util.marshall.Encoder;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.trie.Trie;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

@AlvisNLPModule
public abstract class TreeTaggerTermsProjector extends TrieProjector<SectionResolvedObjects,String[]> {
	private Boolean lemmaKeys = false;
	private SourceStream termsFile;
	private String termFeatureName = "term";
	private String posFeatureName = DefaultNames.getPosTagFeature();
	private String lemmaFeatureName = DefaultNames.getCanonicalFormFeature();

	private static final class AttestedTermsFileLines extends FileLines<Trie<String[]>> {
		private final StringCat termFormBuilder = new StringCat();
		private final StringCat termPosBuilder = new StringCat();
		private final StringCat termLemmaBuilder = new StringCat();
		private final boolean lemmaKeys;
		
		private AttestedTermsFileLines(boolean lemmaKeys) {
			super();
			this.lemmaKeys = lemmaKeys;
			getFormat().setNumColumns(3);
			getFormat().setStrictColumnNumber(true);
		}

		private void init() {
			termFormBuilder.clear();
			termPosBuilder.clear();
			termLemmaBuilder.clear();			
		}

		@Override
		public void processEntry(Trie<String[]> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
			String pos = entry.get(1);
			if ("SENT".equals(pos)) {
				if (termFormBuilder.isEmpty())
					return;
				String termForm = termFormBuilder.toString();
				String termLemma = termLemmaBuilder.toString();
				data.addEntry(lemmaKeys ? termLemma : termForm, new String[] { termForm, termPosBuilder.toString(), termLemma });
				init();
				return;
			}
			if (!termFormBuilder.isEmpty()) {
				termFormBuilder.append(" ");
				termPosBuilder.append(" ");
				termLemmaBuilder.append(" ");
			}
			termFormBuilder.append(entry.get(0));
			termPosBuilder.append(pos);
			termLemmaBuilder.append(entry.get(2));
		}
	}

	@Override
	protected void fillTrie(Logger logger, Trie<String[]> trie, Corpus corpus) throws IOException, ModuleException {
		AttestedTermsFileLines fileLines = new AttestedTermsFileLines(lemmaKeys);
        fileLines.setLogger(logger);
        fileLines.getFormat().setSeparator('\t');
        BufferedReader r = termsFile.getBufferedReader();
        fileLines.process(r, trie);
        r.close();
	}

	@Override
	@TimeThis(task="create-trie", category=TimerCategory.LOAD_RESOURCE)
	protected Trie<String[]> getTrie(ProcessingContext<Corpus> ctx, Logger logger, Corpus corpus) throws IOException, ModuleException {
		return super.getTrie(ctx, logger, corpus);
	}

	@Override
	protected void finish() {
	}

	@Override
	protected boolean marshallingSupported() {
		return false;
	}

	@Override
	protected Decoder<String[]> getDecoder() {
		throw new UnsupportedOperationException("marshalling not supported");
	}

	@Override
	protected Encoder<String[]> getEncoder() {
		throw new UnsupportedOperationException("marshalling not supported");
	}

	@Override
	protected void handleMatch(String[] entry, Annotation a) {
		if (termFeatureName != null)
			a.addFeature(termFeatureName, entry[0]);
		a.addFeature(posFeatureName, entry[1]);
		a.addFeature(lemmaFeatureName, entry[2]);
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Param
	public Boolean getLemmaKeys() {
		return lemmaKeys;
	}

	@Param
	public SourceStream getTermsFile() {
		return termsFile;
	}

	@Param(nameType=NameType.FEATURE)
	public String getTermFeatureName() {
		return termFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getPosFeatureName() {
		return posFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getLemmaFeatureName() {
		return lemmaFeatureName;
	}

	public void setLemmaKeys(Boolean lemmaKeys) {
		this.lemmaKeys = lemmaKeys;
	}

	public void setTermsFile(SourceStream termsFile) {
		this.termsFile = termsFile;
	}

	public void setTermFeatureName(String termFeatureName) {
		this.termFeatureName = termFeatureName;
	}

	public void setPosFeatureName(String posFeatureName) {
		this.posFeatureName = posFeatureName;
	}

	public void setLemmaFeatureName(String lemmaFeatureName) {
		this.lemmaFeatureName = lemmaFeatureName;
	}
}
