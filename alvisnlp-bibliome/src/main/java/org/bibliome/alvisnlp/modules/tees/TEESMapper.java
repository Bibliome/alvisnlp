package org.bibliome.alvisnlp.modules.tees;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.tees.CorpusTEES.Document.Sentence;
import org.bibliome.alvisnlp.modules.tees.CorpusTEES.Document.Sentence.Entity;
import org.bibliome.alvisnlp.modules.tees.CorpusTEES.Document.Sentence.Interaction;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DownCastElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.MultiMapping;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

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
	
	private MultiMapping schema;
	
	private String omitSteps = "SPLIT-SENTENCES,NER";
	private InputDirectory teesHome;

	private final Map<String,CorpusTEES> corpora = new LinkedHashMap<String,CorpusTEES>();
	private final Map<String,Section> sentId2Sections =  new LinkedHashMap<String,Section>();
	private final Map<String,Element> entId2Elements =  new LinkedHashMap<String,Element>();
	private final Map<String,String> origId2teesId = new LinkedHashMap<String,String>();
	
	
	/***
	 * 
	 * 
	 * Mapping adds
	 */
	protected abstract String getSet(Document doc);
	
	protected CorpusTEES getCorpus(String set) {
		if (corpora.containsKey(set)) {
			return corpora.get(set);
		}
		CorpusTEES result = new CorpusTEES();
		corpora.put(set, result);
		return result;
	}

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

		logger.info("Creating TEES input files");
		for (Document documentAlvis : Iterators.loop(documentIterator(evalCtx, corpusAlvis))) {
			String set = getSet(documentAlvis);
//			if(corpora.get(set) == null) {
//				corpora.put(set, new CorpusTEES());
//			}
//			CorpusTEES corpus = corpora.get(set);
			CorpusTEES corpus = getCorpus(set);
			
			CorpusTEES.Document documentTees = new CorpusTEES.Document();		
			documentTees.setId("ALVIS.d" + corpus.getDocument().size());
//			logger.info("adding the document" + documentTees.getId() + " to " + set + " Set");
			Iterator<Section> alvisSectionsIterator = sectionIterator(evalCtx, documentAlvis);
			createTheTeesSentences(documentTees.getSentence(), documentTees.getId(), alvisSectionsIterator, ctx);
//			logger.info("number of sentences " + documentTees.getSentence().size());
			corpus.getDocument().add(documentTees);
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
//		Logger logger = getLogger(ctx);

		for (Section sectionAlvis : Iterators.loop(alvisSectionsIterator)) {
			for (Layer sentLayer : sectionAlvis.getSentences(getTokenLayerName(), getSentenceLayerName())) {
				Annotation sentenceAlvis = sentLayer.getSentenceAnnotation();
				if(sentenceAlvis == null) {
					continue;
				}
					
				// create a Tees sentence
				CorpusTEES.Document.Sentence sentenceTees = new CorpusTEES.Document.Sentence();
				sentenceTees.setId(docId + ".s" + sentId++);
//				logger.info("creating the sentence : " + sentenceTees.getId());
				sentenceTees.setText(sentenceAlvis.getForm()); 
				sentenceTees.setCharOffset(sentenceAlvis.getStart() + "-" + sentenceAlvis.getEnd());
				
				// add the TEES entities
//				logger.info("creating the TEES entities");
				Layer alvisEntitiesLayer = sectionAlvis.ensureLayer(getNamedEntityLayerName());
				createTheTeesEntities(sentenceTees, sentenceTees.getId(), sentenceAlvis, alvisEntitiesLayer, ctx);

				// add the TEES interactions
//				logger.info("creating the TEES interactions ");
				createTheInteractions(sentenceTees, sentenceTees.getId(), sentenceAlvis, ctx);
				
				// add the set sentence
				sentences.add(sentenceTees);
				
				//sent2secId.put(sentenceTees.getId(), sectionAlvis.getStringId());
				sentId2Sections.put(sentenceTees.getId(), sectionAlvis);
				
				// add the analyses TODO
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
	private void createTheTeesEntities(CorpusTEES.Document.Sentence sentenceTees, String sentId, Annotation sentenceAlvis, Layer alvisEntitiesLayer, @SuppressWarnings("unused") ProcessingContext<Corpus> ctx) {
		int entId = 0;
//		Logger logger = getLogger(ctx);

		// loop on entities
		for (Annotation entityAlvis : alvisEntitiesLayer) {
			if(!sentenceAlvis.includes(entityAlvis)) {
				continue;
			}
			// create a tees entity 
			Entity entityTees = new CorpusTEES.Document.Sentence.Entity();
			entityTees.setId(sentId + ".e" + entId++);
//			logger.info("creating the entity " + entityTees.getId());
			entityTees.setOrigId(entityAlvis.getStringId());
			entityTees.setCharOffset((entityAlvis.getStart()- sentenceAlvis.getStart()) + "-" + (entityAlvis.getEnd()-sentenceAlvis.getStart()));
			entityTees.setOrigOffset(entityAlvis.getStart() + "-" + entityAlvis.getEnd()); 
			entityTees.setText(entityAlvis.getForm());
			entityTees.setType(entityAlvis.getLastFeature(this.getNamedEntityTypeFeature()));
			entityTees.setGiven(true);
			// add the entity
			sentenceTees.getEntity().add(entityTees);
			entId2Elements.put(entityTees.getId(), entityAlvis);
			origId2teesId.put(entityTees.getOrigId(), entityTees.getId());
		}
	}

	
	private void createTheInteractions(CorpusTEES.Document.Sentence sentenceTees, String sentId, Annotation sentenceAlvis, ProcessingContext<Corpus> ctx) {
		int intId = 0;
		Logger logger = getLogger(ctx);
		
		Section sec = sentenceAlvis.getSection();

		for (Map.Entry<String,String[]> e : schema.entrySet()) {
			String relName = e.getKey();
			if (!sec.hasRelation(relName)) {
				continue;
			}
			Relation rel = sec.getRelation(relName);
			String[] roles = e.getValue();
			for (Tuple t : rel.getTuples()) {
				if (!(t.hasArgument(roles[0]) && t.hasArgument(roles[1]))) {
					logger.warning("tuple " + t + " lacks argument");
					continue;
				}
				Element arg1 = t.getArgument(roles[0]);
				Element arg2 = t.getArgument(roles[1]);
				Annotation ann1 = DownCastElement.toAnnotation(arg1);
				Annotation ann2 = DownCastElement.toAnnotation(arg2);

				// is the sentence contains the argument annotations
				if(!(sentenceAlvis.includes(ann1) && sentenceAlvis.includes(ann2))) {
					continue;
				}

				// creating interaction
				CorpusTEES.Document.Sentence.Interaction interaction = new CorpusTEES.Document.Sentence.Interaction();
				interaction.setId(sentId + ".i" + intId++);
//				logger.info("creating interaction " + t.getRoles().toString());	
				interaction.setE1(origId2teesId.get(arg1.getStringId()));
				interaction.setE2(origId2teesId.get(arg2.getStringId()));
				interaction.setType(relName);
				interaction.setOrigId(t.getStringId());
				interaction.setDirected(true);
				sentenceTees.getInteraction().add(interaction);
			}
		}
	}
	
	public void setRelations2CorpusAlvis(CorpusTEES corpusTEES) throws ProcessingException {
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
				
				for (Interaction interaction : sentenceTEES.getInteraction()) {
					String type = interaction.getType();
					if (!schema.containsKey(type)) {
						processingException("TEES predicted something not in the schema: " + type);
					}
					String[] roles = schema.get(type);
					Relation rel = sectionAlvis.ensureRelation(this, type);
					Tuple tuple = new Tuple(this, rel);
					tuple.setArgument(roles[0], entId2Elements.get(interaction.getE1()));
					tuple.setArgument(roles[1], entId2Elements.get(interaction.getE2()));
					rel.addTuple(tuple);
				}
			}
		}
	}
	
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

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}
	
	@Param(nameType = NameType.LAYER)
	public String getNamedEntityLayerName() {
		return namedEntityLayerName;
	}

	public void setNamedEntityLayerName(String namedEntityLayerName) {
		this.namedEntityLayerName = namedEntityLayerName;
	}
	
	@Param(nameType = NameType.FEATURE)
	public String getNamedEntityTypeFeature() {
		return namedEntityTypeFeature;
	}

	public void setNamedEntityTypeFeature(String namedEntityTypeFeature) {
		this.namedEntityTypeFeature = namedEntityTypeFeature;
	}
	
	@Param
	public MultiMapping getSchema() {
		return schema;
	}

	public void setSchema(MultiMapping schema) {
		this.schema = schema;
	}

	@Param
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
	
	@Param
	public InputDirectory getTeesHome() {
		return teesHome;
	}

	public void setTeesHome(InputDirectory tEESHome) {
		teesHome = tEESHome;
	}
}
