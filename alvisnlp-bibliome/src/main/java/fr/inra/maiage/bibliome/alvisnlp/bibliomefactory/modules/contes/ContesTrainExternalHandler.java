package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

class ContesTrainExternalHandler extends AbstractContesTermsExternalHandler<ContesTrain> {
	ContesTrainExternalHandler(ProcessingContext<Corpus> processingContext, ContesTrain module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected String getContesModule() {
		return "module_train/main_train.py";
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		createTermsFile();
		createAttributionsFile();
	}

	private void createAttributionsFile() throws IOException {
		TargetStream target = new FileTargetStream("UTF-8", getAttributionsFile().getAbsolutePath());
		try (PrintStream out = target.getPrintStream()) {
			JSONObject attributions = getAttributions();
			out.println(attributions);
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject getAttributions() {
		ContesTrain owner = getModule();
		EvaluationContext ctx = new EvaluationContext(getLogger());
		JSONObject result = new JSONObject();
		for (Section sec : Iterators.loop(owner.sectionIterator(ctx, getAnnotable()))) {
			for (Annotation term : sec.getLayer(owner.getTermLayer())) {
				if (!term.hasFeature(owner.getConceptFeature())) {
					continue;
				}
				String id = term.getStringId();
				JSONArray concepts = new JSONArray();
				concepts.addAll(term.getFeature(owner.getConceptFeature()));
				result.put(id, concepts);
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
		result.add(getContesCommand());
		result.add("--word-vectors");
		result.add(owner.getWordEmbeddings().getAbsolutePath());
		result.add("--terms");
		result.add(getTermsFile().getAbsolutePath());
		result.add("--attributions");
		result.add(getAttributionsFile().getAbsolutePath());
		result.add("--ontology");
		result.add(owner.getOntology().getAbsolutePath());
		result.add("--regression-matrix");
		result.add(owner.getRegressionMatrix().getAbsolutePath());
		return result;
	}
}
