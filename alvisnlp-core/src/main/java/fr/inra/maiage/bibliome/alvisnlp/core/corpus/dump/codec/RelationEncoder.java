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
import java.util.Collections;
import java.util.Map;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.util.marshall.MapWriteCache;
import fr.inra.maiage.bibliome.util.marshall.Marshaller;

public class RelationEncoder extends ElementEncoder<Relation> {
	private final TupleEncoder tupleEncoder;
	private final MapWriteCache<Tuple> tupleCache = MapWriteCache.hashMap();
	private final Marshaller<Tuple> tupleMarshaller;
	
	RelationEncoder(Marshaller<String> stringMarshaller) {
		super(stringMarshaller);
		this.tupleEncoder = new TupleEncoder(stringMarshaller);
		this.tupleMarshaller = new Marshaller<Tuple>(stringMarshaller.getChannel(), tupleEncoder, tupleCache);
	}

	@Override
	public int getSize(Relation object) {
		return REFERENCE_SIZE + 4 + REFERENCE_SIZE * object.size() + super.getSize(object);
	}

	@Override
	public void encode(Relation object, ByteBuffer buf) throws IOException {
		writeString(buf, object.getName());
		int nTuples = object.size();
		buf.putInt(nTuples);
		for (Tuple t : object.getTuples()) {
			long tRef = tupleMarshaller.write(t);
			buf.putLong(tRef);
		}
		super.encode(object, buf);
	}

	public TupleEncoder getTupleEncoder() {
		return tupleEncoder;
	}

	public Marshaller<Tuple> getTupleMarshaller() {
		return tupleMarshaller;
	}
	
	public Map<Tuple,Long> getAllTuples() {
		return Collections.unmodifiableMap(tupleCache.getMap());
	}
}
