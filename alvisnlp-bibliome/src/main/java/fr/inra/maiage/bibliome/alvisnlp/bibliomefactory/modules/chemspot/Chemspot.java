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
	private String fdaDateFeatureName = "FDA";
	private String fdaFeatureName = "FDA_DATE";
	private String meshFeatureName = "MESH";
	private String kegdFeatureName = "KEGD";
	private String keggFeatureName = "KEGG";
	private String hmdbFeatureName = "HMDB";
	private String drugFeatureName = "DRUG";
	private String inchFeatureName = "INCH";
	private String pubsFeatureName = "PUBS";
	private String pubcFeatureName = "PUBC";
	private String casFeatureName = "CAS";
	private String chebFeatureName = "CHEB";
	private String chidFeatureName = "CHID";
	private String chemTypeFeatureName = "chem-type";

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

	@Param(nameType=NameType.FEATURE)
	public String getFdaDateFeatureName() {
		return fdaDateFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getFdaFeatureName() {
		return fdaFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getMeshFeatureName() {
		return meshFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getKegdFeatureName() {
		return kegdFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getKeggFeatureName() {
		return keggFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getHmdbFeatureName() {
		return hmdbFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getDrugFeatureName() {
		return drugFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getInchFeatureName() {
		return inchFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getPubsFeatureName() {
		return pubsFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getPubcFeatureName() {
		return pubcFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getCasFeatureName() {
		return casFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getChebFeatureName() {
		return chebFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getChidFeatureName() {
		return chidFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getChemTypeFeatureName() {
		return chemTypeFeatureName;
	}

	@Param
	public Boolean getNoDict() {
		return noDict;
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
		this.fdaDateFeatureName = fdaDateFeatureName;
	}

	public void setFdaFeatureName(String fdaFeatureName) {
		this.fdaFeatureName = fdaFeatureName;
	}

	public void setMeshFeatureName(String meshFeatureName) {
		this.meshFeatureName = meshFeatureName;
	}

	public void setKegdFeatureName(String kegdFeatureName) {
		this.kegdFeatureName = kegdFeatureName;
	}

	public void setKeggFeatureName(String keggFeatureName) {
		this.keggFeatureName = keggFeatureName;
	}

	public void setHmdbFeatureName(String hmdbFeatureName) {
		this.hmdbFeatureName = hmdbFeatureName;
	}

	public void setDrugFeatureName(String drugFeatureName) {
		this.drugFeatureName = drugFeatureName;
	}

	public void setInchFeatureName(String inchFeatureName) {
		this.inchFeatureName = inchFeatureName;
	}

	public void setPubsFeatureName(String pubsFeatureName) {
		this.pubsFeatureName = pubsFeatureName;
	}

	public void setPubcFeatureName(String pubcFeatureName) {
		this.pubcFeatureName = pubcFeatureName;
	}

	public void setCasFeatureName(String casFeatureName) {
		this.casFeatureName = casFeatureName;
	}

	public void setChebFeatureName(String chebFeatureName) {
		this.chebFeatureName = chebFeatureName;
	}

	public void setChidFeatureName(String chidFeatureName) {
		this.chidFeatureName = chidFeatureName;
	}

	public void setChemTypeFeatureName(String chemTypeFeatureName) {
		this.chemTypeFeatureName = chemTypeFeatureName;
	}
}
