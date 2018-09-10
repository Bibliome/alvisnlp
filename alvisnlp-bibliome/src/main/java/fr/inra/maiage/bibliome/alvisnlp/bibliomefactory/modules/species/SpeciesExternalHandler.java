package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.species;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.filelines.FileLines;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.filelines.TabularFormat;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

class SpeciesExternalHandler extends ExternalHandler<Corpus,Species> {
	private static final String ENCODING = "ISO-8859-1";
	private final Map<String,Section> sectionMap = new LinkedHashMap<String,Section>();;

	SpeciesExternalHandler(ProcessingContext<Corpus> processingContext, Species module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		sectionMap.clear();
		EvaluationContext evalCtx = new EvaluationContext(getLogger());
		try {
			File inputDir = getSpeciesInputDir();
			for (Section sec : Iterators.loop(getModule().sectionIterator(evalCtx, getAnnotable()))) {
				int n = sectionMap.size();
				String fileName = Integer.toHexString(n) + ".txt";
				sectionMap.put(fileName, sec);
				TargetStream target = new FileTargetStream(ENCODING, new OutputFile(inputDir, fileName));
				PrintStream ps = target.getPrintStream();
				String contents = sec.getContents().replace('\n', ' ');
				ps.print(contents);
				ps.close();
			}
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}
	
	private File getSpeciesInputDir() {
		return getTempFile("input");
	}

	@Override
	protected void collect() throws IOException, ModuleException {
		SpeciesFileLines fileLines = new SpeciesFileLines();
		fileLines.process(getOutputFile(), ENCODING, sectionMap);
	}
	
	private class SpeciesFileLines extends FileLines<Map<String,Section>> {
		private SpeciesFileLines() {
			super(SpeciesExternalHandler.this.getLogger());
			TabularFormat format = getFormat();
			format.setMinColumns(4);
			format.setMaxColumns(5);
			format.setStrictColumnNumber(true);
		}

		@Override
		public void processEntry(Map<String, Section> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
			String sourceFile = entry.get(0);
			Section sec = data.get(sourceFile);
			if (sec == null) {
				getLogger().warning("could not make sense of: " + sourceFile);
				return;
			}
			Species owner = getModule();
			Layer layer = sec.ensureLayer(owner.getTargetLayerName());
			int start = Integer.parseInt(entry.get(1));
			int end = Integer.parseInt(entry.get(2)) + 1;
			Annotation a = new Annotation(owner, layer, start, end);
			if (owner.getTaxidFeature() != null && entry.size() == 5) {
				String taxid = entry.get(4);
				a.addFeature(owner.getTaxidFeature(), taxid);
			}
		}
		
	}

	@Override
	protected String getPrepareTask() {
		return "prepare-corpus";
	}

	@Override
	protected String getExecTask() {
		return "species";
	}

	@Override
	protected String getCollectTask() {
		return "species-to-alvisnlp";
	}

	@Override
	protected List<String> getCommandLine() {
		List<String> result = new ArrayList<String>();
		result.add("./species");
		result.add(getSpeciesInputDir().getAbsolutePath());
		return result;
	}

	@Override
	protected void updateEnvironment(Map<String,String> env) {
	}

	@Override
	protected File getWorkingDirectory() {
		return getModule().getSpeciesDir();
	}

	@Override
	protected String getInputFileame() {
		return null;
	}

	@Override
	protected String getOutputFilename() {
		return "output.species";
	}
}
