package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;

public interface DependencyParserModule extends TupleCreator {
	@Param(nameType=NameType.RELATION, defaultValue = "fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames.getDependencyRelationName()")
	public String getDependencyRelation();
	public void setDependencyRelation(String dependencyRelation);
	
	@Param(nameType=NameType.ARGUMENT, defaultValue = "fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames.getDependencySentenceRole()")
	public String getDependencySentenceRole();
	public void setDependencySentenceRole(String dependencySentenceRole);
	
	@Param(nameType=NameType.ARGUMENT, defaultValue = "fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames.getDependencyHeadRole()")
	public String getHeadRole();
	public void setHeadRole(String headRole);
	
	@Param(nameType=NameType.ARGUMENT, defaultValue = "fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames.getDependencyDependentRole()")
	public String getDependentRole();
	public void setDependentRole(String dependentRole);

	@Param(nameType=NameType.FEATURE, defaultValue = "fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames.getDependencyLabelFeatureName()")
	public String getDependencyLabelFeature();
	public void setDependencyLabelFeature(String dependencyLabelFeature);
	
	default Relation ensureRelation(Section sec) {
		return sec.ensureRelation(this, getDependencyRelation());
	}
	
	default Tuple createDependency(Relation rel, Annotation sentence, Annotation head, Annotation mod, String label) {
		Tuple t = new Tuple(this, rel);
		t.setArgument(this.getDependencySentenceRole(), sentence);
		t.setArgument(this.getHeadRole(), head);
		t.setArgument(this.getDependentRole(), mod);
		t.addFeature(this.getDependencyLabelFeature(), label);
		return t;
	}
}
