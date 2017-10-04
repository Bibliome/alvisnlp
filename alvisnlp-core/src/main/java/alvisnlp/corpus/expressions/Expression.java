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


package alvisnlp.corpus.expressions;

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

	public void toString(Appendable a) throws IOException {
		headToString(a);
		a.append('(');
		boolean notFirst = false;
		for (Expression fc : args) {
			if (notFirst)
				a.append(", ");
			else
				notFirst = true;
			fc.toString(a);
		}
		a.append(')');
	}
	
	@Override
	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			toString(sb);
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
