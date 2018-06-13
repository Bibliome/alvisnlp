package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.chemspot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AbstractExternal;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.InputFile;

class ChemspotExternal extends AbstractExternal<Corpus,Chemspot> {
	private final InputDirectory javaHome;
	private final InputDirectory chemspotDir;
	private final boolean noDict;
	private final File inputDir;
	private final File outputDir;
	
	ChemspotExternal(Chemspot owner, ProcessingContext<Corpus> ctx, InputDirectory javaHome, InputDirectory chemspotDir, boolean noDict, File tmpDir) {
		super(owner, ctx);
		this.javaHome = javaHome;
		this.chemspotDir = chemspotDir;
		this.noDict = noDict;
		this.inputDir = new File(tmpDir, "input");
		this.outputDir = new File(tmpDir, "output");
		inputDir.mkdirs();
		outputDir.mkdirs();
	}

	@Override
	public String[] getCommandLineArgs() throws ModuleException {
		List<String> result = new ArrayList<String>();
		result.add(javaHome + "/bin/java");
		result.add("-Xmx12G");
		result.add("-jar");
		result.add("chemspot.jar");
		result.add("-f");
		result.add(inputDir.getAbsolutePath());
		result.add("-o");
		result.add(outputDir.getAbsolutePath());
		if (noDict) {
			result.add("-d");
			result.add("");
		}
		return result.toArray(new String[result.size()]);
	}

	@Override
	public String[] getEnvironment() throws ModuleException {
		return new String[] {
				"JAVA_HOME="+javaHome
		};
	}

	@Override
	public File getWorkingDirectory() throws ModuleException {
		return chemspotDir;
	}
	
	void addInput(String name, String content) throws FileNotFoundException {
		InputFile file = new InputFile(inputDir, name + ".txt");
		try (PrintStream out = new PrintStream(file)) {
			out.print(content);
		}
	}
	
	<D,A> void readOutput(ChemspotFileLines<D,A> fileLines, D data, String name) throws InvalidFileLineEntry, IOException {
		File file = new File(outputDir, name + ".txt.chem");
		fileLines.process(file, "UTF-8", data);
	}
}
