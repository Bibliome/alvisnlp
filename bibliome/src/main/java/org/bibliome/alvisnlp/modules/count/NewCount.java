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


package org.bibliome.alvisnlp.modules.count;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.alvisnlp.modules.aggregate.AggregateValues;
import org.bibliome.util.Iterators;
import org.bibliome.util.count.Count;
import org.bibliome.util.count.CountStats;
import org.bibliome.util.count.TfIdf;
import org.bibliome.util.count.TfIdfDocuments;
import org.bibliome.util.count.TfIdfStats;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(obsoleteUseInstead=AggregateValues.class)
public class NewCount extends CorpusModule<ResolvedObjects> {
	private Expression documents;
	private Expression target;
	private String featureKey;
    private TargetStream countFile;
    private TargetStream tfidfFile;
	private Boolean headers = false;
	
	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		LibraryResolver resolver = getLibraryResolver(ctx);
		Evaluator documents = resolver.resolveNullable(this.documents);
		Evaluator target = resolver.resolveNullable(this.target);
		CountStats<String> stats = new CountStats<String>(new HashMap<String,Count>());
		TfIdfDocuments<Element> tfidf = new TfIdfDocuments<Element>(new HashMap<Element,TfIdfStats>());
		
        EvaluationContext evalCtx = new EvaluationContext(logger);
		for (Element doc : Iterators.loop(documents.evaluateElements(evalCtx, corpus))) {
			for (Element elt : Iterators.loop(target.evaluateElements(evalCtx, doc))) {
				if (!elt.hasFeature(featureKey))
					continue;
				String value = elt.getLastFeature(featureKey);
				stats.incr(value);
				tfidf.incr(doc, value);
			}
		}
		
		try {
			if (countFile != null) {
				PrintStream out = countFile.getPrintStream();
				if (headers)
					out.println("TERM\tOCCURRENCES\n");
				for (Map.Entry<String,Count> e : stats.entryList(true))
					out.println(e.getKey() + '\t' + e.getValue().get());
				out.close();
			}
			
			if (tfidfFile != null) {
				PrintStream out = tfidfFile.getPrintStream();
				if (headers)
					out.println("DOCUMENT\tTERM\tTF\tIDF\tTFIDF");
				for (Map.Entry<Element,TfIdfStats> e1 : tfidf.entrySet()) {
					Element grp = e1.getKey();
					Comparator<TfIdf> comparator = new TfIdf.TfIdfComparator<TfIdf>();
					for (Map.Entry<String,TfIdf> e2 : e1.getValue().entryList(comparator)) {
						TfIdf c = e2.getValue();
						out.println(grp.toString() + '\t' + e2.getKey() + '\t' + c.getTf() + '\t' + c.getIdf() + '\t' + c.getTfIdf());
					}
				}
			}
		}
		catch (IOException ioe) {
			rethrow(ioe);
		}
	}

	@Param
	public Expression getDocuments() {
		return documents;
	}

	@Param
	public Expression getTarget() {
		return target;
	}

	@Param(nameType=NameType.FEATURE)
	public String getFeatureKey() {
		return featureKey;
	}

	@Param(mandatory=false)
	public TargetStream getCountFile() {
		return countFile;
	}

	@Param(mandatory=false)
	public TargetStream getTfidfFile() {
		return tfidfFile;
	}

	@Param
	public Boolean getHeaders() {
		return headers;
	}

	public void setDocuments(Expression documents) {
		this.documents = documents;
	}

	public void setTarget(Expression target) {
		this.target = target;
	}

	public void setFeatureKey(String featureKey) {
		this.featureKey = featureKey;
	}

	public void setCountFile(TargetStream countFile) {
		this.countFile = countFile;
	}

	public void setTfidfFile(TargetStream tfidfFile) {
		this.tfidfFile = tfidfFile;
	}

	public void setHeaders(Boolean headers) {
		this.headers = headers;
	}
}
