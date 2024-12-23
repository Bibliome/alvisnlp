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

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.marshall.DataBuffer;
import fr.inra.maiage.bibliome.util.marshall.Unmarshaller;

public class AnnotationDecoder extends ElementDecoder<Annotation> implements AnnotationCreator {
	private Layer layer;
	
	AnnotationDecoder(Unmarshaller<String> stringUnmarshaller) {
		super(stringUnmarshaller);
	}

	@Override
	public Annotation decode1(DataBuffer buffer) {
		int start = buffer.getInt();
		int end = buffer.getInt();
		return new Annotation(this, layer, start, end);
	}

	@Override
	public Mapping getConstantAnnotationFeatures() {
		return null;
	}

	@Override
	public void setConstantAnnotationFeatures(Mapping constantAnnotationFeatures) {
	}

	Layer getLayer() {
		return layer;
	}

	void setLayer(Layer layer) {
		this.layer = layer;
	}
}
