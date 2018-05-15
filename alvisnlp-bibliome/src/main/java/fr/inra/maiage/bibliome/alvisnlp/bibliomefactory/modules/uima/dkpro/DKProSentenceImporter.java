package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.CompatibilityAnnotationImporter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;

public enum DKProSentenceImporter implements CompatibilityAnnotationImporter<Sentence> {
	INSTANCE;

	@Override
	public Class<Sentence> getAnnotationClass() {
		return Sentence.class;
	}

	@Override
	public String[] getLayerNames(Sentence annotation) {
		return new String[] { DefaultNames.getSentenceLayer() };
	}

	@Override
	public void setFeatures(Annotation alvisAnnotation, Sentence annotation) {
	}
}
