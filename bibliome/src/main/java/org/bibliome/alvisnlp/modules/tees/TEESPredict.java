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

import org.bibliome.util.Iterators;
import org.bibliome.util.files.ExecutableFile;
import org.bibliome.util.files.InputDirectory;
import org.bibliome.util.files.InputFile;
import org.bibliome.util.files.OutputFile;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.External;
import alvisnlp.module.lib.Param;

@AlvisNLPModule
public class TEESPredict extends TeesMapper {
	

	
	private ExecutableFile executable;
	private InputDirectory model;
	private InputDirectory workDir;
	private String omitSteps;
	
	private String internalEncoding = "UTF-8";
	
	private String dependencyRelationName = DefaultNames.getDependencyRelationName();
	private String dependencyLabelFeatureName = DefaultNames.getDependencyLabelFeatureName();
	private String sentenceRole = DefaultNames.getDependencySentenceRole();
	
	private String headRole = DefaultNames.getDependencyHeadRole();
	private String dependentRole = DefaultNames.getDependencyDependentRole();
	

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		
		try {		
			 
		logger.info("creating the External module object ");
		TEESPredictExternal teesPredictExt = new TEESPredictExternal(ctx);
		
		logger.info("Setting the jaxb params ");
		JAXBContext jaxbContext = JAXBContext.newInstance(CorpusTEES.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		logger.info("marsalling the object ");
		CorpusTEES corpusTees = this.createTheTeesCorpus(ctx, corpus);
		jaxbMarshaller.marshal(corpusTees, teesPredictExt.getInput());
		
		logger.info("calling tees-predict ");
		callExternal(ctx, "run-tees-predict", teesPredictExt, internalEncoding, "classify.py");
		
		 }  catch (JAXBException e) {
				e.printStackTrace();
	      }
	}
	
	
	
	
	private final class TEESPredictExternal implements External<Corpus> {
		private final OutputFile input;
		private final InputFile output;
		private final File log;
		private final ProcessingContext<Corpus> ctx;

		private TEESPredictExternal(ProcessingContext<Corpus> ctx) {
			super();
			this.ctx = ctx;
			File tmp = getTempDir(ctx);
			this.input = new OutputFile(workDir.getAbsolutePath(), "tees-o" + ".xml");
			this.output = new InputFile(workDir.getAbsolutePath(), "tees-i" + ".xml");
			this.log = new File(workDir, "ccg.log");
		}

		@Override
		public Module<Corpus> getOwner() {
			return TEESPredict.this;
		}

		@Override
		public String[] getCommandLineArgs() throws ModuleException {
			List<String> clArgs = new ArrayList<String>();
			clArgs.addAll(Arrays.asList(
					executable.getAbsolutePath(),
					"--model",
					model.getAbsolutePath(),
					"--omitSteps",
					omitSteps.toString(),
					"--input",
					this.input.getAbsolutePath(),
					"--output",
					this.output.getAbsolutePath(),
					"--workdir",
					workDir.getAbsolutePath()
			));
			return clArgs.toArray(new String[clArgs.size()]);
		}

		@Override
		public String[] getEnvironment() throws ModuleException {
			return null;
		}

		@Override
		public File getWorkingDirectory() throws ModuleException {
			return null;
		}

		@Override
		public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
			Logger logger = getLogger(ctx);
			try {
				logger.fine("CCG standard error:");
				for (String line = err.readLine(); line != null; line = err.readLine()) {
					logger.fine("    " + line);
				}
				logger.fine("end of CCG standard error");
			}
			catch (IOException ioe) {
				logger.warning("could not read CCG standard error: " + ioe.getMessage());
			}
		}

		
		public OutputFile getInput() {
			return input;
		}

		public InputFile getOutput() {
			return output;
		}
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { this.getTokenLayerName(), this.getSentenceLayerName()};
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Param(nameType = NameType.LAYER)
//	public String getTokenLayerName() {
//		return tokenLayerName;
//	}
//
//	@Param(nameType = NameType.LAYER)
//	public String getSentenceLayerName() {
//		return sentenceLayerName;
//	}

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
	public ExecutableFile getExecutable() {
		return executable;
	}

	public void setExecutable(ExecutableFile executable) {
		this.executable = executable;
	}

	@Param
	public InputDirectory getModel() {
		return model;
	}

	public void setModel(InputDirectory model) {
		this.model = model;
	}

	@Param
	public String getOmitSteps() {
		return omitSteps;
	}

	public void setOmitSteps(String omitSteps) {
		this.omitSteps = omitSteps;
	}

	@Param
	public InputDirectory getWorkDir() {
		return workDir;
	}

	public void setWorkDir(InputDirectory workDir) {
		this.workDir = workDir;
	}

	
	
	
	private void iteratorSnippet(ProcessingContext<Corpus> ctx, Corpus corpus) {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);

		// iteration des documents du corpus
		for (Document doc : Iterators.loop(documentIterator(evalCtx, corpus))) {
			// faire qqch avec doc, par ex
			doc.getId();
			doc.getLastFeature("DOCFEATUREKEY");
			// iteration des sections du document
			for (Section sec : Iterators.loop(sectionIterator(evalCtx, doc))) {
				// faire qqch avec sec, par ex
				sec.getName();
				sec.getLastFeature("SECFEATUREKEY");
				sec.getDocument();
				// iteration des annotations d'un layer dans une section
				Layer layer = sec.ensureLayer("LAYERNAME");
				for (Annotation a : layer) {
					// faire qqch avec a, par ex
					a.getStart();
					a.getEnd();
					a.getLength();
					a.getLastFeature("ANNOTATIONFEATUREKEY");
					a.getSection();
				}

				// iteration des sentences dans une section
				for (Layer sentLayer : sec.getSentences(getTokenLayerName(), getSentenceLayerName())) {
					Annotation sent = sentLayer.getSentenceAnnotation();
					// faire qqch avec sent
					// iteration des mots dans une sentence
					for (Annotation token : sentLayer) {
						// faire qqch avec token
					}
				}

				// iteration des relations dans une section
				for (Relation rel : sec.getAllRelations()) {
					// faire qqch avec la relation, par ex
					rel.getName();
					rel.getSection();
					rel.getLastFeature("RELFEATUREKEY");
					// iteration des tuples d'une relation
					for (Tuple t : rel.getTuples()) {
						// faire qqch avec t, par ex
						t.getRelation();
						t.getLastFeature("TUPLEFEATUREKEY");
						
						t.getArgument("ROLE");
		
						// iterer les arguments
						for (String role : t.getRoles()) {
							Element arg = t.getArgument(role);
							// faire qqch avec arg, par ex
							arg.getLastFeature("FEATUREKEY");
							Annotation a = DownCastElement.toAnnotation(arg);
						}
					}
				}

				// une relation en particulier
				Relation dependencies = sec.getRelation(dependencyRelationName);
				// iterer les tuples
				for (Tuple dep : dependencies.getTuples()) {
					String label = dep.getLastFeature(dependencyLabelFeatureName);
					Element sentence = dep.getArgument(sentenceRole);
					Element head = dep.getArgument(headRole);
					Element dependent = dep.getArgument(dependentRole);
				}
			}
		}

		// on peut iterer les sections sans passer par les documents
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			//
		}
	}


}
