package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

abstract class AbstractContesTermsExternal<T extends AbstractContesTerms> extends AbstractContesExternal<T> {
	private final OutputFile termsFile;

	protected AbstractContesTermsExternal(T owner, Logger logger, File tmpDir) {
		super(owner, logger);
		this.termsFile = new OutputFile(tmpDir, "terms.json");
	}

	protected void createTermsFile(EvaluationContext ctx, Corpus corpus) throws IOException {
		TargetStream target = new FileTargetStream("UTF-8", termsFile);
		try (PrintStream out = target.getPrintStream()) {
			JSONObject terms = getOwner().getTermTokens(ctx, corpus);
			out.println(terms);
		}
	}

	protected OutputFile getTermsFile() {
		return termsFile;
	}
}
