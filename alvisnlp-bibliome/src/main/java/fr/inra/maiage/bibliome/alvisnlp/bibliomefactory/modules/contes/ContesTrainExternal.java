package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

class ContesTrainExternal extends AbstractContesTermsExternal<ContesTrain> {
	private final OutputFile attributionsFile;

	ContesTrainExternal(ContesTrain owner, Logger logger, File tmpDir) {
		super(owner, logger, tmpDir);
		this.attributionsFile = new OutputFile(tmpDir, "attributions.json");
	}

	void createAttributionsFile(EvaluationContext ctx, Corpus corpus) throws IOException {
		TargetStream target = new FileTargetStream("UTF-8", attributionsFile);
		try (PrintStream out = target.getPrintStream()) {
			JSONObject attributions = getOwner().getAttributions(ctx, corpus);
			out.println(attributions);
		}
	}

	@Override
	public String[] getCommandLineArgs() throws ModuleException {
		return new String[] {
				getContesCommand(),
				"--word-vectors",
				getOwner().getWordEmbeddings().getAbsolutePath(),
				"--terms",
				getTermsFile().getAbsolutePath(),
				"--attributions",
				attributionsFile.getAbsolutePath(),
				"--ontology",
				getOwner().getOntology().getAbsolutePath(),
				"--regression-matrix",
				getOwner().getRegressionMatrix().getAbsolutePath()
		};
	}

	@Override
	protected String getContesModule() {
		return "module_train/main_train.py";
	}

	@Override
	protected String getLoggingLabel() {
		return "contes train";
	}
}
