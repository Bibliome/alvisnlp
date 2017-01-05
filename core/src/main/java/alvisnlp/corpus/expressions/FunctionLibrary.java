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

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.bibliome.util.Strings;

import alvisnlp.documentation.Documentable;

public abstract class FunctionLibrary implements Documentable {
	public abstract String getName();

	public abstract Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException;
	
	protected final Evaluator cannotResolve(List<String> ftors, List<Expression> args) throws ResolverException {
		if (ftors.isEmpty())
			throw new ResolverException("could not resolve " + getName() + "/" + args.size());
		throw new ResolverException("could not resolve " + getName() + ":" + toString(ftors) + "/" + args.size());
	}

	protected static final String toString(List<String> ftors) {
		return Strings.join(ftors, ':');
	}

	protected static void checkMinFtors(List<String> ftors, int n) throws ResolverException {
		if (ftors.size() < n)
			throw new ResolverException("missing functor in " + toString(ftors) + " (expected at least " + n + ')');
	}
	
	protected static void checkMaxFtors(List<String> ftors, int n) throws ResolverException {
		if (ftors.size() > n)
			throw new ResolverException("extra functor in " + toString(ftors) + "(expected at most " + n + ')');
	}
	
	protected static void checkRangeFtors(List<String> ftors, int min, int max) throws ResolverException {
		checkMinFtors(ftors, min);
		checkMaxFtors(ftors, max);
	}
	
	protected static void checkExactFtors(List<String> ftors, int n) throws ResolverException {
		checkRangeFtors(ftors, n, n);
	}
	
	protected static void checkMinArity(List<String> ftors, List<Expression> args, int n) throws ResolverException {
		if (args.size() < n)
			throw new ResolverException("missing arguments in " + toString(ftors) + " (expected at least " + n + ')');
	}
	
	protected static void checkMaxArity(List<String> ftors, List<Expression> args, int n) throws ResolverException {
		if (args.size() > n)
			throw new ResolverException("extra arguments in " + toString(ftors) + " (expected at most " + n + ')');
	}
	
	protected static void checkRangeArity(List<String> ftors, List<Expression> args, int min, int max) throws ResolverException {
		checkMinArity(ftors, args, min);
		checkMaxArity(ftors, args, max);
	}
	
	protected static void checkExactArity(List<String> ftors, List<Expression> args, int n) throws ResolverException {
		checkRangeArity(ftors, args, n, n);
	}

	public LibraryResolver newLibraryResolver() throws ResolverException {
		LibraryResolver result = new LibraryResolver();
		result.addLibrary(this);
		return result;
	}

	public LibraryResolver newLibraryResolver(LibraryResolver parent) throws ResolverException {
		LibraryResolver result = new LibraryResolver(parent);
		result.addLibrary(this);
		return result;
	}
	
	public static <L extends FunctionLibrary> L load(Class<L> klass) throws ResolverException {
//		ServiceLoader<L> serviceLoader = ServiceLoader.load(klass);
		ServiceLoader<L> serviceLoader = ServiceLoader.load(klass, FunctionLibrary.class.getClassLoader());
		Iterator<L> iterator = serviceLoader.iterator();
		if (iterator.hasNext())
			return iterator.next();
		throw new ResolverException("could not find library");
	}
}
