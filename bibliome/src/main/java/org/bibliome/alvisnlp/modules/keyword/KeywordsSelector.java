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

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.DefaultExpressions;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.alvisnlp.modules.keyword.KeywordsSelector.KeywordSelectorResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.count.Count;
import org.bibliome.util.count.CountStats;
import org.bibliome.util.count.Stats;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

@AlvisNLPModule(beta=true)
public class KeywordsSelector extends CorpusModule<KeywordSelectorResolvedObjects> {
	private Expression documents = DefaultExpressions.CORPUS_DOCUMENTS;
	private Expression documentId = DefaultExpressions.DOCUMENT_ID;
	private Expression keywords = DefaultExpressions.DOCUMENT_WORDS;
	private Expression keywordForm = DefaultExpressions.ANNOTATION_FORM;
	private TargetStream outFile;
	private String charset = "UTF-8";
	private Character separator = '\t';
	private Integer keywordCount = Integer.MAX_VALUE;
	private Double scoreThreshold = 0.0;
	private String keywordFeature;
	private String scoreFeature;
	private KeywordScoreFunction scoreFunction = Frequency.ABSOLUTE;
	
	static class KeywordSelectorResolvedObjects extends ResolvedObjects {
		private final Evaluator documents;
		private final Evaluator documentId;
		private final Evaluator keywords;
		private final Evaluator keywordForm;
		
		private KeywordSelectorResolvedObjects(ProcessingContext<Corpus> ctx, KeywordsSelector module) throws ResolverException {
			super(ctx, module);
			documents = module.documents.resolveExpressions(rootResolver);
			keywords = module.keywords.resolveExpressions(rootResolver);
			keywordForm = module.keywordForm.resolveExpressions(rootResolver);
			documentId = module.documentId.resolveExpressions(rootResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			documents.collectUsedNames(nameUsage, defaultType);
			keywords.collectUsedNames(nameUsage, defaultType);
			keywordForm.collectUsedNames(nameUsage, defaultType);
			documentId.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Override
	protected KeywordSelectorResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new KeywordSelectorResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try (PrintStream out = getPrintStream()) {
			Logger logger = getLogger(ctx);
			EvaluationContext evalCtx = new EvaluationContext(logger);
			
			Stats<String,Count> docFreq = getDocumentFrequencies(evalCtx, corpus);
			long numDocs = getNumberOfDocuments(evalCtx, corpus);
			long corpusLength = getCorpusLength(evalCtx, corpus);
			KeywordSelectorResolvedObjects resObj = getResolvedObjects();
			for (Element doc : Iterators.loop(resObj.documents.evaluateElements(evalCtx, corpus))) {
				Stats<String,Count> freq = getFrequencies(evalCtx, doc);
				long maxFreq = getMaxFrequency(freq);
				long docLength = scoreFunction.requiresDocumentLength() ? freq.sum() : 0;
				List<KeywordScore> keywordRank = rankKeywords(freq, docFreq, maxFreq, docLength, numDocs, corpusLength);
				handleKeywords(ctx, evalCtx, out, doc, keywordRank);
			}
		}
		catch (IOException e) {
			rethrow(e);
		}
	}
	
	private PrintStream getPrintStream() throws IOException {
		if (outFile == null) {
			return null;
		}
		return outFile.getPrintStream();
	}
	
	private void handleKeywords(ProcessingContext<Corpus> ctx, EvaluationContext evalCtx, PrintStream out, Element doc, List<KeywordScore> keywordRank) {
		if (out != null) {
			writeKeywords(ctx, evalCtx, out, doc, keywordRank);
		}
		if (keywordFeature != null || scoreFeature != null) {
			selectKeywords(doc, keywordRank);
		}
	}
	
	@TimeThis(category=TimerCategory.EXPORT, task="write-keywords")
	protected void writeKeywords(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, EvaluationContext evalCtx, PrintStream out, Element doc, List<KeywordScore> keywordRank) {
		KeywordSelectorResolvedObjects resObj = getResolvedObjects();
		String documentId = resObj.documentId.evaluateString(evalCtx, doc);
		String prefix = documentId + separator;
		for (KeywordScore score : keywordRank) {
			String kw = score.getKeyword();
			double d = score.getScore();
			out.println(prefix + kw + separator + d);
		}
	}

	private void selectKeywords(Element doc, List<KeywordScore> keywordRank) {
		int n = 0;
		for (KeywordScore score : keywordRank) {
			if (n >= keywordCount) {
				break;
			}
			if (score.getScore() < scoreThreshold) {
				break;
			}
			doc.addFeature(keywordFeature, score.getKeyword());
			doc.addFeature(scoreFeature, Double.toString(score.getScore()));
			n++;
		}
	}

	private long getCorpusLength(EvaluationContext evalCtx, Corpus corpus) {
		long result = 0;
		if (scoreFunction.requiresCorpusLength()) {
			KeywordSelectorResolvedObjects resObj = getResolvedObjects();
			for (Element doc : Iterators.loop(resObj.documents.evaluateElements(evalCtx, corpus))) {
				result += resObj.keywords.evaluateInt(evalCtx, doc);
			}
		}
		return result;
	}

	private long getNumberOfDocuments(EvaluationContext evalCtx, Corpus corpus) {
		if (scoreFunction.requiresNumberOfDocuments()) {
			KeywordSelectorResolvedObjects resObj = getResolvedObjects();
			return Iterators.count(resObj.documents.evaluateElements(evalCtx, corpus));
		}
		return 0;
	}

	private List<KeywordScore> rankKeywords(Stats<String,Count> freq, Stats<String,Count> docFreq, long maxFreq, long docLength, long numDocs, long corpusLength) {
		List<KeywordScore> selectScore = new ArrayList<KeywordScore>();
		for (Map.Entry<String,Count> e : freq.entrySet()) {
			String kw = e.getKey();
			long f = e.getValue().get();
			long df = docFreq.safeGet(kw, false).get();
			double s = scoreFunction.getScore(f, df, maxFreq, docLength, numDocs, corpusLength);
			KeywordScore score = new KeywordScore(e.getKey(), s);
			selectScore.add(score);
		}
		Collections.sort(selectScore, KeywordScore.SCORE_COMPARATOR);
		return selectScore;
	}
	
	private long getMaxFrequency(Stats<String,Count> freq) {
		long result = 0;
		if (scoreFunction.requiresMaxFrequency()) {
			for (Count c : freq.values()) {
				result = Math.max(result, c.get());
			}
		}
		return result;
	}
	
	private Stats<String,Count> getFrequencies(EvaluationContext evalCtx, Element doc) {
		Stats<String,Count> result = new CountStats<String>(new HashMap<String,Count>());
		KeywordSelectorResolvedObjects resObj = getResolvedObjects();
		for (Element kw : Iterators.loop(resObj.keywords.evaluateElements(evalCtx, doc))) {
			String kwForm = resObj.keywordForm.evaluateString(evalCtx, kw);
			result.incr(kwForm);
		}
		return result;
	}
	
	private Stats<String,Count> getDocumentFrequencies(EvaluationContext evalCtx, Corpus corpus) {
		Stats<String,Count> result = new CountStats<String>(new HashMap<String,Count>());
		KeywordSelectorResolvedObjects resObj = getResolvedObjects();
		if (scoreFunction.requiresDocumentFrequency()) {
			for (Element doc : Iterators.loop(resObj.documents.evaluateElements(evalCtx, corpus))) {
				Collection<String> docKeywords = new HashSet<String>();
				for (Element kw : Iterators.loop(resObj.keywords.evaluateElements(evalCtx, doc))) {
					String kwForm = resObj.keywordForm.evaluateString(evalCtx, kw);
					docKeywords.add(kwForm);
				}
				result.incrAll(docKeywords);
			}
		}
		return result;
	}

	@Param
	public Expression getKeywords() {
		return keywords;
	}

	@Param
	public Expression getKeywordForm() {
		return keywordForm;
	}

	@Param
	public Integer getKeywordCount() {
		return keywordCount;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getKeywordFeature() {
		return keywordFeature;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getScoreFeature() {
		return scoreFeature;
	}

	@Param
	public KeywordScoreFunction getScoreFunction() {
		return scoreFunction;
	}

	@Param
	public Expression getDocuments() {
		return documents;
	}

	@Param
	public Double getScoreThreshold() {
		return scoreThreshold;
	}

	@Param
	public String getCharset() {
		return charset;
	}

	@Param
	public Character getSeparator() {
		return separator;
	}

	@Param
	public Expression getDocumentId() {
		return documentId;
	}

	@Param(mandatory=false)
	public TargetStream getOutFile() {
		return outFile;
	}

	public void setDocumentId(Expression documentId) {
		this.documentId = documentId;
	}

	public void setOutFile(TargetStream outFile) {
		this.outFile = outFile;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setSeparator(Character separator) {
		this.separator = separator;
	}

	public void setScoreThreshold(Double scoreThreshold) {
		this.scoreThreshold = scoreThreshold;
	}

	public void setDocuments(Expression documents) {
		this.documents = documents;
	}

	public void setKeywords(Expression keywords) {
		this.keywords = keywords;
	}

	public void setKeywordForm(Expression keywordForm) {
		this.keywordForm = keywordForm;
	}

	public void setKeywordCount(Integer keywordCount) {
		this.keywordCount = keywordCount;
	}

	public void setKeywordFeature(String keywordFeature) {
		this.keywordFeature = keywordFeature;
	}

	public void setScoreFeature(String scoreFeature) {
		this.scoreFeature = scoreFeature;
	}

	public void setScoreFunction(KeywordScoreFunction scoreFunction) {
		this.scoreFunction = scoreFunction;
	}
}
