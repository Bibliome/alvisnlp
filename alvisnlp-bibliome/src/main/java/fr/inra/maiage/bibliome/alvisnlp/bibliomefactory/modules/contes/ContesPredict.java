package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.InputFile;

@AlvisNLPModule(beta=true)
public class ContesPredict extends AbstractContesTerms {
	private InputFile regressionMatrix;

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		File tmpDir = getTempDir(ctx);
		ContesPredictExternal external = new ContesPredictExternal(this, logger, tmpDir);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		try {
			external.createTermsFile(evalCtx, corpus);
			callExternal(ctx, "contes-train", external);
			Map<String,String> predictions = external.readPredictions();
			setPredictionFeature(evalCtx, corpus, predictions);
		}
		catch (IOException e) {
			rethrow(e);
		}
	}
	
	private void setPredictionFeature(EvaluationContext ctx, Corpus corpus, Map<String,String> predictions) {
		for (Section sec : Iterators.loop(sectionIterator(ctx, corpus))) {
			for (Annotation term : sec.getLayer(getTermLayer())) {
				String id = term.getStringId();
				if (predictions.containsKey(id)) {
					String conceptId = predictions.get(id);
					term.addFeature(getConceptFeature(), conceptId);
				}
			}
		}
	}

	@Param
	public InputFile getRegressionMatrix() {
		return regressionMatrix;
	}

	public void setRegressionMatrix(InputFile regressionMatrix) {
		this.regressionMatrix = regressionMatrix;
	}
}
