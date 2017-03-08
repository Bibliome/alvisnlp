package org.bibliome.alvisnlp.modules.tees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.bibliome.alvisnlp.modules.tees.CorpusTEES;
import org.bibliome.alvisnlp.modules.tees.CorpusTEES.Document.Sentence;
import org.bibliome.alvisnlp.modules.tees.CorpusTEES.Document.Sentence.Analyses;
import org.bibliome.alvisnlp.modules.tees.CorpusTEES.Document.Sentence.Analyses.Parse.Dependency;
import org.bibliome.alvisnlp.modules.tees.CorpusTEES.Document.Sentence.Analyses.Tokenization.Token;
import org.bibliome.alvisnlp.modules.tees.CorpusTEES.Document.Sentence.Entity;
import org.bibliome.alvisnlp.modules.tees.CorpusTEES.Document.Sentence.Interaction;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;



public class Corpus2InteractionXML {

	
	public CorpusTEES createTheCorpus(Corpus corpus) {
		CorpusTEES corpusTEES = new CorpusTEES(); 
		
		Iterator<Document> documentIt = corpus.documentIterator();

		while (documentIt.hasNext()) {
			// access an alvis document
			Document documentAlvis = documentIt.next();

			// create a TEES document
			CorpusTEES.Document documentTees = new CorpusTEES.Document();
			// adding the id of the TEES document
			documentTees.setId(documentAlvis.getId());
			// set all sentences the TEES document
			documentTees.getSentence().addAll(createTheSentences(documentAlvis, corpus));
			// adding the document to the TEES corpus
			corpusTEES.getDocument().add(documentTees);
		}
		 
		return corpusTEES;
	}


	
	/**
	 * Access the alvis corpus and create all the TEES Sentences
	 * @param documentAlvis
	 * @return
	 */
	private ArrayList<CorpusTEES.Document.Sentence> createTheSentences(Document documentAlvis, Corpus corpus) {
		ArrayList<CorpusTEES.Document.Sentence> sentences = new ArrayList<CorpusTEES.Document.Sentence>();
		
		// elments to access sentences into alvis
		ArrayList<Object> sentenceAlvis = new ArrayList<Object>();
		
		// for *** adding the list of sentences to a document
		for (int i = 0; i < sentenceAlvis.size(); i++) {// for *** adding the list of sentences to a document
			
		CorpusTEES.Document.Sentence sentenceTees = new CorpusTEES.Document.Sentence();
		sentenceTees.setId("sentenceAlvis.get(i).id"); // sentence id
		sentenceTees.setText("sentenceAlvis.get(i).text"); // sentence text
		sentenceTees.setTail("sentenceAlvis.get(i).tail"); // sentence tail
		sentenceTees.setCharOffset("sentenceAlvis.get(i).charOffset"); // sentence charOffset
		// entities
		sentenceTees.getEntity().addAll(createTheEntities(sentenceAlvis.get(i), corpus)); // entities of the sentence
		// interactions
		sentenceTees.getInteraction().addAll(createTheInteractions(sentenceAlvis.get(i), corpus)); // interaction of the sentence
		// set the analyses to the sentence
		sentenceTees.setAnalyses(createTheAnalyses(sentenceAlvis.get(i), corpus)); // analysis of the sentence
		// adding
		sentences.add(sentenceTees);
		}//*** end for adding the list of sentences to a document
		
	
		
		return sentences;
	}

	/**
	 * Access the alvis corpus and create all the entities of a sentence
	 * @param sentenceAlvis
	 * @return
	 */
	private ArrayList<CorpusTEES.Document.Sentence.Entity> createTheEntities(Object sentenceAlvis, Corpus corpus) {
		ArrayList<CorpusTEES.Document.Sentence.Entity> entities = new ArrayList<CorpusTEES.Document.Sentence.Entity>();
		
		return entities;
	}
	
	
	/**
	 * Access the alvis corpus and create all the interactions of a sentence
	 * @param sentenceAlvis
	 * @return
	 */
	private ArrayList<CorpusTEES.Document.Sentence.Interaction> createTheInteractions(Object sentenceAlvis, Corpus corpus) {
		ArrayList<CorpusTEES.Document.Sentence.Interaction> interactions =  new ArrayList<CorpusTEES.Document.Sentence.Interaction>();
		
		
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
	 * Access the alvis corpus and create the tokenization to put into the analyses of a sentence
	 * @param corpus
	 * @return
	 */
	private CorpusTEES.Document.Sentence.Analyses.Tokenization createTheTokenization(Corpus corpus) {
		CorpusTEES.Document.Sentence.Analyses.Tokenization tokenization = new CorpusTEES.Document.Sentence.Analyses.Tokenization();
		tokenization.setProteinNameSplitter(false);
		tokenization.setSource("value");
		tokenization.setTokenizer("value");

		// set the list of tokens
		CorpusTEES.Document.Sentence.Analyses.Tokenization.Token token = new CorpusTEES.Document.Sentence.Analyses.Tokenization.Token();
		token.setId("value");
		token.setHeadScore(0);
		token.setPOS("value");
		token.setText("value");
		token.setCharOffset("value");
		tokenization.getToken().add(token);
		// end set the list of tokens

		return tokenization;
	}
	
	
	
	/**
	 * Access the alvis corpus and create the parse to put into the analyses of a sentence
	 * @param corpus
	 * @return
	 */
	private CorpusTEES.Document.Sentence.Analyses.Parse createTheParse(Corpus corpus) {
		CorpusTEES.Document.Sentence.Analyses.Parse parse = new CorpusTEES.Document.Sentence.Analyses.Parse();
		parse.setParser("value");
		parse.setPennstring("value");
		parse.setProteinNameSplitter(false);
		parse.setSource("value");
		parse.setStanford("value");
		parse.setTokenizer("value");

		// add the list of dependency to the parse
		CorpusTEES.Document.Sentence.Analyses.Parse.Dependency dependency = new CorpusTEES.Document.Sentence.Analyses.Parse.Dependency();
		dependency.setId("value");
		dependency.setT1("value");
		dependency.setT2("value");
		dependency.setType("value");
		parse.getDependency().add(dependency);
		// end add list of dependency to the parse

		// set the list of prhrase to the parse
		CorpusTEES.Document.Sentence.Analyses.Parse.Phrase phrase = new CorpusTEES.Document.Sentence.Analyses.Parse.Phrase();
		phrase.setId("value");
		phrase.setBegin(0);
		phrase.setEnd(0);
		phrase.setType("value");
		phrase.setCharOffset("value");
		parse.getPhrase().add(phrase);
		// end set the list of phrase to the parse

		return parse;
	}

}
