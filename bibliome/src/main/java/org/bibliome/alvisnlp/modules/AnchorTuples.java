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

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.AnchorTuples.AnchorTuplesResolvedObjects;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;

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
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.corpus.expressions.VariableLibrary;
import alvisnlp.corpus.expressions.VariableLibrary.Variable;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.types.EvaluatorMapping;
import alvisnlp.module.types.ExpressionMapping;

@AlvisNLPModule
public abstract class AnchorTuples extends SectionModule<AnchorTuplesResolvedObjects> implements TupleCreator {
	private String relationName;
	private Expression anchor;
	private String anchorRole;
	private ExpressionMapping arguments;
	
	@SuppressWarnings("hiding")
	class AnchorTuplesResolvedObjects extends SectionResolvedObjects {
		private final Evaluator anchor;
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
			Relation rel = sec.ensureRelation(this, relationName);
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

	@Param(nameType=NameType.RELATION)
	public String getRelationName() {
		return relationName;
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

	public void setRelationName(String relationName) {
		this.relationName = relationName;
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
