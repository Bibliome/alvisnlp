package org.bibliome.alvisnlp.modules.tees;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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
	
	private String train = null;
	private String dev = null;
	private String test = null;
	
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
			this.simulatingCorpora(corpus, ctx);
			this.prepareTEESCorpora(ctx, corpus);
			TEESTrainExternal teesTrainExt = new TEESTrainExternal(ctx);
			jaxbm.marshal(this.corpora.get(this.getTrain()), teesTrainExt.getTrainInput());
			jaxbm.marshal(this.corpora.get(this.getDev()), teesTrainExt.getDevInput());
			jaxbm.marshal(this.corpora.get(this.getTest()), teesTrainExt.getTestInput());

			logger.info("TEES training ");
			callExternal(ctx, "run-tees-train", teesTrainExt, internalEncoding, "train.py");


		} catch (JAXBException e) {
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
		this.corpora.put(this.getTrain(), new CorpusTEES());
		this.corpora.put(this.getDev(), new CorpusTEES());
		this.corpora.put(this.getTest(), new CorpusTEES());
		
		logger.info("creating the train, dev, test corpus");
		createTheTeesCorpus(ctx, corpusAlvis);
		
		if(this.corpora.get(this.getTrain()).getDocument().size()==0 || this.corpora.get(this.getTrain()).getDocument().size()==0 || this.corpora.get(this.getTrain()).getDocument().size()==0){
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
	public String getTrain() {
		return train;
	}


	public void setTrain(String train) {
		this.train = train;
	}

	@Param(mandatory=true)
	public String getDev() {
		return dev;
	}

	public void setDev(String dev) {
		this.dev = dev;
	}

	@Param(mandatory=true)
	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
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

		private TEESTrainExternal(ProcessingContext<Corpus> ctx) {
			super();
			this.ctx = ctx;
			File tmp = getTempDir(ctx);
			baseDir = tmp;
			this.trainInput = new OutputFile(tmp.getAbsolutePath(), "train-o" + ".xml");
			this.devInput = new OutputFile(tmp.getAbsolutePath(), "dev-o" + ".xml");
			this.testInput = new OutputFile(tmp.getAbsolutePath(), "test-o" + ".xml");
			
		}

		@Override
		public Module<Corpus> getOwner() {
			return TEESTrain.this;
		}

		@Override
		public String[] getCommandLineArgs() throws ModuleException {
			List<String> clArgs = new ArrayList<String>();
			clArgs.addAll(Arrays.asList(getExecutable().getAbsolutePath(), 
					"--omitSteps",
					getOmitSteps().toString(), 
					"--trainFile", 
					this.trainInput.getAbsolutePath(), 
					"--develFile", 
					this.devInput.getAbsolutePath(), 
					"--testFile", 
					this.testInput.getAbsolutePath(),
					"-o", 
					this.baseDir.getAbsolutePath(),
					"--testModel", 
					getModel()));
			return clArgs.toArray(new String[clArgs.size()]);
		}

		@Override
		public String[] getEnvironment() throws ModuleException {
			return null;
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
