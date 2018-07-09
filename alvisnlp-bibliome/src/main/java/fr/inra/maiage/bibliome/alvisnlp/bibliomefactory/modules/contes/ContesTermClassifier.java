package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.File;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes.ContesTermClassifier.Resolved;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Resolvable;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUser;

public class ContesTermClassifier implements Resolvable<Resolved> {
	private final Expression documentFilter;
	private final Expression sectionFilter;
	private final String termLayerName;
	private final String conceptFeatureName;
	private final File regressionMatrixFile;
	
	public ContesTermClassifier(Expression documentFilter, Expression sectionFilter, String termLayerName, String conceptFeatureName, File regressionMatrixFile) {
		super();
		this.documentFilter = documentFilter;
		this.sectionFilter = sectionFilter;
		this.termLayerName = termLayerName;
		this.conceptFeatureName = conceptFeatureName;
		this.regressionMatrixFile = regressionMatrixFile;
	}
	
	@Override
	public Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new Resolved(resolver);
	}

	public class Resolved implements NameUser {
		private final Evaluator documentFilter;
		private final Evaluator sectionFilter;
		
		private Resolved(LibraryResolver libraryResolver) throws ResolverException {
			this.documentFilter = ContesTermClassifier.this.documentFilter.resolveExpressions(libraryResolver);
			this.sectionFilter = ContesTermClassifier.this.sectionFilter.resolveExpressions(libraryResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			nameUsage.addNames(NameType.LAYER, termLayerName);
			nameUsage.addNames(NameType.FEATURE, conceptFeatureName);
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
			return termLayerName;
		}

		public String getConceptFeatureName() {
			return conceptFeatureName;
		}

		public File getRegressionMatrixFile() {
			return regressionMatrixFile;
		}	
	}
}
