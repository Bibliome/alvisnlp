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


package org.bibliome.alvisnlp.modules.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public enum MappingOperator {
	EXACT {
		@Override
		<T> List<T> getMatches(Map<String,List<T>> mapping, String key) {
			if (mapping.containsKey(key)) {
				return mapping.get(key);
			}
			return Collections.emptyList();
		}

		@Override
		public String toString() {
			return "exact";
		}
	},
	
	PREFIX {
		@Override
		<T> List<T> getMatches(Map<String, List<T>> mapping, String key) {
			List<T> result = new ArrayList<T>();
			for (Map.Entry<String,List<T>> e : mapping.entrySet()) {
				if (key.startsWith(e.getKey())) {
					result.addAll(e.getValue());
				}
			}
			return result;
		}

		@Override
		public String toString() {
			return "prefix";
		}
	}
	;
	
	abstract <T> List<T> getMatches(Map<String,List<T>> mapping, String key);
}

