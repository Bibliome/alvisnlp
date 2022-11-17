package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees.CorpusTEES.Document.Sentence;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees.CorpusTEES.Document.Sentence.Entity;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees.CorpusTEES.Document.Sentence.Interaction;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DownCastElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.MultiMapping;
import fr.inra.maiage.bibliome.util.Iterators;

abstract class TEESMapperExternalHandler<T extends TEESMapper> extends ExternalHandler<Corpus,T> {
	private final Map<String,CorpusTEES> corpora = new LinkedHashMap<String,CorpusTEES>();
	private final Map<String,Section> sentId2Sections =  new LinkedHashMap<String,Section>();
	private final Map<String,Element> entId2Elements =  new LinkedHashMap<String,Element>();
	private final Map<String,String> origId2teesId = new LinkedHashMap<String,String>();

	protected TEESMapperExternalHandler(ProcessingContext<Corpus> processingContext, T module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	protected void createTheTeesCorpus() {
		Logger logger = getLogger();
		EvaluationContext evalCtx = new EvaluationContext(logger);
		TEESMapper owner = getModule();
		logger.info("Creating TEES input files");
		for (Document documentAlvis : Iterators.loop(owner.documentIterator(evalCtx, getAnnotable()))) {
			String set = getSet(documentAlvis);
			CorpusTEES corpus = getCorpus(set);
			
			CorpusTEES.Document documentTees = new CorpusTEES.Document();		
			documentTees.setId("ALVIS.d" + corpus.getDocument().size());
			Iterator<Section> alvisSectionsIterator = owner.sectionIterator(evalCtx, documentAlvis);
			createTheTeesSentences(documentTees.getSentence(), documentTees.getId(), alvisSectionsIterator);
			corpus.getDocument().add(documentTees);
		}
	}

	protected abstract String getSet(Document doc);
	
	protected CorpusTEES getCorpus(String set) {
		if (corpora.containsKey(set)) {
			return corpora.get(set);
		}
		CorpusTEES result = new CorpusTEES();
		corpora.put(set, result);
		return result;
	}

	private List<CorpusTEES.Document.Sentence> createTheTeesSentences(List<CorpusTEES.Document.Sentence> sentences, String docId, Iterator<Section> alvisSectionsIterator) {
		int sentId = 0;
		TEESMapper owner = getModule();
		for (Section sectionAlvis : Iterators.loop(alvisSectionsIterator)) {
			for (Layer sentLayer : sectionAlvis.getSentences(owner.getTokenLayer(), owner.getSentenceLayer())) {
				Annotation sentenceAlvis = sentLayer.getSentenceAnnotation();
				if(sentenceAlvis == null) {
					continue;
				}
					
				// create a Tees sentence
				CorpusTEES.Document.Sentence sentenceTees = new CorpusTEES.Document.Sentence();
				sentenceTees.setId(docId + ".s" + sentId++);
				sentenceTees.setText(sentenceAlvis.getForm()); 
				sentenceTees.setCharOffset(sentenceAlvis.getStart() + "-" + sentenceAlvis.getEnd());
				
				// add the TEES entities
//				logger.info("creating the TEES entities");
				Layer alvisEntitiesLayer = sectionAlvis.ensureLayer(owner.getNamedEntityLayer());
				createTheTeesEntities(sentenceTees, sentenceTees.getId(), sentenceAlvis, alvisEntitiesLayer);

				// add the TEES interactions
//				logger.info("creating the TEES interactions ");
				createTheInteractions(sentenceTees, sentenceTees.getId(), sentenceAlvis);
				
				// add the set sentence
				sentences.add(sentenceTees);
				
				//sent2secId.put(sentenceTees.getId(), sectionAlvis.getStringId());
				sentId2Sections.put(sentenceTees.getId(), sectionAlvis);
				
				// add the analyses TODO
			}
		}
		return sentences;
	}

	private void createTheTeesEntities(CorpusTEES.Document.Sentence sentenceTees, String sentId, Annotation sentenceAlvis, Layer alvisEntitiesLayer) {
		int entId = 0;
		TEESMapper owner = getModule();
		// loop on entities
		for (Annotation entityAlvis : alvisEntitiesLayer) {
			if(!sentenceAlvis.includes(entityAlvis)) {
				continue;
			}
			// create a tees entity 
			Entity entityTees = new CorpusTEES.Document.Sentence.Entity();
			entityTees.setId(sentId + ".e" + entId++);
			entityTees.setOrigId(entityAlvis.getStringId());
			entityTees.setCharOffset((entityAlvis.getStart()- sentenceAlvis.getStart()) + "-" + (entityAlvis.getEnd()-sentenceAlvis.getStart()));
			entityTees.setOrigOffset(entityAlvis.getStart() + "-" + entityAlvis.getEnd()); 
			entityTees.setText(entityAlvis.getForm());
			entityTees.setType(entityAlvis.getLastFeature(owner.getNamedEntityTypeFeature()));
			entityTees.setGiven(true);
			// add the entity
			sentenceTees.getEntity().add(entityTees);
			entId2Elements.put(entityTees.getId(), entityAlvis);
			origId2teesId.put(entityTees.getOrigId(), entityTees.getId());
		}
	}

	private void createTheInteractions(CorpusTEES.Document.Sentence sentenceTees, String sentId, Annotation sentenceAlvis) {
		int intId = 0;
		Logger logger = getLogger();
		
		Section sec = sentenceAlvis.getSection();

		for (Map.Entry<String,String[]> e : getModule().getSchema().entrySet()) {
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
				MultiMapping schema = getModule().getSchema();
				for (Interaction interaction : sentenceTEES.getInteraction()) {
					String type = interaction.getType();
					if (!schema.containsKey(type)) {
						throw new ProcessingException("TEES predicted something not in the schema: " + type);
					}
					String[] roles = schema.get(type);
					Relation rel = sectionAlvis.ensureRelation(getModule(), type);
					Tuple tuple = new Tuple(getModule(), rel);
					tuple.setArgument(roles[0], entId2Elements.get(interaction.getE1()));
					tuple.setArgument(roles[1], entId2Elements.get(interaction.getE2()));
					rel.addTuple(tuple);
				}
			}
		}
	}
	
	protected File getTEESPreprocessingScript() {
		return new File(getModule().getTeesHome(), "preprocess.py");
	}
}
