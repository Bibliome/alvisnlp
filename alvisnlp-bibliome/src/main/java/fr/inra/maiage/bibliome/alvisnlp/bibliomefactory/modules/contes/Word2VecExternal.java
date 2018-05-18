package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.StringLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

class Word2VecExternal extends AbstractContesExternal<Word2Vec> {
	private final File inputFile;
	private final File effectiveTxtFile;
	
	Word2VecExternal(Word2Vec owner, Logger logger, File tmpDir) {
		super(owner, logger);
		this.inputFile = new File(tmpDir, "input.txt");
		if (owner.getTxtFile() == null) {
			this.effectiveTxtFile = new File(tmpDir, "output.txt");
		}
		else {
			this.effectiveTxtFile = getOwner().getTxtFile();
		}
	}
	
	void createInputFile(EvaluationContext ctx, Corpus corpus) throws IOException {
		try (PrintStream ps = new PrintStream(inputFile, "UTF-8")) {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ps, "UTF-8"));
			for (Section sec : Iterators.loop(getOwner().sectionIterator(ctx, corpus))) {
				for (Layer sent : sec.getSentences(getOwner().getTokenLayer(), getOwner().getSentenceLayer())) {
					for (Annotation tok : sent) {
						String form = StringLibrary.normalizeSpace(tok.getLastFeature(getOwner().getFormFeature()));
						bw.write(form);
						bw.newLine();
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
		if (getOwner().getVectorFeature() == null) {
			return;
		}
		Map<String,String> wordVectors = readWordVectors();
		for (Section sec : Iterators.loop(getOwner().sectionIterator(ctx, corpus))) {
			for (Layer sent : sec.getSentences(getOwner().getTokenLayer(), getOwner().getSentenceLayer())) {
				for (Annotation tok : sent) {
					String form = StringLibrary.normalizeSpace(tok.getLastFeature(getOwner().getFormFeature()));
					if (wordVectors.containsKey(form)) {
						String vector = wordVectors.get(form);
						tok.addFeature(getOwner().getVectorFeature(), vector);
					}
					else {
						getLogger().warning("could not find vector for " + form);
					}
				}
			}
		}
	}

	@Override
	protected String getLoggingLabel() {
		return "word2vec";
	}

	@Override
	public String[] getCommandLineArgs() throws ModuleException {
		return new String[] {
				getContesCommand(),
				"--json",
				getOwner().getJsonFile().getAbsolutePath(),
				"--txt",
				effectiveTxtFile.getAbsolutePath(),
				"--vector-size",
				getOwner().getVectorSize().toString(),
				"--window-size",
				getOwner().getWindowSize().toString(),
				"--skip-gram",
				"--min-count",
				getOwner().getMinCount().toString(),
				"--workers",
				getOwner().getWorkers().toString(),
				inputFile.getAbsolutePath()
		};
	}

	@Override
	protected String getContesModule() {
		return "module_word2vec/main_word2vec.py";
	}
}
