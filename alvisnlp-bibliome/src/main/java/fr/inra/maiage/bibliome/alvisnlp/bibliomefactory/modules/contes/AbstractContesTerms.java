package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes.AbstractContesTerms.ContesTermsResolvedObject;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Checkable;
import fr.inra.maiage.bibliome.util.files.AbstractFile;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.InputFile;

public abstract class AbstractContesTerms<F extends AbstractFile,T extends ContesTermClassifier<F>> extends CorpusModule<ContesTermsResolvedObject> implements AbstractContes, Checkable {
	private InputDirectory contesDir;
	private String tokenLayerName = DefaultNames.getWordLayer();
	private String formFeature = Annotation.FORM_FEATURE_NAME;
	private InputFile ontology;
	private InputFile wordEmbeddings;
	private InputFile wordEmbeddingsModel;
	private Double defaultFactor = 1.0;
	private T[] termClassifiers;

	@Override
	@Param(nameType=NameType.LAYER)
	public String getTokenLayerName() {
		return tokenLayerName;
	}

	@Override
	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getFormFeatureName() {
		return formFeature;
	}

	@Param(mandatory=false)
	public InputFile getWordEmbeddings() {
		return wordEmbeddings;
	}

	@Override
	@Param
	public InputDirectory getContesDir() {
		return contesDir;
	}

	@Param
	public InputFile getOntology() {
		return ontology;
	}

	@Param(mandatory=false)
	public InputFile getWordEmbeddingsModel() {
		return wordEmbeddingsModel;
	}

	@Param
	public Double getDefaultFactor() {
		return defaultFactor;
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

	public void setDefaultFactor(Double defaultFactor) {
		this.defaultFactor = defaultFactor;
	}

	public void setWordEmbeddingsModel(InputFile wordEmbeddingsModel) {
		this.wordEmbeddingsModel = wordEmbeddingsModel;
	}

	protected T[] getTermClassifiers() {
		return termClassifiers;
	}

	protected void setTermClassifiers(T[] termClassifier) {
		this.termClassifiers = termClassifier;
	}

	public void setOntology(InputFile ontology) {
		this.ontology = ontology;
	}

	@Override
	public void setContesDir(InputDirectory contesDir) {
		this.contesDir = contesDir;
	}

	@Override
	public void setTokenLayerName(String tokenLayer) {
		this.tokenLayerName = tokenLayer;
	}

	@Override
	@Deprecated
	public void setFormFeatureName(String formFeature) {
		this.formFeature = formFeature;
	}

	public void setWordEmbeddings(InputFile wordEmbeddings) {
		this.wordEmbeddings = wordEmbeddings;
	}
	
	@Override
	protected ContesTermsResolvedObject createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ContesTermsResolvedObject(ctx, this);
	}

	public static class ContesTermsResolvedObject extends ResolvedObjects {
		private final ContesTermClassifier.Resolved[] termClassifiers;
		
		public ContesTermsResolvedObject(ProcessingContext<Corpus> ctx, AbstractContesTerms<?,?> module) throws ResolverException {
			super(ctx, module);
			this.termClassifiers = rootResolver.resolveArray(module.termClassifiers, ContesTermClassifier.Resolved.class);
		}

		public ContesTermClassifier.Resolved[] getTermClassifiers() {
			return termClassifiers;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesArray(this.termClassifiers, defaultType);
		}
	}

	@Override
	public boolean check(Logger logger) {
		if (wordEmbeddings == null) {
			if (wordEmbeddingsModel == null) {
				logger.severe("either one of wordEmbeddings or wordEmbeddingsModel is mandatory");
				return false;
			}
			return true;
		}
		if (wordEmbeddingsModel != null) {
			logger.severe("wordEmbeddings and wordEmbeddingsModel are mutually exclusive");
			return false;
		}
		return true;
	}
}
