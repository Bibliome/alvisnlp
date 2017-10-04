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
import java.util.List;
import java.util.Set;

import org.bibliome.util.marshall.Encoder;
import org.bibliome.util.marshall.Marshaller;

import alvisnlp.corpus.Element;

public abstract class ElementEncoder<E extends Element> implements Encoder<E> {
	protected final Marshaller<String> stringMarshaller;
	
	protected ElementEncoder(Marshaller<String> stringMarshaller) {
		super();
		this.stringMarshaller = stringMarshaller;
	}

	@Override
	public int getSize(E object) {
		int result = 4;
		for (String k : object.getFeatureKeys()) {
			result += 8;
			List<String> values = object.getFeature(k);
			int nValues = values.size();
			result += 4 * nValues;
		}
		return result;
	}

	@Override
	public void encode(E object, ByteBuffer buf) throws IOException {
		Set<String> keys = object.getFeatureKeys();
		int nKeys = keys.size();
		String staticFeatureKey = object.getStaticFeatureKey();
		if (staticFeatureKey != null) {
			nKeys--;
		}
		buf.putInt(nKeys);
		for (String k : keys) {
			if (object.isStaticFeatureKey(k)) {
				continue;
			}
			writeString(buf, k);
			List<String> values = object.getFeature(k);
			int nValues = values.size();
			buf.putInt(nValues);
			for (String v : values) {
				writeString(buf, v);
			}
		}
	}
	
	protected void writeString(ByteBuffer buf, String s) throws IOException {
		int ref = stringMarshaller.write(s);
		buf.putInt(ref);
	}
}
