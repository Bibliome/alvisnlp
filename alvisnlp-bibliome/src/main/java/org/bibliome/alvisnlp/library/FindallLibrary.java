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


package org.bibliome.alvisnlp.library;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;

@Library(value="match", serviceClass="org.bibliome.alvisnlp.library.FindallLibrary")
public abstract class FindallLibrary extends FunctionLibrary {
	private Pattern pattern;
	private Matcher matcher;
	private int position;
	
	public FindallLibrary() {
		super();
	}
	
	void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	void init(CharSequence target) {
		matcher = pattern.matcher(target);
		position = 0;
	}
	
	boolean findNext() {
		if (matcher.find(position)) {
			position = matcher.end();
			return true;
		}
		return false;
	}
	
	@Function
	public final int start(int group) {
		return matcher.start(group);
	}
	
	@Function
	public final int end(int group) {
		return matcher.end(group);
	}
	
	@Function
	public final String group(int group) {
		return matcher.group(group);
	}
	
	@Function
	public final String named(String group) {
		return matcher.group(group);
	}
}
