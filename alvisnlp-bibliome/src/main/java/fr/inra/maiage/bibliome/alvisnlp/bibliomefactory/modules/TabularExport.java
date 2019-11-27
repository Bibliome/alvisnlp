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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.TabularExport.TabularExportResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ConstantsLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.VariableLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.VariableLibrary.Variable;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Checkable;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.Timer;
import fr.inra.maiage.bibliome.util.files.OutputDirectory;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

@AlvisNLPModule
public class TabularExport extends CorpusModule<TabularExportResolvedObjects> implements Checkable {
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
	private File corpusFile = null;
	private Boolean trueCSV = false;
	
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
			if (module.corpusFile == null) {
				files = rootResolver.resolveNullable(module.files);
				fileName = rootResolver.resolveNullable(module.fileName);
			}
			else {
				files = DefaultExpressions.SELF.resolveExpressions(rootResolver);
				fileName = ConstantsLibrary.getInstance(module.corpusFile.getPath());
			}
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
	
	private static void printCSVLine(Timer<TimerCategory> writeTimer, CSVPrinter printer, Evaluator[] valuesEvaluators, EvaluationContext evalCtx, Element elt) throws IOException {
		String[] values = evaluateArray(valuesEvaluators, evalCtx, elt);
		writeTimer.start();
		printer.printRecord((Object[]) values);
		//printer.println();
		writeTimer.stop();
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		TabularExportResolvedObjects resObj = getResolvedObjects();
    	Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		Timer<TimerCategory> writeTimer = getTimer(ctx, "write", TimerCategory.EXPORT, false);
		try {
			for (Element fileElement : Iterators.loop(resObj.files.evaluateElements(evalCtx, corpus))) {
				String fileNameString = resObj.fileName.evaluateString(evalCtx, fileElement);
				if (trueCSV) {
					writeCSV(writeTimer, evalCtx, fileElement, fileNameString);
				}
				else {
					writeRegular(writeTimer, evalCtx, fileElement, fileNameString);
				}
			}
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}
	
	private void writeRegular(Timer<TimerCategory> writeTimer, EvaluationContext evalCtx, Element fileElement, String fileNameString) throws IOException {
		TabularExportResolvedObjects resObj = getResolvedObjects();
		OutputFile outputFile = new OutputFile(outDir, fileNameString);
		TargetStream target = new FileTargetStream(charset, outputFile, append);
		try (PrintStream ps = target.getPrintStream()) {
			if (headers != null) {
				printLine(writeTimer, ps, resObj.headers, evalCtx, fileElement);
			}
			for (Element lineElement : Iterators.loop(resObj.lines.evaluateElements(evalCtx, fileElement))) {
				resObj.lineVar.set(lineElement);
				printLine(writeTimer, ps, resObj.columns, evalCtx, lineElement);
			}
			if (footers != null) {
				printLine(writeTimer, ps, resObj.footers, evalCtx, fileElement);
			}
		}
	}
	
	private void writeCSV(Timer<TimerCategory> writeTimer, EvaluationContext evalCtx, Element fileElement, String fileNameString) throws IOException {
		TabularExportResolvedObjects resObj = getResolvedObjects();
		OutputFile outputFile = new OutputFile(outDir, fileNameString);
		CSVFormat format = CSVFormat.MYSQL.withQuote('"').withDelimiter(separator.charAt(0));
		try (Writer writer = new FileWriter(outputFile)) {
			CSVPrinter printer = new CSVPrinter(writer, format);
			if (headers != null) {
				printCSVLine(writeTimer, printer, resObj.headers, evalCtx, fileElement);
			}
			for (Element lineElement : Iterators.loop(resObj.lines.evaluateElements(evalCtx, fileElement))) {
				resObj.lineVar.set(lineElement);
				printCSVLine(writeTimer, printer, resObj.columns, evalCtx, lineElement);
			}
			if (footers != null) {
				printCSVLine(writeTimer, printer, resObj.footers, evalCtx, fileElement);
			}
		}
	}
	
	@Override
	public boolean check(Logger logger) {
		if (corpusFile == null) {
			if (files == null) {
				logger.severe("either corpusFile or files is mandatory");
				return false;
			}
			if (fileName == null) {
				logger.severe("either corpusFile or fileName is mandatory");
				return false;
			}
			return true;
		}
		if (files != null) {
			logger.warning("corpusFile will override files");
		}
		if (fileName != null) {
			logger.warning("corpusFile will override fileName");
		}
		return true;
	}

	@Param
	public OutputDirectory getOutDir() {
		return outDir;
	}

	@Param(mandatory=false)
	public Expression getFiles() {
		return files;
	}

	@Param(mandatory=false)
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

	@Param(mandatory=false)
	public File getCorpusFile() {
		return corpusFile;
	}

	@Param
	public Boolean getTrueCSV() {
		return trueCSV;
	}

	public void setTrueCSV(Boolean trueCSV) {
		this.trueCSV = trueCSV;
	}

	public void setCorpusFile(File corpusFile) {
		this.corpusFile = corpusFile;
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
