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


package org.bibliome.alvisnlp.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.CartesianProductTuples.CartesianProductTuplesResolvedObjects;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.CartesianProduct;
import org.bibliome.util.Iterators;
import org.bibliome.util.mappers.Mapper;
import org.bibliome.util.mappers.Mappers;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.types.EvaluatorMapping;
import alvisnlp.module.types.ExpressionMapping;

@AlvisNLPModule
public abstract class CartesianProductTuples extends SectionModule<CartesianProductTuplesResolvedObjects> implements TupleCreator {
	private String relationName;
	private Expression anchor;
	private ExpressionMapping arguments;
		
	@SuppressWarnings("hiding")
	class CartesianProductTuplesResolvedObjects extends SectionResolvedObjects {
		private final Evaluator anchor;
		private final EvaluatorMapping arguments;
		
		private CartesianProductTuplesResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
			super(ctx, CartesianProductTuples.this);
			anchor = rootResolver.resolveNullable(CartesianProductTuples.this.anchor);
			arguments = rootResolver.resolveNullable(CartesianProductTuples.this.arguments);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			anchor.collectUsedNames(nameUsage, defaultType);
			arguments.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Override
	protected CartesianProductTuplesResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new CartesianProductTuplesResolvedObjects(ctx);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		List<String> roles = new ArrayList<String>(arguments.keySet());
		CartesianProductTuplesResolvedObjects resObj = getResolvedObjects();
		List<Evaluator> argExprs = new ArrayList<Evaluator>(resObj.arguments.values());
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		ExpressionToAnnotationMapper mapper = new ExpressionToAnnotationMapper(evalCtx);
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			Relation rel = sec.ensureRelation(this, relationName);
			for (Element anchorAnnotation : Iterators.loop(resObj.anchor.evaluateElements(evalCtx, sec))) {
				mapper.anchor = anchorAnnotation;
				List<Collection<Annotation>> generators = new ArrayList<Collection<Annotation>>(argExprs.size());
				Mappers.apply(mapper, argExprs, generators);
//				getLogger().info("generators.size() = " + generators.size());
				CartesianProduct<Annotation> cp = new CartesianProduct<Annotation>(generators);
				while (cp.next()) {
					List<Annotation> args = cp.getElements();
					Tuple t = new Tuple(this, rel);
					for (int i = 0; i < roles.size(); ++i)
						t.setArgument(roles.get(i), args.get(i));
				}
			}
		}
	}
	
	private static final class ExpressionToAnnotationMapper implements Mapper<Evaluator,Collection<Annotation>> {
		private final EvaluationContext ctx;
		private Element anchor;

		private ExpressionToAnnotationMapper(EvaluationContext ctx) {
			super();
			this.ctx = ctx;
		}

		@Override
		public Collection<Annotation> map(Evaluator x) {
			Collection<Annotation> result = new ArrayList<Annotation>();
			for (Element elt : Iterators.loop(x.evaluateElements(ctx, anchor))) {
				Annotation a = DownCastElement.toAnnotation(elt);
				if (a != null)
					result.add(a);
			}
			return result;
		}
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Param(nameType=NameType.RELATION)
	public String getRelationName() {
		return relationName;
	}

	@Param
	public Expression getAnchor() {
		return anchor;
	}

	@Param(nameType=NameType.ARGUMENT)
	public ExpressionMapping getArguments() {
		return arguments;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public void setAnchor(Expression anchor) {
		this.anchor = anchor;
	}

	public void setArguments(ExpressionMapping arguments) {
		this.arguments = arguments;
	}
}
