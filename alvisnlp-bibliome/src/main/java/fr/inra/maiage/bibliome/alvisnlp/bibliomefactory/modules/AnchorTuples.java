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

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.AnchorTuples.AnchorTuplesResolvedObjects;
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
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.VariableLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.VariableLibrary.Variable;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.EvaluatorMapping;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping;
import fr.inra.maiage.bibliome.util.Iterators;

@AlvisNLPModule
public abstract class AnchorTuples extends SectionModule<AnchorTuplesResolvedObjects> implements TupleCreator {
	private String relation;
	private Expression anchor;
	private String anchorRole;
	private ExpressionMapping arguments;

	class AnchorTuplesResolvedObjects extends SectionResolvedObjects {
		@SuppressWarnings("hiding")
		private final Evaluator anchor;
		@SuppressWarnings("hiding")
		private final EvaluatorMapping arguments;
		private final Variable anchorVariable;

		private AnchorTuplesResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
			super(ctx, AnchorTuples.this);
			VariableLibrary anchorLibrary = new VariableLibrary("anchor");
			anchorVariable = anchorLibrary.newVariable(null);
			anchor = rootResolver.resolveNullable(AnchorTuples.this.anchor);
			LibraryResolver anchorResolver = anchorLibrary.newLibraryResolver(rootResolver);
			arguments = anchorResolver.resolveNullable(AnchorTuples.this.arguments);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			anchor.collectUsedNames(nameUsage, defaultType);
			arguments.collectUsedNames(nameUsage, defaultType);
		}
	}

	@Override
	protected AnchorTuplesResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new AnchorTuplesResolvedObjects(ctx);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		AnchorTuplesResolvedObjects resObj = getResolvedObjects();
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		int n = 0;
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			Relation rel = sec.ensureRelation(this, relation);
			for (Element elt : Iterators.loop(resObj.anchor.evaluateElements(evalCtx, sec))) {
				Annotation anchorAnnotation = DownCastElement.toAnnotation(elt);
				resObj.anchorVariable.set(anchorAnnotation);
				if (anchorAnnotation == null) {
					logger.warning("anchor " + elt + " is not an annotation");
					continue;
				}
				n++;
				Tuple t = new Tuple(this, rel);
				t.setArgument(anchorRole, anchorAnnotation);
//				logger.info(anchorRole + " = " + anchor);
				for (Map.Entry<String,Evaluator> e : resObj.arguments.entrySet()) {
					Iterator<Element> it = e.getValue().evaluateElements(evalCtx, anchorAnnotation);
					if (!it.hasNext())
						continue;
					Element argElt = it.next();
					Annotation a = DownCastElement.toAnnotation(argElt);
					if (a == null) {
						logger.warning("argument " + argElt + " is not an annotation");
						continue;
					}
//					logger.info(e.getKey() + " = " + a);
					t.setArgument(e.getKey(), a);
				}
//				logger.info("anchor = " + anchorExpr.evaluateElements(elt).next());
			}
		}
		if (n == 0) {
			logger.warning("created no tuple");
		}
		else {
			logger.info("created " + n + " tuples");
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
	public String getAnchorRole() {
		return anchorRole;
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

	public void setAnchorRole(String anchorRole) {
		this.anchorRole = anchorRole;
	}

	public void setArguments(ExpressionMapping arguments) {
		this.arguments = arguments;
	}
}
