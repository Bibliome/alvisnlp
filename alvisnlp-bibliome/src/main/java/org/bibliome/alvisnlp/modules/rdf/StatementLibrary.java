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


package org.bibliome.alvisnlp.modules.rdf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.bibliome.util.Iterators;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.AbstractIteratorEvaluator;
import alvisnlp.corpus.expressions.AbstractListEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.documentation.Documentation;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;

public class StatementLibrary extends FunctionLibrary {
	private final Model model;
	private final ElementResourceMap resourceMap;

	StatementLibrary(Model model, ElementResourceMap resourceMap) {
		this.model = model;
		this.resourceMap = resourceMap;
	}
	
	@Override
	public String getName() {
		return "stmt";
	}

	@Override
	public Documentation getDocumentation() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static Iterator<Evaluator> resolveArgs(LibraryResolver resolver, List<Expression> args) throws ResolverException {
		List<Evaluator> evaluators = new ArrayList<Evaluator>(args.size());
		for (Expression expr : args) {
			Evaluator eval = expr.resolveExpressions(resolver);
			evaluators.add(eval);
		}
		return evaluators.iterator();
	}
	
	private NodeBuilder getNodeBuilder(String ftor, Evaluator eval) {
		switch (ftor) {
			case "res":
			case "resource":
				return new ResourceURINodeBuilder(eval);
			case "literal":
			case "lit":
			case "mlit":
			case "multi-literal":
			case "literals":
				return new LiteralNodeBuilder(eval);
			case "bool":
			case "boolean":
			case "mbool":
			case "multi-boolean":
			case "booleans":
				return new BooleanLiteralNodeBuilder(eval);
			case "int":
			case "integer":
			case "mint":
			case "multi-integer":
			case "integers":
				return new IntLiteralNodeBuilder(eval);
			case "str":
			case "string":
			case "mstr":
			case "multi-string":
			case "strings":
				return new StringLiteralNodeBuilder(eval);
			case "double":
			case "mdouble":
			case "multi-double":
			case "doubles":
				return new DoubleLiteralNodeBuilder(eval);
		}
		throw new RuntimeException("unknown ftor " + ftor);
	}

	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		checkExactFtors(ftors, 1);
		String ftor = ftors.get(0);
		Iterator<Evaluator> resolvedArgs = resolveArgs(resolver, args);
		ResourceBuilder subjectBuilder = resourceMap;
		Evaluator propertyURI = null;
		NodeBuilder objectBuilder = resourceMap;
		Evaluator objects = null;
		switch (ftor) {
			case "res":
			case "resource":
			case "literal":
			case "lit":
			case "bool":
			case "boolean":
			case "int":
			case "integer":
			case "str":
			case "string":
			case "double": {
				checkRangeArity(ftors, args, 2, 3);
				if (args.size() == 3) {
					subjectBuilder = new ResourceURINodeBuilder(resolvedArgs.next());
				}
				propertyURI = resolvedArgs.next();
				objectBuilder = getNodeBuilder(ftor, resolvedArgs.next());
				break;
			}
			case "mres":
			case "multi-resource":
			case "resources": {
				checkRangeArity(ftors, args, 2, 4);
				if (args.size() == 4) {
					subjectBuilder = new ResourceURINodeBuilder(resolvedArgs.next());
				}
				propertyURI = resolvedArgs.next();
				objects = resolvedArgs.next();
				if (args.size() > 2) {
					objectBuilder = new ResourceURINodeBuilder(resolvedArgs.next());
				}
				break;
			}
			case "mlit":
			case "multi-literal":
			case "literals":
			case "mbool":
			case "multi-boolean":
			case "booleans":
			case "mint":
			case "multi-integer":
			case "integers":
			case "mstr":
			case "multi-string":
			case "strings":
			case "mdouble":
			case "multi-double":
			case "doubles": {
				checkRangeArity(ftors, args, 3, 4);
				if (args.size() == 4) {
					subjectBuilder = new ResourceURINodeBuilder(resolvedArgs.next());
				}
				else {
					subjectBuilder = resourceMap;
				}
				propertyURI = resolvedArgs.next();
				objects = resolvedArgs.next();
				objectBuilder = getNodeBuilder(ftor, resolvedArgs.next());
				break;
			}
			default:
				cannotResolve(ftors, args);
		}
		if (subjectBuilder == null || propertyURI == null || objectBuilder == null) {
			cannotResolve(ftors, args);
		}
		StatementBuilder statementBuilder = new StatementBuilder(subjectBuilder, propertyURI, objectBuilder);
		if (objects == null) {
			return new StatementEvaluator(statementBuilder);
		}
		return new MultiStatementEvaluator(statementBuilder, objects);
	}

	private class StatementBuilder implements NameUser {
		private final ResourceBuilder subjectBuilder;
		private final Evaluator propertyURI;
		private final NodeBuilder objectBuilder;

		private StatementBuilder(ResourceBuilder subjectBuilder, Evaluator propertyURI, NodeBuilder objectBuilder) {
			super();
			this.subjectBuilder = subjectBuilder;
			this.propertyURI = propertyURI;
			this.objectBuilder = objectBuilder;
		}
		
		private void addStatement(EvaluationContext ctx, Element elt) {
			Resource subject = subjectBuilder.createNode(ctx, elt);
			String propertyURI = this.propertyURI.evaluateString(ctx, elt);
			Property property = model.createProperty(propertyURI);
			RDFNode object = objectBuilder.createNode(ctx, elt);
			model.add(subject, property, object);
		}
		
		private void addAllStatements(EvaluationContext ctx, Element elt, List<Element> objects) {
			Resource subject = subjectBuilder.createNode(ctx, elt);
			String propertyURI = this.propertyURI.evaluateString(ctx, elt);
			Property property = model.createProperty(propertyURI);
			for (Element e : objects) {
				RDFNode object = objectBuilder.createNode(ctx, e);
				model.add(subject, property, object);
			}
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			subjectBuilder.collectUsedNames(nameUsage, defaultType);
			propertyURI.collectUsedNames(nameUsage, defaultType);
			objectBuilder.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	private class StatementEvaluator extends AbstractIteratorEvaluator {
		private final StatementBuilder statementBuilder;
		
		private StatementEvaluator(StatementBuilder statementBuilder) {
			super();
			this.statementBuilder = statementBuilder;
		}

		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			statementBuilder.addStatement(ctx, elt);
			return Iterators.singletonIterator(elt);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			statementBuilder.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	private class MultiStatementEvaluator extends AbstractListEvaluator {
		private final StatementBuilder statementBuilder;
		private final Evaluator objects;

		private MultiStatementEvaluator(StatementBuilder statementBuilder, Evaluator objects) {
			super();
			this.statementBuilder = statementBuilder;
			this.objects = objects;
		}

		@Override
		public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
			List<Element> objects = this.objects.evaluateList(ctx, elt);
			statementBuilder.addAllStatements(ctx, elt, objects);
			return objects;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			statementBuilder.collectUsedNames(nameUsage, defaultType);
			objects.collectUsedNames(nameUsage, defaultType);
		}
	}

	private abstract class AbstractNodeBuilder implements NodeBuilder {
		protected final Evaluator specification;

		protected AbstractNodeBuilder(Evaluator specification) {
			super();
			this.specification = specification;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			specification.collectUsedNames(nameUsage, defaultType);
		}
	}

	private class ResourceURINodeBuilder extends AbstractNodeBuilder implements ResourceBuilder {
		private ResourceURINodeBuilder(Evaluator uri) {
			super(uri);
		}

		@Override
		public Resource createNode(EvaluationContext ctx, Element elt) {
			String uri = specification.evaluateString(ctx, elt);
			return model.createResource(uri);
		}
	}

	private class LiteralNodeBuilder extends AbstractNodeBuilder {
		private LiteralNodeBuilder(Evaluator specification) {
			super(specification);
		}

		@Override
		public RDFNode createNode(EvaluationContext ctx, Element elt) {
			String value = specification.evaluateString(ctx, elt);
			return model.createLiteral(value);
		}
	}
	
	private class StringLiteralNodeBuilder extends AbstractNodeBuilder {
		private StringLiteralNodeBuilder(Evaluator specification) {
			super(specification);
		}

		@Override
		public RDFNode createNode(EvaluationContext ctx, Element elt) {
			String value = specification.evaluateString(ctx, elt);
			return model.createTypedLiteral(value);
		}
	}
	
	private class BooleanLiteralNodeBuilder extends AbstractNodeBuilder {
		private BooleanLiteralNodeBuilder(Evaluator specification) {
			super(specification);
		}

		@Override
		public RDFNode createNode(EvaluationContext ctx, Element elt) {
			boolean value = specification.evaluateBoolean(ctx, elt);
			return model.createTypedLiteral(value);
		}
	}
	
	private class IntLiteralNodeBuilder extends AbstractNodeBuilder {
		private IntLiteralNodeBuilder(Evaluator specification) {
			super(specification);
		}

		@Override
		public RDFNode createNode(EvaluationContext ctx, Element elt) {
			int value = specification.evaluateInt(ctx, elt);
			return model.createTypedLiteral(value);
		}
	}
	
	private class DoubleLiteralNodeBuilder extends AbstractNodeBuilder {
		private DoubleLiteralNodeBuilder(Evaluator specification) {
			super(specification);
		}

		@Override
		public RDFNode createNode(EvaluationContext ctx, Element elt) {
			double value = specification.evaluateDouble(ctx, elt);
			return model.createTypedLiteral(value);
		}
	}
}
