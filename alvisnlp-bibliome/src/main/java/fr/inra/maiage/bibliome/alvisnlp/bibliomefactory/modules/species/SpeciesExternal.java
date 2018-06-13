package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.External;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ModuleBase;
import fr.inra.maiage.bibliome.util.filelines.FileLines;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.filelines.TabularFormat;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

class SpeciesExternal extends FileLines<Map<String,Section>> implements External<Corpus> {
	private final InputDirectory corpusDir;
	private final Map<String,Section> sectionMap;
	private final Species owner;
	
	SpeciesExternal(Species owner, Logger logger, InputDirectory corpusDir, Map<String,Section> sectionMap) {
		super(logger);
		this.owner = owner;
		TabularFormat format = getFormat();
		format.setMinColumns(4);
		format.setMaxColumns(5);
		format.setStrictColumnNumber(true);
		this.corpusDir = corpusDir;
		this.sectionMap = sectionMap;
	}

	@Override
	public void processEntry(Map<String, Section> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
		String sourceFile = entry.get(0);
		Section sec = data.get(sourceFile);
		if (sec == null) {
			getLogger().warning("could not make sense of: " + sourceFile);
			return;
		}
		Layer layer = sec.ensureLayer(owner.getTargetLayerName());
		int start = Integer.parseInt(entry.get(1));
		int end = Integer.parseInt(entry.get(2)) + 1;
		Annotation a = new Annotation(owner, layer, start, end);
		if (owner.getTaxidFeature() != null && entry.size() == 5) {
			String taxid = entry.get(4);
			a.addFeature(owner.getTaxidFeature(), taxid);
		}
	}

	@Override
	public Module<Corpus> getOwner() {
		return owner;
	}

	@Override
	public String[] getCommandLineArgs() throws ModuleException {
		return new String[] {
				"./species",
				corpusDir.getAbsolutePath()
		};
	}

	@Override
	public String[] getEnvironment() throws ModuleException {
		return null;
	}

	@Override
	public File getWorkingDirectory() throws ModuleException {
		return owner.getSpeciesDir();
	}

	@Override
	public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
		try {
			process(out, sectionMap);
			Logger logger = getLogger();
			while (true) {
				String line = err.readLine();
				if (line == null)
					break;
				logger.info("    " + line);
			}
		}
		catch (IOException|InvalidFileLineEntry e) {
			ModuleBase.rethrow(e);
		}
	}
}