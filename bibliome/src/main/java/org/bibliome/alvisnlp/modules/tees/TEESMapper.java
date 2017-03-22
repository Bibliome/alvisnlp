package org.bibliome.alvisnlp.modules.tees;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.files.ExecutableFile;
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
import alvisnlp.module.types.Mapping;

/**
 * 
 * @author mba
 *
 */

public abstract class TEESMapper extends SectionModule<SectionResolvedObjects> implements TupleCreator {

	// layer and features
	private String tokenLayerName = DefaultNames.getWordLayer();
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String pos = DefaultNames.getPosTagFeature();
	private String namedEntityLayerName = null;
	private String neFeatureName = DefaultNames.getNamedEntityTypeFeature();
	
	// relation to predict
	private String relationName = null;
	private String relationRole1 = null;
	private String relationRole2 = null;
	
	// corpus params
	private String corporaType = "cName";
	protected Map<String, CorpusTEES> corpora = new HashMap<String, CorpusTEES>();
	protected String defaultKey = "default";
	
	// execution params
	private ExecutableFile executable;
	private String omitSteps = "PREPROCESS=SPLIT-SENTENCES,NE";
	private String model;
	private InputDirectory workDir = null;
	
	
	// Link memories
	private Map<String, Section> sentId2Sections =  new HashMap<String, Section>();
	private Map<String, Element> entId2Elements =  new HashMap<String, Element>();
	
	
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
	public void createTheTeesCorpus(ProcessingContext<Corpus> ctx, Corpus corpusAlvis) {
		int docId = 0;
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);

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
			
			// splitting the documents according to their feature key
			String c = documentAlvis.getLastFeature(this.getCorporaType());
			if(c ==  null){
				if(corpora.get(this.defaultKey) == null) this.corpora.put(this.defaultKey, new CorpusTEES());
				this.corpora.get(this.defaultKey).getDocument().add(documentTees);
			}
			else{
				this.corpora.get(c).getDocument().add(documentTees);
			}
		}
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
				logger.info("creating the TEES interactions ");
				Relation alvisRelationsCollection = sectionAlvis.getRelation(this.getRelationName());
				createTheInteractions(sentenceTees, sentenceTees.getId(), sentenceAlvis, alvisRelationsCollection, corpus, ctx);
				
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
			entityTees.setType(entityAlvis.getLastFeature(this.getNeFeatureName()));
			// set given
			if(this.getNamedEntityLayerName()!=null) entityTees.setGiven(true);
			else entityTees.setGiven(false);
			// add the entity
			sentenceTees.getEntity().add(entityTees);
			entId2Elements.put(entityTees.getId(), entityAlvis);
			}
		}
	}

	
	private void createTheInteractions(CorpusTEES.Document.Sentence sentenceTees, String sentId, Annotation sentenceAlvis, Relation allRelations, Corpus corpus, ProcessingContext<Corpus> ctx) {
		int intId = 0;
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
			
		// loop  relations
		if(allRelations!=null) for (Tuple r : allRelations.getTuples()) {

			// getting the argument from roles
			Element ag1 = r.getArgument(this.getRelationRole1());			
			Element ag2 = r.getArgument(this.getRelationRole2());
			
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
			sentenceTees.getInteraction().add(interaction);
		}
		
		logger.info("End adding interactions");
	}
	
	
	
	public void setRelations2CorpusAlvis(CorpusTEES corpusTEES, Corpus corpusAlvis, ProcessingContext<Corpus> ctx){
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);

		for (int i = 0; i < corpusTEES.getDocument().size(); i++) {
			for (int j = 0; j < corpusTEES.getDocument().get(i).getSentence().size(); j++) {

				Section sectionAlvis = sentId2Sections.get(corpusTEES.getDocument().get(i).getSentence().get(j).getId());
				
				
				Relation relation = sectionAlvis.ensureRelation(this, this.getRelationName());
				logger.info("getting relation :" + relation.getName());
				
				CorpusTEES.Document.Sentence sentenceTEES = corpusTEES.getDocument().get(i).getSentence().get(j);
				
				
				for (int k = 0; k < sentenceTEES.getInteraction().size(); k++) {
					if (sentenceTEES.getInteraction().get(k).getType().compareToIgnoreCase(this.getRelationName())!=0) continue;
					Tuple tuple = new Tuple(this, relation);
					tuple.setArgument(this.getRelationRole1(), entId2Elements.get(sentenceTEES.getInteraction().get(k).getE1()));
					tuple.setArgument(this.getRelationRole2(), entId2Elements.get(sentenceTEES.getInteraction().get(k).getE2()));
					relation.addTuple(tuple);
					logger.info("creating tuple :" + tuple.getStringId());
				}
			}
		}
	}
	
	

	/**
	 * feature handlers
	 */

	@Override
	public Mapping getConstantTupleFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConstantTupleFeatures(Mapping constantRelationFeatures) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mapping getConstantRelationFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConstantRelationFeatures(Mapping constantRelationFeatures) {
		// TODO Auto-generated method stub
		
	}
	
	
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
	public String getNeFeatureName() {
		return neFeatureName;
	}


	public void setNeFeatureName(String namedEntityTypeFeatureName) {
		this.neFeatureName = namedEntityTypeFeatureName;
	}

	@Param(mandatory = false)
	public String getRelationName() {
		return relationName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	@Param(mandatory = false)
	public String getRelationRole1() {
		return relationRole1;
	}

	public void setRelationRole1(String arg1) {
		this.relationRole1 = arg1;
	}

	@Param(mandatory = false)
	public String getRelationRole2() {
		return relationRole2;
	}

	public void setRelationRole2(String arg2) {
		this.relationRole2 = arg2;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}
	

	public String getCorporaType() {
		return corporaType;
	}

	public void setCorporaType(String corporaType) {
		this.corporaType = corporaType;
	}
	
	@Param(mandatory = false)
	public String getOmitSteps() {
		return omitSteps;
	}

	public void setOmitSteps(String omitSteps) {
		this.omitSteps = omitSteps;
	}
	
	
	@Param
	public ExecutableFile getExecutable() {
		return executable;
	}

	public void setExecutable(ExecutableFile executable) {
		this.executable = executable;
	}
	
	@Param
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	
	
	public InputDirectory getWorkDir() {
		return workDir;
	}

	public void setWorkDir(InputDirectory workDir) {
		this.workDir = workDir;
	}

	void simulatingCorpora(Corpus corpusAlvis, ProcessingContext<Corpus> ctx){
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		int it =0;

		// loop on documents
		logger.info("creating the TEES documents ");
		for (Document documentAlvis : Iterators.loop(documentIterator(evalCtx, corpusAlvis))) {
		
		if(it < 2*corpusAlvis.countDocuments()/4) documentAlvis.addFeature(this.corporaType, "train");
		else if(it < 3*corpusAlvis.countDocuments()/4) documentAlvis.addFeature(this.corporaType, "dev");
		else documentAlvis.addFeature(this.corporaType, "test");
		}
	}
	
}
