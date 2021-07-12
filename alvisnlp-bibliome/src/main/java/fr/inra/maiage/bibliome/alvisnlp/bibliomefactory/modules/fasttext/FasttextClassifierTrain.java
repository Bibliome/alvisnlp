package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.fasttext;

import java.io.IOException;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.IntegerMapping;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.files.OutputFile;

@AlvisNLPModule(beta = true)
public class FasttextClassifierTrain extends FasttextClassifierBase<FasttextClassifierTrainResolvedObjects> {
	private OutputFile modelFile;
	private IntegerMapping classWeights;
	private Integer wordGrams;
	private Integer minCharGrams;
	private Integer maxCharGrams;
	private Integer minCount = 1;
	private Integer minCountLabel = 0;
	private Integer buckets;
	private Double samplingThreshold = 0.0001;
	private Double learningRate;
	private Integer learningRateUpdateRate = 100;
	private Integer wordVectorSize;
	private Integer windowSize = 5;
	private Integer epochs;
	private Integer negativeSampling = 5;
	private FasttextLossFunction lossFunction;
	private Integer threads = 12;
	private InputFile pretrainedVectors;
	private Expression validationDocuments;
	private FasttextAttribute[] validationAttributes;
	private Integer autotuneDuration = 300;
	private String[] commandlineOptions;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			FasttextClassifierTrainExternalHandler ext = new FasttextClassifierTrainExternalHandler(ctx, this, corpus);
			ext.start();
		}
		catch (InterruptedException|IOException e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	protected FasttextClassifierTrainResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new FasttextClassifierTrainResolvedObjects(ctx, this);
	}

	protected boolean isValidating() {
		return validationDocuments != null;
	}

	@Param
	public OutputFile getModelFile() {
		return modelFile;
	}

	@Param(mandatory = false)
	public IntegerMapping getClassWeights() {
		return classWeights;
	}

	@Param(mandatory = false)
	public Integer getWordGrams() {
		return wordGrams;
	}

	@Param(mandatory = false)
	public Integer getMinCharGrams() {
		return minCharGrams;
	}

	@Param(mandatory = false)
	public Integer getMaxCharGrams() {
		return maxCharGrams;
	}

	@Param
	public Integer getMinCount() {
		return minCount;
	}

	@Param
	public Integer getMinCountLabel() {
		return minCountLabel;
	}

	@Param(mandatory = false)
	public Integer getBuckets() {
		return buckets;
	}

	@Param
	public Double getSamplingThreshold() {
		return samplingThreshold;
	}

	@Param(mandatory = false)
	public Double getLearningRate() {
		return learningRate;
	}

	@Param
	public Integer getLearningRateUpdateRate() {
		return learningRateUpdateRate;
	}

	@Param(mandatory = false)
	public Integer getWordVectorSize() {
		return wordVectorSize;
	}

	@Param
	public Integer getWindowSize() {
		return windowSize;
	}

	@Param(mandatory = false)
	public Integer getEpochs() {
		return epochs;
	}

	@Param
	public Integer getNegativeSampling() {
		return negativeSampling;
	}

	@Param(mandatory = false)
	public FasttextLossFunction getLossFunction() {
		return lossFunction;
	}

	@Param
	public Integer getThreads() {
		return threads;
	}

	@Param(mandatory = false)
	public InputFile getPretrainedVectors() {
		return pretrainedVectors;
	}

	@Param(mandatory = false)
	public String[] getCommandlineOptions() {
		return commandlineOptions;
	}

	@Param(mandatory = false)
	public Expression getValidationDocuments() {
		return validationDocuments;
	}

	@Param(mandatory = false)
	public FasttextAttribute[] getValidationAttributes() {
		return validationAttributes;
	}

	@Param
	public Integer getAutotuneDuration() {
		return autotuneDuration;
	}

	public void setAutotuneDuration(Integer autotuneDuration) {
		this.autotuneDuration = autotuneDuration;
	}

	public void setValidationDocuments(Expression validationDocuments) {
		this.validationDocuments = validationDocuments;
	}

	public void setValidationAttributes(FasttextAttribute[] validationAttributes) {
		this.validationAttributes = validationAttributes;
	}

	public void setCommandlineOptions(String[] commandlineOptions) {
		this.commandlineOptions = commandlineOptions;
	}

	public void setMinCount(Integer minCount) {
		this.minCount = minCount;
	}

	public void setMinCountLabel(Integer minCountLabel) {
		this.minCountLabel = minCountLabel;
	}

	public void setBuckets(Integer buckets) {
		this.buckets = buckets;
	}

	public void setSamplingThreshold(Double samplingThreshold) {
		this.samplingThreshold = samplingThreshold;
	}

	public void setLearningRate(Double learningRate) {
		this.learningRate = learningRate;
	}

	public void setLearningRateUpdateRate(Integer learningRateUpdateRate) {
		this.learningRateUpdateRate = learningRateUpdateRate;
	}

	public void setWordVectorSize(Integer wordVectorSize) {
		this.wordVectorSize = wordVectorSize;
	}

	public void setWindowSize(Integer windowSize) {
		this.windowSize = windowSize;
	}

	public void setEpochs(Integer epochs) {
		this.epochs = epochs;
	}

	public void setNegativeSampling(Integer negativeSampling) {
		this.negativeSampling = negativeSampling;
	}

	public void setLossFunction(FasttextLossFunction lossFunction) {
		this.lossFunction = lossFunction;
	}

	public void setThreads(Integer threads) {
		this.threads = threads;
	}

	public void setPretrainedVectors(InputFile pretrainedVectors) {
		this.pretrainedVectors = pretrainedVectors;
	}

	public void setWordGrams(Integer wordGrams) {
		this.wordGrams = wordGrams;
	}

	public void setMinCharGrams(Integer minCharGrams) {
		this.minCharGrams = minCharGrams;
	}

	public void setMaxCharGrams(Integer maxCharGrams) {
		this.maxCharGrams = maxCharGrams;
	}

	public void setClassWeights(IntegerMapping classWeights) {
		this.classWeights = classWeights;
	}

	public void setModelFile(OutputFile modelFile) {
		this.modelFile = modelFile;
	}
}
