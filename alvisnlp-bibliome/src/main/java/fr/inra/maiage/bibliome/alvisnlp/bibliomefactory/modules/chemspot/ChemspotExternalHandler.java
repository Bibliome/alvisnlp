package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.chemspot;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.files.InputFile;

class ChemspotExternalHandler extends ExternalHandler<Chemspot> {
	ChemspotExternalHandler(ProcessingContext processingContext, Chemspot module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		getChemspotInputDir().mkdirs();
		getChemspotOutputDir().mkdirs();
		EvaluationContext evalCtx = new EvaluationContext(getLogger());
		File inputDir = getChemspotInputDir();
		for (Section sec : Iterators.loop(getModule().sectionIterator(evalCtx, getCorpus()))) {
			String name = sec.getFileName();
			String content = sec.getContents();
			InputFile file = new InputFile(inputDir, name + ".txt");
			try (PrintStream out = new PrintStream(file)) {
				out.print(content.replace('\n', ' '));
			}
		}
	}
	
	private File getChemspotInputDir() {
		return getTempFile("input");
	}

	@Override
	protected void collect() throws IOException, ModuleException {
		EvaluationContext evalCtx = new EvaluationContext(getLogger());
		ChemspotFileLines<Layer,Annotation> chemspotFileLines = new CorpusChemspotFileLines(getLogger());
		for (Section sec : Iterators.loop(getModule().sectionIterator(evalCtx, getCorpus()))) {
			String name = sec.getFileName();
			Layer layer = sec.ensureLayer(getModule().getTargetLayer());
			readOutput(chemspotFileLines, layer, name);
		}
	}
	
	private <D,A> void readOutput(ChemspotFileLines<D,A> fileLines, D data, String name) throws InvalidFileLineEntry, IOException {
		File file = new File(getChemspotOutputDir(), name + ".txt.chem");
		fileLines.process(file, "UTF-8", data);
	}
	
	private class CorpusChemspotFileLines extends ChemspotFileLines<Layer,Annotation> {
		private final Logger logger;
		
		private CorpusChemspotFileLines(Logger logger) {
			super();
			this.logger = logger;
		}

		@Override
		protected void setFDA_DATE(Annotation annotation, String string) {
			annotation.addFeature(getModule().getFdaDateFeature(), string);
		}

		@Override
		protected void setFDA(Annotation annotation, String string) {
			annotation.addFeature(getModule().getFdaFeature(), string);
		}

		@Override
		protected void setMESH(Annotation annotation, String string) {
			annotation.addFeature(getModule().getMeshFeature(), string);
		}

		@Override
		protected void setKEGD(Annotation annotation, String string) {
			annotation.addFeature(getModule().getKegdFeature(), string);
		}

		@Override
		protected void setKEGG(Annotation annotation, String string) {
			annotation.addFeature(getModule().getKeggFeature(), string);
		}

		@Override
		protected void setHMBD(Annotation annotation, String string) {
			annotation.addFeature(getModule().getHmdbFeature(), string);
		}

		@Override
		protected void setDRUG(Annotation annotation, String string) {
			annotation.addFeature(getModule().getDrugFeature(), string);
		}

		@Override
		protected void setINCH(Annotation annotation, String string) {
			annotation.addFeature(getModule().getInchFeature(), string);
		}

		@Override
		protected void setPUBS(Annotation annotation, String string) {
			annotation.addFeature(getModule().getPubsFeature(), string);
		}

		@Override
		protected void setPUBC(Annotation annotation, String string) {
			annotation.addFeature(getModule().getPubcFeature(), string);
		}

		@Override
		protected void setCAS(Annotation annotation, String string) {
			annotation.addFeature(getModule().getCasFeature(), string);
		}

		@Override
		protected void setCHEB(Annotation annotation, String string) {
			annotation.addFeature(getModule().getChebFeature(), string);
		}

		@Override
		protected void setCHID(Annotation annotation, String string) {
			annotation.addFeature(getModule().getChidFeature(), string);
		}

		@Override
		protected void setType(Annotation annotation, String string) {
			annotation.addFeature(getModule().getChemTypeFeature(), string);
		}

		@Override
		protected Annotation createAnnotation(Layer data, int start0, int end0, String form) {
			int start = start0 + 1;
			int end = end0 + 2;
			Section sec = data.getSection();
			String content = sec.getContents().replace('\n', ' ');
			while (start >= 0) {
				String s = content.substring(start, end);
				if (s.equals(form)) {
					return new Annotation(getModule(), data, start, end);
				}
				start--;
				end--;
			}
			logger.warning("weird chemspot positions: " + form + " / " + content.substring(start0, end0));
			return null;
		}
	}

	@Override
	protected String getPrepareTask() {
		return "write-corpus";
	}

	@Override
	protected String getExecTask() {
		return "chemspot";
	}

	@Override
	protected String getCollectTask() {
		return "chemspot-to-alvisnlp";
	}

	@Override
	protected List<String> getCommandLine() {
		List<String> result = new ArrayList<String>();
		result.add(getModule().getJavaHome() + "/bin/java");
		result.add("-Xmx12G");
		result.add("-jar");
		result.add("chemspot.jar");
		result.add("-f");
		result.add(getChemspotInputDir().getAbsolutePath());
		result.add("-o");
		result.add(getChemspotOutputDir().getAbsolutePath());
		if (getModule().getNoDict()) {
			result.add("-d");
			result.add("");
		}
		return result;
	}
	
	private File getChemspotOutputDir() {
		return getTempFile("output");
	}

	@Override
	protected void updateEnvironment(Map<String,String> env) {
		env.put("JAVA_HOME", getModule().getJavaHome().getAbsolutePath());
	}

	@Override
	protected File getWorkingDirectory() {
		return getModule().getChemspotDir();
	}

	@Override
	protected String getInputFileame() {
		return null;
	}

	@Override
	protected String getOutputFilename() {
		return null;
	}
}
