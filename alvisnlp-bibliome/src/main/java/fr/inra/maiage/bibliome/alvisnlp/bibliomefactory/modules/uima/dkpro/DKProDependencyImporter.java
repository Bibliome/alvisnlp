package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro;

import java.util.Map;

import org.apache.uima.cas.FeatureStructure;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.CompatibilityTupleImporter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;

public enum DKProDependencyImporter implements CompatibilityTupleImporter<Dependency> {
	INSTANCE;

	@Override
	public Class<Dependency> getAnnotationClass() {
		return Dependency.class;
	}

	@Override
	public String getRelationName(Dependency annotation) {
		return DefaultNames.getDependencyRelationName();
	}

	@Override
	public void setFeatures(Tuple tuple, Dependency annotation) {
		tuple.addFeature(DefaultNames.getDependencyLabelFeatureName(), annotation.getDependencyType());
	}

	@Override
	public void setArguments(Map<FeatureStructure,Element> argumentMap, Tuple tuple, Dependency annotation) {
		Token head = annotation.getGovernor();
		Element alvisHead = argumentMap.get(head);
		tuple.setArgument(DefaultNames.getDependencyHeadRole(), alvisHead);
		Token dependent = annotation.getDependent();
		Element alvisDependent = argumentMap.get(dependent);
		tuple.setArgument(DefaultNames.getDependencyDependentRole(), alvisDependent);
		Section sec = tuple.getRelation().getSection();
		Layer sentences = sec.ensureLayer(DefaultNames.getSentenceLayer());
		Layer sentenceCandidates = sentences.including(annotation.getBegin(), annotation.getEnd());
		if (!sentenceCandidates.isEmpty()) {
			Annotation alvisSentence = sentenceCandidates.get(0);
			tuple.setArgument(DefaultNames.getDependencySentenceRole(), alvisSentence);
		}
	}
}
