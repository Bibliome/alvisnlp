package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro;

import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.CompatibilityTupleExporter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;

public enum DependencyExporter implements CompatibilityTupleExporter<Dependency> {
	INSTANCE;

	@Override
	public Dependency create(Map<Element,TOP> argumentMap, JCas jcas, Tuple tuple) {
		Element dependent = tuple.getArgument(DefaultNames.getDependencyDependentRole());
		Token dependentToken = (Token) argumentMap.get(dependent);
		Element head = tuple.getArgument(DefaultNames.getDependencyHeadRole());
		Token headToken = (Token) argumentMap.get(head);
		Dependency dep = new Dependency(jcas, dependentToken.getBegin(), dependentToken.getEnd());
		dep.setGovernor(headToken);
		dep.setDependent(dependentToken);
		dep.setDependencyType(tuple.getLastFeature(DefaultNames.getDependencyLabelFeatureName()));
		return dep;
	}
}
