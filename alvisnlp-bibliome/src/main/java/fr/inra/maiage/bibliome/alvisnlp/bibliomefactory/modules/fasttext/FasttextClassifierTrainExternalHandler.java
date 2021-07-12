package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.fasttext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.IntegerMapping;

public class FasttextClassifierTrainExternalHandler extends FasttextClassifierBaseExternalHandler<FasttextClassifierTrain> {
	public FasttextClassifierTrainExternalHandler(ProcessingContext<Corpus> processingContext, FasttextClassifierTrain module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		EvaluationContext evalCtx = new EvaluationContext(getLogger());
		FasttextClassifierTrain owner = getModule();
		FasttextClassifierBaseResolvedObjects resObj = owner.getResolvedObjects();
		writeDocumentLines(evalCtx, getTrainingFile(), resObj.getDocuments(), resObj.getAttributes(), true, true);
		if (owner.isValidating()) {
			FasttextAttribute.Resolved[] attributes = resObj.getValidationAttributes();
			if (attributes == null) {
				attributes = resObj.getAttributes();
			}
			writeDocumentLines(evalCtx, getValidationFile(), resObj.getValidationDocuments(), attributes, true, false);
		}
	}

	private File getValidationFile() {
		return getTempFile("fasttext-validation.txt");
	}

	private File getTrainingFile() {
		return getTempFile("fasttext-training.txt");
	}
	
	@Override
	protected int getDocWeight(Element doc) {
		FasttextClassifierTrain owner = getModule();
		IntegerMapping map = owner.getClassWeights();
		if (map == null) {
			return 1;
		}
		String theClass = doc.getLastFeature(owner.getClassFeature());
		if (map.containsKey(theClass)) {
			return map.get(theClass);
		}
		return 1;
	}

	@Override
	protected void collect() throws IOException, ModuleException {
		// nothing to collect
	}

	@Override
	protected String getExecTask() {
		return "fasttext-train-supervised";
	}

	@Override
	protected String getCollectTask() {
		return "fasttext-model-collect";
	}

	@Override
	protected List<String> getCommandLine() {
		FasttextClassifierTrain owner = getModule();
		List<String> result = new ArrayList<String>(
				Arrays.asList(
						owner.getFasttextExecutable().getAbsolutePath(),
						"supervised",
						"-minCount",
						owner.getMinCount().toString(),
						"-minCountLabel",
						owner.getMinCountLabel().toString(),
						"-t",
						owner.getSamplingThreshold().toString(),
						"-lrUpdateRate",
						owner.getLearningRateUpdateRate().toString(),
						"-ws",
						owner.getWindowSize().toString(),
						"-neg",
						owner.getNegativeSampling().toString(),
						"-thread",
						owner.getThreads().toString(),
						"-input",
						getTrainingFile().getAbsolutePath(),
						"-output",
						owner.getModelFile().getAbsolutePath()
						));
		addNotNullToCommandLine(result, owner.getWordGrams(), "-wordNgrams");
		addNotNullToCommandLine(result, owner.getMinCharGrams(), "-minn");
		addNotNullToCommandLine(result, owner.getMaxCharGrams(), "-maxn");
		addNotNullToCommandLine(result, owner.getBuckets(), "-bucket");
		addNotNullToCommandLine(result, owner.getPretrainedVectors(), "-pretrainedVectors");
		addNotNullToCommandLine(result, owner.getLossFunction(), "-loss");
		addNotNullToCommandLine(result, owner.getWordVectorSize(), "-dim");
		addNotNullToCommandLine(result, owner.getLearningRate(), "-lr");
		addNotNullToCommandLine(result, owner.getEpochs(), "-epoch");
		if (owner.isValidating()) {
			addToCommandLine(result, "-autotune-validation", getValidationFile().getAbsolutePath());
			addToCommandLine(result, "-autotune-duration", owner.getAutotuneDuration().toString());
		}
		addNotNullToCommandLine(result, owner.getCommandlineOptions());
		return result;
	}

	@Override
	protected String getOutputFilename() {
		return null;
	}
}
