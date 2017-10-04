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
import java.util.Iterator;
import java.util.List;

import org.bibliome.util.Pair;
import org.bibliome.util.mappers.Mapper;
import org.bibliome.util.mappers.Mappers;

import alice.tuprolog.Double;
import alice.tuprolog.Int;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;
import alice.tuprolog.Var;
import alice.tuprolog.lib.JavaLibrary;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.EvaluationType;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.Resolvable;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;

public class FactDefinition implements Resolvable<FactDefinition>, NameUser {
	private final Expression facts;
	private final Expression ctor;
	private final List<Pair<Expression,EvaluationType>> args;
	private final Evaluator resolvedFacts;
	private final Evaluator resolvedCtor;
	private final List<Pair<Evaluator,EvaluationType>> resolvedArgs;

	FactDefinition(Expression facts, Expression ctor, List<Pair<Expression,EvaluationType>> args, Evaluator resolvedFacts, Evaluator resolvedCtor, List<Pair<Evaluator,EvaluationType>> resolvedArgs) {
		super();
		this.facts = facts;
		this.ctor = ctor;
		this.args = args;
		this.resolvedFacts = resolvedFacts;
		this.resolvedCtor = resolvedCtor;
		this.resolvedArgs = resolvedArgs;
	}

	@Override
	public FactDefinition resolveExpressions(LibraryResolver resolver) throws ResolverException {
		List<Pair<Evaluator,EvaluationType>> args = new ArrayList<Pair<Evaluator,EvaluationType>>();
		for (Pair<Expression,EvaluationType> p : this.args)
			args.add(new Pair<Evaluator,EvaluationType>(p.first.resolveExpressions(resolver), p.second));
		return new FactDefinition(facts, ctor, this.args, facts.resolveExpressions(resolver), ctor.resolveExpressions(resolver), args);
	}
	
	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		if (resolvedFacts != null) {
			resolvedFacts.collectUsedNames(nameUsage, defaultType);
		}
		if (resolvedCtor != null) {
			resolvedCtor.collectUsedNames(nameUsage, defaultType);
		}
		if (resolvedArgs != null) {
			for (Pair<Evaluator,EvaluationType> p : resolvedArgs) {
				p.first.collectUsedNames(nameUsage, defaultType);
			}
		}
	}
	
	public Theory getTheory(JavaLibrary javaLibrary, EvaluationContext evalCtx, Element elt) throws InvalidTheoryException {
		List<Element> factElements = this.resolvedFacts.evaluateList(evalCtx, elt);
		Struct[] facts = new Struct[factElements.size()];
		for (int i = 0; i < facts.length; ++i) {
			facts[i] = buildFact(javaLibrary, evalCtx, factElements.get(i));
		}
		return new Theory(new Struct(facts));
	}

	private Struct buildFact(JavaLibrary javaLibrary, EvaluationContext evalCtx, Element elt) {
		String ctor = this.resolvedCtor.evaluateString(evalCtx, elt);
		Term[] args = getArguments(javaLibrary, evalCtx, elt);
		return new Struct(ctor, args);
	}

	private Term[] getArguments(JavaLibrary javaLibrary, EvaluationContext evalCtx, Element elt) {
		Term[] result = new Term[args.size()];
		for (int i = 0; i < result.length; ++i) {
			Pair<Evaluator,EvaluationType> p = resolvedArgs.get(i);
			result[i] = getArg(javaLibrary, evalCtx, elt, p.first, p.second);
		}
		return result;
	}

	private static Term getArg(JavaLibrary javaLibrary, EvaluationContext evalCtx, Element elt, Evaluator value, EvaluationType type) {
		switch (type) {
		case BOOLEAN:
			if (value.evaluateBoolean(evalCtx, elt))
				return Term.TRUE;
			return Term.FALSE;
		case INT:
			return new Int(value.evaluateInt(evalCtx, elt));
		case DOUBLE:
			return new Double(value.evaluateDouble(evalCtx, elt));
		case STRING:
			return new Struct(value.evaluateString(evalCtx, elt).replace("'", "''"));
		case ANNOTATIONS:
		case CORPUS:
		case DOCUMENTS:
		case RELATIONS:
		case SECTIONS:
		case TUPLES:
			Iterator<Element> it = value.evaluateElements(evalCtx, elt);
			if (it.hasNext())
				return javaLibrary.register(it.next());
			return new Var();
		case ELEMENTS:
			List<Element> elements = value.evaluateList(evalCtx, elt);
			Mapper<Element,Struct> mapper = new RegisterObjectMapper<Element>(javaLibrary);
			List<Term> structs = new ArrayList<Term>(elements.size());
			Mappers.apply(mapper, elements, structs);
			return new Struct(structs.toArray(new Term[structs.size()]));
		case UNDEFINED:
			throw new RuntimeException();
		}
		throw new RuntimeException();
	}
	
	private static final class RegisterObjectMapper<T> implements Mapper<T,Struct> {
		private final JavaLibrary javaLibrary;

		private RegisterObjectMapper(JavaLibrary javaLibrary) {
			super();
			this.javaLibrary = javaLibrary;
		}

		@Override
		public Struct map(T x) {
			return javaLibrary.register(x);
		}
	}
}
