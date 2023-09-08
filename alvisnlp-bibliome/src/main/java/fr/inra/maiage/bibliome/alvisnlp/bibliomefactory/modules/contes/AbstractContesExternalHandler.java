package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;

abstract class AbstractContesExternalHandler<R extends ResolvedObjects,T extends CorpusModule<R> & AbstractContes> extends ExternalHandler<T> {
	protected AbstractContesExternalHandler(ProcessingContext processingContext, T module, Corpus annotable) {
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
	
	protected void addAdditionalArguments(List<String> cmdl) {
		String[] aa = getModule().getAdditionalArguments();
		if (aa != null) {
			cmdl.addAll(Arrays.asList(aa));
		}
	}
	
	protected static void addOptionalFile(List<String> cmdl, String opt, File f) {
		if (f != null) {
			cmdl.add(opt);
			cmdl.add(f.getAbsolutePath());
		}
	}
}
