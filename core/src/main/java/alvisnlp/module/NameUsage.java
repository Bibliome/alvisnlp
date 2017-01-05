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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class NameUsage {
	private final Map<String,Set<String>> map = new HashMap<String,Set<String>>();
	
	NameUsage(Collection<String> nameTypes) {
		for (String nt : nameTypes) {
			Set<String> names = new LinkedHashSet<String>();
			map.put(nt, names);
		}
	}
	
	private void checkNameType(String nameType) {
		if (!map.containsKey(nameType)) {
			throw new IllegalArgumentException();
		}
	}
	
	public void addNames(String nameType, Collection<String> names) {
		checkNameType(nameType);
		Set<String> usedNames = map.get(nameType);
		usedNames.addAll(names);
	}
	
	public void addNames(String nameType, String... names) {
		addNames(nameType, Arrays.asList(names));
	}
	
	public Set<String> getUsedNames(String nameType) {
		checkNameType(nameType);
		return Collections.unmodifiableSet(map.get(nameType));
	}
	
	public Set<String> getNameTypes() {
		return Collections.unmodifiableSet(map.keySet());
	}
	
	public void collectUsedNamesNullable(NameUser user, String defaultType) throws ModuleException {
		if (user != null) {
			user.collectUsedNames(this, defaultType);
		}
	}
	
	public <T extends NameUser> void collectUsedNamesArray(T[] users, String defaultType) throws ModuleException {
		for (NameUser user : users) {
			user.collectUsedNames(this, defaultType);
		}
	}
}
