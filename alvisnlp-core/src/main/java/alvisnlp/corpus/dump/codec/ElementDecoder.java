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

import java.nio.ByteBuffer;

import org.bibliome.util.marshall.Decoder;
import org.bibliome.util.marshall.Unmarshaller;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.creators.ElementCreator;

public abstract class ElementDecoder<E extends Element> implements Decoder<E>, ElementCreator {
	protected final Unmarshaller<String> stringUnmarshaller;
	
	protected ElementDecoder(Unmarshaller<String> stringUnmarshaller) {
		super();
		this.stringUnmarshaller = stringUnmarshaller;
	}

	@Override
	public void decode2(ByteBuffer buffer, E object) {
		int nKeys = buffer.getInt();
		for (int i = 0; i < nKeys; ++i) {
			String k = readString(buffer);
			int nValues = buffer.getInt();
			for (int j = 0; j < nValues; ++j) {
				String v = readString(buffer);
				object.addFeature(k, v);
			}
		}
	}
	
	protected String readString(ByteBuffer buf) {
		int ref = buf.getInt();
		return stringUnmarshaller.read(ref);
	}

	@Override
	public String getCreatorNameFeature() {
		return null;
	}

	@Override
	public void setCreatorNameFeature(String nameFeature) {
	}

	@Override
	public String getCreatorName() {
		return null;
	}
}
