package org.bibliome.alvisnlp.modules.tees;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.bibliome.util.Files;
import org.bibliome.util.files.InputFile;
import org.bibliome.util.files.OutputFile;
import org.codehaus.plexus.util.DirectoryScanner;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.NameType;
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
public abstract class TEESClassify extends TEESMapper {	
	private static final String DEFAULT_SET = "corpus";
	
	private String dependencyRelationName = DefaultNames.getDependencyRelationName();
	private String dependencyLabelFeatureName = DefaultNames.getDependencyLabelFeatureName();
	private String sentenceRole = DefaultNames.getDependencySentenceRole();
	private String headRole = DefaultNames.getDependencyHeadRole();
	private String dependentRole = DefaultNames.getDependencyDependentRole();
	
	private InputFile teesModel;

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);

		try {		
//			logger.info("creating the External module object ");
			TEESClassifyExternal teesClassifyExt = new TEESClassifyExternal(ctx);

//			logger.info("producing a interaction xml ");
			JAXBContext jaxbContext = JAXBContext.newInstance(CorpusTEES.class);
			Marshaller jaxbm = jaxbContext.createMarshaller();
			jaxbm.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbm.marshal(this.prepareTEESCorpora(ctx, corpus), teesClassifyExt.getInput());

			logger.info("Predicting with TEES");
			callExternal(ctx, "run-tees-predict", teesClassifyExt, INTERNAL_ENCODING, "tees-classify.py");

			logger.info("Reading TEES output");
			Unmarshaller jaxbu = jaxbContext.createUnmarshaller();
			CorpusTEES corpusTEES = (CorpusTEES) jaxbu.unmarshal(teesClassifyExt.getPredictionFile());

//			logger.info("adding detected relations to Corpus ");
			setRelations2CorpusAlvis(corpusTEES);
			
			logger.info("number of documents : " + corpusTEES.getDocument().size());
		}
		catch (JAXBException|IOException e) {
			rethrow(e);
		}
	}
	
	@Override
	protected String getSet(Document doc) {
		return DEFAULT_SET;
	}

	/**
	 * Build TEES corpus from Alvis corpus
	 * @param ctx
	 * @param corpusAlvis
	 * @return
	 * @throws ProcessingException 
	 */
	public CorpusTEES prepareTEESCorpora(ProcessingContext<Corpus> ctx, Corpus corpusAlvis) {
		this.createTheTeesCorpus(ctx, corpusAlvis);	
		return getCorpus(DEFAULT_SET);
	}
	
	/**
	 * Object resolver and Feature Handlers
	 */
	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] {
				getTokenLayerName(), 
				getSentenceLayerName()
		};
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Param(nameType = NameType.FEATURE)
	public String getDependencyLabelFeatureName() {
		return dependencyLabelFeatureName;
	}

	@Param(nameType = NameType.ARGUMENT)
	public String getSentenceRole() {
		return sentenceRole;
	}

	@Param(nameType = NameType.ARGUMENT)
	public String getHeadRole() {
		return headRole;
	}

	@Param(nameType = NameType.ARGUMENT)
	public String getDependentRole() {
		return dependentRole;
	}

	@Param(nameType = NameType.RELATION)
	public String getDependencyRelationName() {
		return dependencyRelationName;
	}

	public void setDependencyRelationName(String dependencyRelationName) {
		this.dependencyRelationName = dependencyRelationName;
	}

	public void setDependencyLabelFeatureName(String dependencyLabelFeatureName) {
		this.dependencyLabelFeatureName = dependencyLabelFeatureName;
	}

	public void setSentenceRole(String sentenceRole) {
		this.sentenceRole = sentenceRole;
	}

	public void setHeadRole(String headRole) {
		this.headRole = headRole;
	}

	public void setDependentRole(String dependentRole) {
		this.dependentRole = dependentRole;
	}

	@Param
	public InputFile getTeesModel() {
		return teesModel;
	}

	public void setTeesModel(InputFile model) {
		this.teesModel = model;
	}

	/**
	 * 
	 * @author mba
	 *
	 */
	private final class TEESClassifyExternal implements External<Corpus> {
		private final OutputFile input;
		private final String outputStem;
		private final ProcessingContext<Corpus> ctx;
		public final File baseDir;
		private final File script;

		private TEESClassifyExternal(ProcessingContext<Corpus> ctx) throws IOException {
			super();
			this.ctx = ctx;
			File tmp = getTempDir(ctx);
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
		public Module<Corpus> getOwner() {
			return TEESClassify.this;
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
					"TEES_DIR=" + getTeesHome().getAbsolutePath(),
					"TEES_PRE_EXE=" + getTeesHome().getAbsolutePath() + "/Detectors/Preprocessor.py",
					"TEES_CLASSIFY_EXE=" + getTeesHome().getAbsolutePath() + "/classify.py",
					"TEES_CORPUS_IN="  + this.input.getAbsolutePath(),
					"TEES_CORPUS_OUT=" + this.baseDir.getAbsolutePath() + "/train_pre.xml",
					"OUTSTREAM=" + this.outputStem, 
					"OMITSTEPS=" + getOmitSteps().toString(),
					"WORKDIR=" + this.baseDir.getAbsolutePath(),
					"MODEL=" + getTeesModel().getAbsolutePath()
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

		public File getPredictionFile() throws IOException {
//			Logger logger = getLogger(ctx);

			DirectoryScanner scanner = new DirectoryScanner();
			String[] patterns = {this.getOutputStem() + "*pred*.xml.gz" };
			scanner.setIncludes(patterns);
			scanner.setBasedir(this.baseDir.getAbsolutePath());
			scanner.setCaseSensitive(false);
			scanner.scan();
			String[] files = scanner.getIncludedFiles();

//			logger.info("localizing the prediction file : " + files[0]);

			File file = new File(this.baseDir.getAbsolutePath(), files[0]);
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


		public String getOutputStem() {
			return outputStem;
		}
	}
}
