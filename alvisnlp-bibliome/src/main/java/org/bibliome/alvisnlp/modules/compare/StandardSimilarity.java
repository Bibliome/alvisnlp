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


package org.bibliome.alvisnlp.modules.compare;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

public enum StandardSimilarity implements ElementSimilarity {
	ANNOTATION_STRICT {
		@Override
		public double similarity(Element a, Element b) {
			Annotation aa = DownCastElement.toAnnotation(a);
			if (aa == null)
				return 0;
			Annotation ab = DownCastElement.toAnnotation(b);
			if (ab == null)
				return 0;
			if (aa.sameSpan(ab))
				return 1;
			return 0;
		}

		@Override
		public String toString() {
			return "strict";
		}
	},
	
	ANNOTATION_LAX {
		@Override
		public double similarity(Element a, Element b) {
			Annotation aa = DownCastElement.toAnnotation(a);
			if (aa == null)
				return 0;
			Annotation ab = DownCastElement.toAnnotation(b);
			if (ab == null)
				return 0;
			if (aa.overlaps(ab))
				return 1;
			return 0;
		}

		@Override
		public String toString() {
			return "lax";
		}
	},
	
	ANNOTATION_JACCARD {
		@Override
		public double similarity(Element a, Element b) {
			Annotation aa = DownCastElement.toAnnotation(a);
			if (aa == null)
				return 0;
			Annotation ab = DownCastElement.toAnnotation(b);
			if (ab == null)
				return 0;
			int maxStart = Math.max(aa.getStart(), ab.getStart());
			int minEnd = Math.min(aa.getEnd(), ab.getEnd());
			if (minEnd <= maxStart)
				return 0;
			double minStart = Math.min(aa.getStart(), ab.getStart());
			double maxEnd = Math.max(aa.getEnd(), ab.getEnd());
			return (minEnd - maxStart) / (maxEnd - minStart);
		}

		@Override
		public String toString() {
			return "jaccard";
		}
	};

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
	}
}
