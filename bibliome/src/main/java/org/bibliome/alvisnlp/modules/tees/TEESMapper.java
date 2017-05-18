package org.bibliome.alvisnlp.modules.tees;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.tees.CorpusTEES.Document.Sentence;
import org.bibliome.alvisnlp.modules.tees.CorpusTEES.Document.Sentence.Entity;
import org.bibliome.alvisnlp.modules.tees.CorpusTEES.Document.Sentence.Interaction;
import org.bibliome.util.Iterators;
import org.bibliome.util.files.InputDirectory;

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
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.Param;

/**
 * 
 * @author mba
 *
 */

public abstract class TEESMapper extends SectionModule<SectionResolvedObjects> implements TupleCreator {
	protected static final String INTERNAL_ENCODING = "UTF-8";
	
	private String tokenLayerName = DefaultNames.getWordLayer();
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String posFeature = DefaultNames.getPosTagFeature();
	private String namedEntityTypeFeature = DefaultNames.getNamedEntityTypeFeature();
	private String namedEntityLayerName = null;
	
	private String relationName = null;
	private String leftRole = null;
	private String rightRole = null;
	
	private String omitSteps = "SPLIT-SENTENCES,NER";
	private InputDirectory teesHome;

	protected Map<String, CorpusTEES> corpora = new HashMap<String, CorpusTEES>();
	private Map<String, Section> sentId2Sections =  new HashMap<String, Section>();
	private Map<String, Element> entId2Elements =  new HashMap<String, Element>();
	
	
	/***
	 * 
	 * 
	 * Mapping adds
	 */
	
	protected abstract String getSet(Document doc);

	/**
	 * Access the alvis corpus and create the TEES Corpus and documents
	 * 
	 * @param ctx
	 * @param corpusAlvis
	 * @return
	 */
	public void createTheTeesCorpus(ProcessingContext<Corpus> ctx, Corpus corpusAlvis) {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);

		// loop on documents
		logger.info("creating the TEES documents ");
		for (Document documentAlvis : Iterators.loop(documentIterator(evalCtx, corpusAlvis))) {
	
			// splitting the documents according to their corpus set
			String set = getSet(documentAlvis);
			if(corpora.get(set) == null) {
				this.corpora.put(set, new CorpusTEES());
			}
			
			// create a TEES document
			CorpusTEES.Document documentTees = new CorpusTEES.Document();		
			// setting the doc id
//			System.err.println("corpora = " + corpora);
//			System.err.println("set = " + set);
//			System.err.println("corpora.get(set) = " + corpora.get(set));
//			System.err.println("corpora.get(set).getDocument() = " + corpora.get(set).getDocument());
			documentTees.setId("ALVIS.d" + corpora.get(set).getDocument().size());
			logger.info("adding the document" + documentTees.getId() + " to " + set + " Set");
			// setting the doc sentences
			logger.info("creating the TEES sentences of this document " + documentTees.getId());
			Iterator<Section> alvisSectionsIterator = sectionIterator(evalCtx, documentAlvis);
			createTheTeesSentences(documentTees.getSentence(), documentTees.getId(), alvisSectionsIterator, ctx);
			logger.info("number of sentences " + documentTees.getSentence().size());
			// adding the document
			this.corpora.get(set).getDocument().add(documentTees);
			
		}
	}

	/**
	 * Access the alvis corpus and create all the TEES Sentences
	 * @param documentAlvis
	 * 
	 * @return
	 */
	private List<CorpusTEES.Document.Sentence> createTheTeesSentences(List<CorpusTEES.Document.Sentence> sentences, String docId, Iterator<Section> alvisSectionsIterator, ProcessingContext<Corpus> ctx) {
		int sentId = 0;
		Logger logger = getLogger(ctx);

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
				createTheTeesEntities(sentenceTees, sentenceTees.getId(), sentenceAlvis, alvisEntitiesLayer, ctx);

				// add the TEES interactions
				logger.info("creating the TEES interactions ");
				Relation alvisRelationsCollection = sectionAlvis.getRelation(this.getRelationName());
				createTheInteractions(sentenceTees, sentenceTees.getId(), sentenceAlvis, alvisRelationsCollection, ctx);
				
				
				// add the set sentence
				sentences.add(sentenceTees);
				
				//sent2secId.put(sentenceTees.getId(), sectionAlvis.getStringId());
				sentId2Sections.put(sentenceTees.getId(), sectionAlvis);
				
				// add the analyses todo
				
			}
		}
		

		return sentences;
	}

	

	/**
	 * Access the alvis corpus and create all the entities of a sentence
	 * @param sentenceAlvis
	 * 
	 * @return
	 */
	private void createTheTeesEntities(CorpusTEES.Document.Sentence sentenceTees, String sentId, Annotation sentenceAlvis, Layer alvisEntitiesLayer, ProcessingContext<Corpus> ctx) {
		int entId = 0;
		Logger logger = getLogger(ctx);

		// loop on entities
		if (alvisEntitiesLayer!=null)
			for (Annotation entityAlvis : alvisEntitiesLayer) {
				if(entityAlvis != null){
					if(sentenceAlvis.includes(entityAlvis)==false)
						continue;
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
					entityTees.setType(entityAlvis.getLastFeature(this.getNamedEntityTypeFeature()));
					// set given
					if(this.getNamedEntityLayerName()!=null)
						entityTees.setGiven(true);
					else
						entityTees.setGiven(false);
					// add the entity
					sentenceTees.getEntity().add(entityTees);
					entId2Elements.put(entityTees.getId(), entityAlvis);
				}
			}
	}

	
	private void createTheInteractions(CorpusTEES.Document.Sentence sentenceTees, String sentId, Annotation sentenceAlvis, Relation allRelations, ProcessingContext<Corpus> ctx) {
		int intId = 0;
		Logger logger = getLogger(ctx);
			
		// loop  relations
		if(allRelations!=null) for (Tuple r : allRelations.getTuples()) {

			// getting the argument from roles
			Element ag1 = r.getArgument(this.getLeftRole());			
			Element ag2 = r.getArgument(this.getRightRole());
			
			// downcasting argument elements as annotations
			Annotation ann1 = DownCastElement.toAnnotation(ag1);
			Annotation ann2 = DownCastElement.toAnnotation(ag2);
			
			// is the sentence contains the argument annotations
			if(sentenceAlvis.includes(ann1)==false || sentenceAlvis.includes(ann2)==false) continue;
			
			// creating interaction
			CorpusTEES.Document.Sentence.Interaction interaction = new CorpusTEES.Document.Sentence.Interaction();
			// setting id
			interaction.setId(sentId + ".i" + intId++);
			logger.info("creating interaction " + r.getRoles().toString());	
			
			
			// getting tees entities ids from alvis ids
			for (int i = 0; i < sentenceTees.getEntity().size(); i++) {
				if(sentenceTees.getEntity().get(i).getOrigId().compareToIgnoreCase(ann1.getStringId())==0)
				{
					interaction.setE1(sentenceTees.getEntity().get(i).getId());
				}
				else if(sentenceTees.getEntity().get(i).getOrigId().compareToIgnoreCase(ann2.getStringId())==0){
					interaction.setE2(sentenceTees.getEntity().get(i).getId());
				}
			}

			// setting relation type 
			interaction.setType(this.getRelationName());
			// setting relation originId
			interaction.setOrigId(r.getStringId());
			// setting as a directed interaction
			interaction.setDirected(true);
			// adding interaction
			if(interaction.getE1()!=null && interaction.getE2()!=null){
			sentenceTees.getInteraction().add(interaction);
			}
			else intId--;
		}
		
		logger.info("End adding interactions");
	}
	
	public void setRelations2CorpusAlvis(CorpusTEES corpusTEES) {
		for (CorpusTEES.Document docTEES : corpusTEES.getDocument()) {
			for (Sentence sentenceTEES : docTEES.getSentence()) {

				Section sectionAlvis = sentId2Sections.get(sentenceTEES.getId());
				
				for (Entity entity : sentenceTEES.getEntity()) {
					String goldIds = entity.getGoldIds();
					if (goldIds == null) {
						continue;
					}
					if (!entId2Elements.containsKey(goldIds)) {
						continue;
					}
					if (entId2Elements.containsKey(entity.id)) {
						continue;
					}
					Element elt = entId2Elements.get(goldIds);
					entId2Elements.put(entity.id, elt);
				}
				
				Relation relation = sectionAlvis.ensureRelation(this, this.getRelationName());
				
				
				for (Interaction interaction : sentenceTEES.getInteraction()) {
				    //if (sentenceTEES.getInteraction().get(k).getType().compareToIgnoreCase(this.getRelationName())!=0) continue;
//					logger.info("interaction.getType() = " + interaction.getType());
//					logger.info("interaction.getE1() = " + interaction.getE1());
//					logger.info("interaction.getE2() = " + interaction.getE2());
//					logger.info("entId2Elements.get(interaction.getE1()) = " + entId2Elements.get(interaction.getE1()));
//					logger.info("entId2Elements.get(interaction.getE2()) = " + entId2Elements.get(interaction.getE2()));
					
					Tuple tuple = new Tuple(this, relation);
					tuple.setArgument(this.getLeftRole(), entId2Elements.get(interaction.getE1()));
					tuple.setArgument(this.getRightRole(), entId2Elements.get(interaction.getE2()));
					relation.addTuple(tuple);
//					logger.info("creating tuple :" + tuple.getStringId());
				}
			}
		}
	}
	
	

	/**
	 * feature handlers
	 */

	
	/** 
	 * getter and setter
	 * 
	 */
	
	@Param(nameType = NameType.LAYER)
	public String getTokenLayerName() {
		return tokenLayerName;
	}
	
	public void setTokenLayerName(String tokenLayerName) {
		this.tokenLayerName = tokenLayerName;
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
	

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}

	
	@Param(nameType = NameType.FEATURE)
	public String getNamedEntityTypeFeature() {
		return namedEntityTypeFeature;
	}


	public void setNamedEntityTypeFeature(String namedEntityTypeFeature) {
		this.namedEntityTypeFeature = namedEntityTypeFeature;
	}

	@Param(mandatory = false)
	public String getRelationName() {
		return relationName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	@Param(mandatory = false)
	public String getLeftRole() {
		return leftRole;
	}

	public void setLeftRole(String leftRole) {
		this.leftRole = leftRole;
	}

	@Param(mandatory = false)
	public String getRightRole() {
		return rightRole;
	}

	public void setRightRole(String rightRole) {
		this.rightRole = rightRole;
	}

	public String getPosFeature() {
		return posFeature;
	}

	public void setPosFeature(String posFeature) {
		this.posFeature = posFeature;
	}
	
	
	@Param
	public String getOmitSteps() {
		return omitSteps;
	}

	public void setOmitSteps(String omitSteps) {
		this.omitSteps = omitSteps;
	}
	
	@Param(mandatory=true)
	public InputDirectory getTeesHome() {
		return teesHome;
	}


	public void setTeesHome(InputDirectory tEESHome) {
		teesHome = tEESHome;
	}
}
