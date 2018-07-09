package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.File;
import java.util.Map;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;

abstract class AbstractContesExternalHandler<R extends ResolvedObjects,T extends CorpusModule<R> & AbstractContes> extends ExternalHandler<Corpus,T> {
	protected AbstractContesExternalHandler(ProcessingContext<Corpus> processingContext, T module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected String getPrepareTask() {
		return "alvisnlp-to-contes";
	}

	@Override
	protected String getCollectTask() {
		return "contes-to-alvisnlp";
	}
	
	protected String getContesCommand() {
		return new File(getModule().getContesDir(), getContesModule()).getAbsolutePath();
	}

	protected abstract String getContesModule();

	@Override
	protected void updateEnvironment(Map<String,String> env) {
		env.put("PYTHONPATH", getModule().getContesDir().getAbsolutePath());
		env.put("PATH", System.getenv("PATH"));
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
