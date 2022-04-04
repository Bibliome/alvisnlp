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
import java.nio.ByteBuffer;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.util.marshall.Decoder;
import fr.inra.maiage.bibliome.util.marshall.MapReadCache;
import fr.inra.maiage.bibliome.util.marshall.ReadCache;
import fr.inra.maiage.bibliome.util.marshall.Unmarshaller;

public class LayerDecoder implements Decoder<Layer> {
	private final Unmarshaller<String> stringUnmarshaller;
	private final AnnotationDecoder annotationDecoder;
	private final Unmarshaller<Annotation> annotationUnmarshaller;
	private Section section;
	
	LayerDecoder(Unmarshaller<String> stringUnmarshaller, int maxMmapSize) throws IOException {
		this.stringUnmarshaller = stringUnmarshaller;
		this.annotationDecoder = new AnnotationDecoder(stringUnmarshaller);
		ReadCache<Annotation> annotationCache = MapReadCache.hashMap();
		this.annotationUnmarshaller = new Unmarshaller<Annotation>(stringUnmarshaller.getChannel(), annotationDecoder, annotationCache, maxMmapSize);
	}
	
	@Override
	public Layer decode1(ByteBuffer buffer) {
		long nameRef = buffer.getLong();
		String name = stringUnmarshaller.read(nameRef);
		return new Layer(section, name);
	}

	@Override
	public void decode2(ByteBuffer buffer, Layer object) {
		annotationDecoder.setLayer(object);
		int sz = buffer.getInt();
		for (int i = 0; i < sz; ++i) {
			long aRef = buffer.getLong();
			annotationUnmarshaller.read(aRef);
		}
	}

	Section getSection() {
		return section;
	}

	void setSection(Section section) {
		this.section = section;
	}

	public AnnotationDecoder getAnnotationDecoder() {
		return annotationDecoder;
	}

	public Unmarshaller<Annotation> getAnnotationUnmarshaller() {
		return annotationUnmarshaller;
	}
}
