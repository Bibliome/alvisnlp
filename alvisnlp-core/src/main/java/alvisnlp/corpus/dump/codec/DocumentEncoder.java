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

import org.bibliome.util.Iterators;
import org.bibliome.util.marshall.MapWriteCache;
import org.bibliome.util.marshall.Marshaller;
import org.bibliome.util.marshall.WriteCache;

import alvisnlp.corpus.Document;
import alvisnlp.corpus.Section;

public class DocumentEncoder extends ElementEncoder<Document> {
	private final SectionEncoder sectionEncoder;
	private final Marshaller<Section> sectionMarshaller;
	
	DocumentEncoder(Marshaller<String> stringMarshaller) {
		super(stringMarshaller);
		this.sectionEncoder = new SectionEncoder(stringMarshaller);
		WriteCache<Section> sectionCache = MapWriteCache.hashMap();
		this.sectionMarshaller = new Marshaller<Section>(stringMarshaller.getChannel(), sectionEncoder, sectionCache);
	}

	@Override
	public int getSize(Document object) {
		int nSec = object.size();
		return 4 + 4 + 4 * nSec + super.getSize(object);
	}

	@Override
	public void encode(Document object, ByteBuffer buf) throws IOException {
		String id = object.getId();
		writeString(buf, id);
		int nSec = object.size();
		buf.putInt(nSec);
		for (Section sec : Iterators.loop(object.sectionIterator())) {
			int secRef = sectionMarshaller.write(sec);
			buf.putInt(secRef);
		}
		super.encode(object, buf);
	}

	public SectionEncoder getSectionEncoder() {
		return sectionEncoder;
	}

	public Marshaller<Section> getSectionMarshaller() {
		return sectionMarshaller;
	}
}
