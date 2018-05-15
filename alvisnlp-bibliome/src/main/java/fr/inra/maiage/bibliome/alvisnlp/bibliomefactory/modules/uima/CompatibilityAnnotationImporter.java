package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;

public interface CompatibilityAnnotationImporter<A extends org.apache.uima.jcas.tcas.Annotation> extends CompatibilityElementImporter<Annotation,A>  {
	String[] getLayerNames(A annotation);
}
