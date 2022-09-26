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


package fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



public class Expression implements Resolvable<Evaluator> {
	private final String lib;
	private final List<String> ftors;
	private final List<Expression> args;
	
	public Expression(String lib, List<String> ftors, List<Expression> args) {
		super();
		this.lib = lib;
		this.ftors = ftors;
		this.args = args;
	}
	
	@SuppressWarnings("unchecked")
	public Expression(String lib, List<Expression> args) {
		this(lib, Collections.EMPTY_LIST, args);
	}
	
	public Expression(String lib, Expression... args) {
		this(lib, Arrays.asList(args));
	}
	
	public Expression(String lib, String ftor, Expression... args) {
		this(lib, Collections.singletonList(ftor), Arrays.asList(args));
	}
	
	public Expression(String lib, String ftor, List<Expression> args) {
		this(lib, Collections.singletonList(ftor), args);
	}
	
	public Expression(String lib, String ftor1, String ftor2, Expression... args) {
		this(lib, Arrays.asList(ftor1, ftor2), Arrays.asList(args));
	}
	
	public void tree(Appendable a, String indent, String unitIndent) throws IOException {
		a.append(indent);
		headToString(a);
		a.append('\n');
		String newIndent = indent + unitIndent;
		for (Expression fc : args)
			fc.tree(a, newIndent, unitIndent);
	}
	
	private void headToString(Appendable a) throws IOException {
		a.append(lib);
		for (String ftor : ftors) {
			a.append(':');
			a.append(ftor);
		}		
	}

	public void defaultToString(Appendable a) throws IOException {
		headToString(a);
		if (args.size() > 0) {
			a.append('(');
			boolean notFirst = false;
			for (Expression arg : args) {
				if (notFirst)
					a.append(", ");
				else
					notFirst = true;
				arg.toString(a);
			}
			a.append(')');
		}
	}
	
	private void inop(Appendable a) throws IOException {
		inop(a, " " + ftors.get(0) + " ");
	}
	
	private void inop(Appendable a, String op) throws IOException {
		boolean notFirst = false;
		for (Expression arg : args) {
			if (notFirst) {
				a.append(op);
			}
			else {
				notFirst = true;
			}
			arg.toString(a);
		}
	}
	
	private void preop(Appendable a) throws IOException {
		a.append(ftors.get(0));
		args.get(0).toString(a);
	}
	
	private void constantToString(Appendable a) throws IOException {
		String firstFtor = ftors.get(0);
		String value = ftors.get(1);
		if (firstFtor.equals("string")) {
			a.append('"');
			a.append(value);
			a.append('"');
			return;
		}
		a.append(value);
	}
	
	private void booleanToString(Appendable a) throws IOException {
		String op = ftors.get(0);
		if (op.equals("not")) {
			preop(a);
			return;
		}
		inop(a);
	}
	
	private void comparisonToString(Appendable a) throws IOException {
		if (ftors.get(0).equals("any")) {
			a.append("any ");
			a.append(ftors.get(1));
			a.append(" == ");
			args.get(0).toString(a);
			return;
		}
		inop(a);
	}
	
	private void conditionalToString(Appendable a) throws IOException {
		a.append("if ");
		args.get(0).toString(a);
		a.append(" then ");
		args.get(1).toString(a);
		a.append(" else ");
		args.get(2).toString(a);
	}
	
	private void libEllipsisToString(Appendable a) throws IOException {
		String firstFtor = ftors.get(0);
		Expression pretty = new Expression(firstFtor, ftors.subList(1, ftors.size()), args);
		pretty.toString(a);
	}
	
	private void navToString(Appendable a) throws IOException {
		String firstFtor = ftors.get(0);
		switch (firstFtor) {
			case "after":
			case "before":
			case "inside":
			case "outside":
			case "overlapping":
			case "span":
			case "xafter":
			case "xbefore":
			case "xinside":
			case "xoutside":
			case "xoverlapping":
			case "xspan":
			case "layer":
			case "relations":
			case "documents":
			case "args":
			case "sections":
			case "tuples":
			case "relation":
			case "corpus":
			case "document":
			case "section": {
				libEllipsisToString(a);
				break;
			}
			case ".": {
				inop(a, ".");
				break;
			}
			case "assign": {
				args.get(0).toString(a);
				a.append(" as ");
				a.append(ftors.get(1));
				break;
			}
			case "|": {
				inop(a);
				break;
			}
			case "$": {
				a.append('$');
				break;
			}
			default:
				defaultToString(a);
		}
	}
	
	private void propertiesToString(Appendable a) throws IOException {
		String firstFtor = ftors.get(0);
		switch (firstFtor) {
			case "start":
			case "end":
			case "length":
			case "contents": {
				a.append(firstFtor);
				break;
			}
			case "@": {
				a.append('@');
				a.append(ftors.get(1));
				break;
			}
			default:
				defaultToString(a);
		}
	}
	
	private void selectorToString(Appendable a) throws IOException {
		String firstFtor = ftors.get(0);
		a.append(firstFtor.charAt(0));
		args.get(0).toString(a);
		if (args.size() == 2) {
			a.append(',');
			args.get(1).toString(a);
		}
		a.append(firstFtor.charAt(1));
	}
	
	private void setlayerToString(Appendable a) throws IOException {
		String firstFtor = ftors.get(0);
		switch (firstFtor) {
			case "add":
			case "remove": {
				Expression pretty = new Expression(firstFtor, ftors.subList(1, ftors.size()), args);
				pretty.toString(a);
				break;
			}
			default:
				defaultToString(a);
		}
	}
	
	private void strToString(Appendable a) throws IOException {
		if (ftors.get(0).equals("concat")) {
			inop(a, " ^ ");
			return;
		}
		defaultToString(a);
	}
	
	public void toString(Appendable a) throws IOException {
		switch (lib)  {
			case "constant": {
				constantToString(a);
				break;
			}
			case "arithmetic": {
				inop(a);
				break;
			}
			case "boolean": {
				booleanToString(a);
				break;
			}
			case "comparison": {
				comparisonToString(a);
				break;
			}
			case "conditional": {
				conditionalToString(a);
				break;
			}
			case "convert": {
				libEllipsisToString(a);
				break;
			}
			case "nav": {
				navToString(a);
				break;
			}
			case "properties": {
				propertiesToString(a);
				break;
			}
			case "select": {
				selectorToString(a);
				break;
			}
			case "setlayer": {
				setlayerToString(a);
				break;
			}
			case "str": {
				strToString(a);
				break;
			}
			default:
				defaultToString(a);
		}
	}
	
	@Override
	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			toString(sb);
			if (sb.length() == 0) {
				return super.toString();
			}
			return sb.toString();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Evaluator resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return resolver.resolveLibrary(lib).resolveExpression(resolver, ftors, args);
	}
}
