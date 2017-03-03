//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2017.03.03 à 05:12:00 PM CET 
//


package org.bibliome.alvisnlp.modules.tees;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CorpusTEES }
     * 
     */
    public CorpusTEES createCorpus() {
        return new CorpusTEES();
    }

    /**
     * Create an instance of {@link CorpusTEES.Document }
     * 
     */
    public CorpusTEES.Document createCorpusDocument() {
        return new CorpusTEES.Document();
    }

    /**
     * Create an instance of {@link CorpusTEES.Document.Sentence }
     * 
     */
    public CorpusTEES.Document.Sentence createCorpusDocumentSentence() {
        return new CorpusTEES.Document.Sentence();
    }

    /**
     * Create an instance of {@link CorpusTEES.Document.Sentence.Analyses }
     * 
     */
    public CorpusTEES.Document.Sentence.Analyses createCorpusDocumentSentenceAnalyses() {
        return new CorpusTEES.Document.Sentence.Analyses();
    }

    /**
     * Create an instance of {@link CorpusTEES.Document.Sentence.Analyses.Parse }
     * 
     */
    public CorpusTEES.Document.Sentence.Analyses.Parse createCorpusDocumentSentenceAnalysesParse() {
        return new CorpusTEES.Document.Sentence.Analyses.Parse();
    }

    /**
     * Create an instance of {@link CorpusTEES.Document.Sentence.Analyses.Tokenization }
     * 
     */
    public CorpusTEES.Document.Sentence.Analyses.Tokenization createCorpusDocumentSentenceAnalysesTokenization() {
        return new CorpusTEES.Document.Sentence.Analyses.Tokenization();
    }

    /**
     * Create an instance of {@link CorpusTEES.Document.Sentence.Entity }
     * 
     */
    public CorpusTEES.Document.Sentence.Entity createCorpusDocumentSentenceEntity() {
        return new CorpusTEES.Document.Sentence.Entity();
    }

    /**
     * Create an instance of {@link CorpusTEES.Document.Sentence.Interaction }
     * 
     */
    public CorpusTEES.Document.Sentence.Interaction createCorpusDocumentSentenceInteraction() {
        return new CorpusTEES.Document.Sentence.Interaction();
    }

    /**
     * Create an instance of {@link CorpusTEES.Document.Sentence.Analyses.Parse.Dependency }
     * 
     */
    public CorpusTEES.Document.Sentence.Analyses.Parse.Dependency createCorpusDocumentSentenceAnalysesParseDependency() {
        return new CorpusTEES.Document.Sentence.Analyses.Parse.Dependency();
    }

    /**
     * Create an instance of {@link CorpusTEES.Document.Sentence.Analyses.Parse.Phrase }
     * 
     */
    public CorpusTEES.Document.Sentence.Analyses.Parse.Phrase createCorpusDocumentSentenceAnalysesParsePhrase() {
        return new CorpusTEES.Document.Sentence.Analyses.Parse.Phrase();
    }

    /**
     * Create an instance of {@link CorpusTEES.Document.Sentence.Analyses.Tokenization.Token }
     * 
     */
    public CorpusTEES.Document.Sentence.Analyses.Tokenization.Token createCorpusDocumentSentenceAnalysesTokenizationToken() {
        return new CorpusTEES.Document.Sentence.Analyses.Tokenization.Token();
    }

}
