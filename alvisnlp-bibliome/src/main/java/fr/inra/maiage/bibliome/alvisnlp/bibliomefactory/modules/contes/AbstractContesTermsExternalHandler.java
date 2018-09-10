package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

abstract class AbstractContesTermsExternalHandler<T extends AbstractContesTerms> extends AbstractContesExternalHandler<T> {
	protected AbstractContesTermsExternalHandler(ProcessingContext<Corpus> processingContext, T module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	protected void createTermsFile() throws IOException {
		TargetStream target = new FileTargetStream("UTF-8", getTermsFile().getAbsolutePath());
		try (PrintStream out = target.getPrintStream()) {
			JSONObject terms = getTermTokens();
			out.println(terms);
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject getTermTokens() {
		JSONObject result = new JSONObject();
		EvaluationContext ctx = new EvaluationContext(getLogger());
		AbstractContesTerms owner = getModule();
		for (Section sec : Iterators.loop(owner.sectionIterator(ctx, getAnnotable()))) {
			Layer tokens = sec.getLayer(owner.getTokenLayer());
			for (Annotation term : sec.getLayer(owner.getTermLayer())) {
				String id = term.getStringId();
				JSONArray termTokens = new JSONArray();
				for (Annotation t : tokens.between(term)) {
					String form = t.getLastFeature(owner.getFormFeature());
					termTokens.add(form);
				}
				result.put(id, termTokens);
			}
		}
		return result;
	}

	protected File getTermsFile() {
		return getTempFile("terms.json");
	}

	protected File getAttributionsFile() {
		return getTempFile("attributions.json");
	}
}
