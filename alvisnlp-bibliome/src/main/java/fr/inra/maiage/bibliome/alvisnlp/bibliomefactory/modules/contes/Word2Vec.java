package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;
import fr.inra.maiage.bibliome.util.files.OutputFile;

@AlvisNLPModule(beta=true)
public class Word2Vec extends SectionModule<SectionResolvedObjects> {
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String tokenLayer = DefaultNames.getWordLayer();
	private String formFeature = Annotation.FORM_FEATURE_NAME;
	private String vectorFeature = null;
	
	private Integer vectorSize = 200;
	private Integer windowSize = 2;
	private Integer minCount = 0;
	
	private ExecutableFile word2vec;
	private OutputFile jsonFile;
	private OutputFile txtFile;
	private Integer workers;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		File tmpDir = getTempDir(ctx);
		Word2VecExternal external = new Word2VecExternal(this, logger, tmpDir);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		try {
			external.createInputFile(evalCtx, corpus);
			callExternal(ctx, "word2vec", external);
			external.collectTokenVectors(evalCtx, corpus);
		}
		catch (IOException e) {
			rethrow(e);
		}
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { sentenceLayer, tokenLayer };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}


	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Param(nameType=NameType.LAYER)
	public String getSentenceLayer() {
		return sentenceLayer;
	}

	@Param(nameType=NameType.LAYER)
	public String getTokenLayer() {
		return tokenLayer;
	}

	@Param(nameType=NameType.FEATURE)
	public String getFormFeature() {
		return formFeature;
	}

	@Param(nameType=NameType.LAYER, mandatory=false)
	public String getVectorFeature() {
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

	@Param
	public ExecutableFile getWord2vec() {
		return word2vec;
	}

	@Param
	public OutputFile getJsonFile() {
		return jsonFile;
	}

	@Param(mandatory=false)
	public OutputFile getTxtFile() {
		return txtFile;
	}

	@Param
	public Integer getWorkers() {
		return workers;
	}

	public void setSentenceLayer(String sentenceLayer) {
		this.sentenceLayer = sentenceLayer;
	}

	public void setTokenLayer(String tokenLayer) {
		this.tokenLayer = tokenLayer;
	}

	public void setFormFeature(String formFeature) {
		this.formFeature = formFeature;
	}

	public void setVectorFeature(String vectorFeature) {
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

	public void setWord2vec(ExecutableFile word2vec) {
		this.word2vec = word2vec;
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
}
