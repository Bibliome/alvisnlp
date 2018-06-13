package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AbstractExternal;
import fr.inra.maiage.bibliome.util.Files;
import fr.inra.maiage.bibliome.util.files.OutputFile;

/**
 * 
 * @author mba
 *
 */
class TEESClassifyExternal extends AbstractExternal<Corpus,TEESClassify> {
	private final OutputFile input;
	private final String outputStem;
	private final File baseDir;
	private final File script;

	TEESClassifyExternal(TEESClassify owner, ProcessingContext<Corpus> ctx) throws IOException {
		super(owner, ctx);
		File tmp = owner.getTempDir(ctx);
		baseDir = tmp;
		this.input = new OutputFile(tmp.getAbsolutePath(), "tees-o" + ".xml");
		this.outputStem = "tees-i";

		//
		script = new File(tmp, "classify.sh");
		// same ClassLoader as this class
		try (InputStream is = TEESTrain.class.getResourceAsStream("classify.sh")) {
			Files.copy(is, script, 1024, true);
		}
		script.setExecutable(true);
	}

	@Override
	public String[] getCommandLineArgs() throws ModuleException {
		List<String> clArgs = new ArrayList<String>();
		clArgs.addAll(Arrays.asList(script.getAbsolutePath()
				));
		return clArgs.toArray(new String[clArgs.size()]);
	}

	@Override
	public String[] getEnvironment() throws ModuleException {
		return new String[] {
				"PATH=" + System.getenv("PATH"),
				"TEES_DIR=" + getOwner().getTeesHome().getAbsolutePath(),
				"TEES_PRE_EXE=" + getOwner().getTeesHome().getAbsolutePath() + "/Detectors/Preprocessor.py",
				"TEES_CLASSIFY_EXE=" + getOwner().getTeesHome().getAbsolutePath() + "/classify.py",
				"TEES_CORPUS_IN="  + this.input.getAbsolutePath(),
				"TEES_CORPUS_OUT=" + this.baseDir.getAbsolutePath() + "/train_pre.xml",
				"OUTSTREAM=" + this.outputStem, 
				"OMITSTEPS=" + getOwner().getOmitSteps().toString(),
				"WORKDIR=" + this.baseDir.getAbsolutePath(),
				"MODEL=" + getOwner().getTeesModel().getAbsolutePath()
		};
	}

	@Override
	public File getWorkingDirectory() throws ModuleException {
		return this.baseDir;
	}

	public File getPredictionFile() throws IOException {
		//			Logger logger = getLogger(ctx);

		//			DirectoryScanner scanner = new DirectoryScanner();
		//			String[] patterns = {this.getOutputStem() + "*pred*.xml.gz" };
		//			scanner.setIncludes(patterns);
		//			scanner.setBasedir(this.baseDir.getAbsolutePath());
		//			scanner.setCaseSensitive(false);
		//			scanner.scan();
		//			String[] files = scanner.getIncludedFiles();

		//			logger.info("localizing the prediction file : " + files[0]);

		File file = new File(this.baseDir.getAbsolutePath(), "tees-i-pred.xml.gz");
		try (FileInputStream stream = new FileInputStream(file)) {
			try (GZIPInputStream gzipstream = new GZIPInputStream(stream)) {
				String outname = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 3);
				File result = new File(outname);
				Files.copy(gzipstream, result, 2048, false);
				return result;
			}
		}
	}

	public OutputFile getInput() {
		return input;
	}


	//		public String getOutputStem() {
	//			return outputStem;
	//		}
}