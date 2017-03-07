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
	CorpusTEES corpusTEES = new CorpusTEES();

	public void convert(Corpus corpus) {

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

	}


	
	/**
	 * Access the alvis corpus and create all the TEES Sentences
	 * @param documentAlvis
	 * @return
	 */
	private ArrayList<CorpusTEES.Document.Sentence> createTheSentences(Document documentAlvis, Corpus corpus) {
		ArrayList<CorpusTEES.Document.Sentence> sentences = new ArrayList<CorpusTEES.Document.Sentence>();
		
		// elments to access sentences into alvis
		Object sentenceAlvis = new Object();
		
		//*** adding the list of sentences to a document
		CorpusTEES.Document.Sentence sentenceTees = new CorpusTEES.Document.Sentence();
		sentenceTees.setId(value);
		sentenceTees.setText(value);
		sentenceTees.setTail(value);
		sentenceTees.setCharOffset(value);
		// entities
		sentenceTees.getEntity().addAll(createTheEntities(sentenceAlvis, corpus));
		// interaction
		sentenceTees.getInteraction().addAll(createTheInteractions(sentenceAlvis, corpus));
		// set the analyses to the sentence
		sentenceTees.setAnalyses(createAnalyses(sentenceAlvis, corpus));
		// adding
		sentences.add(sentenceTees);
		//*** end adding the list of sentences to a document
		
		return sentences;
	}

	/**
	 * 
	 * @param sentenceAlvis
	 * @return
	 */
	private ArrayList<CorpusTEES.Document.Sentence.Entity> createTheEntities(Object sentenceAlvis, Corpus corpus) {
		ArrayList<CorpusTEES.Document.Sentence.Entity> entities = new ArrayList<CorpusTEES.Document.Sentence.Entity>();
		
		return entities;
	}
	
	
	/**
	 * 
	 * @param sentenceAlvis
	 * @return
	 */
	private ArrayList<CorpusTEES.Document.Sentence.Interaction> createTheInteractions(Object sentenceAlvis, Corpus corpus) {
		ArrayList<CorpusTEES.Document.Sentence.Interaction> interactions =  new ArrayList<CorpusTEES.Document.Sentence.Interaction>();
		
		
		return interactions;
	}

	
	/**
	 * 
	 * @param corpus
	 * @return
	 */
	private CorpusTEES.Document.Sentence.Analyses createAnalyses(Object sentenceAlvis, Corpus corpus) {
		// create an analyse
		CorpusTEES.Document.Sentence.Analyses analysesTees = new CorpusTEES.Document.Sentence.Analyses();
		// set tokenization to the analyse
		analysesTees.setTokenization(createTokenization(corpus));
		// set parsing to the analyse
		analysesTees.setParse(createParse(corpus));

		return analysesTees;
	}

	/**
	 * 
	 * @param corpus
	 * @return
	 */
	private CorpusTEES.Document.Sentence.Analyses.Tokenization createTokenization(Corpus corpus) {
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
	 * 
	 * @param corpus
	 * @return
	 */
	private CorpusTEES.Document.Sentence.Analyses.Parse createParse(Corpus corpus) {
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
