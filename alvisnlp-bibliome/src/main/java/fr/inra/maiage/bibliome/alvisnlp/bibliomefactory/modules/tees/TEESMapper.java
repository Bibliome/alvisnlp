package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.MultiMapping;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

/**
 * 
 * @author mba
 *
 */

public abstract class TEESMapper extends SectionModule<SectionResolvedObjects> implements TupleCreator {
	protected static final String INTERNAL_ENCODING = "UTF-8";

	private String tokenLayer = DefaultNames.getWordLayer();
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String posFeature = DefaultNames.getPosTagFeature();
	private String namedEntityTypeFeature = DefaultNames.getNamedEntityTypeFeature();
	private String namedEntityLayer = null;
	
	private MultiMapping schema;
	
	private String omitSteps = "GENIA_SPLITTER,BANNER";
	private String steps ="LOAD,GENIA_SPLITTER,BANNER,BLLIP_BIO,STANFORD_CONVERT,SPLIT_NAMES,FIND_HEADS,SAVE";

	private ExecutableFile python2Executable;
	private InputDirectory teesHome;
	
	private String detector = "Detectors.EdgeDetector";

	@Param(nameType = NameType.LAYER)
	public String getTokenLayer() {
		return tokenLayer;
	}

	@Param(nameType = NameType.LAYER)
	public String getSentenceLayer() {
		return sentenceLayer;
	}

	@Param(nameType = NameType.LAYER)
	public String getNamedEntityLayer() {
		return namedEntityLayer;
	}

	public void setTokenLayer(String tokenLayer) {
		this.tokenLayer = tokenLayer;
	}

	public void setSentenceLayer(String sentenceLayer) {
		this.sentenceLayer = sentenceLayer;
	}

	public void setNamedEntityLayer(String namedEntityLayer) {
		this.namedEntityLayer = namedEntityLayer;
	}

	
	
	
	
	@Deprecated
	@Param(nameType = NameType.LAYER)
	public String getTokenLayerName() {
		return tokenLayer;
	}
	
	public void setTokenLayerName(String tokenLayerName) {
		this.tokenLayer = tokenLayerName;
	}

	@Deprecated
	@Param(nameType = NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayer;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayer = sentenceLayerName;
	}
	
	@Deprecated
	@Param(nameType = NameType.LAYER)
	public String getNamedEntityLayerName() {
		return namedEntityLayer;
	}

	public void setNamedEntityLayerName(String namedEntityLayerName) {
		this.namedEntityLayer = namedEntityLayerName;
	}
	
	@Param(nameType = NameType.FEATURE)
	public String getNamedEntityTypeFeature() {
		return namedEntityTypeFeature;
	}

	public void setNamedEntityTypeFeature(String namedEntityTypeFeature) {
		this.namedEntityTypeFeature = namedEntityTypeFeature;
	}
	
	@Param
	public MultiMapping getSchema() {
		return schema;
	}

	public void setSchema(MultiMapping schema) {
		this.schema = schema;
	}

	@Param
	public String getPosFeature() {
		return posFeature;
	}

	public void setPosFeature(String posFeature) {
		this.posFeature = posFeature;
	}
	
	@Param
	public String getOmitSteps() {
		return omitSteps;
	}

	public void setOmitSteps(String omitSteps) {
		this.omitSteps = omitSteps;
	}
	
	public String getSteps() {
		return steps;
	}

	public void setSteps(String steps) {
		this.steps = steps;
	}

	
	@Param
	public InputDirectory getTeesHome() {
		return teesHome;
	}

	public void setTeesHome(InputDirectory tEESHome) {
		teesHome = tEESHome;
	}

	@Param
	public ExecutableFile getPython2Executable() {
		return python2Executable;
	}

	public void setPython2Executable(ExecutableFile python2Executable) {
		this.python2Executable = python2Executable;
	}
	
	public String getDetector() {
		return detector;
	}

	public void setDetector(String detector) {
		this.detector = detector;
	}
}
