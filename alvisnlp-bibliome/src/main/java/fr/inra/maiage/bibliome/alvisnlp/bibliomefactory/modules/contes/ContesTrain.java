package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.IOException;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.OutputFile;

@AlvisNLPModule(beta=true)
public class ContesTrain extends AbstractContesTerms {
	private OutputFile regressionMatrix;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			new ContesTrainExternalHandler(ctx, this, corpus).start();
		}
		catch (IOException | InterruptedException e) {
			rethrow(e);
		}
	}

	@Param
	public OutputFile getRegressionMatrix() {
		return regressionMatrix;
	}

	public void setRegressionMatrix(OutputFile regressionMatrix) {
		this.regressionMatrix = regressionMatrix;
	}
}
