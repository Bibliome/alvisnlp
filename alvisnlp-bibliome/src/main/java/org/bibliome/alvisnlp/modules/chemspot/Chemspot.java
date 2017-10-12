package org.bibliome.alvisnlp.modules.chemspot;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
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
			File tmpDir = getTempDir(ctx);
			Logger logger = getLogger(ctx);
			EvaluationContext evalCtx = new EvaluationContext(logger);
			ChemspotExternal<Corpus> external = new ChemspotExternal<>(ctx, this, javaHome, chemspotDir, noDict, tmpDir);
			for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
				String name = sec.getFileName();
				String content = sec.getContents();
				external.addInput(name, content);
			}
			callExternal(ctx, "run-chemspot", external);
			ChemspotFileLines<Layer,Annotation> chemspotFileLines = new CorpusChemspotFileLines(logger);
			for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
				String name = sec.getFileName();
				Layer layer = sec.ensureLayer(targetLayerName);
				external.readOutput(chemspotFileLines, layer, name);
			}
		}
		catch (IOException e) {
			rethrow(e);
		}
	}
	
	private class CorpusChemspotFileLines extends ChemspotFileLines<Layer,Annotation> {
		private final Logger logger;
		
		private CorpusChemspotFileLines(Logger logger) {
			super();
			this.logger = logger;
		}

		@Override
		protected void setFDA_DATE(Annotation annotation, String string) {
			annotation.addFeature(fdaDateFeatureName, string);
		}

		@Override
		protected void setFDA(Annotation annotation, String string) {
			annotation.addFeature(fdaFeatureName, string);
		}

		@Override
		protected void setMESH(Annotation annotation, String string) {
			annotation.addFeature(meshFeatureName, string);
		}

		@Override
		protected void setKEGD(Annotation annotation, String string) {
			annotation.addFeature(kegdFeatureName, string);
		}

		@Override
		protected void setKEGG(Annotation annotation, String string) {
			annotation.addFeature(keggFeatureName, string);
		}

		@Override
		protected void setHMBD(Annotation annotation, String string) {
			annotation.addFeature(hmdbFeatureName, string);
		}

		@Override
		protected void setDRUG(Annotation annotation, String string) {
			annotation.addFeature(drugFeatureName, string);
		}

		@Override
		protected void setINCH(Annotation annotation, String string) {
			annotation.addFeature(inchFeatureName, string);
		}

		@Override
		protected void setPUBS(Annotation annotation, String string) {
			annotation.addFeature(pubsFeatureName, string);
		}

		@Override
		protected void setPUBC(Annotation annotation, String string) {
			annotation.addFeature(pubcFeatureName, string);
		}

		@Override
		protected void setCAS(Annotation annotation, String string) {
			annotation.addFeature(casFeatureName, string);
		}

		@Override
		protected void setCHEB(Annotation annotation, String string) {
			annotation.addFeature(chebFeatureName, string);
		}

		@Override
		protected void setCHID(Annotation annotation, String string) {
			annotation.addFeature(chidFeatureName, string);
		}

		@Override
		protected void setType(Annotation annotation, String string) {
			annotation.addFeature(chemTypeFeatureName, string);
		}

		@Override
		protected Annotation createAnnotation(Layer data, int start0, int end0, String form) {
			int start = start0 + 1;
			int end = end0 + 2;
			Section sec = data.getSection();
			String content = sec.getContents();
			while (start >= 0) {
				String s = content.substring(start, end);
				if (s.equals(form)) {
					return new Annotation(Chemspot.this, data, start, end);
				}
				start--;
				end--;
			}
			logger.warning("weird chemspot positions: " + form + " / " + content.substring(start0, end0));
			return null;
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
