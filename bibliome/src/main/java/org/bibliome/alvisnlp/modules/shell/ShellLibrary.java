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


package org.bibliome.alvisnlp.modules.shell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.ElementVisitor;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.expressions.AbstractIteratorEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.documentation.Documentation;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

public class ShellLibrary extends FunctionLibrary {
	private static final class ShellPosition {
		private final ListIterator<Element> iterator;
		private final int count;
		private Element element = null;
		
		private ShellPosition(List<Element> list) {
			iterator = list.listIterator();
			count = list.size();
			element = iterator.next();
		}
		
		private Iterator<Element> getElement() {
			return Iterators.singletonIterator(element);
		}
	}
	
	private final Deque<ShellPosition> stack = new LinkedList<ShellPosition>();
	private final DumpData dumpData = new DumpData();
	
	ShellLibrary(Corpus corpus) {
		List<Element> topList = Collections.singletonList((Element) corpus);
		ShellPosition topPosition = new ShellPosition(topList);
		stack.add(topPosition);
	}

	@Override
	public String getName() {
		return "shell";
	}

	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		if (ftors.isEmpty())
			return cannotResolve(ftors, args);
		String firstFtor = ftors.get(0);
		if (firstFtor.equals("move") && ftors.size() == 1 && args.size() == 1) {
			Evaluator target = args.get(0).resolveExpressions(resolver);
			return new MoveEvaluator(target);
		}
		if (firstFtor.equals("up") && ftors.size() == 1 && args.size() == 0) {
			return upEvaluator;
		}
		if (firstFtor.equals("next") && ftors.size() == 1 && args.size() == 0) {
			return nextEvaluator;
		}
		if (firstFtor.equals("prev") && ftors.size() == 1 && args.size() == 0) {
			return prevEvaluator;
		}
		if (firstFtor.equals("layers") && ftors.size() == 1 && args.size() == 0) {
			return layersEvaluator;
		}
		if (firstFtor.equals("display") && ftors.size() > 1 && args.size() == 0) {
			return new SetDisplayEvaluator(ftors.subList(1, ftors.size()));
		}
		if (firstFtor.equals("stack") && ftors.size() == 1 && args.size() == 0) {
			return stackEvaluator;
		}
		return cannotResolve(ftors, args);
	}

	@Override
	public Documentation getDocumentation() {
		// TODO Auto-generated method stub
		return null;
	}
	
	DumpData getDumpData() {
		return dumpData;
	}

	private final class MoveEvaluator extends AbstractIteratorEvaluator {
		private final Evaluator target;
		
		private MoveEvaluator(Evaluator target) {
			this.target = target;
		}

		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			List<Element> elements = target.evaluateList(ctx, elt);
			if (elements.isEmpty()) {
				System.err.println("no solution");
				return Iterators.emptyIterator();
			}
			ShellPosition pos = new ShellPosition(elements);
			stack.addLast(pos);
			return pos.getElement();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			target.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	private abstract class NavigateEvaluator extends AbstractIteratorEvaluator {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			ShellPosition pos = stack.getLast();
			if (probe(pos)) {
				pos.element = get(pos);
				return pos.getElement();
			}
			System.err.println(message());
			return Iterators.emptyIterator();
		}
		
		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}

		protected abstract boolean probe(ShellPosition pos);
		protected abstract Element get(ShellPosition pos);
		protected abstract String message();
	}
	
	private final Evaluator nextEvaluator = new NavigateEvaluator() {
		@Override
		protected boolean probe(ShellPosition pos) {
			return pos.iterator.hasNext();
		}

		@Override
		protected Element get(ShellPosition pos) {
			return pos.iterator.next();
		}

		@Override
		protected String message() {
			return "last element";
		}
	};
	
	private final Evaluator prevEvaluator = new NavigateEvaluator() {
		@Override
		protected boolean probe(ShellPosition pos) {
			return pos.iterator.hasPrevious();
		}

		@Override
		protected Element get(ShellPosition pos) {
			return pos.iterator.previous();
		}

		@Override
		protected String message() {
			return "first element";
		}
	};
	
	private final Evaluator upEvaluator = new AbstractIteratorEvaluator() {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			if (stack.size() > 1) {
				stack.removeLast();
			}
			ShellPosition pos = stack.getLast();
			return pos.getElement();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
	
	Element getCurrentElement() {
		ShellPosition pos = stack.getLast();
		return pos.element;
	}

	private static String getPrompt(ShellPosition pos, int indent, boolean prompt) {
		Element elt = pos.element;
		StringBuilder result = new StringBuilder();
		DumpData.indent(result, indent);
		elt.accept(PROMPT_BUILDER, result);
		if (pos.count != 1) {
			result.append(" {");
			result.append(pos.iterator.previousIndex());
			result.append('/');
			result.append(pos.count);
			result.append('}');
		}
		if (prompt) {
			result.append(" > ");
		}
		return result.toString();
	}
	
	private static final ElementVisitor<Void,StringBuilder> PROMPT_BUILDER = new ElementVisitor<Void,StringBuilder>() {
		@Override
		public Void visit(Annotation a, StringBuilder param) {
			visit(a.getSection(), param);
			param.append(".annotation(");
			param.append(a.getStart());
			param.append('-');
			param.append(a.getEnd());
			param.append(' ');
			DumpData.string(param, a.getForm());
			param.append(')');
			return null;
		}

		@Override
		public Void visit(Corpus corpus, StringBuilder param) {
			param.append("corpus");
			return null;
		}

		@Override
		public Void visit(Document doc, StringBuilder param) {
			param.append("document:");
			DumpData.identifier(param, doc.getId());
			return null;
		}

		@Override
		public Void visit(Relation rel, StringBuilder param) {
			visit(rel.getSection(), param);
			param.append(".relation:");
			DumpData.identifier(param, rel.getName());
			return null;
		}

		@Override
		public Void visit(Section sec, StringBuilder param) {
			visit(sec.getDocument(), param);
			param.append(".section:");
			DumpData.identifier(param, sec.getName());
			return null;
		}

		@Override
		public Void visit(Tuple t, StringBuilder param) {
			visit(t.getRelation(), param);
			param.append(".tuple");
			return null;
		}

		@Override
		public Void visit(Element e, StringBuilder param) {
			param.append(e.toString());
			return null;
		}
	};
	
	private static final Evaluator layersEvaluator = new AbstractIteratorEvaluator() {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			Section sec = DownCastElement.toSection(elt);
			if (sec != null) {
				for (Layer layer : sec.getAllLayers()) {
					System.out.println("    " + layer.getName() + " (" + layer.size() + ")");
				}
			}
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
	
	private final class SetDisplayEvaluator extends AbstractIteratorEvaluator {
		private final List<String> options;
		
		private SetDisplayEvaluator(List<String> options) {
			this.options = new ArrayList<String>(options);
		}

		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			for (String opt : options) {
				switch (opt) {
					case "features":
						dumpData.setFeatures(true);
						break;
					case "arguments":
						dumpData.setArguments(true);
						break;
					case "layers":
						dumpData.setLayers(true);
						break;
					case "documents":
						dumpData.setDocuments(true);
						break;
					case "sections":
						dumpData.setSections(true);
						break;
					case "relations":
						dumpData.setRelations(true);
						break;
					case "all":
						dumpData.setFeatures(true);
						dumpData.setArguments(true);
						dumpData.setLayers(true);
						dumpData.setDocuments(true);
						dumpData.setSections(true);
						dumpData.setRelations(true);
						break;
					case "no-features":
						dumpData.setFeatures(false);
						break;
					case "no-arguments":
						dumpData.setArguments(false);
						break;
					case "no-layers":
						dumpData.setLayers(false);
						break;
					case "no-documents":
						dumpData.setDocuments(false);
						break;
					case "no-sections":
						dumpData.setSections(false);
						break;
					case "no-relations":
						dumpData.setRelations(false);
						break;
					case "none":
						dumpData.setFeatures(false);
						dumpData.setArguments(false);
						dumpData.setLayers(false);
						dumpData.setDocuments(false);
						dumpData.setSections(false);
						dumpData.setRelations(false);
						break;
					default:
						ctx.getLogger().warning("unknown display option: " + opt);
				}
			}
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	}
	
	private final Evaluator stackEvaluator = new AbstractIteratorEvaluator() {
		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			int depth = stack.size();
			for (ShellPosition pos : stack) {
				String s = getPrompt(pos, depth, false);
				System.out.println(s);
				depth--;
			}
			return Iterators.emptyIterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
	};
}
