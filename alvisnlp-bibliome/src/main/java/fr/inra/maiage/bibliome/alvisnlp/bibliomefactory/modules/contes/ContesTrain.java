package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.OutputFile;

@AlvisNLPModule(beta=true)
public class ContesTrain extends AbstractContesTerms {
	private OutputFile regressionMatrix;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		File tmpDir = getTempDir(ctx);
		ContesTrainExternal external = new ContesTrainExternal(this, logger, tmpDir);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		try {
			external.createTermsFile(evalCtx, corpus);
			external.createAttributionsFile(evalCtx, corpus);
			callExternal(ctx, "contes-train", external);
		}
		catch (IOException e) {
			rethrow(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	JSONObject getAttributions(EvaluationContext ctx, Corpus corpus) {
		JSONObject result = new JSONObject();
		for (Section sec : Iterators.loop(sectionIterator(ctx, corpus))) {
			for (Annotation term : sec.getLayer(getTermLayer())) {
				if (!term.hasFeature(getConceptFeature())) {
					continue;
				}
				String id = term.getStringId();
				JSONArray concepts = new JSONArray();
				concepts.addAll(term.getFeature(getConceptFeature()));
				result.put(id, concepts);
			}
		}
		return result;
	}

	@Param
	public OutputFile getRegressionMatrix() {
		return regressionMatrix;
	}

	public void setRegressionMatrix(OutputFile regressionMatrix) {
		this.regressionMatrix = regressionMatrix;
	}
}
