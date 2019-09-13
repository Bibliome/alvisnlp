package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes.AbstractContesTerms.ContesTermsResolvedObject;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Pair;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

class ContesPredictExternalHandler extends AbstractContesTermsExternalHandler<InputFile,ContesPredictTermClassifier,ContesPredict> {
	ContesPredictExternalHandler(ProcessingContext<Corpus> processingContext, ContesPredict module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected String getContesModule() {
		return "module_predictor/main_predictor.py";
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		createTermsFiles();
	}

	@Override
	protected void collect() throws IOException, ModuleException {
		ContesTermsResolvedObject resObj = getModule().getResolvedObjects();
		ContesTermClassifier.Resolved[] termClassifiers = resObj.getTermClassifiers();
		for (int i = 0; i < termClassifiers.length; ++i) {
			Map<String,Pair<String,String>> predictions = readPredictions(i);
			setPredictionFeature(predictions, termClassifiers[i]);
		}
	}

	private Map<String,Pair<String,String>> readPredictions(int i) throws IOException {
		Map<String,Pair<String,String>> result = new HashMap<String,Pair<String,String>>();
		SourceStream source = new FileSourceStream("UTF-8", getAttributionsFile(i).getAbsolutePath());
		try (BufferedReader r = source.getBufferedReader()) {
			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				}
				List<String> cols = Strings.split(line, '\t', 0);
				String termId = cols.get(0);
				String conceptId = cols.get(1);
				String sim = cols.get(2);
				result.put(termId, new Pair<String,String>(conceptId, sim));
			}
		}
		return result;
	}

	private void setPredictionFeature(Map<String,Pair<String,String>> predictions, ContesTermClassifier.Resolved termClassifier) {
		EvaluationContext ctx = new EvaluationContext(getLogger());
		Corpus corpus = getAnnotable();
		String conceptFeatureName = termClassifier.getConceptFeatureName();
		String similarityFeatureName = termClassifier.getSimilarityFeatureName();
		Iterator<Section> sectionIt = corpus.sectionIterator(ctx, termClassifier.getDocumentFilter(), termClassifier.getSectionFilter());
		for (Section sec : Iterators.loop(sectionIt)) {
			if (sec.hasLayer(termClassifier.getTermLayerName())) {
				for (Annotation term : sec.getLayer(termClassifier.getTermLayerName())) {
					String id = term.getStringId();
					if (predictions.containsKey(id)) {
						Pair<String,String> p = predictions.get(id);
						term.addFeature(conceptFeatureName, p.first);
						term.addFeature(similarityFeatureName, p.second);
					}
				}
			}
		}
	}

	@Override
	protected String getExecTask() {
		return "contes-predict";
	}

	@Override
	protected List<String> getCommandLine() {
		List<String> result = new ArrayList<String>(20);
		ContesPredict owner = getModule();
		result.add(owner.getPython3Executable().getAbsolutePath());
		result.add(getContesCommand());
		addAdditionalArguments(result);
		addOptionalFile(result, "--word-vectors", owner.getWordEmbeddings());
		addOptionalFile(result, "--word-vectors-bin", owner.getWordEmbeddingsModel());
		result.add("--ontology");
		result.add(owner.getOntology().getAbsolutePath());
		ContesTermsResolvedObject resObj = getModule().getResolvedObjects();
		ContesTermClassifier.Resolved[] termClassifiers = resObj.getTermClassifiers();
		for (int i = 0; i < termClassifiers.length; ++i) {
			result.add("--terms");
			result.add(getTermsFile(i).getAbsolutePath());
			result.add("--regression-matrix");
			result.add(termClassifiers[i].getRegressionMatrixFile().getAbsolutePath());
			result.add("--output");
			result.add(getAttributionsFile(i).getAbsolutePath());
			result.add("--factor");
			result.add(termClassifiers[i].getFactor(owner.getDefaultFactor()).toString());
		}
		return result;
	}
}
