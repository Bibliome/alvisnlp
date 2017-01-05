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

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.projectors.YateaProjector.Term;
import org.bibliome.util.Iterators;
import org.bibliome.util.newprojector.CharFilter;
import org.bibliome.util.newprojector.CharMapper;
import org.bibliome.util.newprojector.Dictionary;
import org.bibliome.util.newprojector.State;
import org.bibliome.util.streams.SourceStream;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule
public abstract class YateaProjector extends Projector<SectionResolvedObjects,Term,Dictionary<Term>> {
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
		private final Dictionary<Term> dict;
		private final StringBuilder chars = new StringBuilder();
		private boolean syntacticAnalysis;
		private boolean dismissed;
		private boolean mnp;
		
		private YateaHandler(Dictionary<Term> dict) {
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

	private SourceStream yateaFile;
	private Boolean projectLemmas = false;
	private String termId = "term-id";
	private String monoHeadId = "mono-head";
	private String termLemma;
	private String termPOS;
	private String head = "head";
	private String modifier = "modifier";
	private Boolean mnpOnly = false;
	
	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	protected Dictionary<Term> newDictionary(State<Term> root, CharFilter charFilter, CharMapper charMapper) {
		return new Dictionary<Term>(root, charFilter, charMapper);
	}

	@Override
	protected void fillDictionary(ProcessingContext<Corpus> ctx, Corpus corpus, Dictionary<Term> dict) throws Exception {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		YateaHandler handler = new YateaHandler(dict);
		for (InputStream is : Iterators.loop(yateaFile.getInputStreams())) {
			parser.parse(is, handler);
		}
	}

	@Override
	protected void handleEntryValues(ProcessingContext<Corpus> ctx, Dictionary<Term> dict, Annotation a, Term entry) throws Exception {
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
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Param
	public SourceStream getYateaFile() {
		return yateaFile;
	}

	@Param
	public Boolean getProjectLemmas() {
		return projectLemmas;
	}

	@Param(nameType=NameType.FEATURE)
	public String getTermId() {
		return termId;
	}

	@Param(nameType=NameType.FEATURE)
	public String getMonoHeadId() {
		return monoHeadId;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getTermLemma() {
		return termLemma;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
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

	@Param
	public Boolean getMnpOnly() {
		return mnpOnly;
	}

	public void setMnpOnly(Boolean mnpOnly) {
		this.mnpOnly = mnpOnly;
	}

	public void setYateaFile(SourceStream yateaFile) {
		this.yateaFile = yateaFile;
	}

	public void setProjectLemmas(Boolean projectLemmas) {
		this.projectLemmas = projectLemmas;
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
