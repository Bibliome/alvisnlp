package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima;

import java.util.Map;

import org.apache.uima.cas.FeatureStructure;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;

public interface CompatibilityTupleImporter<A extends org.apache.uima.jcas.tcas.Annotation> extends CompatibilityElementImporter<Tuple,A> {
	String getRelationName(A annotation);
	void setArguments(Map<FeatureStructure,Element> argumentMap, Tuple tuple, A annotation);
}
