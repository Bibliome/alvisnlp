package org.bibliome.alvisnlp.modules.tees;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import org.bibliome.util.files.OutputFile;
import org.codehaus.plexus.util.DirectoryScanner;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.NameType;
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
public class TEESClassify extends TEESMapper {	

	private String internalEncoding = "UTF-8";

	private String dependencyRelationName = DefaultNames.getDependencyRelationName();
	private String dependencyLabelFeatureName = DefaultNames.getDependencyLabelFeatureName();
	private String sentenceRole = DefaultNames.getDependencySentenceRole();

	private String headRole = DefaultNames.getDependencyHeadRole();
	private String dependentRole = DefaultNames.getDependencyDependentRole();
	

	private String setFeature = null;

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);

		try {		
			logger.info("creating the External module object ");
			TEESClassifyExternal teesClassifyExt = new TEESClassifyExternal(ctx);

			logger.info("producing a interaction xml ");
			JAXBContext jaxbContext = JAXBContext.newInstance(CorpusTEES.class);
			Marshaller jaxbm = jaxbContext.createMarshaller();
			jaxbm.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbm.marshal(this.prepareTEESCorpora(ctx, corpus), teesClassifyExt.getInput());

			logger.info("calling tees-predict ");
			callExternal(ctx, "run-tees-predict", teesClassifyExt, internalEncoding, "tees-classify.py");

			logger.info("Accessing the test prediction file");
			Unmarshaller jaxbu = jaxbContext.createUnmarshaller();
			CorpusTEES corpusTEES = (CorpusTEES) jaxbu.unmarshal(teesClassifyExt.getPredictionFile());

			logger.info("adding detected relations to Corpus ");
			this.writeTEESResult(corpusTEES, corpus, ctx);

			logger.info("number of documents : " + corpusTEES.getDocument().size());

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
	 * @return
	 * @throws ProcessingException 
	 */
	public CorpusTEES prepareTEESCorpora(ProcessingContext<Corpus> ctx, Corpus corpusAlvis) throws ProcessingException{
		if(this.getSetFeature()==null){
			processingException("could not classify : corpus set doesn't exist");
		} 
		this.corpora.put(this.getSetFeature(), new CorpusTEES());
		this.createTheTeesCorpus(ctx, corpusAlvis);	
		return this.corpora.get(this.getSetFeature());
	}
	
	/**
	 * Write the predicted relation into Alvis Corpus
	 * @param corpusTEES
	 * @param corpusAlvis
	 * @param ctx
	 */
	public void writeTEESResult( CorpusTEES corpusTEES, Corpus corpusAlvis, ProcessingContext<Corpus> ctx){
		this.setRelations2CorpusAlvis(corpusTEES, corpusAlvis, ctx);
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
		return new String[] { this.getTokenLayerName(), this.getSentenceLayerName() };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * Getters and setters
	 */
	
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
	public String getSetFeature() {
		return setFeature;
	}


	public void setSetFeature(String setFeature) {
		this.setFeature = setFeature;
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
			InputStream is = TEESTrain.class.getResourceAsStream("classify.sh");
			Files.copy(is, script, 1024, true);
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

//		@Override
//		public String[] getCommandLineArgs() throws ModuleException {
//			List<String> clArgs = new ArrayList<String>();
//			clArgs.addAll(Arrays.asList(getExecutable().getAbsolutePath(), 
//					"--model", 
//					getModel(), 
//					"--omitSteps",
//					getOmitSteps().toString(), 
//					"--input", 
//					this.input.getAbsolutePath(), 
//					"--output", this.outputStem));
//			if (getWorkDir() == null) {
//				clArgs.add("--workdir");
//				clArgs.add(baseDir.getAbsolutePath());
//				// workDir = new InputDirectory(baseDir.getAbsolutePath());
//			}
//			else {
//				clArgs.add("--clearAll");
//				clArgs.add("True");
//				clArgs.add("--workdir");
//				clArgs.add(getWorkDir().getAbsolutePath());
//			}
//			return clArgs.toArray(new String[clArgs.size()]);
//		}

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



		public File getPredictionFile() throws ModuleException, IOException {
			Logger logger = getLogger(ctx);
			//
			DirectoryScanner scanner = new DirectoryScanner();
			String[] patterns = {this.getOutputStem() + "*pred*.xml.gz" };
			scanner.setIncludes(patterns);
			scanner.setBasedir(this.baseDir.getAbsolutePath());
			scanner.setCaseSensitive(false);
			scanner.scan();
			String[] files = scanner.getIncludedFiles();

			logger.info("localizing the prediction file : " + files[0]);
			
			File file = new File(this.baseDir.getAbsolutePath(), files[0]);
			FileInputStream stream = new FileInputStream(file);
			String outname = null;
			FileOutputStream output = null;
			try {
				// open the gziped file to decompress.
				GZIPInputStream gzipstream = new GZIPInputStream(stream);
				byte[] buffer = new byte[2048];

				// create the output file without the .gz extension.
				outname = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 3);
				output = new FileOutputStream(outname);

				// and copy it to a new file
				int len;
				while ((len = gzipstream.read(buffer)) > 0) {
					output.write(buffer, 0, len);
				}
			} finally {
				// both streams must always be closed.
				if (output != null)
					output.close();
				stream.close();
			}

			return new File(outname);
			
		}

		public OutputFile getInput() {
			return input;
		}


		public String getOutputStem() {
			return outputStem;
		}

	}


	
	
	
	
//
//	private void iteratorSnippet(ProcessingContext<Corpus> ctx, Corpus corpus) {
//		Logger logger = getLogger(ctx);
//		EvaluationContext evalCtx = new EvaluationContext(logger);
//
//		// iteration des documents du corpus
//		for (Document doc : Iterators.loop(documentIterator(evalCtx, corpus))) {
//			// faire qqch avec doc, par ex
//			doc.getId();
//			doc.getLastFeature("DOCFEATUREKEY");
//			// iteration des sections du document
//			for (Section sec : Iterators.loop(sectionIterator(evalCtx, doc))) {
//				// faire qqch avec sec, par ex
//				sec.getName();
//				sec.getLastFeature("SECFEATUREKEY");
//				sec.getDocument();
//				// iteration des annotations d'un layer dans une section
//				Layer layer = sec.ensureLayer("LAYERNAME");
//				for (Annotation a : layer) {
//					// faire qqch avec a, par ex
//					a.getStart();
//					a.getEnd();
//					a.getLength();
//					a.getLastFeature("ANNOTATIONFEATUREKEY");
//					a.getSection();
//				}
//
//				// iteration des sentences dans une section
//				for (Layer sentLayer : sec.getSentences(getTokenLayerName(), getSentenceLayerName())) {
//					Annotation sent = sentLayer.getSentenceAnnotation();
//					// faire qqch avec sent
//					// iteration des mots dans une sentence
//					for (Annotation token : sentLayer) {
//						// faire qqch avec token
//					}
//				}
//
//				// iteration des relations dans une section
//				for (Relation rel : sec.getAllRelations()) {
//					// faire qqch avec la relation, par ex
//					rel.getName();
//					rel.getSection();
//					rel.getLastFeature("RELFEATUREKEY");
//					// iteration des tuples d'une relation
//					for (Tuple t : rel.getTuples()) {
//						// faire qqch avec t, par ex
//						t.getRelation();
//						t.getLastFeature("TUPLEFEATUREKEY");
//
//						t.getArgument("ROLE");
//
//						// iterer les arguments
//						for (String role : t.getRoles()) {
//							Element arg = t.getArgument(role);
//							// faire qqch avec arg, par ex
//							arg.getLastFeature("FEATUREKEY");
//							Annotation a = DownCastElement.toAnnotation(arg);
//						}
//					}
//				}
//
//				// une relation en particulier
//				Relation dependencies = sec.getRelation(dependencyRelationName);
//				// iterer les tuples
//				for (Tuple dep : dependencies.getTuples()) {
//					String label = dep.getLastFeature(dependencyLabelFeatureName);
//					Element sentence = dep.getArgument(sentenceRole);
//					Element head = dep.getArgument(headRole);
//					Element dependent = dep.getArgument(dependentRole);
//				}
//			}
//		}
//
//		// on peut iterer les sections sans passer par les documents
//		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
//			//
//		}
//	}
//


}
