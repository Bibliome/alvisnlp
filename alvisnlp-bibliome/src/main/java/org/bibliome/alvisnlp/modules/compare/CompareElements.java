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


package org.bibliome.alvisnlp.modules.compare;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.alvisnlp.modules.compare.CompareElements.CompareElementsResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule
public class CompareElements extends CorpusModule<CompareElementsResolvedObjects> {
	private ElementSimilarity similarity;
	private Expression sections;
	private Expression reference;
	private Expression predicted;
	private TargetStream outFile;
	private Expression face;
	private Boolean showRecall = true;
	private Boolean showPrecision = true;
	private Boolean showFullMatches = true;
	
	static class CompareElementsResolvedObjects extends ResolvedObjects {
		private final Evaluator sections;
		private final Evaluator reference;
		private final Evaluator predicted;
		private final Evaluator face;

		public CompareElementsResolvedObjects(ProcessingContext<Corpus> ctx, CompareElements module) throws ResolverException {
			super(ctx, module);
			sections = rootResolver.resolveNullable(module.sections);
			reference = rootResolver.resolveNullable(module.reference);
			predicted = rootResolver.resolveNullable(module.predicted);
			face = rootResolver.resolveNullable(module.face);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			sections.collectUsedNames(nameUsage, defaultType);
			reference.collectUsedNames(nameUsage, defaultType);
			predicted.collectUsedNames(nameUsage, defaultType);
			face.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Override
	protected CompareElementsResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new CompareElementsResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		CompareElementsResolvedObjects resObj = getResolvedObjects();
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		double globalReferenceScore = 0;
		int nReference = 0;
		double globalPredictedScore = 0;
		int nPredicted = 0;
		PrintStream out = getOutStream();
		for (Element elt : Iterators.loop(resObj.sections.evaluateElements(evalCtx, corpus))) {
			List<ElementMatch> referenceMatch = match(evalCtx, elt, resObj.reference, resObj.predicted);
			List<ElementMatch> predictedMatch = match(evalCtx, elt, resObj.predicted, resObj.reference);
			nReference += referenceMatch.size();
			nPredicted += predictedMatch.size();
			double referenceScore = sum(referenceMatch);
			double predictedScore = sum(predictedMatch);
			globalReferenceScore += referenceScore;
			globalPredictedScore += predictedScore;
			double recall = referenceScore / referenceMatch.size();
			double precision = predictedScore / predictedMatch.size();
			double fScore = fScore(precision, recall);
			
			out.println(elt.toString());
			if (showRecall) {
				out.println("    Reference matches (" + referenceMatch.size() + ") :");
				printMatches(out, evalCtx, referenceMatch);
			}
			if (showPrecision) {
				out.println("    Predicted matches (" + predictedMatch.size() + ") :");
				printMatches(out, evalCtx, predictedMatch);
			}
			if (showRecall)
				out.println("    Recall   : " + recall);
			if (showPrecision)
				out.println("    Precision: " + precision);
			if (showRecall && showPrecision)
				out.println("    F-Score  : " + fScore);
			out.println();
			out.println();
		}
		double globalRecall = globalReferenceScore / nReference;
		double globalPrecision = globalPredictedScore / nPredicted;
		double globalFScore = fScore(globalRecall, globalPrecision);
		out.println("Global results");
		if (showRecall)
			out.println("    Recall   : " + globalRecall);
		if (showPrecision)
			out.println("    Precision: " + globalPrecision);
		if (showRecall && showPrecision)
			out.println("    F-Score  : " + globalFScore);
		out.close();
	}
	
	private PrintStream getOutStream() throws ProcessingException {
		try {
			return outFile.getPrintStream();
		}
		catch (IOException ioe) {
			rethrow(ioe);
		}
		return null;
	}

	private void printMatches(PrintStream out, EvaluationContext evalCtx, List<ElementMatch> l) {
		CompareElementsResolvedObjects resObj = getResolvedObjects();
		for (ElementMatch m : l) {
			if ((m.getScore() == 1) && !showFullMatches) {
				continue;
			}
			out.printf("        [%.4f] ", m.getScore());
			out.println(resObj.face.evaluateString(evalCtx, m.getFirst()));
			out.print("                 ");
			if (m.getSecond() == null)
				out.println("FAIL");
			else
				out.println(resObj.face.evaluateString(evalCtx, m.getSecond()));
		}
		out.println();
	}

	private List<ElementMatch> match(EvaluationContext evalCtx, Element elt, Evaluator expr1, Evaluator expr2) {
		List<ElementMatch> result = new ArrayList<ElementMatch>();
		for (Element e1 : Iterators.loop(expr1.evaluateElements(evalCtx, elt))) {
			ElementMatch m = new ElementMatch(e1);
			m.searchMatch(expr2.evaluateElements(evalCtx, elt), similarity);
			result.add(m);
		}
		return result;
	}
	
	private static double sum(List<ElementMatch> l) {
		double result = 0;
		for (ElementMatch m : l)
			result += m.getScore();
		return result;
	}
	
	private static double fScore(double recall, double precision) {
		return 2 * recall * precision / (recall + precision);
	}

	@Param
	public ElementSimilarity getSimilarity() {
		return similarity;
	}

	@Param
	public Expression getSections() {
		return sections;
	}

	@Param
	public Expression getReference() {
		return reference;
	}

	@Param
	public Expression getPredicted() {
		return predicted;
	}

	@Param
	public TargetStream getOutFile() {
		return outFile;
	}

	@Param
	public Expression getFace() {
		return face;
	}

	@Param
	public Boolean getShowRecall() {
		return showRecall;
	}

	@Param	
	public Boolean getShowPrecision() {
		return showPrecision;
	}

	@Param
	public Boolean getShowFullMatches() {
		return showFullMatches;
	}

	public void setShowFullMatches(Boolean showFullMatches) {
		this.showFullMatches = showFullMatches;
	}

	public void setShowRecall(Boolean showRecall) {
		this.showRecall = showRecall;
	}

	public void setShowPrecision(Boolean showPrecision) {
		this.showPrecision = showPrecision;
	}

	public void setFace(Expression face) {
		this.face = face;
	}

	public void setSimilarity(ElementSimilarity similarity) {
		this.similarity = similarity;
	}

	public void setSections(Expression sections) {
		this.sections = sections;
	}

	public void setReference(Expression reference) {
		this.reference = reference;
	}

	public void setPredicted(Expression predicted) {
		this.predicted = predicted;
	}

	public void setOutFile(TargetStream outFile) {
		this.outFile = outFile;
	}
}
