package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro;

import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.CompatibilityAnnotationExporter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;

public enum SentenceExporter implements CompatibilityAnnotationExporter<Sentence> {
	INSTANCE;

	@Override
	public Sentence create(JCas jcas, Annotation alvisAnnotation, int begin, int end) {
		return new Sentence(jcas, begin, end);
	}
}
