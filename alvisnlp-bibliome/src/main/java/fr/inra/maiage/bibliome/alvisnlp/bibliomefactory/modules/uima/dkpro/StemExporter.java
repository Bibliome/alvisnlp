package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro;

import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Stem;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.CompatibilityAnnotationExporter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;

public enum StemExporter implements CompatibilityAnnotationExporter<Stem> {
	INSTANCE;

	@Override
	public Stem create(JCas jcas, Annotation alvisAnnotation, int begin, int end) {
		Stem stem = new Stem(jcas, begin, end);
		stem.setValue(alvisAnnotation.getLastFeature(DefaultNames.getStemFeature()));
		return stem;
	}
}
