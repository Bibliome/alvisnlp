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


package org.bibliome.alvisnlp.modules.aggregate;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.alvisnlp.modules.aggregate.AggregateValues.AggregateValuesResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.defaultmap.DefaultMap;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

@AlvisNLPModule(beta=true)
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
		
		private AggregateValuesResolvedObjects(ProcessingContext<Corpus> ctx, AggregateValues module) throws ResolverException {
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
	protected AggregateValuesResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new AggregateValuesResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		EntryMap entryMap = aggregateEntries(ctx, corpus);
		writeEntries(ctx, entryMap);
	}

	@TimeThis(task="aggregate")
	protected EntryMap aggregateEntries(ProcessingContext<Corpus> ctx, Corpus corpus) {
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
	protected void writeEntries(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, EntryMap entryMap) throws ProcessingException {
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
			rethrow(e);
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
