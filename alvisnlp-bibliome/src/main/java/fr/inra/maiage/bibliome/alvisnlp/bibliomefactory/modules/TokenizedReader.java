package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

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
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule(beta=true)
public abstract class TokenizedReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator {
	private String sectionName = DefaultNames.getDefaultSectionName();
	private String tokenLayerName = DefaultNames.getWordLayer();
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private SourceStream source;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		try {
			for (BufferedReader reader : Iterators.loop(source.getBufferedReaders())) {
				String name = source.getStreamName(reader);
				logger.fine("reading: " + name);
				Document doc = Document.getDocument(this, corpus, name);
				String contents = readContents(reader);
				Section sec = new Section(this, doc, sectionName, contents);
				Layer tokens = sec.ensureLayer(tokenLayerName);
				Layer sentences = sec.ensureLayer(sentenceLayerName);
				int startOfToken = 0;
				int startOfSentence = 0;
				for (int i = 0; i < contents.length(); ++i) {
					char c = contents.charAt(i);
					if (c == '\n') {
						int end = i;
						if (startOfToken == end) {
							new Annotation(this, sentences, startOfSentence, end);
							startOfSentence = i + 1;
						}
						else {
							new Annotation(this, tokens, startOfToken, end);
						}
						startOfToken = i + 1;
					}
				}
			}
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}
	
	private static String readContents(BufferedReader reader) throws IOException {
		StringBuilder sb = new StringBuilder(2097152);
		char[] buf = new char[2097152];
		int r;
		while ((r = reader.read(buf)) != -1) {
			sb.append(buf, 0, r);
		}
		return sb.toString();
	}
	
	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}
	
	@Param(nameType=NameType.SECTION)
	public String getSectionName() {
		return sectionName;
	}

	@Param(nameType=NameType.LAYER)
	public String getTokenLayerName() {
		return tokenLayerName;
	}

	@Param(nameType=NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayerName;
	}

	@Param
	public SourceStream getSource() {
		return source;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public void setTokenLayerName(String tokenLayerName) {
		this.tokenLayerName = tokenLayerName;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}

	public void setSource(SourceStream source) {
		this.source = source;
	}
}
