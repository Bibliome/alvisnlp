package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima;

import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;

public interface CompatibilityTupleExporter<A extends org.apache.uima.jcas.tcas.Annotation> {
	A create(Map<Element,TOP> argumentMap, JCas jcas, Tuple tuple);
}
