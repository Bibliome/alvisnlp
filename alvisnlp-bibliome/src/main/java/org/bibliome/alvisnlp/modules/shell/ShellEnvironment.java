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

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.alvisnlp.converters.expression.parser.ExpressionParser;
import org.bibliome.alvisnlp.converters.expression.parser.ParseException;
import org.bibliome.alvisnlp.converters.expression.parser.TokenMgrError;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.corpus.expressions.VariableLibrary;

public class ShellEnvironment {
	private final Shell owner;
	private final Locale locale;
	private final EvaluationContext evaluationContext;
	private final ExpressionParser parser = new ExpressionParser((Reader) null);
	private final Deque<ElementPosition> stack = new LinkedList<ElementPosition>();
	private final Map<String,ShellCommand> commands = ShellCommand.getCommands();
	private final VariableLibrary varLib = new VariableLibrary("var");
	private final LibraryResolver resolver;
	
	ShellEnvironment(Shell owner, Logger logger, LibraryResolver resolver, Locale locale, Element elt) throws ResolverException {
		super();
		this.owner = owner;
		this.locale = locale;
		this.evaluationContext = new EvaluationContext(logger);
		this.resolver = new LibraryResolver(resolver);
		this.resolver.addLibrary(varLib);
		try {
			stack.add(new ElementPosition(Collections.singletonList(elt)));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static final class ElementPosition {
		private final ListIterator<Element> iterator;
		private Element element;
		
		private ElementPosition(List<Element> list) throws Exception {
			iterator = list.listIterator();
			if (!iterator.hasNext())
				throw new Exception("no solution");
			element = iterator.next();
		}
	}
	
	private static final Pattern COMMAND = Pattern.compile("\\s*@(\\w+)");
	
	void executeCommand(String cmdStr) {
		try {
			Matcher m = COMMAND.matcher(cmdStr);
			if (m.lookingAt()) {
				String cmd = m.group(1);
				if (commands.containsKey(cmd))
					commands.get(cmd).execute(this, cmdStr.substring(m.end()).trim());
				else
					throw new Exception("unknown command " + cmd + ", type '@help' to get a list of commands");
			}
			else
				ShellCommand.QUERY.execute(this, cmdStr);
		}
		catch (ParseException pe) {
			System.err.println(pe.getMessage());
		}
		catch (TokenMgrError tme) {
			System.err.println(tme.getMessage());
		}
		catch (Exception e) {
			//System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
	}
	
	private Expression parseExpression(String s) throws ParseException {
		Reader r = new StringReader(s);
		parser.ReInit(r);
		return parser.expression();
	}
	
	void push(List<Element> list) throws Exception {
		stack.push(new ElementPosition(list));
	}
	
	void pop() throws Exception {
		if (stack.size() == 1)
			throw new Exception("already at the top...");
		stack.pop();
	}

	Element forward() throws Exception {
		ElementPosition current = stack.peek();
		if (!current.iterator.hasNext())
			throw new Exception("last element");
		current.element = current.iterator.next();
		return current.element;
	}
	
	Element back() throws Exception {
		ElementPosition current = stack.peek();
		current.iterator.previous();
		if (!current.iterator.hasPrevious())
			throw new Exception("first element");
		current.element = current.iterator.previous();
		return current.element;
	}

	EvaluationContext getEvaluationContext() {
		return evaluationContext;
	}

	Element getCurrentElement() {
		ElementPosition current = stack.peek();
		return current.element;
	}
	
	Element[] stack() {
		Element[] result = new Element[stack.size()];
		int i = result.length - 1;
		for (ElementPosition p : stack)
			result[i--] = p.element;
		return result;
	}
	
	public Shell getOwner() {
		return owner;
	}
	
	public Locale getLocale() {
		return locale;
	}

	public VariableLibrary getVarLib() {
		return varLib;
	}

	public LibraryResolver getResolver() {
		return resolver;
	}
	
	Evaluator parseAndResolveExpression(String s) throws ParseException, ResolverException {
		Expression expr = parseExpression(s);
		return expr.resolveExpressions(resolver);
	}
}
