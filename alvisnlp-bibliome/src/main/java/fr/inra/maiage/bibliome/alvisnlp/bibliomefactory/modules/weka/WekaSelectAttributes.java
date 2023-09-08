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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.weka;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.streams.TargetStream;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.AttributeSelection;

@AlvisNLPModule
public class WekaSelectAttributes extends ElementClassifier {
	private String evaluator;
	private String[] evaluatorOptions;
	private String search;
	private String[] searchOptions;

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ProcessingException {
		try {
			Logger logger = getLogger(ctx);
	        EvaluationContext evalCtx = new EvaluationContext(logger);
			IdentifiedInstances<Element> trainingSet = getTrainingSet(ctx, corpus, evalCtx, false);
			String selection = selectAttributes(ctx, trainingSet);
			printResult(ctx, selection);
		}
		catch (Exception e) {
			throw new ProcessingException(e);
		}
	}

	@TimeThis(task="select-attributes")
	protected String selectAttributes(ProcessingContext ctx, IdentifiedInstances<Element> trainingSet) throws Exception {
		ASEvaluation eval = ASEvaluation.forName(evaluator, evaluatorOptions);
		return AttributeSelection.SelectAttributes(eval, getEvalOptions(), trainingSet);
	}

	@TimeThis(task="write-selection", category=TimerCategory.EXPORT)
	protected void printResult(ProcessingContext ctx, String selection) throws IOException {
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
