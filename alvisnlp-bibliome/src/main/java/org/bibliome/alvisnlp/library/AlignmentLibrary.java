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

import java.util.List;

import org.bibliome.util.pairing.AlignmentProbability;
import org.bibliome.util.pairing.AlignmentScore;
import org.bibliome.util.pairing.ClassicAlignmentScore;
import org.bibliome.util.pairing.NeedlemanWunsch;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.AbstractDoubleEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

@Library("align")
public abstract class AlignmentLibrary extends FunctionLibrary {
	@Function
	public static Evaluator score(Evaluator gap, Evaluator match, Evaluator a, Evaluator b) {
		return new NeedlemanWunschEvaluator(false, gap, match, a, b);
	}
	
	@Function
	public static Evaluator proba(Evaluator gap, Evaluator match, Evaluator a, Evaluator b) {
		return new NeedlemanWunschEvaluator(true, gap, match, a, b);
	}
	
	private static final class NeedlemanWunschEvaluator extends AbstractDoubleEvaluator {
		private final boolean probability;
		private final Evaluator gap;
		private final Evaluator match;
		private final Evaluator a;
		private final Evaluator b;
		private final ComparatorContext comparatorContext;
		
		private NeedlemanWunschEvaluator(boolean probability, Evaluator gap, Evaluator match, Evaluator a, Evaluator b, ComparatorContext comparatorContext) {
			super();
			this.probability = probability;
			this.gap = gap;
			this.match = match;
			this.a = a;
			this.b = b;
			this.comparatorContext = comparatorContext;
		}

		private NeedlemanWunschEvaluator(boolean probability, Evaluator gap, Evaluator match, Evaluator a, Evaluator b) {
			this(probability, gap, match, a, b, new ComparatorContext());
		}

		@Override
		public double evaluateDouble(EvaluationContext ctx, Element elt) {
			List<Element> a = this.a.evaluateList(ctx, elt);
			List<Element> b = this.b.evaluateList(ctx, elt);
			AlignmentScore<Element,Element> alignmentScore = getScore(ctx, elt);
			NeedlemanWunsch<Element,Element> matrix = new NeedlemanWunsch<Element,Element>(a, b, alignmentScore);
			matrix.solve();
			return matrix.getScore();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			gap.collectUsedNames(nameUsage, defaultType);
			match.collectUsedNames(nameUsage, defaultType);
			a.collectUsedNames(nameUsage, defaultType);
			b.collectUsedNames(nameUsage, defaultType);
		}

		private AlignmentScore<Element,Element> getScore(EvaluationContext ctx, Element elt) {
			double gap = this.gap.evaluateDouble(ctx, elt);
			if (probability)
				return new ProbabilityScore(ctx, gap);
			return new AdditiveScore(ctx, gap);
		}
		
		private final class ProbabilityScore extends AlignmentProbability<Element,Element> {
			private final EvaluationContext ctx;
			private final double gapValue;

			private ProbabilityScore(EvaluationContext ctx, double gap) {
				super(true);
				this.ctx = ctx;
				this.gapValue = gap;
			}

			@Override
			public double getScore(Element a, Element b) {
				return comparatorContext.evaluateDouble(match, ctx, a, b);
			}

			@Override
			public double getGap() {
				return gapValue;
			}
		}
		
		private final class AdditiveScore extends ClassicAlignmentScore<Element,Element> {
			private final EvaluationContext ctx;
			private final double gapValue;

			private AdditiveScore(EvaluationContext ctx, double gap) {
				super(0);
				this.ctx = ctx;
				this.gapValue = gap;
			}

			@Override
			public double getScore(Element a, Element b) {
				return comparatorContext.evaluateDouble(match, ctx, a, b);
			}

			@Override
			public double getGap() {
				return gapValue;
			}
		}
	}
}
