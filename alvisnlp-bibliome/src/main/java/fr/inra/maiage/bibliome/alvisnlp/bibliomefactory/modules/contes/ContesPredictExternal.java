package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

class ContesPredictExternal extends AbstractContesTermsExternal<ContesPredict> {
	private final InputFile attributionsFile;

	ContesPredictExternal(ContesPredict owner, Logger logger, File tmpDir) {
		super(owner, logger, tmpDir);
		this.attributionsFile = new InputFile(tmpDir, "attributions.json");
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
	public String[] getCommandLineArgs() throws ModuleException {
		return new String[] {
				getContesCommand(),
				"--word-vectors",
				getOwner().getWordEmbeddings().getAbsolutePath(),
				"--terms",
				getTermsFile().getAbsolutePath(),
				"--ontology",
				getOwner().getOntology().getAbsolutePath(),
				"--regression-matrix",
				getOwner().getRegressionMatrix().getAbsolutePath(),
				"--output",
				attributionsFile.getAbsolutePath()
		};
	}

	@Override
	protected String getLoggingLabel() {
		return "contes predictor";
	}

	@Override
	protected String getContesModule() {
		return "module_predictor/main_predictor.py";
	}
}
