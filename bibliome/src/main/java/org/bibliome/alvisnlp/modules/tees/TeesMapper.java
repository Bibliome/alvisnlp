package org.bibliome.alvisnlp.modules.tees;

import java.util.Iterator;
import java.util.List;
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
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.Param;



public abstract class TeesMapper extends SectionModule<SectionResolvedObjects> {

	
	private String tokenLayerName = DefaultNames.getWordLayer();
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	
	// NE feature key
	private String namedEntityLayerName = null;
	private String namedEntityTypeFeatureName = DefaultNames.getNamedEntityTypeFeature();
	
	private String relationName = null;
	private String arg1 = null;
	private String arg2 = null;
	
	
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
		logger.info("creating the TEES documents ");
		for (Document documentAlvis : Iterators.loop(documentIterator(evalCtx, corpusAlvis))) {
			// create a TEES document
			CorpusTEES.Document documentTees = new CorpusTEES.Document();
			// add id of the TEES document		
			documentTees.setId("ALVIS.d"+ docId++);
			logger.info("creating the TEES document " + documentTees.getId());
			// adding all the sentences the TEES document
			logger.info("creating the TEES sentences of this document " + documentTees.getId());
			Iterator<Section> alvisSectionsIterator = sectionIterator(evalCtx, documentAlvis);
			createTheTeesSentences(documentTees.getSentence(), documentTees.getId(), alvisSectionsIterator, documentAlvis, corpusAlvis, ctx);
			// adding the document to the TEES corpus
			logger.info("number of sentences " + documentTees.getSentence().size());
			//
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
	private List<CorpusTEES.Document.Sentence> createTheTeesSentences(List<CorpusTEES.Document.Sentence> sentences, String docId, Iterator<Section> alvisSectionsIterator, Document documentAlvus, Corpus corpus,  ProcessingContext<Corpus> ctx) {
		int sentId = 0;
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		
		// create a TEES sentence list
		//ArrayList<CorpusTEES.Document.Sentence> sentences = new ArrayList<CorpusTEES.Document.Sentence>();

		// loop on sections
		for (Section sectionAlvis : Iterators.loop(alvisSectionsIterator)) {
			// loop on sentences
			for (Layer sentLayer : sectionAlvis.getSentences(this.getTokenLayerName(), this.getSentenceLayerName())) {
				// access to an alvis sentence
				Annotation sentenceAlvis = sentLayer.getSentenceAnnotation();
				
				if(sentenceAlvis == null) continue;
					
				// create a Tees sentence
				CorpusTEES.Document.Sentence sentenceTees = new CorpusTEES.Document.Sentence();
				
				// add general information to sentence
				sentenceTees.setId(docId + ".s" + sentId++);
				logger.info("creating the sentences : " + sentenceTees.getId());
				sentenceTees.setText(sentenceAlvis.getForm()); 
				sentenceTees.setCharOffset(sentenceAlvis.getStart() + "-" + sentenceAlvis.getEnd());
				
				// add the TEES entities
				logger.info("creating the TEES entities");
				Layer alvisEntitiesLayer = sectionAlvis.ensureLayer(this.getNamedEntityLayerName()); // but entities
				createTheTeesEntities(sentenceTees, sentenceTees.getId(), sentenceAlvis, alvisEntitiesLayer, corpus, ctx);

				// add the TEES interactions
				logger.info("creating the TEES interactions :" + sectionAlvis.relations.toString());
				Relation alvisRelationsCollection = sectionAlvis.getRelation(this.getRelationName());
				createTheInteractions(sentenceTees, sentenceTees.getId(), sentenceAlvis, alvisRelationsCollection, corpus, ctx);
				
				// add the set sentence
				sentences.add(sentenceTees);
				
				// add the analyses ???
				
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
	private void createTheTeesEntities(CorpusTEES.Document.Sentence sentenceTees, String sentId, Annotation sentenceAlvis, Layer alvisEntitiesLayer, Corpus corpus,  ProcessingContext<Corpus> ctx) {
		int entId = 0;
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		
		// loop on entities
		if (alvisEntitiesLayer!=null) for (Annotation entityAlvis : alvisEntitiesLayer) {
			if(entityAlvis != null){
			
			if(sentenceAlvis.includes(entityAlvis)==false) continue;
			// create a tees entity 
			CorpusTEES.Document.Sentence.Entity entityTees = new CorpusTEES.Document.Sentence.Entity();
			// add id
			entityTees.setId(sentId + ".e" + entId++);
			// add origin id
			logger.info("creating the entity " + entityTees.getId());
			entityTees.setOrigId(entityAlvis.getStringId());
			// add offset
			entityTees.setCharOffset((entityAlvis.getStart()- sentenceAlvis.getStart()) + "-" + (entityAlvis.getEnd()-sentenceAlvis.getStart()));
			// add origin offset
			entityTees.setOrigOffset(entityAlvis.getStart() + "-" + entityAlvis.getEnd()); 
			// add text
			entityTees.setText(entityAlvis.getForm());
			// add type
			entityTees.setType(entityAlvis.getLastFeature(this.getNamedEntityTypeFeatureName()));
			// set given
			if(this.getNamedEntityLayerName()!=null) entityTees.setGiven(true);
			else entityTees.setGiven(false);
			//
			sentenceTees.getEntity().add(entityTees);
			}
		}
	}

	
	private void createTheInteractions(CorpusTEES.Document.Sentence sentenceTees, String sentId, Annotation sentenceAlvis, Relation allRelations, Corpus corpus, ProcessingContext<Corpus> ctx) {
		int intId = 0;
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
			
		// loop  relations
		if(allRelations!=null) for (Tuple r : allRelations.getTuples()) {

			logger.info("check stop, roles" + r.getRoles().toString());			
			Element ag1 = r.getArgument(this.getArg1());			
			Element ag2 = r.getArgument(this.getArg2());
			
			Annotation ann1 = DownCastElement.toAnnotation(ag1);
			Annotation ann2 = DownCastElement.toAnnotation(ag2);
			
			if(sentenceAlvis.includes(ann1)==false || sentenceAlvis.includes(ann2)==false) continue;
			
			CorpusTEES.Document.Sentence.Interaction interaction = new CorpusTEES.Document.Sentence.Interaction();
			interaction.setId(sentId + ".i" + intId++);
			//String label = dep.getLastFeature(dependencyLabelFeatureName);
			//Element sentence = dep.getArgument(sentenceRole);
			
			// from oldId to charOffsetId
			for (int i = 0; i < sentenceTees.getEntity().size(); i++) {
				if(sentenceTees.getEntity().get(i).getOrigId().compareToIgnoreCase(ann1.getStringId())==0)
				{
					interaction.setE1(sentenceTees.getEntity().get(i).getId());
				}
				else if(sentenceTees.getEntity().get(i).getOrigId().compareToIgnoreCase(ann2.getStringId())==0){
					interaction.setE2(sentenceTees.getEntity().get(i).getId());
				}
			}
			//interaction.setE1(ann1.getStringId() + ":(" + (ann1.getStart()-sentenceAlvis.getStart())+"-"+(ann1.getEnd()-sentenceAlvis.getStart())+")");
			//interaction.setE2(ann2.getStringId() + ":(" + (ann2.getStart()-sentenceAlvis.getStart())+"-"+(ann2.getEnd()-sentenceAlvis.getStart())+")");
			
			interaction.setType(this.getRelationName());
			interaction.setOrigId(r.getStringId());
			
			interaction.setDirected(true);
			sentenceTees.getInteraction().add(interaction);
		}
		
		
//		if (allRelations != null) for (Relation rel : allRelations) {
//			// loop  Tuples
//			
//			for (Tuple t : rel.getTuples()) {
//				Interaction interaction = new Interaction();
//				interaction.setId(sentId + ".i" + intId++);
//				interaction.setE1(null);
//				interaction.setE1(null);
//				
//				logger.info("check stop, tuple" + t.getFeatureKeys());
//				// setting the arguments
//				for (String role : t.getRoles()) {
//					Element arg = t.getArgument(role);
//					if(arg == null) continue;
//					if(role.compareToIgnoreCase(this.getHeadRole())==0){
//						Annotation ann = DownCastElement.toAnnotation(arg);
//						if(ann != null) interaction.setE1(ann.getStringId());
//					}
//					else if(role.compareToIgnoreCase(this.getDependentRole())==0){
//						Annotation ann = DownCastElement.toAnnotation(arg);
//						if(ann != null) interaction.setE2(ann.getStringId());
//					}
//				} // end setting the arguments
//				
//				if (interaction.getE1()!= null && interaction.getE2() != null){
//					logger.info("adding the interaction " + interaction.getId());
//					interaction.setDirected(true);
//					interactions.add(interaction);
//				}	
//			}			
//		}
		logger.info("End adding interactions");
	}

	
	//// getttttttttttt setttt
	
	@Param(nameType = NameType.LAYER)
	public String getTokenLayerName() {
		return tokenLayerName;
	}

	@Param(nameType = NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayerName;
	}
	
	
	@Param(nameType = NameType.LAYER)
	public String getNamedEntityLayerName() {
		return namedEntityLayerName;
	}

	public void setNamedEntityLayerName(String namedEntityLayerName) {
		this.namedEntityLayerName = namedEntityLayerName;
	}
	
	public void setTokenLayerName(String tokenLayerName) {
		this.tokenLayerName = tokenLayerName;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}

	
	@Param(nameType = NameType.FEATURE)
	public String getNamedEntityTypeFeatureName() {
		return namedEntityTypeFeatureName;
	}


	public void setNamedEntityTypeFeatureName(String namedEntityTypeFeatureName) {
		this.namedEntityTypeFeatureName = namedEntityTypeFeatureName;
	}

	@Param
	public String getRelationName() {
		return relationName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
//	

	@Param
	public String getArg1() {
		return arg1;
	}

	public void setArg1(String arg1) {
		this.arg1 = arg1;
	}

	@Param
	public String getArg2() {
		return arg2;
	}

	public void setArg2(String arg2) {
		this.arg2 = arg2;
	}
}
