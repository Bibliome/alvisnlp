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

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;
import alvisnlp.module.ProcessingContext;

public class ResolvedObjects implements NameUser {
	protected final LibraryResolver rootResolver;
	private final Evaluator active;
	
	public ResolvedObjects(ProcessingContext<Corpus> ctx, CorpusModule<? extends ResolvedObjects> module) throws ResolverException {
		rootResolver = module.getLibraryResolver(ctx);
		active = rootResolver.resolveNullable(module.getActive());
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		active.collectUsedNames(nameUsage, defaultType);
	}
	
	Evaluator getActive() {
		return active;
	}
}
