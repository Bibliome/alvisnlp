package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AbstractExternal;
import fr.inra.maiage.bibliome.util.Files;
import fr.inra.maiage.bibliome.util.files.OutputFile;

class TEESTrainExternal extends AbstractExternal<Corpus,TEESTrain> {
	private final OutputFile trainInput;
	private final OutputFile devInput;
	private final OutputFile testInput;
	private final File baseDir;
	private final File script;

	TEESTrainExternal(TEESTrain owner, ProcessingContext<Corpus> ctx) throws IOException {
		super(owner, ctx);
		File tmp = owner.getTempDir(ctx);
		baseDir = tmp;
		this.trainInput = new OutputFile(tmp.getAbsolutePath(), "train-o" + ".xml");
		this.devInput = new OutputFile(tmp.getAbsolutePath(), "devel-o" + ".xml");
		this.testInput = new OutputFile(tmp.getAbsolutePath(), "test-o" + ".xml");
		
		script = new File(tmp, "train.sh");
		// same ClassLoader as this class
		try (InputStream is = TEESTrain.class.getResourceAsStream("train.sh")) {
			Files.copy(is, script, 1024, true);
		}
		script.setExecutable(true);
	}
	
	@Override
	public String[] getCommandLineArgs() throws ModuleException {
		List<String> clArgs = new ArrayList<String>();
		clArgs.addAll(Arrays.asList(
				script.getAbsolutePath()));
		return clArgs.toArray(new String[clArgs.size()]);
	}

	@Override
	public String[] getEnvironment() throws ModuleException {
		return new String[] {
				"PATH=" + System.getenv("PATH"),
				"TEES_DIR=" + getOwner().getTeesHome().getAbsolutePath(),
				"TEES_PRE_EXE=" + getOwner().getTeesHome().getAbsolutePath() + "/Detectors/Preprocessor.py",
				"TEES_TRAIN_EXE=" + getOwner().getTeesHome().getAbsolutePath() + "/train.py",
				"TEES_TRAIN_IN="  + this.trainInput.getAbsolutePath(),
				"TEES_TRAIN_OUT=" + this.baseDir.getAbsolutePath() + "/train_pre.xml",
				"TEES_DEV_IN="  + this.devInput.getAbsolutePath(),
				"TEES_DEV_OUT=" + this.baseDir.getAbsolutePath() + "/dev_pre.xml",
				"OMITSTEPS=" + getOwner().getOmitSteps().toString(),
				"TEES_TEST_IN="  + this.testInput.getAbsolutePath(),
				"TEES_TEST_OUT=" + this.baseDir.getAbsolutePath() + "/test_pre.xml",
				"WORKDIR=" + this.baseDir.getAbsolutePath(),
				"MODELTD=" + getOwner().getModelTargetDir().getAbsolutePath(),
				"MODEL_NAME=" + getOwner().getModelName() 
			};
	}

	@Override
	public File getWorkingDirectory() throws ModuleException {
		return this.baseDir;
	}

	public OutputFile getTrainInput() {
		return trainInput;
	}

	public OutputFile getDevInput() {
		return devInput;
	}

	public OutputFile getTestInput() {
		return testInput;
	}
}