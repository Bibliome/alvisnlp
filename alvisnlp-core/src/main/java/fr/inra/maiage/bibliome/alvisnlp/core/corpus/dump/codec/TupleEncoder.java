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

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.util.marshall.Marshaller;

public class TupleEncoder extends ElementEncoder<Tuple> {
	TupleEncoder(Marshaller<String> stringMarshaller) {
		super(stringMarshaller);
	}

	@Override
	public int getSize(Tuple object) {
		return 4 + (REFERENCE_SIZE + REFERENCE_SIZE) * object.getArity() + super.getSize(object);
	}
	
	@Override
	public void encode(Tuple object, ByteBuffer buf) throws IOException {
		int nArgs = object.getArity();
		buf.putInt(nArgs);
		for (String role : object.getRoles()) {
			writeString(buf, role);
			buf.putLong(0);
		}
		super.encode(object, buf);
	}
}
