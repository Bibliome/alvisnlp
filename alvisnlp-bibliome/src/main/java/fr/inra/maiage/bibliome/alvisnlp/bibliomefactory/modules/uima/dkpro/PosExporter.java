package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro;

import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.CompatibilityAnnotationExporter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;

public enum PosExporter implements CompatibilityAnnotationExporter<POS> {
	INSTANCE;

	@Override
	public POS create(JCas jcas, Annotation alvisAnnotation, int begin, int end) {
		POS pos = new POS(jcas, begin, end);
		pos.setPosValue(alvisAnnotation.getLastFeature(DefaultNames.getPosTagFeature()));
		return pos;
	}
}
