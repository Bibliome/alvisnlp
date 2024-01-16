package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.files.OutputDirectory;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

public abstract class REBERTBaseExternalHandler<R extends REBERTBase> extends ExternalHandler<R> {
	protected final String[] labels;
	protected final EvaluationContext evalCtx;
	protected final Collection<Candidate> candidates;

	public REBERTBaseExternalHandler(ProcessingContext processingContext, R module, Corpus annotable) throws ModuleException {
		super(processingContext, module, annotable);
		this.labels = module.getLabels();
		Logger logger = module.getLogger(processingContext);
		this.evalCtx = new EvaluationContext(logger);
		this.candidates = module.getResolvedObjects().createCandidates(evalCtx, annotable);
	}

	protected boolean hasCandidates() {
		return !candidates.isEmpty();
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		CSVFormat format = CSVFormat.MYSQL.builder().setQuote('"').setDelimiter(',').build();
		try (Writer writer = new FileWriter(getRebertInputFile())) {
			try (CSVPrinter printer = new CSVPrinter(writer, format)) {
				printer.printRecord("text", "sentence_1", "sentence_2", "label");
				for (Candidate cand : candidates) {
					Object[] candRec = cand.getRecord();
					printer.printRecord(candRec);
				}
				getLogger().info("prepared " + candidates.size() + " candidates");
			}
		}
	}

	@Override
	protected String getPrepareTask() {
		return "rebert-prepare-input";
	}

	@Override
	protected String getExecTask() {
		return "rebert-run";
	}

	@Override
	protected String getCollectTask() {
		return "rebert-collect-results";
	}

	protected Map<String,String> getCommandLineMnemonics(boolean deferred) {
		Map<String,String> result = new HashMap<String,String>();
		REBERTBase owner = getModule();
		result.put("CONDA_EXECUTABLE", owner.getConda() == null ? "conda" : owner.getConda().getAbsolutePath());
		result.put("CONDA_ENVIRONMENT", owner.getCondaEnvironment());
		result.put("PYTHON", owner.getPython() == null ? "python" : owner.getPython().getAbsolutePath());
		result.put("REBERT_DIR", owner.getRebertDir().getAbsolutePath());
		result.put("DATA_FILE", deferred ? "input.csv" : getRebertInputFile().getAbsolutePath());
		result.put("OUTPUT_DIR", deferred ? "output" : getRebertOutputDir().getAbsolutePath());
		result.put("FORCE_CPU", owner.getUseGPU() ? "" : "--force_cpu");
		completeCommandLineMnemonics(result, deferred);
		return result;
	}
	
	protected abstract void completeCommandLineMnemonics(Map<String,String> mnemonics, boolean deferred);

	protected String getMnemonicValue(Map<String,String> mnemonics, String key, boolean deferred) {
		if (deferred) {
			return "$" + key;
		}
		return mnemonics.get(key);
	}

	protected String getRebertFile(Map<String,String> mnemonics, String file, boolean deferred) {
		if (deferred) {
			return "$REBERT_DIR/" + file; 
		}
		String rebertDir = mnemonics.get("REBERT_DIR");
		return rebertDir + "/" + file;
	}
	
	protected abstract List<String> getCommandLine(Map<String,String> mnemonics, boolean deferred);

	@Override
	protected List<String> getCommandLine() {
		return getCommandLine(getCommandLineMnemonics(false), false);
	}

	protected void writeRunScript() throws IOException, ModuleException {
		OutputDirectory runScriptDirectory = getModule().getRunScriptDirectory();
		runScriptDirectory.mkdirs();
	
		Map<String,String> mnemonics = getCommandLineMnemonics(true);
		try (PrintStream ps = openRunScript("config.sh")) {
			for (Map.Entry<String,String> e : mnemonics.entrySet()) {
				ps.format("%s=\"%s\"\n", e.getKey(), e.getValue());
			}
		}
		
		List<String> cli = getCommandLine(mnemonics, true);
		try (PrintStream ps = openRunScript("run.sh")) {
			ps.print(". config.sh\n\n");
			Strings.join(ps, cli, ' ');
			ps.println();
		}
		
		prepare();
		OutputFile dataFile = getRunScriptFile("input.csv");
		FileUtils.copyFile(getRebertInputFile(), dataFile);
	}

	private OutputFile getRunScriptFile(String filename) {
		OutputDirectory runScriptDirectory = getModule().getRunScriptDirectory();
		return new OutputFile(runScriptDirectory, filename);
	}

	private PrintStream openRunScript(String filename) throws IOException {
		OutputFile f = getRunScriptFile(filename);
		TargetStream strm = new FileTargetStream("UTF-8", f);
		return strm.getPrintStream();
	}

	private File getRebertInputFile() {
		return getTempFile("input.txt");
	}
	
	protected File getRebertOutputDir() {
		return getTempFile("output");
	}

	@Override
	protected void updateEnvironment(Map<String, String> env) {
		env.put("PYTHONPATH", getModule().getRebertDir().getAbsolutePath());
	}

	@Override
	protected File getWorkingDirectory() {
		return null;
	}

	@Override
	protected String getInputFileame() {
		return null;
	}

}