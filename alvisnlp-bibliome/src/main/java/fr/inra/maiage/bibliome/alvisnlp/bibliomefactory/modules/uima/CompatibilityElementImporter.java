package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;

public interface CompatibilityElementImporter<E extends Element,A extends org.apache.uima.jcas.tcas.Annotation> {
	Class<A> getAnnotationClass();
	void setFeatures(E elements, A annotation);
}
