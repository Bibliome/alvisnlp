package org.bibliome.alvisnlp.modules.tees;

import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.Param;

public class TEESPredict extends SectionModule<SectionResolvedObjects> {
	private String tokenLayerName = DefaultNames.getWordLayer();
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String dependencyLabelFeatureName = DefaultNames.getDependencyLabelFeatureName();
	private String sentenceRole = DefaultNames.getDependencySentenceRole();
	private String headRole = DefaultNames.getDependencyHeadRole();
	private String dependentRole = DefaultNames.getDependencyDependentRole();
	private String dependencyRelationName = DefaultNames.getDependencyRelationName();

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}
	
	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] {
				tokenLayerName,
				sentenceLayerName
		};
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Param(nameType = NameType.LAYER)
	public String getTokenLayerName() {
		return tokenLayerName;
	}

	@Param(nameType = NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayerName;
	}

	@Param(nameType = NameType.FEATURE)
	public String getDependencyLabelFeatureName() {
		return dependencyLabelFeatureName;
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

	@Param(nameType = NameType.RELATION)
	public String getDependencyRelationName() {
		return dependencyRelationName;
	}

	public void setDependencyRelationName(String dependencyRelationName) {
		this.dependencyRelationName = dependencyRelationName;
	}

	public void setDependencyLabelFeatureName(String dependencyLabelFeatureName) {
		this.dependencyLabelFeatureName = dependencyLabelFeatureName;
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

	public void setTokenLayerName(String tokenLayerName) {
		this.tokenLayerName = tokenLayerName;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}

	private void iteratorSnippet(ProcessingContext<Corpus> ctx, Corpus corpus) {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);

		// iteration des documents du corpus
		for (Document doc : Iterators.loop(documentIterator(evalCtx, corpus))) {
			// faire qqch avec doc, par ex
			doc.getId();
			doc.getLastFeature("DOCFEATUREKEY");
			// iteration des sections du document
			for (Section sec : Iterators.loop(sectionIterator(evalCtx, doc))) {
				// faire qqch avec sec, par ex
				sec.getName();
				sec.getLastFeature("SECFEATUREKEY");
				sec.getDocument();
				// iteration des annotations d'un layer dans une section
				Layer layer = sec.ensureLayer("LAYERNAME");
				for (Annotation a : layer) {
					// faire qqch avec a, par ex
					a.getStart();
					a.getEnd();
					a.getLength();
					a.getLastFeature("ANNOTATIONFEATUREKEY");
					a.getSection();
				}
				
				// iteration des sentences dans une section
				for (Layer sentLayer : sec.getSentences(getTokenLayerName(), getSentenceLayerName())) {
					Annotation sent = sentLayer.getSentenceAnnotation();
					// faire qqch avec sent
					// iteration des mots dans une sentence
					for (Annotation token : sentLayer) {
						// faire qqch avec token
					}
				}
				
				// iteration des relations dans une section
				for (Relation rel : sec.getAllRelations()) {
					// faire qqch avec la relation, par ex
					rel.getName();
					rel.getSection();
					rel.getLastFeature("RELFEATUREKEY");
					// iteration des tuples d'une relation
					for (Tuple t : rel.getTuples()) {
						// faire qqch avec t, par ex
						t.getRelation();
						t.getLastFeature("TUPLEFEATUREKEY");
						t.getArgument("ROLE");
						// iterer les arguments
						for (String role : t.getRoles()) {
							Element arg = t.getArgument(role);
							// faire qqch avec arg, par ex
							arg.getLastFeature("FEATUREKEY");
							Annotation a = DownCastElement.toAnnotation(arg);
						}
					}
				}
				
				// une relation en particulier
				Relation dependencies = sec.getRelation(dependencyRelationName );
				// iterer les tuples
				for (Tuple dep : dependencies.getTuples()) {
					String label = dep.getLastFeature(dependencyLabelFeatureName);
					Element sentence = dep.getArgument(sentenceRole);
					Element head = dep.getArgument(headRole);
					Element dependent = dep.getArgument(dependentRole);
				}
			}
		}
		
		// on peut iterer les sections sans passer par les documents
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			//
		}
	}
}
