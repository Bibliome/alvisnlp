package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.StringLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.External;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

class Word2VecExternal implements External<Corpus> {
	private final Word2Vec word2Vec;
	private final Logger logger;
	private final File inputFile;
	private final File effectiveTxtFile;
	
	Word2VecExternal(Word2Vec word2Vec, Logger logger, File tmpDir) {
		this.word2Vec = word2Vec;
		this.logger = logger;
		this.inputFile = new File(tmpDir, "input.txt");
		if (word2Vec.getTxtFile() == null) {
			this.effectiveTxtFile = new File(tmpDir, "output.txt");
		}
		else {
			this.effectiveTxtFile = word2Vec.getTxtFile();
		}
	}
	
	void createInputFile(EvaluationContext ctx, Corpus corpus) throws FileNotFoundException, UnsupportedEncodingException {
		try (PrintStream ps = new PrintStream(inputFile, "UTF-8")) {
			for (Section sec : Iterators.loop(word2Vec.sectionIterator(ctx, corpus))) {
				for (Layer sent : sec.getSentences(word2Vec.getTokenLayer(), word2Vec.getSentenceLayer())) {
					for (Annotation tok : sent) {
						String form = StringLibrary.normalizeSpace(tok.getLastFeature(word2Vec.getFormFeature()));
						ps.println(form);
					}
					ps.println();
				}
			}
		}
	}
	
	private Map<String,String> readWordVectors() throws IOException {
		Map<String,String> result = new HashMap<String,String>();
		SourceStream source = new FileSourceStream("UTF-8", effectiveTxtFile.getAbsolutePath());
		try (BufferedReader r = source.getBufferedReader()) {
			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				}
				int tab = line.trim().indexOf('\t');
				String word = line.substring(0, tab);
				String vector = line.substring(tab+1);
				result.put(word, vector);
			}
		}
		return result;
	}
	
	void collectTokenVectors(EvaluationContext ctx, Corpus corpus) throws IOException {
		if (word2Vec.getVectorFeature() == null) {
			return;
		}
		Map<String,String> wordVectors = readWordVectors();
		for (Section sec : Iterators.loop(word2Vec.sectionIterator(ctx, corpus))) {
			for (Layer sent : sec.getSentences(word2Vec.getTokenLayer(), word2Vec.getSentenceLayer())) {
				for (Annotation tok : sent) {
					String form = StringLibrary.normalizeSpace(tok.getLastFeature(word2Vec.getFormFeature()));
					if (wordVectors.containsKey(form)) {
						String vector = wordVectors.get(form);
						tok.addFeature(word2Vec.getVectorFeature(), vector);
					}
					else {
						logger.warning("could not find vector for " + form);
					}
				}
			}
		}
	}
	
	@Override
	public Module<Corpus> getOwner() {
		return word2Vec;
	}

	@Override
	public String[] getCommandLineArgs() throws ModuleException {
		return new String[] {
				word2Vec.getWord2vec().getAbsolutePath(),
				"--json",
				word2Vec.getJsonFile().getAbsolutePath(),
				"--txt",
				effectiveTxtFile.getAbsolutePath(),
				"--vector-size",
				word2Vec.getVectorSize().toString(),
				"--window-size",
				word2Vec.getWindowSize().toString(),
				"--skip-gram",
				"--min-count",
				word2Vec.getMinCount().toString(),
				"--workers",
				word2Vec.getWorkers().toString(),
				inputFile.getAbsolutePath()
		};
	}

	@Override
	public String[] getEnvironment() throws ModuleException {
		return null;
	}

	@Override
	public File getWorkingDirectory() throws ModuleException {
		return null;
	}

	@Override
	public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
        try {
            logger.fine("word2vec standard error:");
            for (String line = err.readLine(); line != null; line = err.readLine()) {
                logger.fine("    " + line);
            }
            logger.fine("end of word2vec standard error");
        }
        catch (IOException ioe) {
            logger.warning("could not read word2vec standard error: " + ioe.getMessage());
        }
	}
}