package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DefaultExpressions;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json.JsonExport.JsonExportResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ConstantsLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.Checkable;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.OutputDirectory;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

@AlvisNLPModule(beta = true)
public class JsonExport extends CorpusModule<JsonExportResolvedObjects> implements Checkable {
	private OutputDirectory outDir = null;
	private Expression files = null;
	private Expression fileName = null;
	private File corpusFile = null;
	private JsonValue json = null;
	
	static class JsonExportResolvedObjects extends ResolvedObjects {
		private final Evaluator files;
		private final Evaluator fileName;
		private final JsonValue.Resolved json;
		
		private JsonExportResolvedObjects(ProcessingContext<Corpus> ctx, JsonExport module) throws ResolverException {
			super(ctx, module);
			if (module.corpusFile == null) {
				files = rootResolver.resolveNullable(module.files);
				fileName = rootResolver.resolveNullable(module.fileName);
			}
			else {
				files = DefaultExpressions.SELF.resolveExpressions(rootResolver);
				fileName = ConstantsLibrary.getInstance(module.corpusFile.getPath());
			}
			this.json = module.json.resolveExpressions(rootResolver);
		}
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		JsonExportResolvedObjects resObj = getResolvedObjects();
    	Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		try {
			for (Element fileElement : Iterators.loop(resObj.files.evaluateElements(evalCtx, corpus))) {
				String fileNameString = resObj.fileName.evaluateString(evalCtx, fileElement);
				OutputFile outputFile = new OutputFile(outDir, fileNameString);
				TargetStream target = new FileTargetStream("UTF-8", outputFile);
				try (PrintStream ps = target.getPrintStream()) {
					Object jValue = convertToJson(ctx, evalCtx, fileElement);
					writeJson(ctx, ps, jValue);
				}
			}
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}
	
	@TimeThis(task = "write", category = TimerCategory.EXPORT)
	protected void writeJson(ProcessingContext<Corpus> ctx, PrintStream ps, Object jValue) {
		ps.print(jValue);
	}
	
	@TimeThis(task = "convert", category = TimerCategory.PREPARE_DATA)
	protected Object convertToJson(ProcessingContext<Corpus> ctx, EvaluationContext evalCtx, Element fileElement) {
		JsonExportResolvedObjects resObj = getResolvedObjects();
		return resObj.json.create(evalCtx, fileElement);
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

	@Override
	protected JsonExportResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new JsonExportResolvedObjects(ctx, this);
	}

	@Param
	public OutputDirectory getOutDir() {
		return outDir;
	}

	@Param(mandatory = false)
	public Expression getFiles() {
		return files;
	}

	@Param(mandatory = false)
	public Expression getFileName() {
		return fileName;
	}

	@Param(mandatory = false)
	public File getCorpusFile() {
		return corpusFile;
	}

	@Param
	public JsonValue getJson() {
		return json;
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

	public void setCorpusFile(File corpusFile) {
		this.corpusFile = corpusFile;
	}

	public void setJson(JsonValue json) {
		this.json = json;
	}
}
