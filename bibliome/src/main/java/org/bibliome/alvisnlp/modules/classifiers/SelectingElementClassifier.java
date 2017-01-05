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


package org.bibliome.alvisnlp.modules.classifiers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import org.bibliome.util.streams.TargetStream;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.AttributeSelection;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

@AlvisNLPModule
public class SelectingElementClassifier extends ElementClassifier {
	private String evaluator;
	private String[] evaluatorOptions;
	private String search;
	private String[] searchOptions;

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ProcessingException {
		try {
			Logger logger = getLogger(ctx);
	        EvaluationContext evalCtx = new EvaluationContext(logger);
			IdentifiedInstances<Element> trainingSet = getTrainingSet(ctx, corpus, evalCtx, false);
			String selection = selectAttributes(ctx, trainingSet);
			printResult(ctx, selection);
		}
		catch (FileNotFoundException fnfe) {
			rethrow(fnfe);
		}
		catch (Exception e) {
			rethrow(e);
		}
	}

	@TimeThis(task="select-attributes")
	protected String selectAttributes(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, IdentifiedInstances<Element> trainingSet) throws Exception {
		ASEvaluation eval = ASEvaluation.forName(evaluator, evaluatorOptions);
		return AttributeSelection.SelectAttributes(eval, getEvalOptions(), trainingSet);
	}
	
	@TimeThis(task="write-selection", category=TimerCategory.EXPORT)
	protected void printResult(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, String selection) throws IOException {
		try (PrintStream ps = getEvaluationFile().getPrintStream()) {
			ps.print(selection);
		}
	}
	
    private String[] getEvalOptions() {
        if (search == null)
            return null;
        if (searchOptions == null)
            return new String[] { "-s", search };
        String[] result = new String[searchOptions.length + 2];
        result[0] = "-s";
        result[1] = search;
        System.arraycopy(searchOptions, 0, result, 2, searchOptions.length);
        return result;
    }

	@Override
	@Param
	public TargetStream getEvaluationFile() {
		return super.getEvaluationFile();
	}

	@Param
	public String getEvaluator() {
		return evaluator;
	}
	
	@Param(mandatory=false)
	public String[] getEvaluatorOptions() {
		return evaluatorOptions;
	}
	
	@Param(mandatory=false)
	public String getSearch() {
		return search;
	}
	
	@Param(mandatory=false)
	public String[] getSearchOptions() {
		return searchOptions;
	}
	
	public void setEvaluator(String evaluator) {
		this.evaluator = evaluator;
	}
	
	public void setEvaluatorOptions(String[] evaluatorOptions) {
		this.evaluatorOptions = evaluatorOptions;
	}
	
	public void setSearch(String search) {
		this.search = search;
	}
	
	public void setSearchOptions(String[] searchOptions) {
		this.searchOptions = searchOptions;
	}
}
