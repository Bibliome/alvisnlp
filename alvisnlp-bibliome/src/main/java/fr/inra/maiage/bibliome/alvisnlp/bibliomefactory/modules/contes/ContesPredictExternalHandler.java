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
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

class ContesPredictExternalHandler extends AbstractContesTermsExternalHandler<ContesPredict> {
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
			Map<String,String> predictions = readPredictions(i);
			setPredictionFeature(predictions, termClassifiers[i]);
		}
	}
	
	private Map<String,String> readPredictions(int i) throws IOException {
		Map<String,String> result = new HashMap<String,String>();
		SourceStream source = new FileSourceStream("UTF-8", getAttributionsFile(i).getAbsolutePath());
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
	
	private void setPredictionFeature(Map<String,String> predictions, ContesTermClassifier.Resolved termClassifier) {
		EvaluationContext ctx = new EvaluationContext(getLogger());
		Corpus corpus = getAnnotable();
		Iterator<Section> sectionIt = corpus.sectionIterator(ctx, termClassifier.getDocumentFilter(), termClassifier.getSectionFilter());
		for (Section sec : Iterators.loop(sectionIt)) {
			for (Annotation term : sec.getLayer(termClassifier.getTermLayerName())) {
				String id = term.getStringId();
				if (predictions.containsKey(id)) {
					String conceptId = predictions.get(id);
					term.addFeature(termClassifier.getConceptFeatureName(), conceptId);
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
		result.add(getContesCommand());
		result.add("--word-vectors");
		result.add(owner.getWordEmbeddings().getAbsolutePath());
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
		}
		return result;
	}
}
