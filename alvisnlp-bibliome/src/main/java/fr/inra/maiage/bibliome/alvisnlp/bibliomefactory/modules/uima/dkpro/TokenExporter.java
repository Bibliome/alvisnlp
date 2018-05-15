package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro;

import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.CompatibilityAnnotationExporter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;

public enum TokenExporter implements CompatibilityAnnotationExporter<Token> {
	INSTANCE;

	@Override
	public Token create(JCas jcas, Annotation alvisAnnotation, int begin, int end) {
		Token token = new Token(jcas, begin, end);
		if (alvisAnnotation.hasFeature(DefaultNames.getCanonicalFormFeature())) {
			token.setLemma(LemmaExporter.INSTANCE.create(jcas, alvisAnnotation, begin, end));
		}
		if (alvisAnnotation.hasFeature(DefaultNames.getPosTagFeature())) {
			token.setPos(PosExporter.INSTANCE.create(jcas, alvisAnnotation, begin, end));
		}
		if (alvisAnnotation.hasFeature(DefaultNames.getStemFeature())) {
			token.setStem(StemExporter.INSTANCE.create(jcas, alvisAnnotation, begin, end));
		}
		return token;
	}
}
