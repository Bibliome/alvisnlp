package org.bibliome.alvisnlp.modules.chemspot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import alvisnlp.module.Annotable;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.External;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.InputFile;

class ChemspotExternal<T extends Annotable> implements External<T> {
	private final ProcessingContext<T> ctx;
	private final Module<T> owner;
	private final InputDirectory javaHome;
	private final InputDirectory chemspotDir;
	private final boolean noDict;
	private final File inputDir;
	private final File outputDir;
	
	ChemspotExternal(ProcessingContext<T> ctx, Module<T> owner, InputDirectory javaHome, InputDirectory chemspotDir, boolean noDict, File tmpDir) {
		super();
		this.ctx = ctx;
		this.owner = owner;
		this.javaHome = javaHome;
		this.chemspotDir = chemspotDir;
		this.noDict = noDict;
		this.inputDir = new File(tmpDir, "input");
		this.outputDir = new File(tmpDir, "output");
		inputDir.mkdirs();
		outputDir.mkdirs();
	}

	@Override
	public Module<T> getOwner() {
		return owner;
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

	@Override
	public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
        Logger logger = owner.getLogger(ctx);
        try {
            logger.fine("chemspot standard error:");
            for (String line = err.readLine(); line != null; line = err.readLine()) {
                logger.fine("    " + line);
            }
            logger.fine("end of chemspot standard error");
        }
        catch (IOException ioe) {
            logger.warning("could not read chemspot standard error: " + ioe.getMessage());
        }
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
