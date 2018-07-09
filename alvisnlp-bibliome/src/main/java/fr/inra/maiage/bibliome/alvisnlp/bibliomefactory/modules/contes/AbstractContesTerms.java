package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

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
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.InputFile;

public abstract class AbstractContesTerms extends CorpusModule<ContesTermsResolvedObject> implements AbstractContes {
	private InputDirectory contesDir;
	private String tokenLayerName = DefaultNames.getWordLayer();
	private String formFeatureName = Annotation.FORM_FEATURE_NAME;
	private InputFile ontology;
	private InputFile wordEmbeddings;
	private ContesTermClassifier[] termClassifiers;

	@Override
	@Param(nameType=NameType.LAYER)
	public String getTokenLayerName() {
		return tokenLayerName;
	}

	@Override
	@Param(nameType=NameType.FEATURE)
	public String getFormFeatureName() {
		return formFeatureName;
	}

	@Param
	public InputFile getWordEmbeddings() {
		return wordEmbeddings;
	}

	@Param
	public InputDirectory getContesDir() {
		return contesDir;
	}

	@Param
	public InputFile getOntology() {
		return ontology;
	}

	@Param
	public ContesTermClassifier[] getTermClassifier() {
		return termClassifiers;
	}

	public void setTermClassifier(ContesTermClassifier[] termClassifier) {
		this.termClassifiers = termClassifier;
	}

	public void setOntology(InputFile ontology) {
		this.ontology = ontology;
	}

	public void setContesDir(InputDirectory contesDir) {
		this.contesDir = contesDir;
	}

	@Override
	public void setTokenLayerName(String tokenLayer) {
		this.tokenLayerName = tokenLayer;
	}

	@Override
	public void setFormFeatureName(String formFeature) {
		this.formFeatureName = formFeature;
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
		
		public ContesTermsResolvedObject(ProcessingContext<Corpus> ctx, AbstractContesTerms module) throws ResolverException {
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
}
