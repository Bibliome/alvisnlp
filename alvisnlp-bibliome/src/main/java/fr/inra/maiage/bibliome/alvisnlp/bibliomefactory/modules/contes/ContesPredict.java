package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.IOException;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.InputFile;

@AlvisNLPModule(beta=true)
public abstract class ContesPredict extends AbstractContesTerms<InputFile,ContesPredictTermClassifier> {
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			new ContesPredictExternalHandler(ctx, this, corpus).start();
		}
		catch (IOException | InterruptedException e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	@Param
	public ContesPredictTermClassifier[] getTermClassifiers() {
		return super.getTermClassifiers();
	}

	@Override
	public void setTermClassifiers(ContesPredictTermClassifier[] termClassifier) {
		super.setTermClassifiers(termClassifier);
	}
}
