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


package org.bibliome.alvisnlp.modules.trie;

import java.io.IOException;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.trie.ElementProjector.ElementProjectorResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.VariableLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.VariableLibrary.Variable;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ActionInterface;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.marshall.Decoder;
import fr.inra.maiage.bibliome.util.marshall.Encoder;
import fr.inra.maiage.bibliome.util.trie.Trie;

@AlvisNLPModule
public abstract class ElementProjector extends TrieProjector<ElementProjectorResolvedObjects,Element> implements ActionInterface {
	private Expression entries;
	private Expression key;
	private Expression action;
	
	static class ElementProjectorResolvedObjects extends SectionResolvedObjects {
		private final Evaluator entries;
		private final Evaluator key;
		private final Variable entryVar;
		private final Evaluator action;
		private final EvaluationContext actionEvalCtx;
		
		ElementProjectorResolvedObjects(ElementProjector module, ProcessingContext<Corpus> ctx) throws ResolverException {
			super(ctx, module);
			LibraryResolver rootResolver = module.getLibraryResolver(ctx);
			this.entries = module.entries.resolveExpressions(rootResolver);
			this.key = module.key.resolveExpressions(rootResolver);
			VariableLibrary varLib = new VariableLibrary("entry");
			entryVar = varLib.newVariable(null);
			LibraryResolver entryResolver = varLib.newLibraryResolver(rootResolver);
			this.action = module.action.resolveExpressions(entryResolver);
			this.actionEvalCtx = new EvaluationContext(module.getLogger(ctx), module);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			entries.collectUsedNames(nameUsage, defaultType);
			key.collectUsedNames(nameUsage, defaultType);
			action.collectUsedNames(nameUsage, defaultType);
		}
	}

	@Override
	protected void fillTrie(Logger logger, Trie<Element> trie, Corpus corpus) throws IOException, ModuleException {
		ElementProjectorResolvedObjects resObj = getResolvedObjects();
		EvaluationContext evalCtx = new EvaluationContext(logger);
		for (Element entry : Iterators.loop(resObj.entries.evaluateElements(evalCtx, corpus))) {
			String key = resObj.key.evaluateString(evalCtx, entry);
//			System.err.println("key = " + key);
			trie.addEntry(key, entry);
		}
	}

	@Override
	protected void finish() {
		ElementProjectorResolvedObjects resObj = getResolvedObjects();
		resObj.actionEvalCtx.commit();
	}

	@Override
	@TimeThis(task="create-trie", category=TimerCategory.LOAD_RESOURCE)
	protected Trie<Element> getTrie(ProcessingContext<Corpus> ctx, Logger logger, Corpus corpus) throws IOException, ModuleException {
		return super.getTrie(ctx, logger, corpus);
	}

	@Override
	protected boolean marshallingSupported() {
		return false;
	}

	@Override
	protected Decoder<Element> getDecoder() {
		return null;
	}

	@Override
	protected Encoder<Element> getEncoder() {
		return null;
	}

	@Override
	protected void handleMatch(Element value, Annotation a) {
		ElementProjectorResolvedObjects resObj = getResolvedObjects();
		resObj.entryVar.set(value);
		Iterators.deplete(resObj.action.evaluateElements(resObj.actionEvalCtx, a));
	}

	@Override
	protected ElementProjectorResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ElementProjectorResolvedObjects(this, ctx);
	}

	@Param
	public Expression getEntries() {
		return entries;
	}

	@Param
	public Expression getKey() {
		return key;
	}

	@Param
	public Expression getAction() {
		return action;
	}

	public void setEntries(Expression entries) {
		this.entries = entries;
	}

	public void setKey(Expression key) {
		this.key = key;
	}

	public void setAction(Expression action) {
		this.action = action;
	}
}
