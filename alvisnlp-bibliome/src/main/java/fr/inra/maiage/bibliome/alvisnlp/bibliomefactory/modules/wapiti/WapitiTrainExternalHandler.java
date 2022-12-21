package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti.WapitiTrain.WapitiTrainResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;

class WapitiTrainExternalHandler extends AbstractWapitiExternalHandler<WapitiTrain> {
	WapitiTrainExternalHandler(ProcessingContext<Corpus> processingContext, WapitiTrain module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		super.prepare();
		WapitiTrain owner = getModule();
		WapitiTrainResolvedObjects resObj = owner.createResolvedObjects(getProcessingContext());
		WapitiAttribute.Resolved[] attributes = resObj.getAttributes();
		try (PrintStream ps = new PrintStream(getPatternFile())) {
			for (int iAtt = 0; iAtt < attributes.length; ++iAtt) {
				WapitiAttribute.Resolved att = attributes[iAtt];
				for (int pos : att.getWindow()) {
					int abs = Math.abs(pos);
					char sign = getSign(pos);
					ps.format("U%d%c%d:%%x[%+d,%d]\n", iAtt, sign, abs, pos, iAtt);
				}
			}
		}
	}

	private char getSign(int pos) {
		if (pos == 0) {
			return 'z';
		}
		if (pos > 0) {
			return 'p';
		}
		return 'n';
	}

	@Override
	protected void fillAdditionalCommandLineArgs(List<String> args) {
		WapitiTrain owner = getModule();
		addOption(args, "--type", owner.getModelType());
		addOption(args, "--algo", owner.getTrainAlgorithm());
		addOption(args, "--pattern", getPatternFile().getAbsolutePath());
	}

	private File getPatternFile() {
		return getTempFile("pattern.crf");
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
