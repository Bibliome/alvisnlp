package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.IOException;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.OutputFile;

@AlvisNLPModule(beta=true)
public class Word2Vec extends AbstractContes {
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String vectorFeature = null;
	
	private Integer vectorSize = 200;
	private Integer windowSize = 2;
	private Integer minCount = 0;
	
	private OutputFile jsonFile;
	private OutputFile txtFile;
	private Integer workers;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			new Word2VecExternalHandler(ctx, this, corpus).start();
		}
		catch (IOException | InterruptedException e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { sentenceLayer, getTokenLayer() };
	}

	@Param(nameType=NameType.LAYER)
	public String getSentenceLayer() {
		return sentenceLayer;
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
