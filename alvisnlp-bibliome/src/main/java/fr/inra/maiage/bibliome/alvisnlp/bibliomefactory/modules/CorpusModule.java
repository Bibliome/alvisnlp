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

import java.util.ServiceLoader;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.UserFunction;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.UserLibrary;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.standard.ModuleLibrary;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ConstantsLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.FunctionLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUser;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ModuleBase;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;

public abstract class CorpusModule<T extends ResolvedObjects> extends ModuleBase implements NameUser {
	private Expression active = ConstantsLibrary.TRUE;
	private UserFunction[] userFunctions = new UserFunction[0];
	
	private T resolvedObjects;

	@Param
	public Expression getActive() {
		return active;
	}

	@Param
	public UserFunction[] getUserFunctions() {
		return userFunctions;
	}

	public void setUserFunctions(UserFunction[] userFunctions) {
		this.userFunctions = userFunctions;
	}

	public void setActive(Expression active) {
		this.active = active;
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		getResolvedObjects().collectUsedNames(nameUsage, defaultType);
	}
	
	public T getResolvedObjects() {
		return resolvedObjects;
	}
	
	protected abstract T createResolvedObjects(ProcessingContext ctx) throws ResolverException;

	@Override
	public boolean testProcess(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		if (super.testProcess(ctx, corpus)) {
//			System.err.println("[testProcess] getClass() = " + getClass()); 
//			System.err.println("[testProcess] resolvedObjects = " + resolvedObjects);
			Evaluator active = resolvedObjects.getActive();
			if (active.evaluateBoolean(new EvaluationContext(logger), corpus))
				return true;
			getLogger(ctx).info("skipping (inactive)");
		}
		return false;
	}

	protected LibraryResolver getLibraryResolver(ProcessingContext ctx) throws ResolverException {
		LibraryResolver result = new LibraryResolver();
		Class<FunctionLibrary> klass = FunctionLibrary.class;
		for (FunctionLibrary lib : ServiceLoader.load(klass, klass.getClassLoader())) {
			result.addLibrary(lib);
			if (lib instanceof ModuleLibrary) {
				ModuleLibrary modLib = ((ModuleLibrary) lib);
				modLib.setCtx(ctx);
				modLib.setModule(this);
			}
			if (lib instanceof UserLibrary) {
				fillUserLib((UserLibrary) lib);
			}
		}
		// XXX workaround
		if (!result.hasLibrary(ConstantsLibrary.NAME)) {
			FunctionLibrary lib = new ConstantsLibrary();
			result.addLibrary(lib);
		}
		return result;
	}
	
	private void fillUserLib(UserLibrary userLib) {
		for (Module mod = this; mod != null; mod = mod.getSequence()) {
			if (mod instanceof CorpusModule) {
				CorpusModule<?> cmod = (CorpusModule<?>) mod;
				for (UserFunction fun : cmod.getUserFunctions()) {
					userLib.addFunction(fun);
				}
			}
		}
	}

	@TimeThis(task="commit")
	protected void commit(ProcessingContext ctx, EvaluationContext evalCtx) {
		evalCtx.commit();
	}

	@Override
	public void clean() {
		super.clean();
		resolvedObjects = null;
//		System.err.println("clean");
	}

	@Override
	public void init(ProcessingContext ctx) throws ModuleException {
		super.init(ctx);
		resolvedObjects = createResolvedObjects(ctx);
//		System.err.println("getClass() = " + getClass()); 
//		System.err.println("resolvedObjects = " + resolvedObjects);
	}
}
