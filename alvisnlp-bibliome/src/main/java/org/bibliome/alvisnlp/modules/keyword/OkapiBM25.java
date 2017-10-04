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

public class OkapiBM25 implements KeywordScoreFunction {
	private final double k1;
	private final double b;
	
	public OkapiBM25(double k1, double b) {
		super();
		this.k1 = k1;
		this.b = b;
	}

	@Override
	public boolean requiresDocumentFrequency() {
		return true;
	}

	@Override
	public boolean requiresMaxFrequency() {
		return false;
	}

	@Override
	public boolean requiresDocumentLength() {
		return true;
	}

	@Override
	public boolean requiresNumberOfDocuments() {
		return true;
	}

	@Override
	public boolean requiresCorpusLength() {
		return true;
	}

	@Override
	public double getScore(long freq, long docFreq, long maxFreq, long docLength, long numDocs, long corpusLength) {
		double idf = getIDF(docFreq, numDocs);
		return idf * (freq * (k1 + 1)) / (freq + k1 * (1 - b + b * (Math.pow(docLength,2) / corpusLength)));
	}

	private static double getIDF(long docFreq, long numDocs) {
		return Math.log((numDocs - docFreq + 0.5)/(docFreq + 0.5));
	}
}
