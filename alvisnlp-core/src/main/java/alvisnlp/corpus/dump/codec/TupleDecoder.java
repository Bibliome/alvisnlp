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

import org.bibliome.util.marshall.Unmarshaller;

import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.module.types.Mapping;

public class TupleDecoder extends ElementDecoder<Tuple> implements TupleCreator {
	private Relation relation;
	
	TupleDecoder(Unmarshaller<String> stringUnmarshaller) {
		super(stringUnmarshaller);
	}

	@Override
	public Tuple decode1(ByteBuffer buffer) {
		int nArgs = buffer.getInt();
		for (int i = 0; i < nArgs; ++i) {
			buffer.getInt();
			buffer.getInt(); // skip arguments
		}
		return new Tuple(this, relation);
	}

	@Override
	public Mapping getConstantRelationFeatures() {
		return null;
	}

	@Override
	public void setConstantRelationFeatures(Mapping constantRelationFeatures) {
	}

	@Override
	public Mapping getConstantTupleFeatures() {
		return null;
	}

	@Override
	public void setConstantTupleFeatures(Mapping constantRelationFeatures) {
	}

	Relation getRelation() {
		return relation;
	}

	void setRelation(Relation relation) {
		this.relation = relation;
	}
}
