package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.util.Files;

class TEESTrainExternalHandler extends TEESMapperExternalHandler<TEESTrain> {
	private final JAXBContext jaxbContext;

	TEESTrainExternalHandler(ProcessingContext<Corpus> processingContext, TEESTrain module, Corpus annotable) throws JAXBException {
		super(processingContext, module, annotable);
		this.jaxbContext = JAXBContext.newInstance(CorpusTEES.class);
	}

	@Override
	protected String getSet(Document doc) {
		return doc.getLastFeature(getModule().getCorpusSetFeature());
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		createTEESTrainerScript();
		try {
			Marshaller jaxbm = jaxbContext.createMarshaller();
			jaxbm.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			createTheTeesCorpus();
			TEESTrain owner = getModule();
			for (String set : new String[] { owner.getTrainSetValue(), owner.getDevSetValue(), owner.getTestSetValue() }) {
				CorpusTEES corpus = getCorpus(set);
				if (corpus.getDocument().isEmpty()) {
					throw new ProcessingException("could not do training : "+set+" is empty");
				}
			}
			jaxbm.marshal(getCorpus(owner.getTrainSetValue()), getTEESTrainTrainInputFile());
			jaxbm.marshal(getCorpus(owner.getDevSetValue()), getTEESTrainDevInputFile());
			jaxbm.marshal(getCorpus(owner.getTestSetValue()), getTEESTrainTestInputFile());
		}
		catch (JAXBException e) {
			throw new ProcessingException(e);
		}
	}

	private File getTEESTrainerScript() {
		return getTempFile("train.sh");
	}
	
	private void createTEESTrainerScript() throws IOException {
		File script = getTEESTrainerScript();
		// same ClassLoader as this class
		try (InputStream is = TEESTrain.class.getResourceAsStream("train.sh")) {
			Files.copy(is, script, 1024, true);
		}
		script.setExecutable(true);
	}
	
	private File getTEESTrainTrainInputFile() {
		return getTempFile("tees-train-input.xml");
	}
	
	private File getTEESTrainDevInputFile() {
		return getTempFile("tees-dev-input.xml");
	}
	
	private File getTEESTrainTestInputFile() {
		return getTempFile("tees-test-input.xml");
	}

	@Override
	protected void collect() throws IOException, ModuleException {
	}

	@Override
	protected String getPrepareTask() {
		return "alvisnlp-to-tees";
	}

	@Override
	protected String getExecTask() {
		return "tees-train";
	}

	@Override
	protected String getCollectTask() {
		return "collect";
	}

	@Override
	protected List<String> getCommandLine() {
		return Collections.singletonList(getTEESTrainerScript().getAbsolutePath());
	}

	@Override
	protected void updateEnvironment(Map<String,String> env) {
		TEESTrain owner = getModule();
//		env.put("PATH", System.getenv("PATH"));
		env.put("PYTHON2", owner.getPython2Executable().getAbsolutePath());
		env.put("TEES_DIR", owner.getTeesHome().getAbsolutePath());
		env.put("TEES_PRE_EXE", getTEESPreprocessingScript().getAbsolutePath());
		env.put("TEES_TRAIN_EXE", getTEESTrainScript().getAbsolutePath());
		env.put("TEES_TRAIN_IN", getTEESTrainTrainInputFile().getAbsolutePath());
		env.put("TEES_TRAIN_OUT", getTempFile("preprocessed-train.xml").getAbsolutePath());
		env.put("TEES_DEV_IN", getTEESTrainDevInputFile().getAbsolutePath());
		env.put("TEES_DEV_OUT", getTempFile("preprocessed-dev.xml").getAbsolutePath());
		env.put("STEPS", owner.getSteps().replace(owner.getOmitSteps()+ ",", ""));
		env.put("TEES_TEST_IN", getTEESTrainTestInputFile().getAbsolutePath());
		env.put("TEES_TEST_OUT", getTempFile("preprocessed-test.xml").getAbsolutePath());
		env.put("WORKDIR", getTempDir().getAbsolutePath());
		env.put("MODELTD", owner.getModelTargetDir().getAbsolutePath());
		env.put("MODEL_NAME", owner.getModelName());
		env.put("TASK", owner.getTask());
		env.put("DETECTOR", owner.getDetector());
	}
	
	private File getTEESTrainScript() {
		return new File(getModule().getTeesHome(), "train.py");
	}

	@Override
	protected File getWorkingDirectory() {
		return getTempDir();
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
