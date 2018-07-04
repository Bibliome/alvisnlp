package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.MultiMapping;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

/**
 * 
 * @author mba
 *
 */

public abstract class TEESMapper extends SectionModule<SectionResolvedObjects> implements TupleCreator {
	protected static final String INTERNAL_ENCODING = "UTF-8";

	private String tokenLayerName = DefaultNames.getWordLayer();
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String posFeature = DefaultNames.getPosTagFeature();
	private String namedEntityTypeFeature = DefaultNames.getNamedEntityTypeFeature();
	private String namedEntityLayerName = null;
	
	private MultiMapping schema;
	
	private String omitSteps = "SPLIT-SENTENCES,NER";
	private InputDirectory teesHome;

	@Param(nameType = NameType.LAYER)
	public String getTokenLayerName() {
		return tokenLayerName;
	}
	
	public void setTokenLayerName(String tokenLayerName) {
		this.tokenLayerName = tokenLayerName;
	}

	@Param(nameType = NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayerName;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}
	
	@Param(nameType = NameType.LAYER)
	public String getNamedEntityLayerName() {
		return namedEntityLayerName;
	}

	public void setNamedEntityLayerName(String namedEntityLayerName) {
		this.namedEntityLayerName = namedEntityLayerName;
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
	
	@Param
	public InputDirectory getTeesHome() {
		return teesHome;
	}

	public void setTeesHome(InputDirectory tEESHome) {
		teesHome = tEESHome;
	}
}
