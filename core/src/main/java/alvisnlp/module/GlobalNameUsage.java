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


package alvisnlp.module;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bibliome.util.defaultmap.DefaultLinkedHashSetLinkedHashMap;
import org.bibliome.util.defaultmap.DefaultMap;

public class GlobalNameUsage {
	private final Map<String,DefaultMap<String,Set<String>>> map = new LinkedHashMap<String,DefaultMap<String,Set<String>>>();
	
	public GlobalNameUsage(Collection<String> nameTypes) {
		for (String nt : nameTypes) {
			DefaultMap<String,Set<String>> names = new DefaultLinkedHashSetLinkedHashMap<String,String>();
			map.put(nt, names);
		}
	}
	
	private void checkNameType(String nameType) {
		if (!map.containsKey(nameType)) {
			throw new IllegalArgumentException();
		}
	}
	
	public void registerUsedName(String id, String nameType, String name) {
		checkNameType(nameType);
		DefaultMap<String,Set<String>> names = map.get(nameType);
		names.safeGet(name).add(id);
	}
	
	public void registerUsedNames(String id, NameUser user, String defaultType) throws ModuleException {
		NameUsage nameUsage = new NameUsage(map.keySet());
		user.collectUsedNames(nameUsage, defaultType);
		registerUsedNames(id, nameUsage);
	}
	
	private void registerUsedNames(String id, NameUsage nameUsage) {
		for (Map.Entry<String,DefaultMap<String,Set<String>>> e : map.entrySet()) {
			String nameType = e.getKey();
			DefaultMap<String,Set<String>> names = e.getValue();
			for (String name : nameUsage.getUsedNames(nameType)) {
				Set<String> users = names.safeGet(name);
				users.add(id);
			}
		}
	}
	
	public Map<String,Set<String>> getUsedNames(String nameType) {
		checkNameType(nameType);
		return map.get(nameType);
	}
	
	public Set<String> getNameTypes() {
		return Collections.unmodifiableSet(map.keySet());
	}
}
