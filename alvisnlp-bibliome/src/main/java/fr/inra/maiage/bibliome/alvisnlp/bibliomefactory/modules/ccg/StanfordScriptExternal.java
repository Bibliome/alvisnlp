package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.External;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ModuleBase;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

class StanfordScriptExternal implements External<Corpus> {
	private final OutputFile input;
	private final String output;
	private final ProcessingContext<Corpus> ctx;
	private final CCGParser owner;

	StanfordScriptExternal(CCGParser owner, ProcessingContext<Corpus> ctx) {
		super();
		this.ctx = ctx;
		File tmp = owner.getTempDir(ctx);
		input = new OutputFile(tmp, "corpus.dep");
		output = tmp + "/corpus.sd";
		this.owner = owner;
	}

	@Override
	public Module<Corpus> getOwner() {
		return owner;
	}

	@Override
	public String[] getCommandLineArgs() throws ModuleException {
		return new String[] {
				owner.getStanfordScript().getAbsolutePath(),
				"--ccgbank",
				input.getAbsolutePath()
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

	@Override
	public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
		try {
			owner.getLogger(ctx).info("reading Stanford Script output");
			TargetStream target = new FileTargetStream(owner.getInternalEncoding(), this.getOutput());
			PrintStream output = target.getPrintStream();
			while (true) {
				String line = out.readLine();
				if (line == null)
					break;
				output.println(line);
			}
			output.close();
		}
		catch (FileNotFoundException fnfe) {
			ModuleBase.rethrow(fnfe);
		}
		catch (IOException ioe) {
			ModuleBase.rethrow(ioe);
		}
	}

	public String getOutput() {
		return output;
	}
}