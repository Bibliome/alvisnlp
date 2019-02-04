package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes.AbstractContesTerms.ContesTermsResolvedObject;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

class ContesTrainExternalHandler extends AbstractContesTermsExternalHandler<OutputFile,ContesTrainTermClassifier,ContesTrain> {
	ContesTrainExternalHandler(ProcessingContext<Corpus> processingContext, ContesTrain module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected String getContesModule() {
		return "module_train/main_train.py";
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		createTermsFiles();
		createAttributionsFile();
	}

	private void createAttributionsFile() throws IOException {
		ContesTermsResolvedObject resObj = getModule().getResolvedObjects();
		ContesTermClassifier.Resolved[] termClassifiers = resObj.getTermClassifiers();
		for (int i = 0; i < termClassifiers.length; ++i) {
			TargetStream target = new FileTargetStream("UTF-8", getAttributionsFile(i).getAbsolutePath());
			try (PrintStream out = target.getPrintStream()) {
				JSONObject attributions = getAttributions(termClassifiers[i]);
				out.println(attributions);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject getAttributions(ContesTermClassifier.Resolved termClassifier) {
		EvaluationContext ctx = new EvaluationContext(getLogger());
		Corpus corpus = getAnnotable();
		Iterator<Section> sectionIt = corpus.sectionIterator(ctx, termClassifier.getDocumentFilter(), termClassifier.getSectionFilter());
		JSONObject result = new JSONObject();
		for (Section sec : Iterators.loop(sectionIt)) {
			if (sec.hasLayer(termClassifier.getTermLayerName())) {
				for (Annotation term : sec.getLayer(termClassifier.getTermLayerName())) {
					if (!term.hasFeature(termClassifier.getConceptFeatureName())) {
						continue;
					}
					String id = term.getStringId();
					JSONArray concepts = new JSONArray();
					concepts.addAll(term.getFeature(termClassifier.getConceptFeatureName()));
					result.put(id, concepts);
				}
			}
		}
		return result;
	}

	@Override
	protected void collect() throws IOException, ModuleException {
	}

	@Override
	protected String getExecTask() {
		return "contes-train";
	}

	@Override
	protected List<String> getCommandLine() {
		ContesTrain owner = getModule();
		List<String> result = new ArrayList<String>(20);
		result.add(owner.getPython3Executable().getAbsolutePath());
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
			result.add("--attributions");
			result.add(getAttributionsFile(i).getAbsolutePath());
			result.add("--regression-matrix");
			result.add(termClassifiers[i].getRegressionMatrixFile().getAbsolutePath());
		}
		return result;
	}
}
