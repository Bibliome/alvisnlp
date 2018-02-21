package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.External;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

public class ContesPredictExternal implements External<Corpus> {
	private final ContesPredict predict;
	private final Logger logger;
	private final OutputFile termsFile;
	private final InputFile attributionsFile;

	ContesPredictExternal(ContesPredict predict, Logger logger, File tmpDir) {
		this.predict = predict;
		this.logger = logger;
		this.termsFile = new OutputFile(tmpDir, "terms.json");
		this.attributionsFile = new InputFile(tmpDir, "attributions.json");
	}

	void createTermsFile(EvaluationContext ctx, Corpus corpus) throws IOException {
		TargetStream target = new FileTargetStream("UTF-8", termsFile);
		try (PrintStream out = target.getPrintStream()) {
			JSONObject terms = predict.getTerms(ctx, corpus);
			out.println(terms);
		}
	}
	
	Map<String,String> readPredictions() throws IOException {
		Map<String,String> result = new HashMap<String,String>();
		SourceStream source = new FileSourceStream("UTF-8", attributionsFile);
		try (BufferedReader r = source.getBufferedReader()) {
			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				}
				int tab = line.indexOf('\t');
				String termId = line.substring(0, tab);
				String conceptId = line.substring(tab + 1);
				result.put(termId, conceptId);
			}
		}
		return result;
	}

	@Override
	public Module<Corpus> getOwner() {
		return predict;
	}

	@Override
	public String[] getCommandLineArgs() throws ModuleException {
		return new String[] {
				new File(predict.getContesDir(), "module_predictor/main_predictor.py").getAbsolutePath(),
				"--word-vectors",
				predict.getWordEmbeddings().getAbsolutePath(),
				"--terms",
				termsFile.getAbsolutePath(),
				"--ontology",
				predict.getOntology().getAbsolutePath(),
				"--regression-matrix",
				predict.getRegressionMatrix().getAbsolutePath(),
				"--output",
				attributionsFile.getAbsolutePath()
		};
	}

	@Override
	public String[] getEnvironment() throws ModuleException {
		return new String[] {
				"PYTHONPATH=" + predict.getContesDir().getAbsolutePath(),
				"PATH=" + System.getenv("PATH")
		};
	}

	@Override
	public File getWorkingDirectory() throws ModuleException {
		return null;
	}

	@Override
	public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
        try {
            logger.fine("contes predictor standard error:");
            for (String line = err.readLine(); line != null; line = err.readLine()) {
                logger.fine("    " + line);
            }
            logger.fine("end of contes predictor standard error");
        }
        catch (IOException ioe) {
            logger.warning("could not read contes predictor standard error: " + ioe.getMessage());
        }
	}
}
