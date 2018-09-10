package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima;

import org.apache.uima.jcas.JCas;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;

public interface CompatibilityAnnotationExporter<A extends org.apache.uima.jcas.tcas.Annotation> {
	A create(JCas jcas, Annotation alvisAnnotation, int begin, int end);
}
