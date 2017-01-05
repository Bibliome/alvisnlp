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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DownCastJSON {
	static final JSONObject toObject(Object o) {
		if (o instanceof JSONObject)
			return (JSONObject) o;
		return null;
	}
	
	static final JSONArray toArray(Object o) {
		if (o instanceof JSONArray)
			return (JSONArray) o;
		return null;
	}
	
	static final Number toNumber(Object o) {
		if (o instanceof Number)
			return (Number) o;
		return null;
	}
	
	static final String toString(Object o) {
		if (o instanceof String)
			return (String) o;
		return null;
	}
	
	static final Boolean toBoolean(Object o) {
		if (o instanceof Boolean)
			return (Boolean) o;
		return null;
	}
}
