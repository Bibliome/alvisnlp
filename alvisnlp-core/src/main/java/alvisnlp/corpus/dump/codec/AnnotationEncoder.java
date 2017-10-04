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

import org.bibliome.util.marshall.Marshaller;

import alvisnlp.corpus.Annotation;

public class AnnotationEncoder extends ElementEncoder<Annotation> {
	public AnnotationEncoder(Marshaller<String> stringMarshaller) {
		super(stringMarshaller);
	}

	@Override
	public int getSize(Annotation object) {
		return 4 + 4 + super.getSize(object);
	}

	@Override
	public void encode(Annotation object, ByteBuffer buf) throws IOException {
		buf.putInt(object.getStart());
		buf.putInt(object.getEnd());
		super.encode(object, buf);
	}

}
