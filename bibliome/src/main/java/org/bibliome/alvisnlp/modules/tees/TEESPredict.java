package org.bibliome.alvisnlp.modules.tees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.tees.CorpusTEES.Document.Sentence.Interaction;
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
	private String dependencyRelationName = DefaultNames.getDependencyRelationName();
	private String dependencyLabelFeatureName = DefaultNames.getDependencyLabelFeatureName();
	private String sentenceRole = DefaultNames.getDependencySentenceRole();
	private String headRole = DefaultNames.getDependencyHeadRole();
	private String dependentRole = DefaultNames.getDependencyDependentRole();
	
	// NE feature key
	private String namedEntityFeatureName4Entities = DefaultNames.getNamedEntityTypeFeature();
	private String namedEntityFeatureName4Events = DefaultNames.getNamedEntityTypeFeature();
	//
	private boolean givenEntities = true;


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
		return new String[] { tokenLayerName, sentenceLayerName };
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

	public String getNamedEntityFeatureName4Entities() {
		return namedEntityFeatureName4Entities;
	}

	public void setNamedEntityFeatureName4Entities(String namedEntityFeatureName) {
		this.namedEntityFeatureName4Entities = namedEntityFeatureName;
	}

	public String getNamedEntityFeatureName4Events() {
		return namedEntityFeatureName4Events;
	}

	public void setNamedEntityFeatureName4Events(String namedEntityFeatureName4Events) {
		this.namedEntityFeatureName4Events = namedEntityFeatureName4Events;
	}

	public boolean isGivenEntities() {
		return givenEntities;
	}

	public void setGivenEntities(boolean givenEntities) {
		this.givenEntities = givenEntities;
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
				Relation dependencies = sec.getRelation(dependencyRelationName);
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

	/***
	 * 
	 * 
	 * Mapping adds
	 */

	/**
	 * Access the alvis corpus and create the TEES Corpus and documents
	 * 
	 * @param ctx
	 * @param corpusAlvis
	 * @return
	 */
	public CorpusTEES createTheTeesCorpus(ProcessingContext<Corpus> ctx, Corpus corpusAlvis) {
		int docId = 0;
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);

		CorpusTEES corpusTEES = new CorpusTEES();

		// loop on documents
		for (Document documentAlvis : Iterators.loop(documentIterator(evalCtx, corpusAlvis))) {
			// create a TEES document
			CorpusTEES.Document documentTees = new CorpusTEES.Document();
			// add id of the TEES document		
			documentTees.setId("TEES.d"+ docId++);
			// adding all the sentences the TEES document
			Iterator<Section> alvisSectionsIterator = sectionIterator(evalCtx, documentAlvis);
			documentTees.getSentence().addAll(createTheTeesSentences(documentTees.getId(), alvisSectionsIterator, documentAlvis, corpusAlvis));
			// adding the document to the TEES corpus
			corpusTEES.getDocument().add(documentTees);
		}

		return corpusTEES;
	}

	/**
	 * Access the alvis corpus and create all the TEES Sentences
	 * 
	 * @param documentAlvis
	 * @return
	 */
	private ArrayList<CorpusTEES.Document.Sentence> createTheTeesSentences(String docId, Iterator<Section> alvisSectionsIterator, Document documentAlvus, Corpus corpus) {
		int sentId = 0;
		// create a TEES sentence list
		ArrayList<CorpusTEES.Document.Sentence> sentences = new ArrayList<CorpusTEES.Document.Sentence>();

		// loop on sections
		for (Section sectionAlvis : Iterators.loop(alvisSectionsIterator)) {
			// loop on sentences
			for (Layer sentLayer : sectionAlvis.getSentences(getTokenLayerName(), getSentenceLayerName())) {
				// access to an alvis sentence
				Annotation sentenceAlvis = sentLayer.getSentenceAnnotation();
				
				// create a Tees sentence
				CorpusTEES.Document.Sentence sentenceTees = new CorpusTEES.Document.Sentence();
				
				// add general information to sentence
				sentenceTees.setId(docId + ".s" + sentId++);
				sentenceTees.setText(sentenceAlvis.getForm()); 
				sentenceTees.setCharOffset(sentenceAlvis.getStart() + "-" + sentenceAlvis.getEnd());
				
				// add the TEES entities
				Layer alvisEntitiesLayer = sectionAlvis.ensureLayer(this.getTokenLayerName());
				sentenceTees.getEntity().clear();
				sentenceTees.getEntity().addAll(createTheTeesEntities(sentenceTees.getId(), sentenceAlvis, alvisEntitiesLayer, corpus));

				// add the TEES interactions
				Collection<Relation> alvisRelationsCollection = sectionAlvis.getAllRelations();
				sentenceTees.getInteraction().clear();
				sentenceTees.getInteraction().addAll(createTheInteractions(sentenceTees.getId(), sentenceAlvis, alvisRelationsCollection, corpus));
				sentences.add(sentenceTees);
				
				// add the analyses
				
			}
		} // *** end for adding the list of sentences to a document

		return sentences;
	}

	

	/**
	 * Access the alvis corpus and create all the entities of a sentence
	 * 
	 * @param sentenceAlvis
	 * @return
	 */
	private ArrayList<CorpusTEES.Document.Sentence.Entity> createTheTeesEntities(String sentId, Annotation sentenceAlvis, Layer alvisEntitiesLayer, Corpus corpus) {
		int entId = 0;
		// create tees entities list
		ArrayList<CorpusTEES.Document.Sentence.Entity> entities = new ArrayList<CorpusTEES.Document.Sentence.Entity>();

		// loop on entities
		for (Annotation entityAlvis : alvisEntitiesLayer) {
			// create a tees entity 
			CorpusTEES.Document.Sentence.Entity entityTees = new CorpusTEES.Document.Sentence.Entity();
			// add id
			entityTees.setId(sentId + ".e" + entId++);
			// add origin id
			entityTees.setOrigId(entityAlvis.getStringId());
			// add offset
			entityTees.setCharOffset(entityAlvis.getStart() + "-" + entityAlvis.getEnd());
			// add origin offset
			entityTees.setOrigOffset(entityAlvis.getStart() + "-" + entityAlvis.getEnd()); 
			// add text
			entityTees.setText(entityAlvis.getForm());
			// add type
			entityTees.setType(entityAlvis.getLastFeature(this.getNamedEntityFeatureName4Entities()));
			// add status of the entities
			entityTees.setGiven(this.isGivenEntities());


		}
		return entities;
	}

	
	private ArrayList<Interaction> createTheInteractions(String sentId, Annotation sentenceAlvis, Collection<Relation> allRelations, Corpus corpus) {
		int intId = 0;
		ArrayList<Interaction> interactions = new ArrayList<Interaction>();
		
		// loop  relations
		for (Relation rel : allRelations) {
			// loop  Tuples
			for (Tuple t : rel.getTuples()) {
				// get the relation arguments
				Element leftE = t.getArgument(this.headRole);
				Element rightE = t.getArgument(this.dependentRole);

				// filter out the binary arguments
				if(sentenceAlvis.includes(DownCastElement.toAnnotation(leftE)) && sentenceAlvis.includes(DownCastElement.toAnnotation(rightE))){
				// create a tees interaction
				Interaction interaction = new Interaction();
				// add id
				interaction.setId(sentId + ".i" + intId++);
				// add left entity
				interaction.setE1(leftE.getStringId());
				// add right entity
				interaction.setE2(rightE.getStringId());
				// add the relation is directed
				interaction.setDirected(true);
				// add the type of the relation
				interaction.setType(t.getLastFeature(this.getNamedEntityFeatureName4Events()));
				// add the interaction to the interaction list
				interactions.add(interaction);
				}
			}
		}
			return interactions;
	}

}
