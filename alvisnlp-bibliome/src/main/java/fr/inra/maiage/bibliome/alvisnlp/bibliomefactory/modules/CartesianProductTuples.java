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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CartesianProductTuples.CartesianProductTuplesResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DownCastElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.EvaluatorMapping;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping;
import fr.inra.maiage.bibliome.util.CartesianProduct;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.mappers.Mapper;
import fr.inra.maiage.bibliome.util.mappers.Mappers;

@AlvisNLPModule
public abstract class CartesianProductTuples extends SectionModule<CartesianProductTuplesResolvedObjects> implements TupleCreator {
	private String relation;
	private Expression anchor;
	private ExpressionMapping arguments;

	class CartesianProductTuplesResolvedObjects extends SectionResolvedObjects {
		private final Evaluator anchor;
		private final EvaluatorMapping arguments;

		private CartesianProductTuplesResolvedObjects(ProcessingContext ctx) throws ResolverException {
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
	protected CartesianProductTuplesResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new CartesianProductTuplesResolvedObjects(ctx);
	}

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		List<String> roles = new ArrayList<String>(arguments.keySet());
		CartesianProductTuplesResolvedObjects resObj = getResolvedObjects();
		List<Evaluator> argExprs = new ArrayList<Evaluator>(resObj.arguments.values());
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		ExpressionToAnnotationMapper mapper = new ExpressionToAnnotationMapper(evalCtx);
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			Relation rel = sec.ensureRelation(this, relation);
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

	@Deprecated
	@Param(nameType=NameType.RELATION)
	public String getRelationName() {
		return relation;
	}

	@Param
	public Expression getAnchor() {
		return anchor;
	}

	@Param(nameType=NameType.ARGUMENT)
	public ExpressionMapping getArguments() {
		return arguments;
	}

	@Param(nameType=NameType.RELATION)
	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public void setRelationName(String relationName) {
		this.relation = relationName;
	}

	public void setAnchor(Expression anchor) {
		this.anchor = anchor;
	}

	public void setArguments(ExpressionMapping arguments) {
		this.arguments = arguments;
	}
}
