package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.opennlp;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;

@AlvisNLPModule(beta=true)
public class OpenNLPDocumentCategorizer extends OpenNLPDocumentCategorizerBase {
	private SourceStream model;
	private String scoreFeature;
	private String scoresFeaturePrefix;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		OpenNLPDocumentCategorizerResolvedObjects resObj = getResolvedObjects();
		try (InputStream is = model.getInputStream()) {
			DoccatModel dcmodel = new DoccatModel(is);
			DocumentCategorizerME categorizer = new DocumentCategorizerME(dcmodel);
			for (Element doc : Iterators.loop(resObj.getDocuments(evalCtx, corpus))) {
				String[] input = resObj.getDocumentTokens(evalCtx, doc);
				double[] scores = categorizer.categorize(input);
				double bestScore = Double.MIN_VALUE;
				String bestCategory = null;
				for (int i = 0; i < categorizer.getNumberOfCategories(); ++i) {
					double score = scores[i];
					if (score > bestScore) {
						bestScore = score;
						bestCategory = categorizer.getCategory(i);
					}
					if (scoresFeaturePrefix != null) {
						doc.addFeature(scoresFeaturePrefix + categorizer.getCategory(i), Double.toString(score));
					}
				}
				doc.addFeature(getCategoryFeature(), bestCategory);
				doc.addFeature(scoreFeature, Double.toString(bestScore));
			}
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

	@Param
	public SourceStream getModel() {
		return model;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getScoreFeature() {
		return scoreFeature;
	}

	@Param(mandatory=false)
	public String getScoresFeaturePrefix() {
		return scoresFeaturePrefix;
	}

	public void setModel(SourceStream model) {
		this.model = model;
	}

	public void setScoreFeature(String scoreFeature) {
		this.scoreFeature = scoreFeature;
	}

	public void setScoresFeaturePrefix(String scoresFeaturePrefix) {
		this.scoresFeaturePrefix = scoresFeaturePrefix;
	}
}
