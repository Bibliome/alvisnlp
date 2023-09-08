package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti;

import java.io.File;
import java.io.IOException;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;

class WapitiTrainExternalHandler extends AbstractWapitiExternalHandler<WapitiTrain> {
	WapitiTrainExternalHandler(ProcessingContext processingContext, WapitiTrain module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected void fillAdditionalCommandLineArgs(List<String> args) {
		WapitiTrain owner = getModule();
		addOption(args, "--type", owner.getModelType());
		addOption(args, "--algo", owner.getTrainAlgorithm());
		addOption(args, "--pattern", owner.getPatternFile().getAbsolutePath());
	}

	@Override
	protected String getMode() {
		return "train";
	}

	@Override
	protected File getWapitiOutputFile() {
		return getModule().getModelFile();
	}

	@Override
	protected void collect() throws IOException, ModuleException {
	}
}
