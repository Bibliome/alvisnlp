package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.IOException;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Checkable;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.OutputFile;

@AlvisNLPModule(beta=true)
public abstract class Word2Vec extends SectionModule<SectionResolvedObjects> implements AbstractContes, Checkable {
	private InputDirectory contesDir;

	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String tokenLayer = DefaultNames.getWordLayer();
	private String formFeature = Annotation.FORM_FEATURE_NAME;
	private String vectorFeature = null;

	private Integer vectorSize = 200;
	private Integer windowSize = 2;
	private Integer minCount = 0;

	private OutputFile jsonFile;
	private OutputFile txtFile;
	private OutputFile modelFile;
	private Integer workers;

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		try {
			new Word2VecExternalHandler(ctx, this, corpus).start();
		}
		catch (IOException | InterruptedException e) {
			throw new ProcessingException(e);
		}
	}

	@Param(nameType=NameType.LAYER)
	public String getSentenceLayer() {
	    return this.sentenceLayer;
	};

	public void setSentenceLayer(String sentenceLayer) {
	    this.sentenceLayer = sentenceLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayer;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getVectorFeatureName() {
		return vectorFeature;
	}

	@Param
	public Integer getVectorSize() {
		return vectorSize;
	}

	@Param
	public Integer getWindowSize() {
		return windowSize;
	}

	@Param
	public Integer getMinCount() {
		return minCount;
	}

	@Param(mandatory=false)
	public OutputFile getJsonFile() {
		return jsonFile;
	}

	@Param(mandatory=false)
	public OutputFile getTxtFile() {
		return txtFile;
	}

	@Param(mandatory=false)
	public OutputFile getModelFile() {
		return modelFile;
	}

	@Param
	public Integer getWorkers() {
		return workers;
	}

	@Override
	@Param(nameType=NameType.LAYER)
	public String getTokenLayer() {
	    return this.tokenLayer;
	};

	public void setTokenLayer(String tokenLayer) {
	    this.tokenLayer = tokenLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getTokenLayerName() {
		return tokenLayer;
	}

	@Override
	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getFormFeatureName() {
		return formFeature;
	}

	@Override
	@Param
	public InputDirectory getContesDir() {
		return contesDir;
	}

	@Override
	@Param(nameType=NameType.FEATURE)
	public String getFormFeature() {
		return formFeature;
	}

	@Override
	public void setFormFeature(String formFeature) {
		this.formFeature = formFeature;
	}

	public void setModelFile(OutputFile binFile) {
		this.modelFile = binFile;
	}

	@Override
	public void setContesDir(InputDirectory contesDir) {
		this.contesDir = contesDir;
	}

	@Override
	public void setTokenLayerName(String tokenLayer) {
		this.tokenLayer = tokenLayer;
	}

	@Override
	public void setFormFeatureName(String formFeature) {
		this.formFeature = formFeature;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getVectorFeature() {
		return vectorFeature;
	}

	public void setVectorFeature(String vectorFeature) {
		this.vectorFeature = vectorFeature;
	}

	public void setSentenceLayerName(String sentenceLayer) {
		this.sentenceLayer = sentenceLayer;
	}

	public void setVectorFeatureName(String vectorFeature) {
		this.vectorFeature = vectorFeature;
	}

	public void setVectorSize(Integer vectorSize) {
		this.vectorSize = vectorSize;
	}

	public void setWindowSize(Integer windowSize) {
		this.windowSize = windowSize;
	}

	public void setMinCount(Integer minCount) {
		this.minCount = minCount;
	}

	public void setJsonFile(OutputFile jsonFile) {
		this.jsonFile = jsonFile;
	}

	public void setTxtFile(OutputFile txtFile) {
		this.txtFile = txtFile;
	}

	public void setWorkers(Integer workers) {
		this.workers = workers;
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { sentenceLayer , tokenLayer };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public boolean check(Logger logger) {
		if ((jsonFile == null) && (txtFile == null) && (modelFile == null)) {
			logger.severe("no vector or model output file, either jsonFile, txtFile or modelFile is mandatory");
			return false;
		}
		return true;
	}
}
