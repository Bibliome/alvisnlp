package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg.CCGBase.CCGResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

class CCGPosTaggerExternalHandler extends AbstractCCGExternalHandler<CCGResolvedObjects,CCGPosTagger> {
	CCGPosTaggerExternalHandler(ProcessingContext<Corpus> processingContext, CCGPosTagger module, Corpus annotable, int run, List<Layer> sentences) {
		super(processingContext, module, annotable, run, sentences, false);
	}
	
	private File getCCGPosTaggerOutputFile() {
		return getTempFile(String.format(BASE, run, ".pos"));
	}

	@Override
	protected void collect() throws IOException, ModuleException {
		SourceStream source = new FileSourceStream(getModule().getInternalEncoding(), getCCGPosTaggerOutputFile().getAbsolutePath());
		try (BufferedReader r = source.getBufferedReader()) {
			for (Layer sent : sentences)
				readSentence(r, sent);
		}
	}

	private void readSentence(BufferedReader r, Layer sentence) throws IOException, ProcessingException {
		CCGPosTagger owner = getModule();
		boolean reachedEOS = false;
		for (Annotation word : sentence) {
			if (word.getLastFeature(owner.getFormFeatureName()).isEmpty())
				continue;
			if (reachedEOS)
				throw new ProcessingException("CCG sentence was too short");
			String pos = r.readLine();
			if (pos == null)
				throw new ProcessingException("CCG was short");
			if (pos.endsWith("\tEOS")) {
				reachedEOS = true;
				pos = pos.substring(0, pos.length() - 4);
			}
			if (owner.getKeepPreviousPos() && word.hasFeature(owner.getPosFeatureName()))
				continue;
			word.addFeature(owner.getPosFeatureName(), pos.intern());
		}
		if (!reachedEOS)
			throw new ProcessingException("CCG sentence was too long: " + sentence.getSentenceAnnotation());
	}

	@Override
	protected String getExecTask() {
		return "ccg-pos";
	}

	@Override
	protected String getCollectTask() {
		return "ccg-pos-to-alvisnlp";
	}

	@Override
	protected List<String> getCommandLine() {
		CCGPosTagger owner = getModule();
		return Arrays.asList(
				owner.getExecutable().getAbsolutePath(),
				"--model",
				owner.getModel().getAbsolutePath(),
				"--input",
				getCCGInputFile().getAbsolutePath(),
				"--output",
				getCCGPosTaggerOutputFile().getAbsolutePath(),
				"--ofmt",
				"%p\\n\\tEOS\\n",
				"--maxwords",
				Integer.toString(getMaxLength())
				);
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

	@Override
	protected String getOutputFilename() {
		return null;
	}
}
