//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2017.03.03 à 05:12:00 PM CET 
//


package org.bibliome.alvisnlp.modules.tees;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour anonymous complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="document" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="sentence" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="entity" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="given" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                                     &lt;attribute name="headOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="origId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="origOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="interaction" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="directed" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                                     &lt;attribute name="e1" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="e2" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="event" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                                     &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="origId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="analyses">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="tokenization">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="token" maxOccurs="unbounded">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;attribute name="POS" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                         &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                         &lt;attribute name="headScore" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                                         &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                         &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                       &lt;/restriction>
 *                                                     &lt;/complexContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                               &lt;/sequence>
 *                                               &lt;attribute name="ProteinNameSplitter" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                                               &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="tokenizer" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="parse">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="dependency" maxOccurs="unbounded">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                         &lt;attribute name="t1" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                         &lt;attribute name="t2" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                         &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                       &lt;/restriction>
 *                                                     &lt;/complexContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="phrase" maxOccurs="unbounded">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;attribute name="begin" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                                         &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                         &lt;attribute name="end" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                                         &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                         &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                       &lt;/restriction>
 *                                                     &lt;/complexContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                               &lt;/sequence>
 *                                               &lt;attribute name="ProteinNameSplitter" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                                               &lt;attribute name="parser" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="pennstring" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="stanford" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="tokenizer" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="tail" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "document"
})
@XmlRootElement(name = "corpus")
public class CorpusTEES {

    @XmlElement(required = true)
    protected List<CorpusTEES.Document> document;
    @XmlAttribute(name = "source")
    protected String source;

    /**
     * Gets the value of the document property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the document property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocument().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CorpusTEES.Document }
     * 
     * 
     */
    public List<CorpusTEES.Document> getDocument() {
        if (document == null) {
            document = new ArrayList<CorpusTEES.Document>();
        }
        return this.document;
    }

    /**
     * Obtient la valeur de la propriété source.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Définit la valeur de la propriété source.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="sentence" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="entity" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="given" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *                           &lt;attribute name="headOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="origId" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="origOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="interaction" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="directed" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *                           &lt;attribute name="e1" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="e2" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="event" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *                           &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="origId" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="analyses">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="tokenization">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="token" maxOccurs="unbounded">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;attribute name="POS" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                               &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                               &lt;attribute name="headScore" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                               &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                               &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                             &lt;/restriction>
     *                                           &lt;/complexContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                     &lt;/sequence>
     *                                     &lt;attribute name="ProteinNameSplitter" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *                                     &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="tokenizer" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="parse">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="dependency" maxOccurs="unbounded">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                               &lt;attribute name="t1" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                               &lt;attribute name="t2" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                               &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                             &lt;/restriction>
     *                                           &lt;/complexContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                       &lt;element name="phrase" maxOccurs="unbounded">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;attribute name="begin" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                               &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                               &lt;attribute name="end" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                               &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                               &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                             &lt;/restriction>
     *                                           &lt;/complexContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                     &lt;/sequence>
     *                                     &lt;attribute name="ProteinNameSplitter" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *                                     &lt;attribute name="parser" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="pennstring" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="stanford" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="tokenizer" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="tail" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "sentence"
    })
    public static class Document {

        @XmlElement(required = true)
        protected List<CorpusTEES.Document.Sentence> sentence;
        @XmlAttribute(name = "id")
        protected String id;

        /**
         * Gets the value of the sentence property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the sentence property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSentence().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link CorpusTEES.Document.Sentence }
         * 
         * 
         */
        public List<CorpusTEES.Document.Sentence> getSentence() {
            if (sentence == null) {
                sentence = new ArrayList<CorpusTEES.Document.Sentence>();
            }
            return this.sentence;
        }

        /**
         * Obtient la valeur de la propriété id.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Définit la valeur de la propriété id.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }


        /**
         * <p>Classe Java pour anonymous complex type.
         * 
         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="entity" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="given" type="{http://www.w3.org/2001/XMLSchema}boolean" />
         *                 &lt;attribute name="headOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="origId" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="origOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="interaction" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="directed" type="{http://www.w3.org/2001/XMLSchema}boolean" />
         *                 &lt;attribute name="e1" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="e2" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="event" type="{http://www.w3.org/2001/XMLSchema}boolean" />
         *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="origId" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="analyses">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="tokenization">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="token" maxOccurs="unbounded">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;attribute name="POS" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                     &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                     &lt;attribute name="headScore" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                                     &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                     &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                   &lt;/restriction>
         *                                 &lt;/complexContent>
         *                               &lt;/complexType>
         *                             &lt;/element>
         *                           &lt;/sequence>
         *                           &lt;attribute name="ProteinNameSplitter" type="{http://www.w3.org/2001/XMLSchema}boolean" />
         *                           &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="tokenizer" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="parse">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="dependency" maxOccurs="unbounded">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                     &lt;attribute name="t1" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                     &lt;attribute name="t2" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                     &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                   &lt;/restriction>
         *                                 &lt;/complexContent>
         *                               &lt;/complexType>
         *                             &lt;/element>
         *                             &lt;element name="phrase" maxOccurs="unbounded">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;attribute name="begin" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                                     &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                     &lt;attribute name="end" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                                     &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                     &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                   &lt;/restriction>
         *                                 &lt;/complexContent>
         *                               &lt;/complexType>
         *                             &lt;/element>
         *                           &lt;/sequence>
         *                           &lt;attribute name="ProteinNameSplitter" type="{http://www.w3.org/2001/XMLSchema}boolean" />
         *                           &lt;attribute name="parser" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="pennstring" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="stanford" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="tokenizer" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="tail" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "entity",
            "interaction",
            "analyses"
        })
        public static class Sentence {

            @XmlElement(required = true)
            protected List<CorpusTEES.Document.Sentence.Entity> entity;
            @XmlElement(required = true)
            protected List<CorpusTEES.Document.Sentence.Interaction> interaction;
            @XmlElement(required = true)
            protected CorpusTEES.Document.Sentence.Analyses analyses;
            @XmlAttribute(name = "charOffset")
            protected String charOffset;
            @XmlAttribute(name = "id")
            protected String id;
            @XmlAttribute(name = "tail")
            protected String tail;
            @XmlAttribute(name = "text")
            protected String text;

            /**
             * Gets the value of the entity property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the entity property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getEntity().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link CorpusTEES.Document.Sentence.Entity }
             * 
             * 
             */
            public List<CorpusTEES.Document.Sentence.Entity> getEntity() {
                if (entity == null) {
                    entity = new ArrayList<CorpusTEES.Document.Sentence.Entity>();
                }
                return this.entity;
            }

            /**
             * Gets the value of the interaction property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the interaction property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getInteraction().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link CorpusTEES.Document.Sentence.Interaction }
             * 
             * 
             */
            public List<CorpusTEES.Document.Sentence.Interaction> getInteraction() {
                if (interaction == null) {
                    interaction = new ArrayList<CorpusTEES.Document.Sentence.Interaction>();
                }
                return this.interaction;
            }

            /**
             * Obtient la valeur de la propriété analyses.
             * 
             * @return
             *     possible object is
             *     {@link CorpusTEES.Document.Sentence.Analyses }
             *     
             */
            public CorpusTEES.Document.Sentence.Analyses getAnalyses() {
                return analyses;
            }

            /**
             * Définit la valeur de la propriété analyses.
             * 
             * @param value
             *     allowed object is
             *     {@link CorpusTEES.Document.Sentence.Analyses }
             *     
             */
            public void setAnalyses(CorpusTEES.Document.Sentence.Analyses value) {
                this.analyses = value;
            }

            /**
             * Obtient la valeur de la propriété charOffset.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCharOffset() {
                return charOffset;
            }

            /**
             * Définit la valeur de la propriété charOffset.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCharOffset(String value) {
                this.charOffset = value;
            }

            /**
             * Obtient la valeur de la propriété id.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getId() {
                return id;
            }

            /**
             * Définit la valeur de la propriété id.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setId(String value) {
                this.id = value;
            }

            /**
             * Obtient la valeur de la propriété tail.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTail() {
                return tail;
            }

            /**
             * Définit la valeur de la propriété tail.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTail(String value) {
                this.tail = value;
            }

            /**
             * Obtient la valeur de la propriété text.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getText() {
                return text;
            }

            /**
             * Définit la valeur de la propriété text.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setText(String value) {
                this.text = value;
            }


            /**
             * <p>Classe Java pour anonymous complex type.
             * 
             * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="tokenization">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="token" maxOccurs="unbounded">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;attribute name="POS" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                           &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                           &lt;attribute name="headScore" type="{http://www.w3.org/2001/XMLSchema}int" />
             *                           &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                           &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                         &lt;/restriction>
             *                       &lt;/complexContent>
             *                     &lt;/complexType>
             *                   &lt;/element>
             *                 &lt;/sequence>
             *                 &lt;attribute name="ProteinNameSplitter" type="{http://www.w3.org/2001/XMLSchema}boolean" />
             *                 &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="tokenizer" type="{http://www.w3.org/2001/XMLSchema}string" />
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="parse">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="dependency" maxOccurs="unbounded">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                           &lt;attribute name="t1" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                           &lt;attribute name="t2" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                           &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                         &lt;/restriction>
             *                       &lt;/complexContent>
             *                     &lt;/complexType>
             *                   &lt;/element>
             *                   &lt;element name="phrase" maxOccurs="unbounded">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;attribute name="begin" type="{http://www.w3.org/2001/XMLSchema}int" />
             *                           &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                           &lt;attribute name="end" type="{http://www.w3.org/2001/XMLSchema}int" />
             *                           &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                           &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                         &lt;/restriction>
             *                       &lt;/complexContent>
             *                     &lt;/complexType>
             *                   &lt;/element>
             *                 &lt;/sequence>
             *                 &lt;attribute name="ProteinNameSplitter" type="{http://www.w3.org/2001/XMLSchema}boolean" />
             *                 &lt;attribute name="parser" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="pennstring" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="stanford" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="tokenizer" type="{http://www.w3.org/2001/XMLSchema}string" />
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "tokenization",
                "parse"
            })
            public static class Analyses {

                @XmlElement(required = true)
                protected CorpusTEES.Document.Sentence.Analyses.Tokenization tokenization;
                @XmlElement(required = true)
                protected CorpusTEES.Document.Sentence.Analyses.Parse parse;

                /**
                 * Obtient la valeur de la propriété tokenization.
                 * 
                 * @return
                 *     possible object is
                 *     {@link CorpusTEES.Document.Sentence.Analyses.Tokenization }
                 *     
                 */
                public CorpusTEES.Document.Sentence.Analyses.Tokenization getTokenization() {
                    return tokenization;
                }

                /**
                 * Définit la valeur de la propriété tokenization.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link CorpusTEES.Document.Sentence.Analyses.Tokenization }
                 *     
                 */
                public void setTokenization(CorpusTEES.Document.Sentence.Analyses.Tokenization value) {
                    this.tokenization = value;
                }

                /**
                 * Obtient la valeur de la propriété parse.
                 * 
                 * @return
                 *     possible object is
                 *     {@link CorpusTEES.Document.Sentence.Analyses.Parse }
                 *     
                 */
                public CorpusTEES.Document.Sentence.Analyses.Parse getParse() {
                    return parse;
                }

                /**
                 * Définit la valeur de la propriété parse.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link CorpusTEES.Document.Sentence.Analyses.Parse }
                 *     
                 */
                public void setParse(CorpusTEES.Document.Sentence.Analyses.Parse value) {
                    this.parse = value;
                }


                /**
                 * <p>Classe Java pour anonymous complex type.
                 * 
                 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;sequence>
                 *         &lt;element name="dependency" maxOccurs="unbounded">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                 &lt;attribute name="t1" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                 &lt;attribute name="t2" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *               &lt;/restriction>
                 *             &lt;/complexContent>
                 *           &lt;/complexType>
                 *         &lt;/element>
                 *         &lt;element name="phrase" maxOccurs="unbounded">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;attribute name="begin" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *                 &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                 &lt;attribute name="end" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *               &lt;/restriction>
                 *             &lt;/complexContent>
                 *           &lt;/complexType>
                 *         &lt;/element>
                 *       &lt;/sequence>
                 *       &lt;attribute name="ProteinNameSplitter" type="{http://www.w3.org/2001/XMLSchema}boolean" />
                 *       &lt;attribute name="parser" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="pennstring" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="stanford" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="tokenizer" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "dependency",
                    "phrase"
                })
                public static class Parse {

                    @XmlElement(required = true)
                    protected List<CorpusTEES.Document.Sentence.Analyses.Parse.Dependency> dependency;
                    @XmlElement(required = true)
                    protected List<CorpusTEES.Document.Sentence.Analyses.Parse.Phrase> phrase;
                    @XmlAttribute(name = "ProteinNameSplitter")
                    protected Boolean proteinNameSplitter;
                    @XmlAttribute(name = "parser")
                    protected String parser;
                    @XmlAttribute(name = "pennstring")
                    protected String pennstring;
                    @XmlAttribute(name = "source")
                    protected String source;
                    @XmlAttribute(name = "stanford")
                    protected String stanford;
                    @XmlAttribute(name = "tokenizer")
                    protected String tokenizer;

                    /**
                     * Gets the value of the dependency property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the JAXB object.
                     * This is why there is not a <CODE>set</CODE> method for the dependency property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getDependency().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link CorpusTEES.Document.Sentence.Analyses.Parse.Dependency }
                     * 
                     * 
                     */
                    public List<CorpusTEES.Document.Sentence.Analyses.Parse.Dependency> getDependency() {
                        if (dependency == null) {
                            dependency = new ArrayList<CorpusTEES.Document.Sentence.Analyses.Parse.Dependency>();
                        }
                        return this.dependency;
                    }

                    /**
                     * Gets the value of the phrase property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the JAXB object.
                     * This is why there is not a <CODE>set</CODE> method for the phrase property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getPhrase().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link CorpusTEES.Document.Sentence.Analyses.Parse.Phrase }
                     * 
                     * 
                     */
                    public List<CorpusTEES.Document.Sentence.Analyses.Parse.Phrase> getPhrase() {
                        if (phrase == null) {
                            phrase = new ArrayList<CorpusTEES.Document.Sentence.Analyses.Parse.Phrase>();
                        }
                        return this.phrase;
                    }

                    /**
                     * Obtient la valeur de la propriété proteinNameSplitter.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Boolean }
                     *     
                     */
                    public Boolean isProteinNameSplitter() {
                        return proteinNameSplitter;
                    }

                    /**
                     * Définit la valeur de la propriété proteinNameSplitter.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Boolean }
                     *     
                     */
                    public void setProteinNameSplitter(Boolean value) {
                        this.proteinNameSplitter = value;
                    }

                    /**
                     * Obtient la valeur de la propriété parser.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getParser() {
                        return parser;
                    }

                    /**
                     * Définit la valeur de la propriété parser.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setParser(String value) {
                        this.parser = value;
                    }

                    /**
                     * Obtient la valeur de la propriété pennstring.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getPennstring() {
                        return pennstring;
                    }

                    /**
                     * Définit la valeur de la propriété pennstring.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setPennstring(String value) {
                        this.pennstring = value;
                    }

                    /**
                     * Obtient la valeur de la propriété source.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getSource() {
                        return source;
                    }

                    /**
                     * Définit la valeur de la propriété source.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setSource(String value) {
                        this.source = value;
                    }

                    /**
                     * Obtient la valeur de la propriété stanford.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getStanford() {
                        return stanford;
                    }

                    /**
                     * Définit la valeur de la propriété stanford.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setStanford(String value) {
                        this.stanford = value;
                    }

                    /**
                     * Obtient la valeur de la propriété tokenizer.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getTokenizer() {
                        return tokenizer;
                    }

                    /**
                     * Définit la valeur de la propriété tokenizer.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setTokenizer(String value) {
                        this.tokenizer = value;
                    }


                    /**
                     * <p>Classe Java pour anonymous complex type.
                     * 
                     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
                     * 
                     * <pre>
                     * &lt;complexType>
                     *   &lt;complexContent>
                     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *       &lt;attribute name="t1" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *       &lt;attribute name="t2" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *     &lt;/restriction>
                     *   &lt;/complexContent>
                     * &lt;/complexType>
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "")
                    public static class Dependency {

                        @XmlAttribute(name = "id")
                        protected String id;
                        @XmlAttribute(name = "t1")
                        protected String t1;
                        @XmlAttribute(name = "t2")
                        protected String t2;
                        @XmlAttribute(name = "type")
                        protected String type;

                        /**
                         * Obtient la valeur de la propriété id.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getId() {
                            return id;
                        }

                        /**
                         * Définit la valeur de la propriété id.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setId(String value) {
                            this.id = value;
                        }

                        /**
                         * Obtient la valeur de la propriété t1.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getT1() {
                            return t1;
                        }

                        /**
                         * Définit la valeur de la propriété t1.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setT1(String value) {
                            this.t1 = value;
                        }

                        /**
                         * Obtient la valeur de la propriété t2.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getT2() {
                            return t2;
                        }

                        /**
                         * Définit la valeur de la propriété t2.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setT2(String value) {
                            this.t2 = value;
                        }

                        /**
                         * Obtient la valeur de la propriété type.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getType() {
                            return type;
                        }

                        /**
                         * Définit la valeur de la propriété type.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setType(String value) {
                            this.type = value;
                        }

                    }


                    /**
                     * <p>Classe Java pour anonymous complex type.
                     * 
                     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
                     * 
                     * <pre>
                     * &lt;complexType>
                     *   &lt;complexContent>
                     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *       &lt;attribute name="begin" type="{http://www.w3.org/2001/XMLSchema}int" />
                     *       &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *       &lt;attribute name="end" type="{http://www.w3.org/2001/XMLSchema}int" />
                     *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *     &lt;/restriction>
                     *   &lt;/complexContent>
                     * &lt;/complexType>
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "")
                    public static class Phrase {

                        @XmlAttribute(name = "begin")
                        protected Integer begin;
                        @XmlAttribute(name = "charOffset")
                        protected String charOffset;
                        @XmlAttribute(name = "end")
                        protected Integer end;
                        @XmlAttribute(name = "id")
                        protected String id;
                        @XmlAttribute(name = "type")
                        protected String type;

                        /**
                         * Obtient la valeur de la propriété begin.
                         * 
                         * @return
                         *     possible object is
                         *     {@link Integer }
                         *     
                         */
                        public Integer getBegin() {
                            return begin;
                        }

                        /**
                         * Définit la valeur de la propriété begin.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link Integer }
                         *     
                         */
                        public void setBegin(Integer value) {
                            this.begin = value;
                        }

                        /**
                         * Obtient la valeur de la propriété charOffset.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getCharOffset() {
                            return charOffset;
                        }

                        /**
                         * Définit la valeur de la propriété charOffset.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setCharOffset(String value) {
                            this.charOffset = value;
                        }

                        /**
                         * Obtient la valeur de la propriété end.
                         * 
                         * @return
                         *     possible object is
                         *     {@link Integer }
                         *     
                         */
                        public Integer getEnd() {
                            return end;
                        }

                        /**
                         * Définit la valeur de la propriété end.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link Integer }
                         *     
                         */
                        public void setEnd(Integer value) {
                            this.end = value;
                        }

                        /**
                         * Obtient la valeur de la propriété id.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getId() {
                            return id;
                        }

                        /**
                         * Définit la valeur de la propriété id.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setId(String value) {
                            this.id = value;
                        }

                        /**
                         * Obtient la valeur de la propriété type.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getType() {
                            return type;
                        }

                        /**
                         * Définit la valeur de la propriété type.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setType(String value) {
                            this.type = value;
                        }

                    }

                }


                /**
                 * <p>Classe Java pour anonymous complex type.
                 * 
                 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;sequence>
                 *         &lt;element name="token" maxOccurs="unbounded">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;attribute name="POS" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                 &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                 &lt;attribute name="headScore" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                 &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *               &lt;/restriction>
                 *             &lt;/complexContent>
                 *           &lt;/complexType>
                 *         &lt;/element>
                 *       &lt;/sequence>
                 *       &lt;attribute name="ProteinNameSplitter" type="{http://www.w3.org/2001/XMLSchema}boolean" />
                 *       &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="tokenizer" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "token"
                })
                public static class Tokenization {

                    @XmlElement(required = true)
                    protected List<CorpusTEES.Document.Sentence.Analyses.Tokenization.Token> token;
                    @XmlAttribute(name = "ProteinNameSplitter")
                    protected Boolean proteinNameSplitter;
                    @XmlAttribute(name = "source")
                    protected String source;
                    @XmlAttribute(name = "tokenizer")
                    protected String tokenizer;

                    /**
                     * Gets the value of the token property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the JAXB object.
                     * This is why there is not a <CODE>set</CODE> method for the token property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getToken().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link CorpusTEES.Document.Sentence.Analyses.Tokenization.Token }
                     * 
                     * 
                     */
                    public List<CorpusTEES.Document.Sentence.Analyses.Tokenization.Token> getToken() {
                        if (token == null) {
                            token = new ArrayList<CorpusTEES.Document.Sentence.Analyses.Tokenization.Token>();
                        }
                        return this.token;
                    }

                    /**
                     * Obtient la valeur de la propriété proteinNameSplitter.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Boolean }
                     *     
                     */
                    public Boolean isProteinNameSplitter() {
                        return proteinNameSplitter;
                    }

                    /**
                     * Définit la valeur de la propriété proteinNameSplitter.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Boolean }
                     *     
                     */
                    public void setProteinNameSplitter(Boolean value) {
                        this.proteinNameSplitter = value;
                    }

                    /**
                     * Obtient la valeur de la propriété source.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getSource() {
                        return source;
                    }

                    /**
                     * Définit la valeur de la propriété source.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setSource(String value) {
                        this.source = value;
                    }

                    /**
                     * Obtient la valeur de la propriété tokenizer.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getTokenizer() {
                        return tokenizer;
                    }

                    /**
                     * Définit la valeur de la propriété tokenizer.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setTokenizer(String value) {
                        this.tokenizer = value;
                    }


                    /**
                     * <p>Classe Java pour anonymous complex type.
                     * 
                     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
                     * 
                     * <pre>
                     * &lt;complexType>
                     *   &lt;complexContent>
                     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *       &lt;attribute name="POS" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *       &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *       &lt;attribute name="headScore" type="{http://www.w3.org/2001/XMLSchema}int" />
                     *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *       &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *     &lt;/restriction>
                     *   &lt;/complexContent>
                     * &lt;/complexType>
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "")
                    public static class Token {

                        @XmlAttribute(name = "POS")
                        protected String pos;
                        @XmlAttribute(name = "charOffset")
                        protected String charOffset;
                        @XmlAttribute(name = "headScore")
                        protected Integer headScore;
                        @XmlAttribute(name = "id")
                        protected String id;
                        @XmlAttribute(name = "text")
                        protected String text;

                        /**
                         * Obtient la valeur de la propriété pos.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getPOS() {
                            return pos;
                        }

                        /**
                         * Définit la valeur de la propriété pos.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setPOS(String value) {
                            this.pos = value;
                        }

                        /**
                         * Obtient la valeur de la propriété charOffset.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getCharOffset() {
                            return charOffset;
                        }

                        /**
                         * Définit la valeur de la propriété charOffset.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setCharOffset(String value) {
                            this.charOffset = value;
                        }

                        /**
                         * Obtient la valeur de la propriété headScore.
                         * 
                         * @return
                         *     possible object is
                         *     {@link Integer }
                         *     
                         */
                        public Integer getHeadScore() {
                            return headScore;
                        }

                        /**
                         * Définit la valeur de la propriété headScore.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link Integer }
                         *     
                         */
                        public void setHeadScore(Integer value) {
                            this.headScore = value;
                        }

                        /**
                         * Obtient la valeur de la propriété id.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getId() {
                            return id;
                        }

                        /**
                         * Définit la valeur de la propriété id.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setId(String value) {
                            this.id = value;
                        }

                        /**
                         * Obtient la valeur de la propriété text.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getText() {
                            return text;
                        }

                        /**
                         * Définit la valeur de la propriété text.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setText(String value) {
                            this.text = value;
                        }

                    }

                }

            }


            /**
             * <p>Classe Java pour anonymous complex type.
             * 
             * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;attribute name="charOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="given" type="{http://www.w3.org/2001/XMLSchema}boolean" />
             *       &lt;attribute name="headOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="origId" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="origOffset" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class Entity {

                @XmlAttribute(name = "charOffset")
                protected String charOffset;
                @XmlAttribute(name = "given")
                protected Boolean given;
                @XmlAttribute(name = "headOffset")
                protected String headOffset;
                @XmlAttribute(name = "id")
                protected String id;
                @XmlAttribute(name = "origId")
                protected String origId;
                @XmlAttribute(name = "origOffset")
                protected String origOffset;
                @XmlAttribute(name = "text")
                protected String text;
                @XmlAttribute(name = "type")
                protected String type;

                /**
                 * Obtient la valeur de la propriété charOffset.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getCharOffset() {
                    return charOffset;
                }

                /**
                 * Définit la valeur de la propriété charOffset.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setCharOffset(String value) {
                    this.charOffset = value;
                }

                /**
                 * Obtient la valeur de la propriété given.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Boolean }
                 *     
                 */
                public Boolean isGiven() {
                    return given;
                }

                /**
                 * Définit la valeur de la propriété given.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Boolean }
                 *     
                 */
                public void setGiven(Boolean value) {
                    this.given = value;
                }

                /**
                 * Obtient la valeur de la propriété headOffset.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getHeadOffset() {
                    return headOffset;
                }

                /**
                 * Définit la valeur de la propriété headOffset.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setHeadOffset(String value) {
                    this.headOffset = value;
                }

                /**
                 * Obtient la valeur de la propriété id.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getId() {
                    return id;
                }

                /**
                 * Définit la valeur de la propriété id.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setId(String value) {
                    this.id = value;
                }

                /**
                 * Obtient la valeur de la propriété origId.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getOrigId() {
                    return origId;
                }

                /**
                 * Définit la valeur de la propriété origId.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setOrigId(String value) {
                    this.origId = value;
                }

                /**
                 * Obtient la valeur de la propriété origOffset.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getOrigOffset() {
                    return origOffset;
                }

                /**
                 * Définit la valeur de la propriété origOffset.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setOrigOffset(String value) {
                    this.origOffset = value;
                }

                /**
                 * Obtient la valeur de la propriété text.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getText() {
                    return text;
                }

                /**
                 * Définit la valeur de la propriété text.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setText(String value) {
                    this.text = value;
                }

                /**
                 * Obtient la valeur de la propriété type.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getType() {
                    return type;
                }

                /**
                 * Définit la valeur de la propriété type.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setType(String value) {
                    this.type = value;
                }

            }


            /**
             * <p>Classe Java pour anonymous complex type.
             * 
             * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;attribute name="directed" type="{http://www.w3.org/2001/XMLSchema}boolean" />
             *       &lt;attribute name="e1" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="e2" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="event" type="{http://www.w3.org/2001/XMLSchema}boolean" />
             *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="origId" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class Interaction {

                @XmlAttribute(name = "directed")
                protected Boolean directed;
                @XmlAttribute(name = "e1")
                protected String e1;
                @XmlAttribute(name = "e2")
                protected String e2;
                @XmlAttribute(name = "event")
                protected Boolean event;
                @XmlAttribute(name = "id")
                protected String id;
                @XmlAttribute(name = "origId")
                protected String origId;
                @XmlAttribute(name = "type")
                protected String type;

                /**
                 * Obtient la valeur de la propriété directed.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Boolean }
                 *     
                 */
                public Boolean isDirected() {
                    return directed;
                }

                /**
                 * Définit la valeur de la propriété directed.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Boolean }
                 *     
                 */
                public void setDirected(Boolean value) {
                    this.directed = value;
                }

                /**
                 * Obtient la valeur de la propriété e1.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getE1() {
                    return e1;
                }

                /**
                 * Définit la valeur de la propriété e1.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setE1(String value) {
                    this.e1 = value;
                }

                /**
                 * Obtient la valeur de la propriété e2.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getE2() {
                    return e2;
                }

                /**
                 * Définit la valeur de la propriété e2.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setE2(String value) {
                    this.e2 = value;
                }

                /**
                 * Obtient la valeur de la propriété event.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Boolean }
                 *     
                 */
                public Boolean isEvent() {
                    return event;
                }

                /**
                 * Définit la valeur de la propriété event.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Boolean }
                 *     
                 */
                public void setEvent(Boolean value) {
                    this.event = value;
                }

                /**
                 * Obtient la valeur de la propriété id.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getId() {
                    return id;
                }

                /**
                 * Définit la valeur de la propriété id.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setId(String value) {
                    this.id = value;
                }

                /**
                 * Obtient la valeur de la propriété origId.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getOrigId() {
                    return origId;
                }

                /**
                 * Définit la valeur de la propriété origId.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setOrigId(String value) {
                    this.origId = value;
                }

                /**
                 * Obtient la valeur de la propriété type.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getType() {
                    return type;
                }

                /**
                 * Définit la valeur de la propriété type.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setType(String value) {
                    this.type = value;
                }

            }

        }

    }

}
