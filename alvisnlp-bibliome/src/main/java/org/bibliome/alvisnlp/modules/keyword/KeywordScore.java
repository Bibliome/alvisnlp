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


package org.bibliome.alvisnlp.modules.keyword;

import java.util.Comparator;

public class KeywordScore {
	private final String keyword;
	private final double score;
	
	KeywordScore(String keyword, double score) {
		super();
		this.keyword = keyword;
		this.score = score;
	}

	String getKeyword() {
		return keyword;
	}

	double getScore() {
		return score;
	}
	
	static final Comparator<KeywordScore> SCORE_COMPARATOR = new Comparator<KeywordScore>() {
		@Override
		public int compare(KeywordScore o1, KeywordScore o2) {
			return Double.compare(o2.score, o1.score);
		}
	};
}
