package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.InputFile;

/**
 *
 * @author mba
 *
 */

@AlvisNLPModule
public abstract class TEESClassify extends TEESMapper {
	private String dependencyRelation = DefaultNames.getDependencyRelationName();
	private String dependencyLabelFeature = DefaultNames.getDependencyLabelFeatureName();
	private String sentenceRole = DefaultNames.getDependencySentenceRole();
	private String headRole = DefaultNames.getDependencyHeadRole();
	private String dependentRole = DefaultNames.getDependencyDependentRole();

	private InputFile teesModel;


	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		try {
			new TEESClassifyExternalHandler(ctx, this, corpus).start();
		}
		catch (JAXBException | IOException | InterruptedException e) {
			throw new ProcessingException(e);
		}
	}

	/**
	 * Object resolver and Feature Handlers
	 */
	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] {
				getTokenLayer(),
				getSentenceLayer()
		};
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Deprecated
	@Param(nameType = NameType.FEATURE)
	public String getDependencyLabelFeatureName() {
		return dependencyLabelFeature;
	}

	@Param(nameType = NameType.ARGUMENT)
	public String getSentenceRole() {
		return sentenceRole;
	}

	@Param(nameType = NameType.ARGUMENT)
	public String getHeadRole() {
		return headRole;
	}

	@Param(nameType = NameType.ARGUMENT)
	public String getDependentRole() {
		return dependentRole;
	}

	@Deprecated
	@Param(nameType = NameType.RELATION)
	public String getDependencyRelationName() {
		return dependencyRelation;
	}

	@Param(nameType = NameType.FEATURE)
	public String getDependencyLabelFeature() {
		return dependencyLabelFeature;
	}

	@Param(nameType = NameType.RELATION)
	public String getDependencyRelation() {
		return dependencyRelation;
	}

	public void setDependencyRelation(String dependencyRelation) {
		this.dependencyRelation = dependencyRelation;
	}

	public void setDependencyLabelFeature(String dependencyLabelFeature) {
		this.dependencyLabelFeature = dependencyLabelFeature;
	}

	public void setDependencyRelationName(String dependencyRelationName) {
		this.dependencyRelation = dependencyRelationName;
	}

	public void setDependencyLabelFeatureName(String dependencyLabelFeatureName) {
		this.dependencyLabelFeature = dependencyLabelFeatureName;
	}

	public void setSentenceRole(String sentenceRole) {
		this.sentenceRole = sentenceRole;
	}

	public void setHeadRole(String headRole) {
		this.headRole = headRole;
	}

	public void setDependentRole(String dependentRole) {
		this.dependentRole = dependentRole;
	}

	@Param
	public InputFile getTeesModel() {
		return teesModel;
	}

	public void setTeesModel(InputFile model) {
		this.teesModel = model;
	}
}
