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
import java.nio.channels.FileChannel;

import org.bibliome.util.Iterators;
import org.bibliome.util.marshall.MapWriteCache;
import org.bibliome.util.marshall.Marshaller;
import org.bibliome.util.marshall.StringCodec;
import org.bibliome.util.marshall.WriteCache;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;

public class CorpusEncoder extends ElementEncoder<Corpus> {
	private final DocumentEncoder docEncoder;
	private final Marshaller<Document> docMarshaller;
	
	private CorpusEncoder(Marshaller<String> stringMarshaller) {
		super(stringMarshaller);
		this.docEncoder = new DocumentEncoder(stringMarshaller);
		WriteCache<Document> docCache = MapWriteCache.hashMap();
		this.docMarshaller = new Marshaller<Document>(stringMarshaller.getChannel(), docEncoder, docCache);
	}
	
	public CorpusEncoder(FileChannel channel) {
		this(createStringMarshaller(channel));
	}
	
	private static Marshaller<String> createStringMarshaller(FileChannel channel) {
		WriteCache<String> stringCache = MapWriteCache.hashMap();
		return new Marshaller<String>(channel, StringCodec.INSTANCE, stringCache);
	}

	@Override
	public int getSize(Corpus object) {
		int nDocs = object.countDocuments();
		return 4 + 4 * nDocs + super.getSize(object);
	}

	@Override
	public void encode(Corpus object, ByteBuffer buf) throws IOException {
		int nDocs = object.countDocuments();
		buf.putInt(nDocs);
		for (Document doc : Iterators.loop(object.documentIterator())) {
			int docRef = docMarshaller.write(doc);
			buf.putInt(docRef);
		}
		super.encode(object, buf);
	}

	public DocumentEncoder getDocEncoder() {
		return docEncoder;
	}

	public Marshaller<Document> getDocMarshaller() {
		return docMarshaller;
	}
}
