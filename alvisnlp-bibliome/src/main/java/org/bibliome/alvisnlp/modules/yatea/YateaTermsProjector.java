package org.bibliome.alvisnlp.modules.yatea;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.trie.TrieProjector;
import org.bibliome.alvisnlp.modules.yatea.YateaTermsProjector.Term;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.marshall.Decoder;
import fr.inra.maiage.bibliome.util.marshall.Encoder;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.trie.Trie;

@AlvisNLPModule
public abstract class YateaTermsProjector extends TrieProjector<SectionResolvedObjects,Term> {
	private Boolean mnpOnly = false;
	private Boolean projectLemmas = false;
	private SourceStream yateaFile;
	private String termId = "term-id";
	private String monoHeadId = "mono-head";
	private String termLemma;
	private String termPOS = DefaultNames.getPosTagFeature();
	private String head = "head";
	private String modifier = "modifier";
	
	public static final class Term {
		private String id;
		private String form;
		private String lemma;
		private String pos;
		private String monoHead;
		private String head;
		private String modifier;
		
		@Override
		public String toString() {
			return "Term [id=" + id + ", form=" + form + "]";
		}
	}

	private final class YateaHandler extends DefaultHandler {
		private Term currentTerm;
		private final Trie<Term> dict;
		private final StringBuilder chars = new StringBuilder();
		private boolean syntacticAnalysis;
		private boolean dismissed;
		private boolean mnp;
		
		private YateaHandler(Trie<Term> dict) {
			super();
			this.dict = dict;
		}

		@Override
		public void startDocument() throws SAXException {
			currentTerm = null;
			chars.setLength(0);
			syntacticAnalysis = false;
			dismissed = false;
			mnp = false;
		}
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			chars.append(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (dismissed) {
				return;
			}
			if (mnpOnly && !mnp) {
				return;
			}
			switch (qName) {
			case "ID":
				if (currentTerm.id == null)
					currentTerm.id = getTermId();
				break;
			case "FORM":
				if (currentTerm.form == null) {
					currentTerm.form = chars.toString();
				}
				break;
			case "LEMMA":
				currentTerm.lemma = chars.toString();
				break;
			case "SYNTACTIC_CATEGORY":
				currentTerm.pos = chars.toString();
				break;
			case "SYNTACTIC_ANALYSIS":
				syntacticAnalysis = false;
				break;
			case "HEAD":
				if (syntacticAnalysis)
					currentTerm.head = getTermId();
				else
					currentTerm.monoHead = getTermId();
				break;
			case "MODIFIER":
				currentTerm.modifier = getTermId();
				break;
			case "TERM_CANDIDATE":
				String key = projectLemmas ? currentTerm.lemma : currentTerm.form;
				dict.addEntry(key, currentTerm);
				break;
			}
		}
		
		private String getTermId() {
			return chars.toString().trim();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			switch (qName) {
			case "TERM_CANDIDATE":
				dismissed = "TRUE".equals(attributes.getValue("DISMISSED"));
				mnp = "1".equals(attributes.getValue("MNP_STATUS"));
				currentTerm = new Term();
				break;
			case "SYNTACTIC_ANALYSIS":
				syntacticAnalysis = true;
				break;
			}
			chars.setLength(0);
		}
	}

	@Override
	protected void fillTrie(Logger logger, Trie<Term> trie, Corpus corpus) throws IOException, ModuleException {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			YateaHandler handler = new YateaHandler(trie);
			for (InputStream is : Iterators.loop(yateaFile.getInputStreams())) {
				parser.parse(is, handler);
			}
		}
		catch (ParserConfigurationException|SAXException e) {
			rethrow(e);
		}
	}

	@Override
	protected void finish() {
	}

	@Override
	protected boolean marshallingSupported() {
		return false;
	}

	@Override
	protected Decoder<Term> getDecoder() {
		throw new UnsupportedOperationException("marshalling not supported");
	}

	@Override
	protected Encoder<Term> getEncoder() {
		throw new UnsupportedOperationException("marshalling not supported");
	}

	@Override
	protected void handleMatch(Term entry, Annotation a) {
		a.addFeature(termId, entry.id);
		a.addFeature(monoHeadId, entry.monoHead);
		a.addFeature(termLemma, entry.lemma);
		a.addFeature(termPOS, entry.pos);
		if (entry.head != null)
			a.addFeature(head, entry.head);
		if (entry.modifier != null)
			a.addFeature(modifier, entry.modifier);
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	@TimeThis(task="create-trie", category=TimerCategory.LOAD_RESOURCE)
	protected Trie<Term> getTrie(ProcessingContext<Corpus> ctx, Logger logger, Corpus corpus) throws IOException, ModuleException {
		return super.getTrie(ctx, logger, corpus);
	}

	@Param
	public Boolean getMnpOnly() {
		return mnpOnly;
	}

	@Param
	public Boolean getProjectLemmas() {
		return projectLemmas;
	}

	@Param
	public SourceStream getYateaFile() {
		return yateaFile;
	}

	@Param(nameType=NameType.FEATURE)
	public String getTermId() {
		return termId;
	}

	@Param(nameType=NameType.FEATURE)
	public String getMonoHeadId() {
		return monoHeadId;
	}

	@Param(nameType=NameType.FEATURE)
	public String getTermLemma() {
		return termLemma;
	}

	@Param(nameType=NameType.FEATURE)
	public String getTermPOS() {
		return termPOS;
	}

	@Param(nameType=NameType.FEATURE)
	public String getHead() {
		return head;
	}

	@Param(nameType=NameType.FEATURE)
	public String getModifier() {
		return modifier;
	}

	public void setMnpOnly(Boolean mnpOnly) {
		this.mnpOnly = mnpOnly;
	}

	public void setProjectLemmas(Boolean projectLemmas) {
		this.projectLemmas = projectLemmas;
	}

	public void setYateaFile(SourceStream yateaFile) {
		this.yateaFile = yateaFile;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public void setMonoHeadId(String monoHeadId) {
		this.monoHeadId = monoHeadId;
	}

	public void setTermLemma(String termLemma) {
		this.termLemma = termLemma;
	}

	public void setTermPOS(String termPOS) {
		this.termPOS = termPOS;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
}
