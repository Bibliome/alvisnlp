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


package alvisnlp.corpus.dump.codec;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.bibliome.util.marshall.MapReadCache;
import org.bibliome.util.marshall.ReadCache;
import org.bibliome.util.marshall.Unmarshaller;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;

public class CorpusDecoder extends ElementDecoder<Corpus> {
	private final DocumentDecoder docDecoder;
	private final Unmarshaller<Document> docUnmarshaller;
	
	public CorpusDecoder(Unmarshaller<String> stringUnmarshaller) throws IOException {
		super(stringUnmarshaller);
		this.docDecoder = new DocumentDecoder(stringUnmarshaller);
		ReadCache<Document> docCache = MapReadCache.hashMap();
		this.docUnmarshaller = new Unmarshaller<Document>(stringUnmarshaller.getChannel(), docDecoder, docCache);
	}

	@Override
	public Corpus decode1(ByteBuffer buffer) {
		Corpus result = new Corpus();
		docDecoder.setCorpus(result);
		int nDocs = buffer.getInt();
		for (int i = 0; i < nDocs; ++i) {
			int docRef = buffer.getInt();
			docUnmarshaller.read(docRef);
		}
		return result;
	}

	public DocumentDecoder getDocDecoder() {
		return docDecoder;
	}

	public Unmarshaller<Document> getDocUnmarshaller() {
		return docUnmarshaller;
	}
}
