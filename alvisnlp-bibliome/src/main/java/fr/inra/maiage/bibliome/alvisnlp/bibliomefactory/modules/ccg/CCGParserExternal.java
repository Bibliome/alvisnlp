package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AbstractExternal;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.files.OutputFile;

class CCGParserExternal extends AbstractExternal<Corpus,CCGParser> {
	private final int maxLength;
	private final OutputFile input;
	private final InputFile output;
	private final File log;

	CCGParserExternal(CCGParser owner, ProcessingContext<Corpus> ctx, int n, int maxLength) {
		super(owner, ctx);
		this.maxLength = maxLength;
		File tmp = owner.getTempDir(ctx);
		String h = String.format("corpus_%8H", n);
		input = new OutputFile(tmp, h + ".txt");
		output = new InputFile(tmp, h + ".dep");
		log = new File(tmp, "ccg.log");
	}

	@Override
	public String[] getCommandLineArgs() throws ModuleException {
		List<String> clArgs = new ArrayList<String>();
		clArgs.addAll(Arrays.asList(
				getOwner().getExecutable().getAbsolutePath(),
				"--model",
				getOwner().getParserModel().getAbsolutePath(),
				"--super",
				getOwner().getSuperModel().getAbsolutePath(),
				"--parser-maxsupercats",
				getOwner().getMaxSuperCats().toString(),
				"--input",
				getInput().getAbsolutePath(),
				"--output",
				getOutput().getAbsolutePath(),
				"--parser-maxwords",
				Integer.toString(maxLength * 2),
				"--super-maxwords",
				Integer.toString(maxLength * 2),
				"--log",
				log.getAbsolutePath()
		));
		if (getOwner().getStanfordMarkedUpScript() != null) {
			clArgs.add("--parser-markedup");
			clArgs.add(getOwner().getStanfordMarkedUpScript().getAbsolutePath());
		}
		return clArgs.toArray(new String[clArgs.size()]);
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