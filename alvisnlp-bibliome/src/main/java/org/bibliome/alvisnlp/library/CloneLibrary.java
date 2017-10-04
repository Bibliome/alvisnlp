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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.expressions.AbstractIteratorEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

@Library("clone")
public abstract class CloneLibrary extends FunctionLibrary {
	public static final String NAME = "clone";
	
	private static abstract class AbstractCloneFeaturesEvaluator extends AbstractIteratorEvaluator {
		private final Evaluator source;

		protected AbstractCloneFeaturesEvaluator(Evaluator source) {
			super();
			this.source = source;
		}

		protected abstract List<String> getFeatureKeys(EvaluationContext ctx, Element source);
		
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element target) {
			Iterator<Element> sourceIt = source.evaluateElements(ctx, target);
			if (!sourceIt.hasNext()) {
				return Iterators.emptyIterator();
			}
			Element source = sourceIt.next();
			for (String key : getFeatureKeys(ctx, source)) {
				if (source.hasFeature(key) && !source.isStaticFeatureKey(key) && !target.isStaticFeatureKey(key)) {
					for (String value : source.getFeature(key)) {
						ctx.registerSetFeature(target, key, value);
					}
				}
			}
			return Iterators.singletonIterator(target);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			source.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	private static class EvaluatorCloneFeaturesEvaluator extends AbstractCloneFeaturesEvaluator {
		private final Collection<Evaluator> keys;

		private EvaluatorCloneFeaturesEvaluator(Evaluator source, Collection<Evaluator> keys) {
			super(source);
			this.keys = keys;
		}

		@Override
		protected List<String> getFeatureKeys(EvaluationContext ctx, Element source) {
			List<String> result = new ArrayList<String>(keys.size());
			for (Evaluator eval : keys) {
				String k = eval.evaluateString(ctx, source);
				result.add(k);
			}
			return result;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			for (Evaluator eval : keys) {
				eval.collectUsedNames(nameUsage, defaultType);
			}
		}
	}
	
	private static class ListCloneFeaturesEvaluator extends AbstractCloneFeaturesEvaluator {
		private final List<String> keys;

		private ListCloneFeaturesEvaluator(Evaluator source, List<String> keys) {
			super(source);
			this.keys = keys;
		}

		@Override
		protected List<String> getFeatureKeys(EvaluationContext ctx, Element source) {
			return keys;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			nameUsage.addNames(NameType.FEATURE, keys);
		}
	}
	
	private static class AllCloneFeaturesEvaluator extends AbstractCloneFeaturesEvaluator {
		private AllCloneFeaturesEvaluator(Evaluator source) {
			super(source);
		}

		@Override
		protected List<String> getFeatureKeys(EvaluationContext ctx, Element source) {
			return new ArrayList<String>(source.getFeatureKeys());
		}
	}

	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		if (!ftors.isEmpty()) {
			String firstFtor = ftors.get(0);
			switch (firstFtor) {
				case "features": {
					int ftorArity = ftors.size();
					int arity = args.size();
					if (ftorArity == 1 && arity == 1) {
						Evaluator source = args.get(0).resolveExpressions(resolver);
						return new AllCloneFeaturesEvaluator(source);
					}
					if (ftorArity == 1 && arity > 1) {
						Evaluator source = args.get(0).resolveExpressions(resolver);
						List<Evaluator> keys = new ArrayList<Evaluator>(arity - 1);
						for (int i = 1; i < arity; ++i) {
							Evaluator k = args.get(i).resolveExpressions(resolver);
							keys.add(k);
						}
						return new EvaluatorCloneFeaturesEvaluator(source, keys);
					}
					if (ftorArity > 1 && arity == 1) {
						Evaluator source = args.get(0).resolveExpressions(resolver);
						List<String> keys = ftors.subList(1, ftorArity);
						return new ListCloneFeaturesEvaluator(source, keys);
					}
					break;
				}
				case "arguments": {
					int ftorArity = ftors.size();
					int arity = args.size();
					if (ftorArity == 1 && arity == 1) {
						Evaluator source = args.get(0).resolveExpressions(resolver);
						return new AllCloneArgumentsEvaluator(source);
					}
					if (ftorArity == 1 && arity > 1) {
						Evaluator source = args.get(0).resolveExpressions(resolver);
						List<Evaluator> keys = new ArrayList<Evaluator>(arity - 1);
						for (int i = 1; i < arity; ++i) {
							Evaluator k = args.get(i).resolveExpressions(resolver);
							keys.add(k);
						}
						return new EvaluatorCloneArgumentsEvaluator(source, keys);
					}
					if (ftorArity > 1 && arity == 1) {
						Evaluator source = args.get(0).resolveExpressions(resolver);
						List<String> keys = ftors.subList(1, ftorArity);
						return new ListCloneArgumentsEvaluator(source, keys);
					}
					break;
				}
			}
		}
		return cannotResolve(ftors, args);
	}
	
	private static abstract class AbstractCloneArgumentsEvaluator extends AbstractIteratorEvaluator {
		private final Evaluator source;

		protected AbstractCloneArgumentsEvaluator(Evaluator source) {
			super();
			this.source = source;
		}

		protected abstract List<String> getArgumentRoles(EvaluationContext ctx, Tuple source);
		
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element targetElt) {
			Tuple target = DownCastElement.toTuple(targetElt);
			if (target == null) {
				return Iterators.emptyIterator();
			}
			Iterator<Element> sourceIt = source.evaluateElements(ctx, target);
			if (!sourceIt.hasNext()) {
				return Iterators.emptyIterator();
			}
			Element sourceElt = sourceIt.next();
			Tuple source = DownCastElement.toTuple(sourceElt);
			if (source != null) {
				for (String role : getArgumentRoles(ctx, source)) {
					if (source.hasArgument(role)) {
						ctx.registerSetArgument(target, role, source.getArgument(role));
					}
				}
			}
			return Iterators.singletonIterator(target);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			source.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	private static class EvaluatorCloneArgumentsEvaluator extends AbstractCloneArgumentsEvaluator {
		private final Collection<Evaluator> keys;

		private EvaluatorCloneArgumentsEvaluator(Evaluator source, Collection<Evaluator> keys) {
			super(source);
			this.keys = keys;
		}

		@Override
		protected List<String> getArgumentRoles(EvaluationContext ctx, Tuple source) {
			List<String> result = new ArrayList<String>(keys.size());
			for (Evaluator eval : keys) {
				String k = eval.evaluateString(ctx, source);
				result.add(k);
			}
			return result;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			for (Evaluator eval : keys) {
				eval.collectUsedNames(nameUsage, defaultType);
			}
		}
	}
	
	private static class ListCloneArgumentsEvaluator extends AbstractCloneArgumentsEvaluator {
		private final List<String> roles;

		private ListCloneArgumentsEvaluator(Evaluator source, List<String> roles) {
			super(source);
			this.roles = roles;
		}

		@Override
		protected List<String> getArgumentRoles(EvaluationContext ctx, Tuple source) {
			return roles;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			nameUsage.addNames(NameType.ARGUMENT, roles);
		}
	}
	
	private static class AllCloneArgumentsEvaluator extends AbstractCloneArgumentsEvaluator {
		private AllCloneArgumentsEvaluator(Evaluator source) {
			super(source);
		}

		@Override
		protected List<String> getArgumentRoles(EvaluationContext ctx, Tuple source) {
			return new ArrayList<String>(source.getRoles());
		}
	}
}
