package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.CompatibilityAnnotationImporter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;

public enum DKProTokenImporter implements CompatibilityAnnotationImporter<Token> {
	INSTANCE;

	@Override
	public Class<Token> getAnnotationClass() {
		return Token.class;
	}

	@Override
	public String[] getLayerNames(Token annotation) {
		return new String[] { DefaultNames.getWordLayer() };
	}

	@Override
	public void setFeatures(Annotation alvisAnnotation, Token annotation) {
		alvisAnnotation.addFeature(DefaultNames.getCanonicalFormFeature(), annotation.getLemma().getValue());
		alvisAnnotation.addFeature(DefaultNames.getStemFeature(), annotation.getStem().getValue());
		alvisAnnotation.addFeature(DefaultNames.getPosTagFeature(), annotation.getPos().getPosValue());
	}
}
