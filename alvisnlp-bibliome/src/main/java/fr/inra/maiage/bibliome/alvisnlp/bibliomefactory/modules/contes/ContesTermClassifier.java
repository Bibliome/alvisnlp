package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.File;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Resolvable;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUser;
import fr.inra.maiage.bibliome.util.files.AbstractFile;

public abstract class ContesTermClassifier<F extends AbstractFile> implements Resolvable<ContesTermClassifier.Resolved> {
	private final Expression documentFilter;
	private final Expression sectionFilter;
	private final String termLayerName;
	private final String conceptFeatureName;
	private final F regressionMatrixFile;
	
	protected ContesTermClassifier(Expression documentFilter, Expression sectionFilter, String termLayerName, String conceptFeatureName, F regressionMatrixFile) {
		super();
		this.documentFilter = documentFilter;
		this.sectionFilter = sectionFilter;
		this.termLayerName = termLayerName;
		this.conceptFeatureName = conceptFeatureName;
		this.regressionMatrixFile = regressionMatrixFile;
	}
	
	@Override
	public Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new Resolved(this, resolver);
	}

	public static class Resolved implements NameUser {
		private final ContesTermClassifier<?> contesTermClassifier;
		private final Evaluator documentFilter;
		private final Evaluator sectionFilter;
		
		private Resolved(ContesTermClassifier<?> contesTermClassifier, LibraryResolver libraryResolver) throws ResolverException {
			this.contesTermClassifier = contesTermClassifier;
			this.documentFilter = contesTermClassifier.documentFilter.resolveExpressions(libraryResolver);
			this.sectionFilter = contesTermClassifier.sectionFilter.resolveExpressions(libraryResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			nameUsage.addNames(NameType.LAYER, contesTermClassifier.termLayerName);
			nameUsage.addNames(NameType.FEATURE, contesTermClassifier.conceptFeatureName);
			documentFilter.collectUsedNames(nameUsage, defaultType);
			documentFilter.collectUsedNames(nameUsage, defaultType);
		}

		public Evaluator getDocumentFilter() {
			return documentFilter;
		}

		public Evaluator getSectionFilter() {
			return sectionFilter;
		}

		public String getTermLayerName() {
			return contesTermClassifier.termLayerName;
		}

		public String getConceptFeatureName() {
			return contesTermClassifier.conceptFeatureName;
		}

		public File getRegressionMatrixFile() {
			return contesTermClassifier.regressionMatrixFile;
		}	
	}
}
