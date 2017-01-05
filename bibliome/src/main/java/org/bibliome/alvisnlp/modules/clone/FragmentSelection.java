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


package org.bibliome.alvisnlp.modules.clone;

import org.bibliome.util.mappers.ParamMapper;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.module.types.Mapping;

public enum FragmentSelection implements ParamMapper<Section,Layer,String>, AnnotationCreator {
	INCLUDE {
		@Override
		public Layer map(Section sec, String layerName) {
			Layer layer = sec.ensureLayer(layerName);
			Layer result = new Layer(layer.getSection());
			if (layer.isEmpty())
				return result;
			Annotation first = layer.first();
			int start = first.getStart();
			int end = first.getEnd();
			for (Annotation a : layer) {
				if (a.getStart() > end) {
					new Annotation(this, result, start, end);
					start = a.getStart();
					end = a.getEnd();
					continue;
				}
				end = Math.max(end, a.getEnd());
			}
			return result;
		}

		@Override
		public String toString() {
			return "include";
		}
	},
	
	EXLUDE {
		@Override
		public Layer map(Section sec, String layerName) {
			Layer layer = sec.ensureLayer(layerName);
			Layer result = new Layer(sec);
			int len = sec.getContents().length();
			if (layer.isEmpty()) {
				new Annotation(this, result, 0, len);
				return result;
			}
			int start = 0;
			for (Annotation a : layer) {
				if (a.getStart() > start)
					new Annotation(this, result, start, a.getStart());
				start = Math.max(start, a.getEnd());
			}
			if (start < len)
				new Annotation(this, result, start, len);
			return result;
		}

		@Override
		public String toString() {
			return "exclude";
		}
	};

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

	@Override
	public Mapping getConstantAnnotationFeatures() {
		return null;
	}

	@Override
	public void setConstantAnnotationFeatures(Mapping constantAnnotationFeatures) {
	}
}
