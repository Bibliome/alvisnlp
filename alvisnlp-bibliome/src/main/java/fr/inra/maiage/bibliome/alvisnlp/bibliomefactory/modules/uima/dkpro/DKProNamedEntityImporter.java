package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.CompatibilityAnnotationImporter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;

public enum DKProNamedEntityImporter implements CompatibilityAnnotationImporter<NamedEntity> {
	INSTANCE;

	@Override
	public Class<NamedEntity> getAnnotationClass() {
		return NamedEntity.class;
	}

	@Override
	public String[] getLayerNames(NamedEntity annotation) {
		return new String[] { DefaultNames.getNamedEntityLayer(), annotation.getValue() };
	}

	@Override
	public void setFeatures(Annotation alvisAnnotation, NamedEntity annotation) {
		alvisAnnotation.addFeature(DefaultNames.getNamedEntityTypeFeature(), annotation.getValue());
	}
}
