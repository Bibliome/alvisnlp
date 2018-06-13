package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg;

import java.io.File;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AbstractExternal;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.files.OutputFile;

class CCGPosTaggerExternal extends AbstractExternal<Corpus,CCGPosTagger> {
	private final int maxLength;
	private final OutputFile input;
	private final InputFile output;
	
	CCGPosTaggerExternal(CCGPosTagger owner, ProcessingContext<Corpus> ctx, int n, int maxLength) {
		super(owner, ctx);
		this.maxLength = maxLength;
		File tmp = owner.getTempDir(ctx);
		String h = String.format("corpus_%8H", n);
		input = new OutputFile(tmp, h + ".txt");
		output = new InputFile(tmp, h + ".pos");
	}

	@Override
	public String[] getCommandLineArgs() throws ModuleException {
		return new String[] {
				getOwner().getExecutable().getAbsolutePath(),
				"--model",
				getOwner().getModel().getAbsolutePath(),
				"--input",
				getInput().getAbsolutePath(),
				"--output",
				getOutput().getAbsolutePath(),
				"--ofmt",
				"%p\\n\\tEOS\\n",
				"--maxwords",
				Integer.toString(maxLength)
		};
	}

	@Override
	public String[] getEnvironment() throws ModuleException {
		return null;
	}

	@Override
	public File getWorkingDirectory() throws ModuleException {
		return null;
	}

	public OutputFile getInput() {
		return input;
	}

	public InputFile getOutput() {
		return output;
	}
}