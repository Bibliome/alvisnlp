package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg.CCGBase.CCGResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

abstract class AbstractCCGExternalHandler<T extends CCGResolvedObjects,M extends CCGBase<T>> extends ExternalHandler<Corpus,M> {
	protected static final String BASE = "corpus_%8H%s";

	protected final int run;
	protected final List<Layer> sentences;
	private final boolean inputNeedsPos;
	
	protected AbstractCCGExternalHandler(ProcessingContext<Corpus> processingContext, M module, Corpus annotable, int run, List<Layer> sentences, boolean inputNeedsPos) {
		super(processingContext, module, annotable);
		this.run = run;
		this.sentences = sentences;
		this.inputNeedsPos = inputNeedsPos;
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		TargetStream target = new FileTargetStream(getModule().getInternalEncoding(), getCCGInputFile().getAbsolutePath());
		try (PrintStream out = target.getPrintStream()) {
			printSentences(out);
		}
	}
	
	protected File getCCGInputFile() {
		return getTempFile(String.format(BASE, run, ".txt"));
	}

	protected void printSentences(PrintStream out) {
		StringBuilder sb = new StringBuilder();
		for (Layer sent : sentences)
			printSentence(out, sb, sent);
	}
	
	private void printSentence(PrintStream out, StringBuilder sb, Layer sentence) {
		String formFeatureName = getModule().getFormFeatureName();
		String posFeatureName = getModule().getPosFeatureName();
		boolean notFirst = false;
		for (Annotation word : sentence) {
			String form = word.getLastFeature(formFeatureName);
			if (form.isEmpty())
				continue;
			sb.setLength(0);
			if (notFirst)
				sb.append(' ');
			else
				notFirst = true;
			Strings.escapeWhitespaces(sb, form, '|', '.');
			if (inputNeedsPos) {
				sb.append('|');
				sb.append(word.getLastFeature(posFeatureName));
			}
			out.print(sb);
		}
		out.println();
	}
	
	protected int getMaxLength() {
		int result = 0;
		for (Layer layer : sentences) {
			result = Math.max(result, layer.size());
		}
		return result;
	}

	@Override
	protected String getPrepareTask() {
		return "alvisnlp-to-ccg";
	}
}
