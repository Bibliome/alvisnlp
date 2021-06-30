package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.opennlp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.IntegerMapping;
import fr.inra.maiage.bibliome.util.Checkable;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.TargetStream;
import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.doccat.NGramFeatureGenerator;
import opennlp.tools.ml.AbstractTrainer;
import opennlp.tools.util.CollectionObjectStream;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;

@AlvisNLPModule(beta=true)
public class OpenNLPDocumentCategorizerTrain extends OpenNLPDocumentCategorizerBase implements Checkable {
	private TargetStream model;
	private String language;
	private OpenNLPAlgorithm algorithm = OpenNLPAlgorithm.PERCEPTRON;
	private Boolean bagOfWords = true;
	private Integer nGrams = null;
	private IntegerMapping classWeights;
	private Integer iterations = 100;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		ObjectStream<DocumentSample> trainingSet = getTrainingSet(evalCtx, corpus);
		TrainingParameters mlParams = ModelUtil.createDefaultTrainingParameters();
		mlParams.put(AbstractTrainer.ALGORITHM_PARAM, algorithm.paramValue);
		mlParams.put(AbstractTrainer.VERBOSE_PARAM, "false");
		mlParams.put(AbstractTrainer.ITERATIONS_PARAM, iterations);
		try (OutputStream os = model.getOutputStream()) {
			DoccatFactory factory = new DoccatFactory(getFeatureGenerators());
			DoccatModel dcmodel = DocumentCategorizerME.train(language, trainingSet, mlParams, factory);
			dcmodel.serialize(os);
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}
	
	private FeatureGenerator[] getFeatureGenerators() throws InvalidFormatException {
		List<FeatureGenerator> result = new ArrayList<FeatureGenerator>(2);
		if (bagOfWords) {
			result.add(new BagOfWordsFeatureGenerator());
		}
		if (nGrams != null) {
			result.add(new NGramFeatureGenerator(2, nGrams));
		}
		return result.toArray(new FeatureGenerator[2]);
	}
	
	private int getWeight(String theClass) {
		if (classWeights == null) {
			return 1;
		}
		if (classWeights.containsKey(theClass)) {
			return classWeights.get(theClass);
		}
		return 1;
	}
	
	private DocumentSample getDocumentSample(EvaluationContext evalCtx, Element doc) {
		OpenNLPDocumentCategorizerResolvedObjects resObj = getResolvedObjects();
		String[] tokens = resObj.getDocumentTokens(evalCtx, doc);
		String category = doc.getLastFeature(getCategoryFeature());
		return new DocumentSample(category, tokens);
	}
	
	private ObjectStream<DocumentSample> getTrainingSet(EvaluationContext evalCtx, Corpus corpus) {
		Collection<DocumentSample> result = new ArrayList<DocumentSample>();
		OpenNLPDocumentCategorizerResolvedObjects resObj = getResolvedObjects();
		for (Element doc : Iterators.loop(resObj.getDocuments(evalCtx, corpus))) {
			DocumentSample ds = getDocumentSample(evalCtx, doc);
			int w = getWeight(ds.getCategory());
			for (int i = 0; i < w; ++i) {
				result.add(ds);
			}
		}
		return new CollectionObjectStream<DocumentSample>(result);
	}

	@Override
	public boolean check(Logger logger) {
		if ((!bagOfWords) && (nGrams == null)) {
			logger.severe("must chose a feature generation scheme, either set bagOfWords and/or nGrams");
			return false;
		}
		if ((nGrams != null) && (nGrams < 2)) {
			logger.severe("invalid value for nGrams, must be greater than 1");
			return false;
		}
		return true;
	}

	@Param
	public TargetStream getModel() {
		return model;
	}

	@Param
	public String getLanguage() {
		return language;
	}

	@Param
	public OpenNLPAlgorithm getAlgorithm() {
		return algorithm;
	}

	@Param
	public Boolean getBagOfWords() {
		return bagOfWords;
	}

	@Param(mandatory=false)
	public Integer getnGrams() {
		return nGrams;
	}

	@Param(mandatory=false)
	public IntegerMapping getClassWeights() {
		return classWeights;
	}

	@Param
	public Integer getIterations() {
		return iterations;
	}

	public void setIterations(Integer iterations) {
		this.iterations = iterations;
	}

	public void setClassWeights(IntegerMapping classWeights) {
		this.classWeights = classWeights;
	}

	public void setBagOfWords(Boolean bagOfWords) {
		this.bagOfWords = bagOfWords;
	}

	public void setnGrams(Integer nGrams) {
		this.nGrams = nGrams;
	}

	public void setAlgorithm(OpenNLPAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public void setModel(TargetStream model) {
		this.model = model;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
