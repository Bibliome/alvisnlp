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

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.ElementVisitor;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;

@Library("type")
public abstract class TypeLibrary extends FunctionLibrary {
	private static final ElementVisitor<String,Void> GET_TYPE_VISITOR = new ElementVisitor<String,Void>() {
		@Override
		public String visit(Annotation a, Void param) {
			return "A";
		}

		@Override
		public String visit(Corpus corpus, Void param) {
			return "C";
		}

		@Override
		public String visit(Document doc, Void param) {
			return "D";
		}

		@Override
		public String visit(Relation rel, Void param) {
			return "R";
		}

		@Override
		public String visit(Section sec, Void param) {
			return "S";
		}

		@Override
		public String visit(Tuple t, Void param) {
			return "T";
		}

		@Override
		public String visit(Element e, Void param) {
			return e.getClass().getCanonicalName();
		}
	};
	
	private static enum TestType implements ElementVisitor<Boolean,Void> {
		CORPUS(true, false, false, false, false, false),
		DOCUMENT(false, true, false, false, false, false),
		SECTION(false, false, true, false, false, false),
		ANNOTATION(false, false, false, true, false, false),
		RELATION(false, false, false, false, true, false),
		TUPLE(false, false, false, false, false, true);
		
		private final boolean corpus;
		private final boolean document;
		private final boolean section;
		private final boolean annotation;
		private final boolean relation;
		private final boolean tuple;
		
		private TestType(boolean corpus, boolean document, boolean section, boolean annotation, boolean relation, boolean tuple) {
			this.corpus = corpus;
			this.document = document;
			this.section = section;
			this.annotation = annotation;
			this.relation = relation;
			this.tuple = tuple;
		}

		@Override
		public Boolean visit(Annotation a, Void param) {
			return annotation;
		}

		@Override
		public Boolean visit(Corpus corpus, Void param) {
			return this.corpus;
		}

		@Override
		public Boolean visit(Document doc, Void param) {
			return document;
		}

		@Override
		public Boolean visit(Relation rel, Void param) {
			return relation;
		}

		@Override
		public Boolean visit(Section sec, Void param) {
			return section;
		}

		@Override
		public Boolean visit(Tuple t, Void param) {
			return tuple;
		}
		
		@Override
		public Boolean visit(Element e, Void param) {
			return false;
		}

		private static TestType get(char c) {
			switch (c) {
			case 'c':
			case 'C':
				return TestType.CORPUS;
			case 'd':
			case 'D':
				return TestType.DOCUMENT;
			case 's':
			case 'S':
				return TestType.SECTION;
			case 'a':
			case 'A':
				return TestType.ANNOTATION;
			case 'r':
			case 'R':
				return TestType.RELATION;
			case 't':
			case 'T':
				return TestType.TUPLE;
			}
			return null;
		}
	}
	
	@Function
	public static final String get(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		return elt.accept(GET_TYPE_VISITOR, null);
	}
	
	@Function(ftors=1)
	public static final boolean test(@SuppressWarnings("unused") EvaluationContext ctx, Element elt, String t) {
		if (t.isEmpty())
			return false;
		TestType test = TestType.get(t.charAt(0));
		if (test == null)
			return false;
		return elt.accept(test, null);
	}
	
	@Function
	public static final boolean corpus(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		return elt.accept(TestType.CORPUS, null);
	}
	
	@Function
	public static final boolean document(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		return elt.accept(TestType.DOCUMENT, null);
	}
	
	@Function
	public static final boolean section(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		return elt.accept(TestType.SECTION, null);
	}
	
	@Function
	public static final boolean annotation(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		return elt.accept(TestType.ANNOTATION, null);
	}
	
	@Function
	public static final boolean relation(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		return elt.accept(TestType.RELATION, null);
	}
	
	@Function
	public static final boolean tuple(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		return elt.accept(TestType.TUPLE, null);
	}
}
