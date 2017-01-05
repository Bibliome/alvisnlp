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


package org.bibliome.alvisnlp.modules.wok;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.bibliome.alvisnlp.modules.wok.readers.FeatureSetter;
import org.bibliome.alvisnlp.modules.wok.readers.FeatureSetter.Target;
import org.bibliome.alvisnlp.modules.wok.readers.FormatVersion;
import org.bibliome.alvisnlp.modules.wok.readers.NoOpFieldReader;
import org.bibliome.alvisnlp.modules.wok.readers.PublicationType;
import org.bibliome.alvisnlp.modules.wok.readers.SectionBuilder;
import org.bibliome.util.Strings;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.module.ProcessingException;

public class WoKReaderStatus {
	private final WebOfKnowledgeReader owner;
	private FieldReader reader = NoOpFieldReader.INSTANCE;
	private final Corpus corpus;
	private int documentCount = 0;
	private Document document;
	private final Collection<String> unhandledFields = new HashSet<String>();
	private final List<String> headers = new ArrayList<String>(100);
	
	public WoKReaderStatus(WebOfKnowledgeReader webOfKnowledgeReader, Corpus corpus) {
		owner = webOfKnowledgeReader;
		this.corpus = corpus;
	}
	
	private FieldReader getFieldReader(String fieldCode) {
		switch (fieldCode) {
		// Version:
		case "VR":
			return FormatVersion.V_1_0;
			
			// Documents
		case "PT":
			return PublicationType.INSTANCE;

			// Sections
		case "TI":
		case "AB":
		case "FX":
			return new SectionBuilder(fieldCode);
			
			// Features
		case "AU": 
		case "AF":
		case "BA":
		case "BF":
		case "CA":
		case "GP":
		case "BE":
		case "SO": // could be section?
		case "SE":
		case "BS":
		case "LA":
		case "CT":
		case "CY":
		case "CL":
		case "SP":
		case "HO":
		case "C1":
		case "RP":
		case "EM":
		case "RI":
		case "OI":
		case "FU":
		case "TC":
		case "Z9":
		case "PU":
		case "PI":
		case "PA":
		case "SN":
		case "BN":
		case "J9":
		case "JI":
		case "PD":
		case "PY":
		case "VL":
		case "IS":
		case "PN":
		case "SU":
		case "MA":
		case "BP":
		case "EP":
		case "AR":
		case "DI":
		case "D2":
		case "PG":
		case "P2":
		case "GA":
		case "UT":
		case "SI":
		case "NR":
			return new FeatureSetter(Target.DOCUMENT, fieldCode, false);
			
			// Keywords
		case "DE":
		case "DT":
		case "ID":
		case "CR":
		case "WC":
		case "SC":
			return new FeatureSetter(Target.DOCUMENT, fieldCode, true);
			
			// Ignored
		case "ER":
		case "EF":
		case "FN":
			return NoOpFieldReader.INSTANCE;
		}
		unhandledFields.add(fieldCode);
		return new FeatureSetter(Target.DOCUMENT, fieldCode, false);
	}
	
	void readLine(String line) {
		if (line.startsWith("   ")) {
			reader.addLine(this, line.trim());
			return;
		}
		reader.finish(this);
		String fieldCode = line.substring(0, 2);
		reader = getFieldReader(fieldCode);
		reader.start(this);
		reader.addLine(this, line.length() > 2 ? line.substring(3).trim(): "");
	}
	
	void readTabularLine(String line) throws ProcessingException {
		if (headers.isEmpty()) {
			Strings.split(line, '\t', 0, headers);
			return;
		}
		List<String> values = Strings.split(line, '\t', 0);
		if (values.size() != headers.size()) {
			throw new ProcessingException("expected " + headers.size() + " columns, got " + values.size());
		}
		for (int i = 0; i < values.size(); ++i) {
			String fieldCode = headers.get(i).trim();
			reader = getFieldReader(fieldCode);
			reader.start(this);
			reader.addLine(this, values.get(i));
			reader.finish(this);
		}
	}

	public Corpus getCorpus() {
		return corpus;
	}

	public Document getDocument() {
		return document;
	}

	public Collection<String> getUnhandledFields() {
		return Collections.unmodifiableCollection(unhandledFields);
	}

	public WebOfKnowledgeReader getOwner() {
		return owner;
	}

	public int getDocumentCount() {
		return documentCount;
	}

	public void setDocument(Document document) {
		this.document = document;
		documentCount++;
	}
}
