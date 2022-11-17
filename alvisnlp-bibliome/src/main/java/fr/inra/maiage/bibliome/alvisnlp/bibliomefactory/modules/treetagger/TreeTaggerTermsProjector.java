package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.treetagger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.trie.TrieProjector;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.StringCat;
import fr.inra.maiage.bibliome.util.filelines.FileLines;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.marshall.Decoder;
import fr.inra.maiage.bibliome.util.marshall.Encoder;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.trie.Trie;

@AlvisNLPModule
public abstract class TreeTaggerTermsProjector extends TrieProjector<SectionResolvedObjects,String[]> {
	private Boolean lemmaKeys = false;
	private SourceStream termsFile;
	private String termFeature = "term";
	private String posFeature = DefaultNames.getPosTagFeature();
	private String lemmaFeature = DefaultNames.getCanonicalFormFeature();

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
		if (termFeature != null)
			a.addFeature(termFeature, entry[0]);
		a.addFeature(posFeature, entry[1]);
		a.addFeature(lemmaFeature, entry[2]);
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

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getTermFeatureName() {
		return termFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getPosFeatureName() {
		return posFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getLemmaFeatureName() {
		return lemmaFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getTermFeature() {
		return termFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getPosFeature() {
		return posFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getLemmaFeature() {
		return lemmaFeature;
	}

	public void setTermFeature(String termFeature) {
		this.termFeature = termFeature;
	}

	public void setPosFeature(String posFeature) {
		this.posFeature = posFeature;
	}

	public void setLemmaFeature(String lemmaFeature) {
		this.lemmaFeature = lemmaFeature;
	}

	public void setLemmaKeys(Boolean lemmaKeys) {
		this.lemmaKeys = lemmaKeys;
	}

	public void setTermsFile(SourceStream termsFile) {
		this.termsFile = termsFile;
	}

	public void setTermFeatureName(String termFeatureName) {
		this.termFeature = termFeatureName;
	}

	public void setPosFeatureName(String posFeatureName) {
		this.posFeature = posFeatureName;
	}

	public void setLemmaFeatureName(String lemmaFeatureName) {
		this.lemmaFeature = lemmaFeatureName;
	}
}
