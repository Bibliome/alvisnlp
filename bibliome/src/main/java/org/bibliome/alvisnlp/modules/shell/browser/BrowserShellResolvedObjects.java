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


package org.bibliome.alvisnlp.modules.shell.browser;

import java.io.Reader;
import java.io.StringReader;

import org.bibliome.alvisnlp.converters.expression.parser.ExpressionParser;
import org.bibliome.alvisnlp.converters.expression.parser.ParseException;
import org.bibliome.alvisnlp.modules.ResolvedObjects;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.ElementVisitor;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ProcessingContext;

public class BrowserShellResolvedObjects extends ResolvedObjects {
	private final EvaluationContext evalCtx;
	private final Evaluator corpusLabel;
	private final Evaluator documentLabel;
	private final Evaluator sectionLabel;
	private final Evaluator annotationLabel;
	private final Evaluator relationLabel;
	private final Evaluator tupleLabel;
	private final Evaluator argumentLabelPrefix;

	BrowserShellResolvedObjects(ProcessingContext<Corpus> ctx, BrowserShell module) throws ResolverException {
		super(ctx, module);
		this.evalCtx = new EvaluationContext(module.getLogger(ctx));
		this.corpusLabel = module.getCorpusLabel().resolveExpressions(rootResolver);
		this.documentLabel = module.getDocumentLabel().resolveExpressions(rootResolver);
		this.sectionLabel = module.getSectionLabel().resolveExpressions(rootResolver);
		this.annotationLabel = module.getAnnotationLabel().resolveExpressions(rootResolver);
		this.relationLabel = module.getRelationLabel().resolveExpressions(rootResolver);
		this.tupleLabel = module.getTupleLabel().resolveExpressions(rootResolver);
		this.argumentLabelPrefix = module.getArgumentLabelPrefix().resolveExpressions(rootResolver);
	}
	
	Evaluator parseAndResolve(String exprStr) throws ResolverException, ParseException {
		Reader reader = new StringReader(exprStr);
		ExpressionParser parser = new ExpressionParser(reader);
		Expression expr = parser.top();
		return expr.resolveExpressions(rootResolver);
	}

	EvaluationContext getEvalCtx() {
		return evalCtx;
	}

	String getElementNodeLabel(Element elt) {
		return elt.accept(ElementNodeLabel.INSTANCE, this);
	}

	private static enum ElementNodeLabel implements ElementVisitor<String,BrowserShellResolvedObjects> {
		INSTANCE;

		@Override
		public String visit(Annotation a, BrowserShellResolvedObjects param) {
			return param.annotationLabel.evaluateString(param.evalCtx, a);
		}

		@Override
		public String visit(Corpus corpus, BrowserShellResolvedObjects param) {
			return param.corpusLabel.evaluateString(param.evalCtx, corpus);
		}

		@Override
		public String visit(Document doc, BrowserShellResolvedObjects param) {
			return param.documentLabel.evaluateString(param.evalCtx, doc);
		}

		@Override
		public String visit(Relation rel, BrowserShellResolvedObjects param) {
			return param.relationLabel.evaluateString(param.evalCtx, rel);
		}

		@Override
		public String visit(Section sec, BrowserShellResolvedObjects param) {
			return param.sectionLabel.evaluateString(param.evalCtx, sec);
		}

		@Override
		public String visit(Tuple t, BrowserShellResolvedObjects param) {
			return param.tupleLabel.evaluateString(param.evalCtx, t);
		}

		@Override
		public String visit(Element e, BrowserShellResolvedObjects param) {
			return param.argumentLabelPrefix.evaluateString(param.evalCtx, e) + e.getOriginal().accept(this, param);
		}
	}
}
