package org.bibliome.alvisnlp.modules.tees;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.Param;

public class TEESPredict extends SectionModule<SectionResolvedObjects> {
	private String tokenLayerName = DefaultNames.getWordLayer();
	private String sentenceLayerName = DefaultNames.getSentenceLayer();

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

	public void setTokenLayerName(String tokenLayerName) {
		this.tokenLayerName = tokenLayerName;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}

//	private void iteratorSnippet(ProcessingContext<Corpus> ctx, Corpus corpus) {
//		Logger logger = getLogger(ctx);
//		EvaluationContext evalCtx = new EvaluationContext(logger);
//
//		// iteration des documents du corpus
//		for (Document doc : Iterators.loop(documentIterator(evalCtx, corpus))) {
//			// faire qqch avec doc, par ex
//			doc.getId();
//			doc.getLastFeature("DOCFEATUREKEY");
//			// iteration des sections du document
//			for (Section sec : Iterators.loop(sectionIterator(evalCtx, doc))) {
//				// faire qqch avec sec, par ex
//				sec.getName();
//				sec.getLastFeature("SECFEATUREKEY");
//				sec.getDocument();
//				// iteration des annotations d'un layer dans une section
//				Layer layer = sec.ensureLayer("LAYERNAME");
//				for (Annotation a : layer) {
//					// faire qqch avec a, par ex
//					a.getStart();
//					a.getEnd();
//					a.getLength();
//					a.getLastFeature("ANNOTATIONFEATUREKEY");
//					a.getSection();
//				}
//
//				// iteration des sentences dans une section
//				for (Layer sentLayer : sec.getSentences(getTokenLayerName(), getSentenceLayerName())) {
//					Annotation sent = sentLayer.getSentenceAnnotation();
//					// faire qqch avec sent
//					// iteration des mots dans une sentence
//					for (Annotation token : sentLayer) {
//						// faire qqch avec token
//					}
//				}
//			}
//		}
//
//		// on peut iterer les sections sans passer par les documents
//		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
//			//
//		}
//	}

	/**
	 * Access the alvis corpus and create the TEES Corpus and documents
	 * @param ctx
	 * @param corpusAlvis
	 * @return
	 */
	public CorpusTEES createTheTeesCorpus(ProcessingContext<Corpus> ctx, Corpus corpusAlvis) {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);

		CorpusTEES corpusTEES = new CorpusTEES();

		// iteration des documents du corpus
		for (Document documentAlvis : Iterators.loop(documentIterator(evalCtx, corpusAlvis))) {
			// create a TEES document
			CorpusTEES.Document documentTees = new CorpusTEES.Document();
			// adding the id of the TEES document
			documentTees.setId(documentAlvis.getId());
			// set all sentences the TEES document /!\ instruction to be to be changed
			documentTees.getSentence().addAll(createTheTeesSentences(sectionIterator(evalCtx, documentAlvis), corpusAlvis));
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
	private ArrayList<CorpusTEES.Document.Sentence> createTheTeesSentences(Iterator<Section> sectionIt, Corpus corpus) {
		ArrayList<CorpusTEES.Document.Sentence> sentences = new ArrayList<CorpusTEES.Document.Sentence>();
		
		// loop on sections
		for (Section sectionAlvis : Iterators.loop(sectionIt)) {
			// loop on sentences
			for (Layer sentLayer : sectionAlvis.getSentences(getTokenLayerName(), getSentenceLayerName())) {
				// get an alvis sentence
				Annotation sentenceAlvis = sentLayer.getSentenceAnnotation();
				// mapping to a tees sentence
				CorpusTEES.Document.Sentence sentenceTees = new CorpusTEES.Document.Sentence();
				sentenceTees.setId(sentenceAlvis.getStringId());
				sentenceTees.setText(sentenceAlvis.getForm()); // is it the text content  ?
				sentenceTees.setCharOffset(sentenceAlvis.getStart() + "-" + sentenceAlvis.getEnd());
				// sentenceTees.setTail(sentenceAlvis.??));
				
	

				// add all the entities, /!\ instruction to be to be changed
				sentenceTees.getEntity().addAll(createTheTeesEntities(sentLayer, corpus));
				
				// The tees interactions of this sentence /!\ instruction to be to be changed
				// sentenceTees.getInteraction().addAll(createTheInteractions(sentenceAlvis.get(i), corpus)); 
				
				// the tees analyses of this sentence /!\ instruction to be to be changed
				// sentenceTees.setAnalyses(createTheAnalyses(sentenceAlvis.get(i), corpus)); 
				
				// adding
				sentences.add(sentenceTees);
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
	private ArrayList<CorpusTEES.Document.Sentence.Entity> createTheTeesEntities(Layer entitylayer, Corpus corpus) {
		ArrayList<CorpusTEES.Document.Sentence.Entity> entities = new ArrayList<CorpusTEES.Document.Sentence.Entity>();

		// loop on entities
		for (Annotation entityAlvis : entitylayer) {
			CorpusTEES.Document.Sentence.Entity entityTees = new CorpusTEES.Document.Sentence.Entity();
			entityTees.setId(entityAlvis.getStringId());
			entityTees.setCharOffset(entityAlvis.getStart() + "-" + entityAlvis.getEnd());
			entityTees.setOrigOffset(entityAlvis.getStart() + "-" + entityAlvis.getEnd()); // is it, I'm not sure
			// entityTees.setHeadOffset(???);
			entityTees.setText(entityAlvis.getForm());
			entityTees.setType(entityAlvis.getLastFeature("ANNOTATIONFEATUREKEY"));
			// entityTees.setType(token.);
			
		}
		return entities;
	}

	/**
	 * Access the alvis corpus and create all the interactions of a sentence
	 * 
	 * @param sentenceAlvis
	 * @return
	 */
	private ArrayList<CorpusTEES.Document.Sentence.Interaction> createTheInteractions(Object sentenceAlvis,
			Corpus corpus) {
		ArrayList<CorpusTEES.Document.Sentence.Interaction> interactions = new ArrayList<CorpusTEES.Document.Sentence.Interaction>();

		return interactions;
	}

	/**
	 * Access the alvis corpus and create all the interactions of a sentence
	 * 
	 * @param corpus
	 * @return
	 */
	private CorpusTEES.Document.Sentence.Analyses createTheAnalyses(Object sentenceAlvis, Corpus corpus) {
		// create an analyse
		CorpusTEES.Document.Sentence.Analyses analysesTees = new CorpusTEES.Document.Sentence.Analyses();
		// set tokenization to the analyse
		analysesTees.setTokenization(createTheTokenization(corpus));
		// set parsing to the analyse
		analysesTees.setParse(createTheParse(corpus));

		return analysesTees;
	}

	/**
	 * Access the alvis corpus and create the tokenization to put into the
	 * analyses of a sentence
	 * 
	 * @param corpus
	 * @return
	 */
	private CorpusTEES.Document.Sentence.Analyses.Tokenization createTheTokenization(Corpus corpus) {
		CorpusTEES.Document.Sentence.Analyses.Tokenization tokenization = new CorpusTEES.Document.Sentence.Analyses.Tokenization();
		tokenization.setProteinNameSplitter(value);
		tokenization.setSource(value);
		tokenization.setTokenizer(value);

		// set the list of tokens
		CorpusTEES.Document.Sentence.Analyses.Tokenization.Token token = new CorpusTEES.Document.Sentence.Analyses.Tokenization.Token();
		token.setId(value);
		token.setHeadScore(value);
		token.setPOS(value);
		token.setText(value);
		token.setCharOffset(value);
		tokenization.getToken().add(token);
		// end set the list of tokens

		return tokenization;
	}

	/**
	 * Access the alvis corpus and create the parse to put into the analyses of
	 * a sentence
	 * 
	 * @param corpus
	 * @return
	 */
	private CorpusTEES.Document.Sentence.Analyses.Parse createTheParse(Corpus corpus) {
		CorpusTEES.Document.Sentence.Analyses.Parse parse = new CorpusTEES.Document.Sentence.Analyses.Parse();
		parse.setParser(value);
		parse.setPennstring(value);
		parse.setProteinNameSplitter(value);
		parse.setSource(value);
		parse.setStanford(value);
		parse.setTokenizer(value);

		// add the list of dependency to the parse
		CorpusTEES.Document.Sentence.Analyses.Parse.Dependency dependency = new CorpusTEES.Document.Sentence.Analyses.Parse.Dependency();
		dependency.setId(value);
		dependency.setT1(value);
		dependency.setT2(value);
		dependency.setType(value);
		parse.getDependency().add(dependency);
		// end add list of dependency to the parse

		// set the list of prhrase to the parse
		CorpusTEES.Document.Sentence.Analyses.Parse.Phrase phrase = new CorpusTEES.Document.Sentence.Analyses.Parse.Phrase();
		phrase.setId(value);
		phrase.setBegin(value);
		phrase.setEnd(value);
		phrase.setType(value);
		phrase.setCharOffset(value);
		parse.getPhrase().add(phrase);
		// end set the list of phrase to the parse

		return parse;
	}
}
