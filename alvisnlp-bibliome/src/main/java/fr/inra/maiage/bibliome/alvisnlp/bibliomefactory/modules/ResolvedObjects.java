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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUser;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;

public class ResolvedObjects implements NameUser {
	protected final LibraryResolver rootResolver;
	private final Evaluator active;
	
	public ResolvedObjects(ProcessingContext ctx, CorpusModule<? extends ResolvedObjects> module) throws ResolverException {
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
