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


package org.bibliome.alvisnlp.modules.cadixe;

import java.util.HashMap;
import java.util.UUID;

import org.bibliome.util.defaultmap.DefaultMap;
import org.json.simple.JSONObject;

class AnnotationReference {
	private Integer annotationSet;
	private final String id;
	
	AnnotationReference() {
		super();
		id = UUID.randomUUID().toString();
	}

	Integer getAnnotationSet() {
		return annotationSet;
	}

	String getId() {
		return id;
	}

	void setAnnotationSet(int annotationSet) {
		this.annotationSet = annotationSet;
	}
	
	static class Record<T> extends DefaultMap<T,AnnotationReference> {
		Record() {
			super(true, new HashMap<T,AnnotationReference>());
		}

		@Override
		protected AnnotationReference defaultValue(T key) {
			return new AnnotationReference();
		}
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject asJSON() {
		JSONObject result = new JSONObject();
		result.put("set_id", annotationSet);
		result.put("ann_id", id);
		return result;
	}
}
