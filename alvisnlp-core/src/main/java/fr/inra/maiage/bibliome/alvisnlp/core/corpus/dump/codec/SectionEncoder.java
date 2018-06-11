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
import java.nio.channels.FileChannel;
import java.util.Collection;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.util.marshall.MapWriteCache;
import fr.inra.maiage.bibliome.util.marshall.Marshaller;
import fr.inra.maiage.bibliome.util.marshall.WriteCache;

public class SectionEncoder extends ElementEncoder<Section> {
	private final LayerEncoder layerEncoder;
	private final Marshaller<Layer> layerMarshaller;
	private final RelationEncoder relationEncoder;
	private final Marshaller<Relation> relationMarshaller;
	
	SectionEncoder(Marshaller<String> stringMarshaller) {
		super(stringMarshaller);
		this.layerEncoder = new LayerEncoder(stringMarshaller);
		FileChannel channel = stringMarshaller.getChannel();
		this.layerMarshaller = new Marshaller<Layer>(channel, layerEncoder);
		this.relationEncoder = new RelationEncoder(stringMarshaller);
		WriteCache<Relation> relationCache = MapWriteCache.hashMap();
		this.relationMarshaller = new Marshaller<Relation>(channel, relationEncoder, relationCache);
	}

	@Override
	public int getSize(Section object) {
		int nLayers = object.getAllLayers().size();
		int nRelations = object.getAllRelations().size();
		return REFERENCE_SIZE + REFERENCE_SIZE + 4 + REFERENCE_SIZE * nLayers + 4 + REFERENCE_SIZE * nRelations + super.getSize(object);
	}

	@Override
	public void encode(Section object, ByteBuffer buf) throws IOException {
		writeString(buf, object.getName());
		writeString(buf, object.getContents());
		
		Collection<Layer> layers = object.getAllLayers();
		int nLayers = layers.size();
		buf.putInt(nLayers);
		for (Layer layer : layers) {
			long layerRef = layerMarshaller.write(layer);
			buf.putLong(layerRef);
		}
		
		Collection<Relation> relations = object.getAllRelations();
		int nRelations = relations.size();
		buf.putInt(nRelations);
		for (Relation rel : relations) {
			long relRef = relationMarshaller.write(rel);
			buf.putLong(relRef);
		}

		super.encode(object, buf);
	}

	public LayerEncoder getLayerEncoder() {
		return layerEncoder;
	}

	public Marshaller<Layer> getLayerMarshaller() {
		return layerMarshaller;
	}

	public RelationEncoder getRelationEncoder() {
		return relationEncoder;
	}

	public Marshaller<Relation> getRelationMarshaller() {
		return relationMarshaller;
	}
}
