package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes.AbstractContesTerms.ContesTermsResolvedObject;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.AbstractFile;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

abstract class AbstractContesTermsExternalHandler<F extends AbstractFile,T extends ContesTermClassifier<F>,M extends AbstractContesTerms<F,T>> extends AbstractContesExternalHandler<ContesTermsResolvedObject,M> {
	protected AbstractContesTermsExternalHandler(ProcessingContext<Corpus> processingContext, M module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	protected void createTermsFiles() throws IOException {
		ContesTermsResolvedObject resObj = getModule().getResolvedObjects();
		ContesTermClassifier.Resolved[] termClassifiers = resObj.getTermClassifiers();
		for (int i = 0; i < termClassifiers.length; ++i) {
			TargetStream target = new FileTargetStream("UTF-8", getTermsFile(i).getAbsolutePath());
			try (PrintStream out = target.getPrintStream()) {
				JSONObject terms = getTermTokens(termClassifiers[i]);
				out.println(terms);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject getTermTokens(ContesTermClassifier.Resolved termClassifier) {
		JSONObject result = new JSONObject();
		EvaluationContext ctx = new EvaluationContext(getLogger());
		AbstractContesTerms<F,T> owner = getModule();
		Corpus corpus = getAnnotable();
		Iterator<Section> sectionIt = corpus.sectionIterator(ctx, termClassifier.getDocumentFilter(), termClassifier.getSectionFilter());
		for (Section sec : Iterators.loop(sectionIt)) {
			Layer tokens = sec.getLayer(owner.getTokenLayerName());
			if (sec.hasLayer(termClassifier.getTermLayerName())) {
				for (Annotation term : sec.getLayer(termClassifier.getTermLayerName())) {
					String id = term.getStringId();
					JSONArray termTokens = new JSONArray();
					for (Annotation t : tokens.between(term)) {
						String form = t.getLastFeature(owner.getFormFeatureName());
						termTokens.add(form);
					}
					result.put(id, termTokens);
				}
			}
			else {
				getLogger().warning("no layer " + termClassifier.getTermLayerName());
			}
		}
		return result;
	}

	protected File getTermsFile(int i) {
		return getTempFile("terms_" + i + ".json");
	}

	protected File getAttributionsFile(int i) {
		return getTempFile("attributions_" + i + ".json");
	}
}
