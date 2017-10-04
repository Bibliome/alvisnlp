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
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.DocumentCreator;
import alvisnlp.module.types.Mapping;

public class DocumentDecoder extends ElementDecoder<Document> implements DocumentCreator {
	private final SectionDecoder sectionDecoder;
	private final Unmarshaller<Section> sectionUnmarshaller;
	private Corpus corpus;
	
	DocumentDecoder(Unmarshaller<String> stringUnmarshaller) throws IOException {
		super(stringUnmarshaller);
		this.sectionDecoder = new SectionDecoder(stringUnmarshaller);
		ReadCache<Section> sectionCache = MapReadCache.hashMap();
		this.sectionUnmarshaller = new Unmarshaller<Section>(stringUnmarshaller.getChannel(), sectionDecoder, sectionCache);
	}

	@Override
	public Document decode1(ByteBuffer buffer) {
		String id = readString(buffer);
		Document result = Document.getDocument(this, corpus, id);
		sectionDecoder.setDoc(result);
		int nSec = buffer.getInt();
		for (int i = 0; i < nSec; ++i) {
			int secRef = buffer.getInt();
			sectionUnmarshaller.read(secRef);
		}
		return result;
	}

	@Override
	public Mapping getConstantDocumentFeatures() {
		return null;
	}

	@Override
	public void setConstantDocumentFeatures(Mapping constantDocumentFeatures) {
	}

	Corpus getCorpus() {
		return corpus;
	}

	void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}

	public SectionDecoder getSectionDecoder() {
		return sectionDecoder;
	}

	public Unmarshaller<Section> getSectionUnmarshaller() {
		return sectionUnmarshaller;
	}
}
