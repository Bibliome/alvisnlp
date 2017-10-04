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

import org.bibliome.alvisnlp.modules.TabularExport.TabularExportResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.Strings;
import org.bibliome.util.Timer;
import org.bibliome.util.files.OutputDirectory;
import org.bibliome.util.files.OutputFile;
import org.bibliome.util.streams.FileTargetStream;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.corpus.expressions.VariableLibrary;
import alvisnlp.corpus.expressions.VariableLibrary.Variable;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule
public class TabularExport extends CorpusModule<TabularExportResolvedObjects> {
	private OutputDirectory outDir;
	private Expression files;
	private Expression fileName;
	private Expression lines;
	private Expression[] headers;
	private Expression[] footers;
	private Expression[] columns;
	private String charset = "UTF-8";
	private String separator = "\t";
	private Boolean append = false;
	private Boolean trim = false;
	
	static class TabularExportResolvedObjects extends ResolvedObjects {
		private final Variable lineVar;
		private final Evaluator files;
		private final Evaluator fileName;
		private final Evaluator lines;
		private final Evaluator[] headers;
		private final Evaluator[] footers;
		private final Evaluator[] columns;

		private TabularExportResolvedObjects(ProcessingContext<Corpus> ctx, TabularExport module) throws ResolverException {
			super(ctx, module);
			VariableLibrary lineLib = new VariableLibrary("line");
			lineVar = lineLib.newVariable(null);
			LibraryResolver columnResolver = lineLib.newLibraryResolver(rootResolver);
			files = rootResolver.resolveNullable(module.files);
			fileName = rootResolver.resolveNullable(module.fileName);
			lines = rootResolver.resolveNullable(module.lines);
			headers = module.headers != null ? rootResolver.resolveArray(module.headers, Evaluator.class) : null;
			footers = module.footers != null ? rootResolver.resolveArray(module.footers, Evaluator.class) : null;
			columns = columnResolver.resolveArray(module.columns, Evaluator.class);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			files.collectUsedNames(nameUsage, defaultType);
			fileName.collectUsedNames(nameUsage, defaultType);
			lines.collectUsedNames(nameUsage, defaultType);
			if (headers != null) {
				nameUsage.collectUsedNamesArray(headers, defaultType);
			}
			if (footers != null) {
				nameUsage.collectUsedNamesArray(footers, defaultType);
			}
			nameUsage.collectUsedNamesArray(columns, defaultType);
		}
	}
	
	private static final String[] evaluateArray(Evaluator[] evaluators, EvaluationContext ctx, Element elt) {
		String[] result = new String[evaluators.length];
		for (int i = 0; i < result.length; ++i)
			result[i] = evaluators[i].evaluateString(ctx, elt);
		return result;
	}
	
	@Override
	protected TabularExportResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new TabularExportResolvedObjects(ctx, this);
	}
	
	private void printLine(Timer<TimerCategory> writeTimer, PrintStream ps, Evaluator[] valuesEvaluators, EvaluationContext evalCtx, Element elt) {
		String[] values = evaluateArray(valuesEvaluators, evalCtx, elt);
		writeTimer.start();
		String line = Strings.join(values, separator);
		if (trim) {
			line = line.trim();
		}
		ps.println(line);
		writeTimer.stop();
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		TabularExportResolvedObjects resObj = getResolvedObjects();
    	Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		Timer<TimerCategory> writeTimer = getTimer(ctx, "write", TimerCategory.EXPORT, false);
		for (Element fileElement : Iterators.loop(resObj.files.evaluateElements(evalCtx, corpus))) {
			String fileNameString = resObj.fileName.evaluateString(evalCtx, fileElement);
			OutputFile outputFile = new OutputFile(outDir, fileNameString);
			TargetStream target = new FileTargetStream(charset, outputFile, append);
			try (PrintStream ps = target.getPrintStream()) {
				if (headers != null) {
					printLine(writeTimer, ps, resObj.headers, evalCtx, fileElement);
//					String[] values = evaluateArray(resObj.headers, evalCtx, fileElement);
//					writeTimer.start();
//					Strings.join(ps, values, separator);
//					ps.println();
//					writeTimer.stop();
				}
				for (Element lineElement : Iterators.loop(resObj.lines.evaluateElements(evalCtx, fileElement))) {
					resObj.lineVar.set(lineElement);
					printLine(writeTimer, ps, resObj.columns, evalCtx, lineElement);
//					String[] values = evaluateArray(resObj.columns, evalCtx, lineElement);
//					writeTimer.start();
//					Strings.join(ps, values, separator);
//					ps.println();
//					writeTimer.stop();
				}
				if (footers != null) {
					printLine(writeTimer, ps, resObj.footers, evalCtx, fileElement);
				}
			}
			catch (IOException e) {
				rethrow(e);
			}
		}
	}

	@Param
	public OutputDirectory getOutDir() {
		return outDir;
	}

	@Param
	public Expression getFiles() {
		return files;
	}

	@Param
	public Expression getFileName() {
		return fileName;
	}

	@Param
	public Expression getLines() {
		return lines;
	}

	@Param
	public Expression[] getColumns() {
		return columns;
	}

	@Param
	public String getCharset() {
		return charset;
	}

	@Param
	public String getSeparator() {
		return separator;
	}

	@Param
	public Boolean getAppend() {
		return append;
	}

	@Param(mandatory=false)
	public Expression[] getHeaders() {
		return headers;
	}

	@Param(mandatory=false)
	public Expression[] getFooters() {
		return footers;
	}

	@Param
	public Boolean getTrim() {
		return trim;
	}

	public void setTrim(Boolean trim) {
		this.trim = trim;
	}

	public void setFooters(Expression[] footers) {
		this.footers = footers;
	}

	public void setHeaders(Expression[] headers) {
		this.headers = headers;
	}

	public void setAppend(Boolean append) {
		this.append = append;
	}

	public void setOutDir(OutputDirectory outDir) {
		this.outDir = outDir;
	}

	public void setFiles(Expression files) {
		this.files = files;
	}

	public void setFileName(Expression fileName) {
		this.fileName = fileName;
	}

	public void setLines(Expression lines) {
		this.lines = lines;
	}

	public void setColumns(Expression[] columns) {
		this.columns = columns;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}
}
