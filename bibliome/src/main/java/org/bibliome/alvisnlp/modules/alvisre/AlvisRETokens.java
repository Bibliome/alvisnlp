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


package org.bibliome.alvisnlp.modules.alvisre;

import java.io.PrintStream;

import org.bibliome.alvisnlp.modules.DefaultExpressions;
import org.bibliome.alvisnlp.modules.alvisre.AlvisREAnnotations.ResolvedAlvisREAnnotation;
import org.bibliome.alvisnlp.modules.alvisre.AlvisRETokens.Resolved;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.ConstantsLibrary;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

public class AlvisRETokens extends AlvisREAnnotations<Resolved> {
	private final Expression form;
	private final Expression[] layers;
	private final Expression start;
	private final Expression end;

	public AlvisRETokens(Expression items, Expression type, Expression form, Expression[] layers, Expression start, Expression end) {
		super(items, type);
		this.form = form;
		this.layers = layers;
		this.start = start;
		this.end = end;
	}

	static AlvisRETokens getAnnotationAlvisRETokens(Expression items, Expression type, Expression[] layers) {
		Expression form = DefaultExpressions.ANNOTATION_FORM;
		Expression start = DefaultExpressions.ANNOTATION_START;
		Expression end = DefaultExpressions.ANNOTATION_END;
		return new AlvisRETokens(items, type, form, layers, start, end);
	}
	
	static AlvisRETokens getSentencesAlvisRETokens() {
		Expression items = DefaultExpressions.SECTION_SENTENCES;
		Expression type = ConstantsLibrary.create("Sentence");
		Expression[] layers = new Expression[] {};
		return getAnnotationAlvisRETokens(items, type, layers);
	}
	
	static AlvisRETokens getWordsAlvisRETokens() {
		Expression items = DefaultExpressions.SECTION_WORDS;
		Expression type = ConstantsLibrary.create("Word");
		Expression[] layers = new Expression[] {
				DefaultExpressions.WORD_POS,
				DefaultExpressions.WORD_LEMMA
		};
		return getAnnotationAlvisRETokens(items, type, layers);
	}
	
	@Override
	public Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new Resolved(resolver, this);
	}

	public static class Resolved extends ResolvedAlvisREAnnotation {
		private final Evaluator form;
		private final Evaluator[] layers;
		private final Evaluator start;
		private final Evaluator end;
		
		private Resolved(LibraryResolver resolver, AlvisRETokens tokens) throws ResolverException {
			super(resolver, tokens);
			this.form = tokens.form.resolveExpressions(resolver);
			this.layers = resolver.resolveArray(tokens.layers, Evaluator.class);
			this.start = tokens.start.resolveExpressions(resolver);
			this.end = tokens.end.resolveExpressions(resolver);
		}

		@Override
		protected String addElement(SectionsMerger merger, Element elt) {
			return merger.addToken(elt);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			form.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesArray(layers, defaultType);
			start.collectUsedNames(nameUsage, defaultType);
			end.collectUsedNames(nameUsage, defaultType);
		}
		
		@Override
		protected void printInfo(PrintStream out, SectionsMerger merger, EvaluationContext ctx, Element elt) {
			int start = this.start.evaluateInt(ctx, elt);
			int end = this.end.evaluateInt(ctx, elt);
			String form = this.form.evaluateString(ctx, elt);
			out.format(" %d %d\t%s", merger.correctOffset(start), merger.correctOffset(end), form.replace('\n', ' '));
			for (Evaluator l : layers) {
				String ls = l.evaluateString(ctx, elt);
				out.print('|');
				out.print(ls);
			}
		}
	}
}
