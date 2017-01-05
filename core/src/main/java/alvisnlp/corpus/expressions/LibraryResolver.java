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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LibraryResolver {
	private final LibraryResolver parent;
	private final Map<String,FunctionLibrary> libraries = new HashMap<String,FunctionLibrary>();
	
	public LibraryResolver(LibraryResolver parent) {
		this.parent = parent;
	}
	
	public LibraryResolver() {
		this(null);
	}
	
	public boolean hasLibrary(String name) {
		if (libraries.containsKey(name)) {
			return true;
		}
		if (parent == null) {
			return false;
		}
		return parent.hasLibrary(name);
	}
	
	public FunctionLibrary resolveLibrary(String name) throws ResolverException {
		if (libraries.containsKey(name))
			return libraries.get(name);
		if (parent == null)
			throw new ResolverException("unknown library '" + name + "'");
		return parent.resolveLibrary(name);
	}
	
	public void addLibrary(FunctionLibrary lib) throws ResolverException {
		String name = lib.getName();
		if (libraries.containsKey(name))
			throw new ResolverException("duplicate library " + name + name + "'");
		libraries.put(name, lib);
	}
	
	public <T,U extends Resolvable<T>> List<T> resolveList(List<U> list, List<T> result) throws ResolverException {
		for (U r : list) {
			T s = r.resolveExpressions(this);
			if (s == null) {
				System.err.println("BOUH! " + r);
			}
			result.add(s);
		}
		return result;
	}
	
	public <T,U extends Resolvable<T>> List<T> resolveList(List<U> list) throws ResolverException {
		return resolveList(list, new ArrayList<T>());
	}
	
	public <T,U extends Resolvable<T>> T[] resolveArray(U[] expressions, Class<T> componentClass) throws ResolverException {
		@SuppressWarnings("unchecked")
		T[] result = (T[]) Array.newInstance(componentClass, expressions.length);
		for (int i = 0; i < result.length; ++i)
			result[i] = expressions[i].resolveExpressions(this);
		return result;
	}
	
	public <T,U extends Resolvable<T>> Collection<T> resolveCollection(Collection<U> expressions, Collection<T> evaluators) throws ResolverException {
		for (U e : expressions)
			evaluators.add(e.resolveExpressions(this));
		return evaluators;
	}

	public <T,U extends Resolvable<T>> Collection<T> resolveCollection(Collection<U> expressions) throws ResolverException {
		return resolveCollection(expressions, new ArrayList<T>(expressions.size()));
	}
	
	public <T> T resolveNullable(Resolvable<T> resolvable) throws ResolverException {
		if (resolvable == null)
			return null;
		return resolvable.resolveExpressions(this);
	}
}
