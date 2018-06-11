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
import java.util.Map;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.RelationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.marshall.MapReadCache;
import fr.inra.maiage.bibliome.util.marshall.Unmarshaller;

public class RelationDecoder extends ElementDecoder<Relation> implements RelationCreator {
	private final TupleDecoder tupleDecoder;
	private final MapReadCache<Tuple> tupleCache;
	private final Unmarshaller<Tuple> tupleUnmarshaller;
	private Section section;

	RelationDecoder(Unmarshaller<String> stringUnmarshaller) throws IOException {
		super(stringUnmarshaller);
		this.tupleDecoder = new TupleDecoder(stringUnmarshaller);
		this.tupleCache = MapReadCache.hashMap();
		this.tupleUnmarshaller = new Unmarshaller<Tuple>(stringUnmarshaller.getChannel(), tupleDecoder, tupleCache);
	}

	@Override
	public Relation decode1(ByteBuffer buffer) {
		String name = readString(buffer);
		Relation relation = new Relation(this, section, name);
		tupleDecoder.setRelation(relation);
		int nTuples = buffer.getInt();
		for (int i = 0; i < nTuples; ++i) {
			long tRef = buffer.getLong();
			tupleUnmarshaller.read(tRef);
		}
		return relation;
	}

	@Override
	public Mapping getConstantRelationFeatures() {
		return null;
	}

	@Override
	public void setConstantRelationFeatures(Mapping constantRelationFeatures) {
	}

	Section getSection() {
		return section;
	}

	void setSection(Section section) {
		this.section = section;
	}

	public Unmarshaller<Tuple> getTupleUnmarshaller() {
		return tupleUnmarshaller;
	}
	
	public Map<Long,Tuple> getAllTuples() {
		return tupleCache.getMap();
	}
}
