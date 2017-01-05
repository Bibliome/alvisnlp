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

import org.bibliome.util.marshall.MapReadCache;
import org.bibliome.util.marshall.ReadCache;
import org.bibliome.util.marshall.Unmarshaller;

import alvisnlp.corpus.Document;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.module.types.Mapping;

public class SectionDecoder extends ElementDecoder<Section> implements SectionCreator {
	private final LayerDecoder layerDecoder;
	private final Unmarshaller<Layer> layerUnmarshaller;
	private final RelationDecoder relationDecoder;
	private final Unmarshaller<Relation> relationUnmarshaller;
	private Document doc;

	SectionDecoder(Unmarshaller<String> stringUnmarshaller) throws IOException {
		super(stringUnmarshaller);
		this.layerDecoder = new LayerDecoder(stringUnmarshaller);
		this.layerUnmarshaller = new Unmarshaller<Layer>(stringUnmarshaller.getChannel(), layerDecoder);
		this.relationDecoder = new RelationDecoder(stringUnmarshaller);
		ReadCache<Relation> relationCache = MapReadCache.hashMap();
		this.relationUnmarshaller = new Unmarshaller<Relation>(stringUnmarshaller.getChannel(), relationDecoder, relationCache);
	}

	@Override
	public Section decode1(ByteBuffer buffer) {
		String name = readString(buffer);
		String contents = readString(buffer);
		Section result = new Section(this, doc, name, contents);
		
		layerDecoder.setSection(result);
		int nLayers = buffer.getInt();
		for (int i = 0; i < nLayers; ++i) {
			int layerRef = buffer.getInt();
			layerUnmarshaller.read(layerRef);
		}
		
		relationDecoder.setSection(result);
		int nRelations = buffer.getInt();
		for (int i = 0; i < nRelations; ++i) {
			int relRef = buffer.getInt();
			relationUnmarshaller.read(relRef);
		}
		
		return result;
	}

	@Override
	public Mapping getConstantSectionFeatures() {
		return null;
	}

	@Override
	public void setConstantSectionFeatures(Mapping constantSectionFeatures) {
	}

	Document getDoc() {
		return doc;
	}

	void setDoc(Document doc) {
		this.doc = doc;
	}

	public LayerDecoder getLayerDecoder() {
		return layerDecoder;
	}

	public RelationDecoder getRelationDecoder() {
		return relationDecoder;
	}

	public Unmarshaller<Relation> getRelationUnmarshaller() {
		return relationUnmarshaller;
	}
}
