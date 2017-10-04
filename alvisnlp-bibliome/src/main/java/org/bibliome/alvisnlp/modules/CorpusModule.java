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

import java.util.ServiceLoader;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.library.UserFunction;
import org.bibliome.alvisnlp.library.UserLibrary;
import org.bibliome.alvisnlp.library.standard.ModuleLibrary;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.expressions.ConstantsLibrary;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.ModuleBase;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

public abstract class CorpusModule<T extends ResolvedObjects> extends ModuleBase<Corpus> implements NameUser {
	private Expression active = ConstantsLibrary.TRUE;
	private UserFunction[] userFunctions = new UserFunction[0];
	
	private T resolvedObjects;

	@Param(defaultDoc = "Expression evaluated as a boolean with the corpus as context element. If the result is false, then the module does not process the module.")
	public Expression getActive() {
		return active;
	}

	@Param(defaultDoc = "Set user functions available for this module. If this module is a sequence, then all compounds inherit its functions.")
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
	
	protected T getResolvedObjects() {
		return resolvedObjects;
	}
	
	protected abstract T createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException;

	@Override
	public boolean testProcess(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
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

	protected LibraryResolver getLibraryResolver(ProcessingContext<Corpus> ctx) throws ResolverException {
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
		for (Module<Corpus> mod = this; mod != null; mod = mod.getSequence()) {
			if (mod instanceof CorpusModule) {
				CorpusModule<?> cmod = (CorpusModule<?>) mod;
				for (UserFunction fun : cmod.getUserFunctions()) {
					userLib.addFunction(fun);
				}
			}
		}
	}

	@SuppressWarnings("static-method")
	@TimeThis(task="commit")
	protected void commit(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, EvaluationContext evalCtx) {
		evalCtx.commit();
	}

	@Override
	public void clean() {
		super.clean();
		resolvedObjects = null;
//		System.err.println("clean");
	}

	@Override
	public void init(ProcessingContext<Corpus> ctx) throws ModuleException {
		super.init(ctx);
		resolvedObjects = createResolvedObjects(ctx);
//		System.err.println("getClass() = " + getClass()); 
//		System.err.println("resolvedObjects = " + resolvedObjects);
	}
}
