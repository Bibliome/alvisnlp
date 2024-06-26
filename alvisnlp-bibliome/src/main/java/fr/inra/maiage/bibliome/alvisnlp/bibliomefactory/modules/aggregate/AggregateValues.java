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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.aggregate;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.aggregate.AggregateValues.AggregateValuesResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.defaultmap.DefaultMap;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

@AlvisNLPModule
public class AggregateValues extends CorpusModule<AggregateValuesResolvedObjects> {
	private Expression entries;
	private Expression key;
	private Aggregator[] aggregators = new Aggregator[] {};
	private Character separator = '\t';
	private TargetStream outFile;

	static class AggregateValuesResolvedObjects extends ResolvedObjects {
		private final Evaluator entries;
		private final Evaluator key;
		private final Aggregator.Resolved[] aggregators;

		private AggregateValuesResolvedObjects(ProcessingContext ctx, AggregateValues module) throws ResolverException {
			super(ctx, module);
			entries = module.entries.resolveExpressions(rootResolver);
			key = module.key.resolveExpressions(rootResolver);
			aggregators = rootResolver.resolveArray(module.aggregators, Aggregator.Resolved.class);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			entries.collectUsedNames(nameUsage, defaultType);
			key.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesArray(aggregators, defaultType);
		}
	}

	@Override
	protected AggregateValuesResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new AggregateValuesResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		EntryMap entryMap = aggregateEntries(ctx, corpus);
		writeEntries(ctx, entryMap);
	}

	@TimeThis(task="aggregate")
	protected EntryMap aggregateEntries(ProcessingContext ctx, Corpus corpus) {
		AggregateValuesResolvedObjects resObj = getResolvedObjects();
		EntryMap result = new EntryMap(resObj.aggregators);
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		for (Element entry : Iterators.loop(resObj.entries.evaluateElements(evalCtx, corpus))) {
			String entryKey = resObj.key.evaluateString(evalCtx, entry);
			Object[] values = result.safeGet(entryKey);
			for (int i = 0; i < values.length; ++i) {
				resObj.aggregators[i].incorporateEntry(values[i], evalCtx, entry);
			}
		}
		return result;
	}

	@TimeThis(task="write", category=TimerCategory.EXPORT)
	protected void writeEntries(ProcessingContext ctx, EntryMap entryMap) throws ProcessingException {
		AggregateValuesResolvedObjects resObj = getResolvedObjects();
		try (PrintStream out = outFile.getPrintStream()) {
			for (Map.Entry<String,Object[]> e : entryMap.entrySet()) {
				out.print(e.getKey());
				Object[] values = e.getValue();
				for (int i = 0; i < values.length; ++i) {
					out.print(separator);
					String value = resObj.aggregators[i].toString(values[i]);
					out.print(value);
				}
				out.println();
			}
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

	protected static class EntryMap extends DefaultMap<String,Object[]> {
		private final Aggregator.Resolved[] aggregators;

		private EntryMap(Aggregator.Resolved[] aggregators) {
			super(true, new TreeMap<String,Object[]>());
			this.aggregators = aggregators;
		}

		@Override
		protected Object[] defaultValue(String key) {
			Object[] result = new Object[aggregators.length];
			for (int i = 0; i < result.length; ++i) {
				result[i] = aggregators[i].createValue();
			}
			return result;
		}
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
	public Aggregator[] getAggregators() {
		return aggregators;
	}

	@Param
	public Character getSeparator() {
		return separator;
	}

	@Param
	public TargetStream getOutFile() {
		return outFile;
	}

	public void setEntries(Expression entries) {
		this.entries = entries;
	}

	public void setKey(Expression key) {
		this.key = key;
	}

	public void setAggregators(Aggregator[] aggregators) {
		this.aggregators = aggregators;
	}

	public void setSeparator(Character separator) {
		this.separator = separator;
	}

	public void setOutFile(TargetStream outFile) {
		this.outFile = outFile;
	}
}
