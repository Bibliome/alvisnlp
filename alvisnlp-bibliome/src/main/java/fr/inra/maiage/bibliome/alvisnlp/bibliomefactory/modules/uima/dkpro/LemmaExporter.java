package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro;

import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.CompatibilityAnnotationExporter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;

public enum LemmaExporter implements CompatibilityAnnotationExporter<Lemma> {
	INSTANCE;

	@Override
	public Lemma create(JCas jcas, Annotation alvisAnnotation, int begin, int end) {
		Lemma lemma = new Lemma(jcas, begin, end);
		lemma.setValue(alvisAnnotation.getLastFeature(DefaultNames.getCanonicalFormFeature()));
		return lemma;
	}
	
}
