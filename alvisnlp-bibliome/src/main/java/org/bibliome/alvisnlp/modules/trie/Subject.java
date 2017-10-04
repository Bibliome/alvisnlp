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


package org.bibliome.alvisnlp.modules.trie;

import java.util.List;

import org.bibliome.util.trie.Match;
import org.bibliome.util.trie.Matcher;
import org.bibliome.util.trie.StandardMatchControl;

import alvisnlp.corpus.Section;
import alvisnlp.module.NameUser;

public interface Subject extends NameUser {
	/**
	 * Searches for matches in the specified section.
	 * @param matcher
	 * @param sec
	 * @return the position at which the search has stopped.
	 */
	<T> List<Match<T>> search(Matcher<T> matcher, Section sec);

	void correctControl(StandardMatchControl control);
}
