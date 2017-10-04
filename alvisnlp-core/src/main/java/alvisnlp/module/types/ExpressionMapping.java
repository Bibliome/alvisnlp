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


package alvisnlp.module.types;

import java.util.LinkedHashMap;
import java.util.Map;

import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.Resolvable;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;

public class ExpressionMapping extends LinkedHashMap<String,Expression> implements Resolvable<EvaluatorMapping>, NameUser {
	private static final long serialVersionUID = 1L;

	public ExpressionMapping() {
		super();
	}

	@Override
	public EvaluatorMapping resolveExpressions(LibraryResolver resolver) throws ResolverException {
		EvaluatorMapping result = new EvaluatorMapping();
		for (Map.Entry<String,Expression> e : entrySet())
			result.put(e.getKey(), e.getValue().resolveExpressions(resolver));
		return result;
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		if (defaultType != null) {
			nameUsage.addNames(defaultType, keySet());
		}
	}
}
