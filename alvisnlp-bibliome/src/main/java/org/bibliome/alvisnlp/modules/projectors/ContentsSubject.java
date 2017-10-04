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


package org.bibliome.alvisnlp.modules.projectors;

import org.bibliome.util.newprojector.CharFilter;
import org.bibliome.util.newprojector.Dictionary;
import org.bibliome.util.newprojector.Matcher;
import org.bibliome.util.newprojector.chars.Filters;

import alvisnlp.corpus.Section;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

public class ContentsSubject implements Subject {
	public static final ContentsSubject PLAIN = new ContentsSubject(Filters.ACCEPT_ALL, Filters.ACCEPT_ALL);
	public static final ContentsSubject WORD = new ContentsSubject(Filters.START_WORD, Filters.END_WORD);
	public static final ContentsSubject PREFIX = new ContentsSubject(Filters.START_WORD, Filters.ACCEPT_ALL);
	public static final ContentsSubject SUFFIX = new ContentsSubject(Filters.ACCEPT_ALL, Filters.END_WORD);
	
	private final CharFilter startFilter;
	private final CharFilter endFilter;
	
	public ContentsSubject(CharFilter startFilter, CharFilter endFilter) {
		super();
		this.startFilter = startFilter;
		this.endFilter = endFilter;
	}

	@Override
	public <T> void match(Section sec, Dictionary<T> dict, Matcher<T> matcher) {
		dict.match(matcher, sec.getContents());
		matcher.endMatches();
	}

	@Override
	public boolean isCharPos() {
		return true;
	}

	@Override
	public CharFilter getStartFilter() {
		return startFilter;
	}

	@Override
	public CharFilter getEndFilter() {
		return endFilter;
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
	}
}
