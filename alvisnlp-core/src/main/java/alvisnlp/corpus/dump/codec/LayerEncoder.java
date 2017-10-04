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

import org.bibliome.util.marshall.Encoder;
import org.bibliome.util.marshall.MapWriteCache;
import org.bibliome.util.marshall.Marshaller;
import org.bibliome.util.marshall.WriteCache;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Layer;

public class LayerEncoder implements Encoder<Layer> {
	private final Marshaller<String> stringMarshaller;
	private final AnnotationEncoder annotationEncoder;
	private final Marshaller<Annotation> annotationMarshaller;
	
	LayerEncoder(Marshaller<String> stringMarshaller) {
		super();
		this.stringMarshaller = stringMarshaller;
		this.annotationEncoder = new AnnotationEncoder(stringMarshaller);
		WriteCache<Annotation> annotationCache = MapWriteCache.hashMap();
		this.annotationMarshaller = new Marshaller<Annotation>(stringMarshaller.getChannel(), annotationEncoder, annotationCache);
	}

	@Override
	public int getSize(Layer object) {
		return 4 + 4 + 4 * object.size();
	}

	@Override
	public void encode(Layer object, ByteBuffer buf) throws IOException {
		int nameRef = stringMarshaller.write(object.getName());
		buf.putInt(nameRef);
		int sz = object.size();
		buf.putInt(sz);
		for (Annotation a : object) {
			int aRef = annotationMarshaller.write(a);
			buf.putInt(aRef);
		}
	}

	public AnnotationEncoder getAnnotationEncoder() {
		return annotationEncoder;
	}

	public Marshaller<Annotation> getAnnotationMarshaller() {
		return annotationMarshaller;
	}
}
