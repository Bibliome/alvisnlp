package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.External;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Files;
import fr.inra.maiage.bibliome.util.files.OutputFile;


/**
 * 
 * @author mba
 *
 */

@AlvisNLPModule
public abstract class TEESTrain extends TEESMapper {
	private String corpusSetFeature = "set";
	private String trainSetValue = "train";
	private String devSetValue = "dev";
	private String testSetValue = "test";

	private OutputFile modelTargetDir;
	private String modelName = "test-model";

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(CorpusTEES.class);
//			logger.info("Accessing the corpora");
			Marshaller jaxbm = jaxbContext.createMarshaller();
			jaxbm.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// marshaling
			this.prepareTEESCorpora(ctx, corpus);
			TEESTrainExternal teesTrainExt = new TEESTrainExternal(ctx);
			jaxbm.marshal(getCorpus(getTrainSetValue()), teesTrainExt.getTrainInput());
			jaxbm.marshal(getCorpus(getDevSetValue()), teesTrainExt.getDevInput());
			jaxbm.marshal(getCorpus(getTestSetValue()), teesTrainExt.getTestInput());

			logger.info("Training classifier");
			callExternal(ctx, "run-tees-train", teesTrainExt, INTERNAL_ENCODING, "tees-train.sh");
		}
		catch (JAXBException|IOException e) {
			rethrow(e);
		}
	}
	
	@Override
	protected String getSet(Document doc) {
		return doc.getLastFeature(getCorpusSetFeature());
	}

	/**
	 * Build TEES corpus from Alvis corpus
	 * @param ctx
	 * @param corpusAlvis
	 * @throws ProcessingException 
	 */
	public void prepareTEESCorpora(ProcessingContext<Corpus> ctx, Corpus corpusAlvis) throws ProcessingException{
//		Logger logger = getLogger(ctx);
		
//		logger.info("creating the train, dev, test corpus");
		createTheTeesCorpus(ctx, corpusAlvis);

		for (String set : new String[] { getTrainSetValue(), getDevSetValue(), getTestSetValue() }) {
			CorpusTEES corpus = getCorpus(set);
			if (corpus.getDocument().isEmpty()) {
				processingException("could not do training : "+set+" is empty");
			}
		}
	}
	
	/**
	 * object resolver and feature handler
	 */
	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] {
				getTokenLayerName(),
				getSentenceLayerName(),
				getNamedEntityLayerName()
		};
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Param
	public OutputFile getModelTargetDir() {
		return modelTargetDir;
	}

	public void setModelTargetDir(OutputFile model) {
		this.modelTargetDir = model;
	}

	@Param
	public String getCorpusSetFeature() {
		return corpusSetFeature;
	}

	public void setCorpusSetFeature(String corpusSetFeature) {
		this.corpusSetFeature = corpusSetFeature;
	}
	
	@Param
	public String getTrainSetValue() {
		return trainSetValue;
	}

	public void setTrainSetValue(String trainSetValue) {
		this.trainSetValue = trainSetValue;
	}

	@Param
	public String getDevSetValue() {
		return devSetValue;
	}

	public void setDevSetValue(String devSetValue) {
		this.devSetValue = devSetValue;
	}

	@Param
	public String getTestSetValue() {
		return testSetValue;
	}

	public void setTestSetValue(String testSetValue) {
		this.testSetValue = testSetValue;
	}

	
	@Param(mandatory=false)
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	private final class TEESTrainExternal implements External<Corpus> {
		private final OutputFile trainInput;
		private final OutputFile devInput;
		private final OutputFile testInput;
		private final ProcessingContext<Corpus> ctx;
		public final File baseDir;
		private final File script;

		private TEESTrainExternal(ProcessingContext<Corpus> ctx) throws IOException {
			super();
			this.ctx = ctx;
			File tmp = getTempDir(ctx);
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
		public Module<Corpus> getOwner() {
			return TEESTrain.this;
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
					"TEES_DIR=" + getTeesHome().getAbsolutePath(),
					"TEES_PRE_EXE=" + getTeesHome().getAbsolutePath() + "/Detectors/Preprocessor.py",
					"TEES_TRAIN_EXE=" + getTeesHome().getAbsolutePath() + "/train.py",
					"TEES_TRAIN_IN="  + this.trainInput.getAbsolutePath(),
					"TEES_TRAIN_OUT=" + this.baseDir.getAbsolutePath() + "/train_pre.xml",
					"TEES_DEV_IN="  + this.devInput.getAbsolutePath(),
					"TEES_DEV_OUT=" + this.baseDir.getAbsolutePath() + "/dev_pre.xml",
					"OMITSTEPS=" + getOmitSteps().toString(),
					"TEES_TEST_IN="  + this.testInput.getAbsolutePath(),
					"TEES_TEST_OUT=" + this.baseDir.getAbsolutePath() + "/test_pre.xml",
					"WORKDIR=" + this.baseDir.getAbsolutePath(),
					"MODELTD=" + getModelTargetDir().getAbsolutePath(),
					"MODEL_NAME=" + getModelName() 
				};
		}

		@Override
		public File getWorkingDirectory() throws ModuleException {
			return this.baseDir;
		}

		@Override
		public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
			Logger logger = getLogger(ctx);
			try {
				logger.fine("TEES standard error");
				for (String line = err.readLine(); line != null; line = err.readLine()) {
					logger.fine("    " + line);
				}
				logger.fine("end of TEES classifier error");
			} catch (IOException ioe) {
				logger.warning("could not read TEES standard error: " + ioe.getMessage());
			}
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
}
