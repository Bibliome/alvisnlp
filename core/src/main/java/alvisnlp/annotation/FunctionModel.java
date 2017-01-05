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


package alvisnlp.annotation;

import java.io.IOException;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.bibliome.util.Strings;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import alvisnlp.corpus.expressions.AbstractBooleanEvaluator;
import alvisnlp.corpus.expressions.AbstractDoubleEvaluator;
import alvisnlp.corpus.expressions.AbstractEvaluator;
import alvisnlp.corpus.expressions.AbstractIntEvaluator;
import alvisnlp.corpus.expressions.AbstractIteratorEvaluator;
import alvisnlp.corpus.expressions.AbstractListEvaluator;
import alvisnlp.corpus.expressions.AbstractStringEvaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.Function;

class FunctionModel {
	private final int ord;
	private final String firstFtor;
	private final int ftors;
	private final String[] nameTypes;
	private final String callMethod;
	private final boolean staticMethod;
	private final boolean needsEvalCtx;
	private final boolean needsLibraryResolver;
	private final ArgType returnType;
	private final String[] ftorNames;
	private final ArgModel[] args;

	FunctionModel(ModelContext ctx, ExecutableElement method, String firstFtor, String callMethod, int ftors, String[] nameTypes, int ord) throws ModelException {
		if (!(ModelContext.isPublic(method) || ModelContext.isProtected(method)))
			throw new ModelException("function implementation must be either public or protected: " + method);
		this.ord = ord;
		this.firstFtor = firstFtor;
		this.callMethod = callMethod;
		this.ftors = ftors;
		this.nameTypes = nameTypes;
		staticMethod = ModelContext.isStatic(method);
		List<? extends VariableElement> params = method.getParameters();
		needsEvalCtx = !params.isEmpty() && ctx.isEvaluationContext(params.get(0).asType());
		needsLibraryResolver = !params.isEmpty() && ctx.isLibraryResolver(params.get(0).asType());
		if (needsEvalCtx && !ctx.isElement(params.get(1).asType()))
			throw new ModelException("the second parameter of this function implementation must be of type " + alvisnlp.corpus.Element.class + ": " + method);
		int argOffset = needsEvalCtx ? 2 : (needsLibraryResolver ? 1 : 0);
		if (params.size() < ftors + argOffset)
			throw new ModelException("the function implementation must have at least [ftors] parameters of type " + String.class + ": " + method);
		ftorNames = new String[ftors];
		for (int i = 0; i < ftors; ++i) {
			VariableElement arg = params.get(i + argOffset);
			if (!ctx.isString(arg.asType()))
				throw new ModelException("the function implementation must have at least [ftors] parameters of type " + String.class + ": " + method);
			ftorNames[i] = arg.getSimpleName().toString();
		}
		returnType = getType(ctx, method, method.getReturnType());
		if (returnType == ArgType.EXPRESSION)
			throw new ModelException("illegal function return type: " + method);
		args = new ArgModel[params.size() - (ftors + argOffset)];
		for (int i = 0; i < args.length; ++i) {
			VariableElement var = params.get(i + argOffset + ftors);
			args[i] = new ArgModel(ctx, method, var);
			if (args[i].type == ArgType.EXPRESSION) {
				if (!needsLibraryResolver)
					throw new ModelException("this function should need a LibraryResolver parameter: " + method);
				if (returnType != ArgType.EVALUATOR)
					throw new ModelException("this function should return an Evaluator: " + method);
			}
		}
		if (returnType == ArgType.EVALUATOR) {
			if (needsEvalCtx) {
				throw new ModelException("the function implementation cannot have EvaluationContext parameter and return Evaluator: " + method);
			}
			if (nameTypes.length > 0) {
				ctx.warning("the function implementation returns Evaluator, the name types will be ignored: " + method);
			}
			for (ArgModel arg : args)
				if (arg.type != ArgType.EVALUATOR && arg.type != ArgType.EXPRESSION)
					throw new ModelException("the function implementation can only have Expression or Evaluator parameters: " + method);
		}
	}
	
	private static class ArgModel {
		private final String name;
		private final ArgType type;
		
		private ArgModel(String name, ArgType type) {
			super();
			this.name = name;
			this.type = type;
		}
		
		private ArgModel(ModelContext ctx, ExecutableElement method, VariableElement var) throws ModelException {
			this(var.getSimpleName().toString(), getType(ctx, method, var.asType()));
		}

		@Override
		public String toString() {
			return name;
		}
	}
	
	private static enum ArgType {
		BOOLEAN(AbstractBooleanEvaluator.class, "boolean", "evaluateBoolean"),
		INT(AbstractIntEvaluator.class, "int", "evaluateInt"),
		DOUBLE(AbstractDoubleEvaluator.class, "double", "evaluateDouble"),
		STRING(AbstractStringEvaluator.class, "String", "evaluateString"),
		ITERATOR(AbstractIteratorEvaluator.class, "java.util.Iterator<alvisnlp.corpus.Element>", "evaluateElements"),
		LIST(AbstractListEvaluator.class, "java.util.List<alvisnlp.corpus.Element>", "evaluateList"),
		EVALUATOR(AbstractEvaluator.class, null, null),
		EXPRESSION(Expression.class, null, null);
		
		private final String baseClass;
		private final String returnType;
		private final String evaluationMethod;
		
		private ArgType(Class<?> baseClass, String returnType, String evaluationMethod) {
			this.baseClass = baseClass.getCanonicalName();
			this.returnType = returnType;
			this.evaluationMethod = evaluationMethod;
		}
	}
	
	private static ArgType getType(ModelContext ctx, ExecutableElement method, TypeMirror type) throws ModelException {
		switch (type.getKind()) {
			case BOOLEAN: return ArgType.BOOLEAN;
			case INT: return ArgType.INT;
			case DOUBLE: return ArgType.DOUBLE;
			case DECLARED:
				if (ctx.isString(type))
					return ArgType.STRING;
				if (ctx.isCharSequence(type))
					return ArgType.STRING;
				if (ctx.isElementIterator(type))
					return ArgType.ITERATOR;
				if (ctx.isElementList(type))
					return ArgType.LIST;
				if (ctx.isEvaluator(type))
					return ArgType.EVALUATOR;
				if (ctx.isExpression(type))
					return ArgType.EXPRESSION;
				throw new ModelException("unsupported function parameter type " + type + ": " + method);
			default:
				throw new ModelException("unsupported function parameter type " + type + ": " + method);
		}
	}

	Element toDOM(Document doc) {
		Element result = doc.createElement("function");
		result.setAttribute("ord", Integer.toString(ord));
		result.setAttribute("first-ftor", firstFtor);
		result.setAttribute("call-method", callMethod);
		result.setAttribute("static", staticMethod ? "static" : "");
		result.setAttribute("base-class", returnType.baseClass);
		if (returnType.returnType != null) {
			result.setAttribute("return-type", returnType.returnType);
		}
		if (returnType.evaluationMethod != null) {
			result.setAttribute("evaluation-method", returnType.evaluationMethod);
		}
		result.setAttribute("needs-evaluation-context", Boolean.toString(needsEvalCtx));
		result.setAttribute("needs-library-resolver", Boolean.toString(needsLibraryResolver));
		result.setAttribute("custom-expression-implementation", Boolean.toString(returnType == ArgType.EVALUATOR));
		for (int i = 0; i < ftors; ++i) {
			Element ftorElt = doc.createElement("ftor");
			if (i < nameTypes.length && !nameTypes[i].equals(Function.NO_NAME_TYPE)) {
				ftorElt.setAttribute("name-type", nameTypes[i]);
			}
			result.appendChild(ftorElt);
		}
		for (ArgModel argModel : args) {
			Element arg = doc.createElement("arg");
			result.appendChild(arg);
			ArgType t = argModel.type;
			if (t.evaluationMethod != null)
				arg.setAttribute("evaluation-method", t.evaluationMethod);
			arg.setAttribute("is-expression", Boolean.toString(t == ArgType.EXPRESSION));
		}
		return result;
	}
	
	public String toString(String libraryName) throws IOException {
		StringBuilder synopsis = new StringBuilder(libraryName);
		synopsis.append(':');
		synopsis.append(firstFtor);
		for (String ftor : ftorNames) {
			synopsis.append(':');
			synopsis.append(ftor);
		}
		synopsis.append('(');
		Strings.joinStrings(synopsis, args, ", ");
		synopsis.append(')');
		return synopsis.toString();
	}
	
	void fillDocumentation(Document doc, String libraryName, Element parent) throws IOException {
		Element elt = XMLUtils.createElement(doc, parent, 2, "function-doc");
		elt.setAttribute("first-ftor", firstFtor);
		elt.setAttribute("synopsis", toString(libraryName));
	}
	
	String getFirtstFtor() {
		return firstFtor;
	}
	
	int numFtors() {
		return ftors;
	}
	
	int numArgs() {
		return args.length;
	}
}
