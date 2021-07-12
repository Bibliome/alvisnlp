package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.fasttext;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

public class FasttextClassifierLabelExternalHandler extends FasttextClassifierBaseExternalHandler<FasttextClassifierBaseResolvedObjects,FasttextClassifierLabel> {
	public FasttextClassifierLabelExternalHandler(ProcessingContext<Corpus> processingContext, FasttextClassifierLabel module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected int getDocWeight(Element doc) {
		return 1;
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		EvaluationContext evalCtx = new EvaluationContext(getLogger());
		FasttextClassifierLabel owner = getModule();
		FasttextClassifierBaseResolvedObjects resObj = owner.getResolvedObjects();
		writeDocumentLines(evalCtx, getFasttextInputFile(), resObj.getDocuments(), resObj.getAttributes(), false, false);
	}

	@Override
	protected void collect() throws IOException, ModuleException {
		FasttextClassifierLabel owner = getModule();
		FasttextClassifierBaseResolvedObjects resObj = owner.getResolvedObjects();
		EvaluationContext evalCtx = new EvaluationContext(getLogger());
		SourceStream source = new FileSourceStream("UTF-8", getOutputFile().getAbsolutePath());
		try (BufferedReader r = source.getBufferedReader()) {
			for (Element doc : Iterators.loop(resObj.getDocuments().evaluateElements(evalCtx, getAnnotable()))) {
				String line = r.readLine();
				if (line == null) {
					throw new ProcessingException("fasttext output is short on lines: " + getOutputFile());
				}
				if (!line.startsWith(FASTTEXT_CLASS_FEATURE_PREFIX)) {
					throw new ProcessingException("malformed output: " + line);
				}
				String label = line.substring(FASTTEXT_CLASS_FEATURE_PREFIX.length());
				doc.addFeature(owner.getClassFeature(), label);
			}
		}
	}

	@Override
	protected String getExecTask() {
		return "fasttext-label";
	}

	@Override
	protected String getCollectTask() {
		return "fasttext-label-collect";
	}

	@Override
	protected List<String> getCommandLine() {
		FasttextClassifierLabel owner = getModule();
		return Arrays.asList(
				owner.getFasttextExecutable().getAbsolutePath(),
				owner.getProbabilityFeature() == null ? "predict" : "predict-prob",
				owner.getModelFile().getAbsolutePath(),
				getFasttextInputFile().getAbsolutePath()
				);
	}

	private File getFasttextInputFile() {
		return getTempFile("fasttext-input.txt");
	}

	@Override
	protected String getOutputFilename() {
		return "fasttext-output.txt";
	}
}
