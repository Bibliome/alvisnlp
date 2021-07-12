package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.fasttext;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

public abstract class FasttextClassifierBaseExternalHandler<R extends FasttextClassifierBaseResolvedObjects, M extends FasttextClassifierBase<R>> extends ExternalHandler<Corpus,M> {
	protected static final String FASTTEXT_CLASS_FEATURE_PREFIX = "__label__";

	protected FasttextClassifierBaseExternalHandler(ProcessingContext<Corpus> processingContext, M module, Corpus annotable) {
		super(processingContext, module, annotable);
	}
	
	protected void writeDocumentLines(EvaluationContext evalCtx, File file, Evaluator documents, FasttextAttribute.Resolved[] attributes, boolean includeClass, boolean classWeights) throws IOException {
		TargetStream target = new FileTargetStream("UTF-8", file.getAbsolutePath());
		try (PrintStream ps = target.getPrintStream()) {
			int nDocs = 0;
			for (Element doc : Iterators.loop(documents.evaluateElements(evalCtx, getAnnotable()))) {
				Collection<String> line = getDocumentLine(evalCtx, doc, attributes, includeClass);
				int w = classWeights ? getDocWeight(doc) : 1;
				for (int i = 0; i < w; ++i) {
					Strings.join(ps, line, '\t');
					ps.println();
				}
				nDocs++;
			}
			getLogger().info("# of documents: " + nDocs);
		}
	}

	protected abstract int getDocWeight(Element doc);

	@Override
	protected String getPrepareTask() {
		return "alvisnlp-to-fasttext";
	}

	protected Collection<String> getDocumentLine(EvaluationContext evalCtx, Element doc, FasttextAttribute.Resolved[] attributes, boolean includeClass) {
		FasttextClassifierBase<R> owner = getModule();
		Collection<String> result = new ArrayList<String>();
		for (FasttextAttribute.Resolved attr : attributes) {
			String value = getAttributeValue(attr, evalCtx, doc);
			result.add(value);
		}
		if (includeClass) {
			String theClass = doc.getLastFeature(owner.getClassFeature());
			result.add(FASTTEXT_CLASS_FEATURE_PREFIX + theClass);
		}
		return result;		
	}

	private String getAttributeValue(FasttextAttribute.Resolved attr, EvaluationContext evalCtx, Element doc) {
		Collection<String> tokens = new ArrayList<String>();
		for (Element token : Iterators.loop(attr.getTokens().evaluateElements(evalCtx, doc))) {
			String form = attr.getForm().evaluateString(evalCtx, token);
			form = Strings.normalizeSpace(form);
			form = form.replace(' ', '_');
			tokens.add(form);
		}
		return Strings.join(tokens, ' ');
	}

	@Override
	protected void updateEnvironment(Map<String,String> env) {
	}

	@Override
	protected File getWorkingDirectory() {
		return null;
	}

	@Override
	protected String getInputFileame() {
		return null;
	}
}