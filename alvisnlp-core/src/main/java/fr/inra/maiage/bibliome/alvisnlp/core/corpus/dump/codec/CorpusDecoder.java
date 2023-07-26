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


package fr.inra.maiage.bibliome.alvisnlp.core.corpus.dump.codec;

import java.io.IOException;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.util.marshall.DataBuffer;
import fr.inra.maiage.bibliome.util.marshall.MapReadCache;
import fr.inra.maiage.bibliome.util.marshall.ReadCache;
import fr.inra.maiage.bibliome.util.marshall.Unmarshaller;

public class CorpusDecoder extends ElementDecoder<Corpus> {
	private final DocumentDecoder docDecoder;
	private final Unmarshaller<Document> docUnmarshaller;
	private final Corpus corpus;
	
	public CorpusDecoder(Unmarshaller<String> stringUnmarshaller, Corpus corpus) throws IOException {
		super(stringUnmarshaller);
		this.docDecoder = new DocumentDecoder(stringUnmarshaller);
		ReadCache<Document> docCache = MapReadCache.hashMap();
		this.docUnmarshaller = new Unmarshaller<Document>(stringUnmarshaller.getChannel(), docDecoder, docCache);
		this.corpus = corpus;
	}

	@Override
	public Corpus decode1(DataBuffer buffer) {
		docDecoder.setCorpus(corpus);
		int nDocs = buffer.getInt();
		for (int i = 0; i < nDocs; ++i) {
			long docRef = buffer.getLong();
			docUnmarshaller.read(docRef);
		}
		return corpus;
	}

	public DocumentDecoder getDocDecoder() {
		return docDecoder;
	}

	public Unmarshaller<Document> getDocUnmarshaller() {
		return docUnmarshaller;
	}
}
