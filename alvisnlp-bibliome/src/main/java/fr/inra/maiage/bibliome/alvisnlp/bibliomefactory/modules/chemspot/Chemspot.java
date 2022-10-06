package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.chemspot;

import java.io.IOException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

@AlvisNLPModule(beta=true)
public abstract class Chemspot extends SectionModule<SectionResolvedObjects> implements AnnotationCreator {
	private InputDirectory javaHome = new InputDirectory(System.getenv("JAVA_HOME"));
	private InputDirectory chemspotDir;
	private Boolean noDict = false;
	private String targetLayerName = "chemspot";
	private String fdaDateFeature = "FDA";
	private String fdaFeature = "FDA_DATE";
	private String meshFeature = "MESH";
	private String kegdFeature = "KEGD";
	private String keggFeature = "KEGG";
	private String hmdbFeature = "HMDB";
	private String drugFeature = "DRUG";
	private String inchFeature = "INCH";
	private String pubsFeature = "PUBS";
	private String pubcFeature = "PUBC";
	private String casFeature = "CAS";
	private String chebFeature = "CHEB";
	private String chidFeature = "CHID";
	private String chemTypeFeature = "chem-type";

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			new ChemspotExternalHandler(ctx, this, corpus).start();
		}
		catch (IOException | InterruptedException e) {
			throw new ProcessingException(e);
		}
	}
	
	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Param
	public InputDirectory getJavaHome() {
		return javaHome;
	}

	@Param
	public InputDirectory getChemspotDir() {
		return chemspotDir;
	}

	@Param(nameType=NameType.LAYER)
	public String getTargetLayerName() {
		return targetLayerName;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getFdaDateFeatureName() {
		return fdaDateFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getFdaFeatureName() {
		return fdaFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getMeshFeatureName() {
		return meshFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getKegdFeatureName() {
		return kegdFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getKeggFeatureName() {
		return keggFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getHmdbFeatureName() {
		return hmdbFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getDrugFeatureName() {
		return drugFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getInchFeatureName() {
		return inchFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getPubsFeatureName() {
		return pubsFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getPubcFeatureName() {
		return pubcFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getCasFeatureName() {
		return casFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getChebFeatureName() {
		return chebFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getChidFeatureName() {
		return chidFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getChemTypeFeatureName() {
		return chemTypeFeature;
	}

	@Param
	public Boolean getNoDict() {
		return noDict;
	}

	@Param(nameType=NameType.FEATURE)
	public String getFdaDateFeature() {
		return fdaDateFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getFdaFeature() {
		return fdaFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getMeshFeature() {
		return meshFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getKegdFeature() {
		return kegdFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getKeggFeature() {
		return keggFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getHmdbFeature() {
		return hmdbFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getDrugFeature() {
		return drugFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getInchFeature() {
		return inchFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getPubsFeature() {
		return pubsFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getPubcFeature() {
		return pubcFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getCasFeature() {
		return casFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getChebFeature() {
		return chebFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getChidFeature() {
		return chidFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getChemTypeFeature() {
		return chemTypeFeature;
	}

	public void setFdaDateFeature(String fdaDateFeature) {
		this.fdaDateFeature = fdaDateFeature;
	}

	public void setFdaFeature(String fdaFeature) {
		this.fdaFeature = fdaFeature;
	}

	public void setMeshFeature(String meshFeature) {
		this.meshFeature = meshFeature;
	}

	public void setKegdFeature(String kegdFeature) {
		this.kegdFeature = kegdFeature;
	}

	public void setKeggFeature(String keggFeature) {
		this.keggFeature = keggFeature;
	}

	public void setHmdbFeature(String hmdbFeature) {
		this.hmdbFeature = hmdbFeature;
	}

	public void setDrugFeature(String drugFeature) {
		this.drugFeature = drugFeature;
	}

	public void setInchFeature(String inchFeature) {
		this.inchFeature = inchFeature;
	}

	public void setPubsFeature(String pubsFeature) {
		this.pubsFeature = pubsFeature;
	}

	public void setPubcFeature(String pubcFeature) {
		this.pubcFeature = pubcFeature;
	}

	public void setCasFeature(String casFeature) {
		this.casFeature = casFeature;
	}

	public void setChebFeature(String chebFeature) {
		this.chebFeature = chebFeature;
	}

	public void setChidFeature(String chidFeature) {
		this.chidFeature = chidFeature;
	}

	public void setChemTypeFeature(String chemTypeFeature) {
		this.chemTypeFeature = chemTypeFeature;
	}

	public void setNoDict(Boolean noDict) {
		this.noDict = noDict;
	}

	public void setJavaHome(InputDirectory javaHome) {
		this.javaHome = javaHome;
	}

	public void setChemspotDir(InputDirectory chemspotDir) {
		this.chemspotDir = chemspotDir;
	}

	public void setTargetLayerName(String targetLayerName) {
		this.targetLayerName = targetLayerName;
	}

	public void setFdaDateFeatureName(String fdaDateFeatureName) {
		this.fdaDateFeature = fdaDateFeatureName;
	}

	public void setFdaFeatureName(String fdaFeatureName) {
		this.fdaFeature = fdaFeatureName;
	}

	public void setMeshFeatureName(String meshFeatureName) {
		this.meshFeature = meshFeatureName;
	}

	public void setKegdFeatureName(String kegdFeatureName) {
		this.kegdFeature = kegdFeatureName;
	}

	public void setKeggFeatureName(String keggFeatureName) {
		this.keggFeature = keggFeatureName;
	}

	public void setHmdbFeatureName(String hmdbFeatureName) {
		this.hmdbFeature = hmdbFeatureName;
	}

	public void setDrugFeatureName(String drugFeatureName) {
		this.drugFeature = drugFeatureName;
	}

	public void setInchFeatureName(String inchFeatureName) {
		this.inchFeature = inchFeatureName;
	}

	public void setPubsFeatureName(String pubsFeatureName) {
		this.pubsFeature = pubsFeatureName;
	}

	public void setPubcFeatureName(String pubcFeatureName) {
		this.pubcFeature = pubcFeatureName;
	}

	public void setCasFeatureName(String casFeatureName) {
		this.casFeature = casFeatureName;
	}

	public void setChebFeatureName(String chebFeatureName) {
		this.chebFeature = chebFeatureName;
	}

	public void setChidFeatureName(String chidFeatureName) {
		this.chidFeature = chidFeatureName;
	}

	public void setChemTypeFeatureName(String chemTypeFeatureName) {
		this.chemTypeFeature = chemTypeFeatureName;
	}
}
