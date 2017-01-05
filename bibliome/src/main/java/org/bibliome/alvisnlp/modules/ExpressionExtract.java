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

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.ExpressionExtract.ExpressionExtractResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.Strings;
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
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(obsoleteUseInstead=TabularExport.class)
public class ExpressionExtract extends CorpusModule<ExpressionExtractResolvedObjects> {
	private Expression target;
	private Expression[] fields;
	private TargetStream outFile;
	private String[] headers;
	private Character separator = '\t';

	@SuppressWarnings("hiding")
	class ExpressionExtractResolvedObjects extends ResolvedObjects {
		private final Evaluator target;
		private final Evaluator[] fields;
		
		private ExpressionExtractResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
			super(ctx, ExpressionExtract.this);
			target = rootResolver.resolveNullable(ExpressionExtract.this.target);
			fields = rootResolver.resolveArray(ExpressionExtract.this.fields, Evaluator.class);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			target.collectUsedNames(nameUsage, defaultType);
			for (Evaluator field : fields) {
				field.collectUsedNames(nameUsage, defaultType);
			}
		}
	}
	
	@Override
	protected ExpressionExtractResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ExpressionExtractResolvedObjects(ctx);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			PrintStream out = outFile.getPrintStream();
			if (headers != null) {
				if (headers.length != fields.length)
					getLogger(ctx).warning("different number of fields and headers");
				Strings.join(out, headers, separator);
				out.println();
			}
			ExpressionExtractResolvedObjects resObj = getResolvedObjects();
			Logger logger = getLogger(ctx);
			EvaluationContext evalCtx = new EvaluationContext(logger);
			for (Element elt : Iterators.loop(resObj.target.evaluateElements(evalCtx, corpus))) {
				String[] values = new String[fields.length];
				for (int i = 0; i < values.length; ++i)
					values[i] = resObj.fields[i].evaluateString(evalCtx, elt).replace('\n', ' ');
				Strings.join(out, values, separator);
				out.println();
			}
			out.close();
		}
		catch (IOException ioe) {
			rethrow(ioe);
		}
	}

	@Param
	public Expression getTarget() {
		return target;
	}

	@Param
	public Expression[] getFields() {
		return fields;
	}

	@Param
	public TargetStream getOutFile() {
		return outFile;
	}

	@Param(mandatory=false)
	public String[] getHeaders() {
		return headers;
	}

	public void setHeaders(String[] headers) {
		this.headers = headers;
	}

	public void setTarget(Expression target) {
		this.target = target;
	}

	public void setFields(Expression[] fields) {
		this.fields = fields;
	}

	public void setOutFile(TargetStream outFile) {
		this.outFile = outFile;
	}
}
