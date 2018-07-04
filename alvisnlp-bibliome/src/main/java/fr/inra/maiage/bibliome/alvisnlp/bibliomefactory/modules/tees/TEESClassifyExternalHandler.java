package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.util.Files;

public class TEESClassifyExternalHandler extends TEESMapperExternalHandler<TEESClassify> {
	private static final String DEFAULT_SET = "corpus";
	private static final String OUTPUT_PREFIX = "tees-output";
	private final JAXBContext jaxbContext;

	protected TEESClassifyExternalHandler(ProcessingContext<Corpus> processingContext, TEESClassify module, Corpus annotable) throws JAXBException {
		super(processingContext, module, annotable);
		jaxbContext = JAXBContext.newInstance(CorpusTEES.class);
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		try {
			Marshaller jaxbm = jaxbContext.createMarshaller();
			jaxbm.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			createTheTeesCorpus();
			jaxbm.marshal(getCorpus(DEFAULT_SET), getTEESClassifierInputFile());
		}
		catch (JAXBException e) {
			throw new ProcessingException(e);
		}
		createTEESClassifierScript();
	}

	private File getTEESClassifierInputFile() {
		return getTempFile("tees-input.xml");
	}
	
	@Override
	protected String getSet(Document doc) {
		return DEFAULT_SET;
	}

	@Override
	protected void collect() throws IOException, ModuleException {
		try {
			Unmarshaller jaxbu = jaxbContext.createUnmarshaller();
			CorpusTEES corpusTEES = (CorpusTEES) jaxbu.unmarshal(getPredictionFile());
			setRelations2CorpusAlvis(corpusTEES);
		}
		catch (JAXBException e) {
			throw new ProcessingException(e);
		}
	}

	public File getPredictionFile() throws IOException {
		try (FileInputStream stream = new FileInputStream(getTEESClassifierZippedOutputFile())) {
			try (GZIPInputStream gzipstream = new GZIPInputStream(stream)) {
				File result = getTEESClassifierOutputFile();
				Files.copy(gzipstream, result, 2048, false);
				return result;
			}
		}
	}
	
	private File getTEESClassifierZippedOutputFile() {
		return getTempFile(OUTPUT_PREFIX + "-pred.xml.gz");
	}
	
	private File getTEESClassifierOutputFile() {
		return getTempFile(OUTPUT_PREFIX + "-pred.xml");
	}

	@Override
	protected String getPrepareTask() {
		return "alvisnlp-to-tees";
	}

	@Override
	protected String getExecTask() {
		return "tees-classify";
	}

	@Override
	protected String getCollectTask() {
		return "tees-to-alvisnlp";
	}

	@Override
	protected List<String> getCommandLine() {
		return Collections.singletonList(getTEESClassifierScript().getAbsolutePath());
	}
	
	private File getTEESClassifierScript() {
		return getTempFile("classify.sh");
	}
	
	private void createTEESClassifierScript() throws IOException {
		File script = getTEESClassifierScript();
		try (InputStream is = TEESTrain.class.getResourceAsStream("classify.sh")) {
			Files.copy(is, script, 1024, true);
		}
		script.setExecutable(true);
	}

	@Override
	protected void updateEnvironment(Map<String,String> env) {
		TEESClassify owner = getModule();
		env.put("PATH", System.getenv("PATH"));
		env.put("TEES_DIR", owner.getTeesHome().getAbsolutePath());
		env.put("TEES_PRE_EXE",  getTEESPreprocessingScript().getAbsolutePath());
		env.put("TEES_CLASSIFY_EXE",  getTEESClassifyScript().getAbsolutePath());
		env.put("TEES_CORPUS_IN", getTEESClassifierInputFile().getAbsolutePath());
		env.put("TEES_CORPUS_OUT", getTempFile("preprocessed.xml").getAbsolutePath());
		env.put("OUTSTREAM", OUTPUT_PREFIX);
		env.put("OMITSTEPS",  owner.getOmitSteps());
		env.put("WORKDIR",  getTempDir().getAbsolutePath());
		env.put("MODEL",  owner.getTeesModel().getAbsolutePath());
	}
	
	private File getTEESClassifyScript() {
		return new File(getModule().getTeesHome(), "classify.py");
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
