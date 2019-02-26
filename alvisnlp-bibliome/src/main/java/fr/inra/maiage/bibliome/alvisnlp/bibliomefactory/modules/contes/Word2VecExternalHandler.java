package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.StringLibrary;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

class Word2VecExternalHandler extends AbstractContesExternalHandler<SectionResolvedObjects,Word2Vec> {
	Word2VecExternalHandler(ProcessingContext<Corpus> processingContext, Word2Vec module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected String getContesModule() {
		return "module_word2vec/main_word2vec.py";
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		createInputFile();
	}
	
	private void createInputFile() throws IOException {
		Word2Vec owner = getModule();
		EvaluationContext ctx = new EvaluationContext(getLogger());
		try (PrintStream ps = new PrintStream(getWord2VecInputFile(), "UTF-8")) {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ps, "UTF-8"));
			for (Section sec : Iterators.loop(owner.sectionIterator(ctx, getAnnotable()))) {
				for (Layer sent : sec.getSentences(owner.getTokenLayerName(), owner.getSentenceLayerName())) {
					for (Annotation tok : sent) {
						String form = StringLibrary.normalizeSpace(tok.getLastFeature(owner.getFormFeatureName()));
						bw.write(form);
						bw.newLine();
					}
					bw.newLine();
				}
			}
		}
	}

	private File getWord2VecInputFile() {
		return getTempFile("input.txt");
	}

	@Override
	protected void collect() throws IOException, ModuleException {
		collectTokenVectors();
	}
	
	private void collectTokenVectors() throws IOException {
		Word2Vec owner = getModule();
		if (owner.getVectorFeatureName() == null) {
			return;
		}
		EvaluationContext ctx = new EvaluationContext(getLogger());
		Map<String,String> wordVectors = readWordVectors();
		for (Section sec : Iterators.loop(owner.sectionIterator(ctx, getAnnotable()))) {
			for (Layer sent : sec.getSentences(owner.getTokenLayerName(), owner.getSentenceLayerName())) {
				for (Annotation tok : sent) {
					String form = StringLibrary.normalizeSpace(tok.getLastFeature(owner.getFormFeatureName()));
					if (wordVectors.containsKey(form)) {
						String vector = wordVectors.get(form);
						tok.addFeature(owner.getVectorFeatureName(), vector);
					}
					else {
						getLogger().warning("could not find vector for " + form);
					}
				}
			}
		}
	}
	
	private Map<String,String> readWordVectors() throws IOException {
		Map<String,String> result = new HashMap<String,String>();
		SourceStream source = new FileSourceStream("UTF-8", getEffectiveTxtFile().getAbsolutePath());
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

	@Override
	protected String getExecTask() {
		return "word2vec";
	}

	@Override
	protected List<String> getCommandLine() {
		List<String> result = new ArrayList<String>(20);
		Word2Vec owner = getModule();
		result.add(owner.getPython3Executable().getAbsolutePath());
		result.add(getContesCommand());
		addAdditionalArguments(result);
		addOptionalFile(result, "--json", owner.getJsonFile());
		addOptionalFile(result, "--txt", getEffectiveTxtFile());
		addOptionalFile(result, "--bin", owner.getModelFile());
		result.add("--vector-size");
		result.add(owner.getVectorSize().toString());
		result.add("--window-size");
		result.add(owner.getWindowSize().toString());
		result.add("--skip-gram");
		result.add("--min-count");
		result.add(owner.getMinCount().toString());
		result.add("--workers");
		result.add(owner.getWorkers().toString());
		result.add(getWord2VecInputFile().getAbsolutePath());
		return result;
	}
	
	private File getEffectiveTxtFile() {
		Word2Vec owner = getModule();
		OutputFile txtFile = owner.getTxtFile();
		if (txtFile == null) {
			if (owner.getVectorFeatureName() == null) {
				return null;
			}
			return getTempFile("output.txt");
		}
		return txtFile;
	}
}
