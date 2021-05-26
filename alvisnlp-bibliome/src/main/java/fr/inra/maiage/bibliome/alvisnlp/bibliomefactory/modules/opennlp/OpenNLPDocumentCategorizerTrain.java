package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.opennlp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.TargetStream;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.util.CollectionObjectStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;

@AlvisNLPModule(beta=true)
public class OpenNLPDocumentCategorizerTrain extends OpenNLPDocumentCategorizerBase {
	private TargetStream model;
	private String language;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		ObjectStream<DocumentSample> trainingSet = getTrainingSet(evalCtx, corpus);
		TrainingParameters mlParams = ModelUtil.createDefaultTrainingParameters();
		DoccatFactory factory = new DoccatFactory();
		try (OutputStream os = model.getOutputStream()) {
			DoccatModel dcmodel = DocumentCategorizerME.train(language, trainingSet, mlParams, factory);
			dcmodel.serialize(os);
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
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
			result.add(ds);
		}
		return new CollectionObjectStream<DocumentSample>(result);
	}
}
