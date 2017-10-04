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


package org.bibliome.alvisnlp.modules.prolog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bibliome.util.Iterators;
import org.bibliome.util.StringCat;

import alice.tuprolog.Double;
import alice.tuprolog.Int;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.lib.InvalidObjectIdException;
import alice.tuprolog.lib.JavaLibrary;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.AbstractEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.EvaluationType;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.documentation.Documentation;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

public class SolveInfoLibrary extends FunctionLibrary {
	private JavaLibrary javaLibrary;
	private SolveInfo solveInfo;

	SolveInfoLibrary() {
		super();
	}
	
	public JavaLibrary getJavaLibrary() {
		return javaLibrary;
	}

	public void setJavaLibrary(JavaLibrary javaLibrary) {
		this.javaLibrary = javaLibrary;
	}

	public SolveInfo getSolveInfo() {
		return solveInfo;
	}

	public void setSolveInfo(SolveInfo solveInfo) {
		this.solveInfo = solveInfo;
	}

	@Override
	public String getName() {
		return "goal";
	}

	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		checkExactFtors(ftors, 1);
		checkExactArity(ftors, args, 0);
		return new SolveInfoEvaluator(ftors.get(0));
	}

	private class SolveInfoEvaluator extends AbstractEvaluator {
		private final String varName;

		private SolveInfoEvaluator(String varName) {
			super();
			this.varName = varName;
		}
		
		private Term getTerm() {
			try {
				return solveInfo.getVarValue(varName);
			}
			catch (NoSolutionException e) {
				return null;
			}
		}

		@Override
		public boolean evaluateBoolean(EvaluationContext ctx, Element elt) {
			Term term = getTerm();
			if (term == null)
				return false;
			if (term.isEmptyList())
				return false;
			if (term.isList())
				return true;
			if (term.isAtom())
				return !term.toString().isEmpty();
			if (term instanceof alice.tuprolog.Number)
				return ((alice.tuprolog.Number) term).intValue() != 0;
			throw new RuntimeException();
		}

		@Override
		public int evaluateInt(EvaluationContext ctx, Element elt) {
			return termToInt(getTerm());
		}

		private int termToInt(Term term) {
			if (term == null)
				return 0;
			if (term.isList())
				return ((Struct) term).listSize();
			if (term.isAtom()) {
				try {
					return Integer.parseInt(term.toString());
				}
				catch (NumberFormatException e) {
					return 0;
				}
			}
			if (term instanceof alice.tuprolog.Number)
				return ((alice.tuprolog.Number) term).intValue();
			throw new RuntimeException();
		}
		
		@Override
		public double evaluateDouble(EvaluationContext ctx, Element elt) {
			return termToDouble(getTerm());
		}

		private double termToDouble(Term term) {
			if (term == null)
				return 0;
			if (term.isList())
				return ((Struct) term).listSize();
			if (term.isAtom()) {
				try {
					return java.lang.Double.parseDouble(term.toString());
				}
				catch (NumberFormatException e) {
					return 0;
				}
			}
			if (term instanceof alice.tuprolog.Number)
				return ((alice.tuprolog.Number) term).doubleValue();
			throw new RuntimeException();
		}

		@Override
		public String evaluateString(EvaluationContext ctx, Element elt) {
			StringCat strcat = new StringCat();
			evaluateString(ctx, elt, strcat);
			return strcat.toString();
		}

		@Override
		public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
			termToString(getTerm(), strcat);
		}

		private void termToString(Term term, StringCat strcat) {
			if (term == null)
				return;
			if (term.isEmptyList())
				return;
			if (term.isList())
				listToString((Struct) term, strcat);
			strcat.append(term.toString());
		}

		@SuppressWarnings("unchecked")
		private void listToString(Struct struct, StringCat strcat) {
			for (Term t : Iterators.loop((Iterator<Term>) struct.listIterator()))
				termToString(t, strcat);
		}

		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			return evaluateList(ctx, elt).iterator();
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
			Term term = getTerm();
			if (term == null)
				return Collections.emptyList();
			if (term.isEmptyList())
				return Collections.emptyList();
			if (term.isAtom()) {
				Element e = termToElement(term);
				if (e == null)
					return Collections.emptyList();
				return Collections.singletonList(e);
			}
			if (term.isList()) {
				Struct struct = (Struct) term;
				List<Element> result = new ArrayList<Element>(struct.listSize());
				for (Term t : Iterators.loop((Iterator<Term>) struct.listIterator())) {
					Element e = termToElement(t);
					if (e != null)
						result.add(e);
				}
				return result;
			}
			throw new RuntimeException();
		}
		
		private Element termToElement(Term term) {
			if (term == null)
				return null;
			if (term.isEmptyList())
				return null;
			if (!term.isAtom())
				return null;
			Struct id = (Struct) term;
			try {
				return (Element) javaLibrary.getRegisteredObject(id);
			}
			catch (InvalidObjectIdException e) {
				return null;
			}
		}

		@Override
		public boolean testEquality(EvaluationContext ctx, Evaluator that,	Element elt, boolean mayDelegate) {
			Term term = getTerm();
			if (term == null)
				return !that.evaluateBoolean(ctx, elt);
			if (term.isEmptyList())
				return !that.evaluateBoolean(ctx, elt);
			if (term.isAtom()) {
				Element e = termToElement(term);
				if (e == null)
					return term.toString().equals(that.evaluateString(ctx, elt));
				if (mayDelegate)
					return that.testEquality(ctx, this, elt, false);
				Iterator<Element> it = that.evaluateElements(ctx, elt);
				if (!it.hasNext())
					return false;
				if (e.equals(it.next()))
					return !it.hasNext();
				return false;
			}
			if (term.isList()) {
				if (mayDelegate)
					return that.testEquality(ctx, this, elt, false);
				@SuppressWarnings("unchecked")
				Iterator<? extends Term> thisIt = ((Struct) term).listIterator();
				Iterator<Element> thatIt = that.evaluateElements(ctx, elt);
				while (thisIt.hasNext()) {
					if (!thatIt.hasNext())
						return false;
					Element t = termToElement(thisIt.next());
					if (t == null)
						return false;
					Element e = thatIt.next();
					if (!t.equals(e))
						return false;
				}
				return !thatIt.hasNext();
			}
			throw new RuntimeException();
		}

		@Override
		public Collection<EvaluationType> getTypes() {
			Term term = getTerm();
			if (term == null)
				return Collections.singleton(EvaluationType.ELEMENTS);
			if (term.isList())
				return Collections.singleton(EvaluationType.ELEMENTS);
			if (term.isAtom()) {
				Element e = termToElement(term);
				if (e == null)
					Collections.singleton(EvaluationType.STRING);
				return Collections.singleton(EvaluationType.ELEMENTS);
			}
			if (term instanceof Int)
				return Collections.singleton(EvaluationType.INT);
			if (term instanceof Double)
				return Collections.singleton(EvaluationType.DOUBLE);
			throw new RuntimeException();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	}
	
	@Override
	public Documentation getDocumentation() {
		return null;
	}
}
