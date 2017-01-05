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


package org.bibliome.alvisnlp.modules.wok.readers;

import org.bibliome.alvisnlp.modules.wok.FieldReader;
import org.bibliome.alvisnlp.modules.wok.WebOfKnowledgeReader;
import org.bibliome.alvisnlp.modules.wok.WoKReaderStatus;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;

public enum PublicationType implements FieldReader {
	INSTANCE;

	@Override
	public void start(WoKReaderStatus status) {
		WebOfKnowledgeReader dc = status.getOwner();
		String path = dc.getPath();
		int nDocs = status.getDocumentCount();
		Corpus corpus = status.getCorpus();
		Document doc = Document.getDocument(dc, corpus, path + nDocs);
		status.setDocument(doc);
	}

	@Override
	public void addLine(WoKReaderStatus status, String line) {
		Document doc = status.getDocument();
		doc.addFeature("publication-type", line);
	}

	@Override
	public void finish(WoKReaderStatus status) {
	}
}
