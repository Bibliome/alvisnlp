package org.bibliome.alvisnlp.modules.tees;

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

import org.bibliome.util.Files;
import org.bibliome.util.files.OutputFile;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.External;
import alvisnlp.module.lib.Param;


/**
 * 
 * @author mba
 *
 */

@AlvisNLPModule
public class TEESTrain extends TEESMapper {
	
	
	private String trainSetFeature = null;
	private String devSetFeature = null;
	private String testSetFeature = null;
	

	private String internalEncoding = "UTF-8";

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
	
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);

		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(CorpusTEES.class);
			logger.info("Accessing the corpora");
			Marshaller jaxbm = jaxbContext.createMarshaller();
			jaxbm.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// marshaling
			this.prepareTEESCorpora(ctx, corpus);
			TEESTrainExternal teesTrainExt = new TEESTrainExternal(ctx);
			jaxbm.marshal(this.corpora.get(this.getTrainSetFeature()), teesTrainExt.getTrainInput());
			jaxbm.marshal(this.corpora.get(this.getDevSetFeature()), teesTrainExt.getDevInput());
			jaxbm.marshal(this.corpora.get(this.getTestSetFeature()), teesTrainExt.getTestInput());

			logger.info("TEES training ");
			callExternal(ctx, "run-tees-train", teesTrainExt, internalEncoding, "tees-train.sh");


		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	/**
	 * Build TEES corpus from Alvis corpus
	 * @param ctx
	 * @param corpusAlvis
	 * @throws ProcessingException 
	 */
	public void prepareTEESCorpora(ProcessingContext<Corpus> ctx, Corpus corpusAlvis) throws ProcessingException{
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		
		logger.info("preparing the train, dev, test corpus");
		this.corpora.put(this.getTrainSetFeature(), new CorpusTEES());
		this.corpora.put(this.getDevSetFeature(), new CorpusTEES());
		this.corpora.put(this.getTestSetFeature(), new CorpusTEES());
		
		logger.info("creating the train, dev, test corpus");
		createTheTeesCorpus(ctx, corpusAlvis);
		
		if(this.corpora.get(this.getTrainSetFeature()).getDocument().size()==0 || this.corpora.get(this.getTrainSetFeature()).getDocument().size()==0 || this.corpora.get(this.getTrainSetFeature()).getDocument().size()==0){
			processingException("could not do training : train, dev or test is empty");
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	/**
	 * getters and setters
	 * 
	 */
	@Param(mandatory=true)
	public String getTrainSetFeature() {
		return trainSetFeature;
	}


	public void setTrainSetFeature(String train) {
		this.trainSetFeature = train;
	}

	@Param(mandatory=true)
	public String getDevSetFeature() {
		return devSetFeature;
	}

	public void setDevSetFeature(String dev) {
		this.devSetFeature = dev;
	}

	@Param(mandatory=true)
	public String getTestSetFeature() {
		return testSetFeature;
	}

	public void setTestSetFeature(String test) {
		this.testSetFeature = test;
	}


	/**
	 * 
	 * @author mba
	 *
	 */
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
			
			//
			script = new File(tmp, "train.sh");
			// same ClassLoader as this class
			InputStream is = TEESTrain.class.getResourceAsStream("train.sh");
			Files.copy(is, script, 1024, true);
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
					"MODEL=" + getModel().getAbsolutePath()
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
				logger.fine("TEES standard error:");
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
