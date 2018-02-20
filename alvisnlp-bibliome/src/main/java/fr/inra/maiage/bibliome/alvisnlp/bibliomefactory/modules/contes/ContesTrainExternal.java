package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.External;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

class ContesTrainExternal implements External<Corpus> {
	private final ContesTrain train;
	private final Logger logger;
	private final OutputFile termsFile;
	private final OutputFile attributionsFile;

	ContesTrainExternal(ContesTrain train, Logger logger, File tmpDir) {
		this.train = train;
		this.logger = logger;
		this.termsFile = new OutputFile(tmpDir, "terms.json");
		this.attributionsFile = new OutputFile(tmpDir, "attributions.json");
	}
	
	void createTermsFile(EvaluationContext ctx, Corpus corpus) throws IOException {
		TargetStream target = new FileTargetStream("UTF-8", termsFile);
		try (PrintStream out = target.getPrintStream()) {
			JSONObject terms = train.getTerms(ctx, corpus);
			out.println(terms);
		}
	}

	void createAttributionsFile(EvaluationContext ctx, Corpus corpus) throws IOException {
		TargetStream target = new FileTargetStream("UTF-8", attributionsFile);
		try (PrintStream out = target.getPrintStream()) {
			JSONObject attributions = train.getAttributions(ctx, corpus);
			out.println(attributions);
		}
	}

	@Override
	public Module<Corpus> getOwner() {
		return train;
	}

	@Override
	public String[] getCommandLineArgs() throws ModuleException {
		return new String[] {
			new File(train.getContesDir(), "module_train/main_train.py").getAbsolutePath(),
			"--word-vectors",
			train.getWordEmbeddings().getAbsolutePath(),
			"--terms",
			termsFile.getAbsolutePath(),
			"--attributions",
			attributionsFile.getAbsolutePath(),
			"--ontology",
			train.getOntology().getAbsolutePath(),
			"--regression-matrix",
			train.getRegressionMatrix().getAbsolutePath()
		};
	}

	@Override
	public String[] getEnvironment() throws ModuleException {
		return new String[] {
				"PYTHONPATH=" + train.getContesDir().getAbsolutePath(),
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
            logger.fine("contes train standard error:");
            for (String line = err.readLine(); line != null; line = err.readLine()) {
                logger.fine("    " + line);
            }
            logger.fine("end of contes train standard error");
        }
        catch (IOException ioe) {
            logger.warning("could not read contes train standard error: " + ioe.getMessage());
        }
	}
}
