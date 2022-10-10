package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
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
	private String task = "None";



	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			new TEESTrainExternalHandler(ctx, this, corpus).start();
		}
		catch (JAXBException | IOException | InterruptedException e) {
			throw new ProcessingException(e);
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


	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}
}
