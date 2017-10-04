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

public enum TFIDF implements KeywordScoreFunction {
	RAW {
		@Override
		public boolean requiresMaxFrequency() {
			return false;
		}

		@Override
		protected double getTF(long freq, long maxFreq) {
			return freq;
		}
	},
	
	BOOLEAN {
		@Override
		public boolean requiresMaxFrequency() {
			return false;
		}

		@Override
		protected double getTF(long freq, long maxFreq) {
			return freq == 0 ? 0 : 1;
		}
	},
	
	LOGARITHMIC {
		@Override
		public boolean requiresMaxFrequency() {
			return false;
		}

		@Override
		protected double getTF(long freq, long maxFreq) {
			return Math.log(freq + 1);
		}
	},
	
	AUGMENTED {
		@Override
		public boolean requiresMaxFrequency() {
			return true;
		}

		@Override
		protected double getTF(long freq, long maxFreq) {
			return 0.5 + (0.5 * freq) / maxFreq;
		}
	};

	@Override
	public double getScore(long freq, long docFreq, long maxFreq, long docLength, long numDocs, long corpusLength) {
		double tf = getTF(freq, maxFreq);
		double idf = getIDF(docFreq, numDocs);
		return tf * idf;
	}
	
	protected abstract double getTF(long freq, long maxFreq);
	
	private static double getIDF(long docFreq, long numDocs) {
		return Math.log(((double) numDocs) / docFreq);
	}

	@Override
	public boolean requiresDocumentFrequency() {
		return true;
	}

	@Override
	public boolean requiresNumberOfDocuments() {
		return true;
	}

	@Override
	public boolean requiresDocumentLength() {
		return false;
	}

	@Override
	public boolean requiresCorpusLength() {
		return false;
	}
}
