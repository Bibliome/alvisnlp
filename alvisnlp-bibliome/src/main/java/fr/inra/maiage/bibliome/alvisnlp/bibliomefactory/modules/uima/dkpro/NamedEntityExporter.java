package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro;

import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.CompatibilityAnnotationExporter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;

public enum NamedEntityExporter implements CompatibilityAnnotationExporter<NamedEntity> {
	INSTANCE;

	@Override
	public NamedEntity create(JCas jcas, Annotation alvisAnnotation, int begin, int end) {
		NamedEntity ne = new NamedEntity(jcas, begin, end);
		ne.setValue(alvisAnnotation.getLastFeature(DefaultNames.getNamedEntityTypeFeature()));
		ne.setIdentifier(alvisAnnotation.getLastFeature(DefaultNames.getExternalReferenceFeatureName()));
		return ne;
	}
}
