/*
Copyright 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2016.08.24 à 10:55:16 AM CEST 
//


package fr.jouy.inra.maiage.bibliome.alvis.web;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element name="synopsis">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="p" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="module-doc">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="description">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="p">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="this" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *                                       &lt;element name="param" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
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
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="param-doc" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="p" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="mandatory" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
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
 *       &lt;attribute name="author" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="short-target" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "synopsis",
    "moduleDoc"
})
@XmlRootElement(name = "alvisnlp-doc")
@Deprecated
public class AlvisnlpDoc {

    @XmlElement(required = true)
    protected AlvisnlpDoc.Synopsis synopsis;
    @XmlElement(name = "module-doc", required = true)
    protected AlvisnlpDoc.ModuleDoc moduleDoc;
    @XmlAttribute(name = "author")
    protected String author;
    @XmlAttribute(name = "date")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    @XmlAttribute(name = "short-target")
    protected String shortTarget;
    @XmlAttribute(name = "target")
    protected String target;

    /**
     * Obtient la valeur de la propriété synopsis.
     * 
     * @return
     *     possible object is
     *     {@link AlvisnlpDoc.Synopsis }
     *     
     */
    public AlvisnlpDoc.Synopsis getSynopsis() {
        return synopsis;
    }

    /**
     * Définit la valeur de la propriété synopsis.
     * 
     * @param value
     *     allowed object is
     *     {@link AlvisnlpDoc.Synopsis }
     *     
     */
    public void setSynopsis(AlvisnlpDoc.Synopsis value) {
        this.synopsis = value;
    }

    /**
     * Obtient la valeur de la propriété moduleDoc.
     * 
     * @return
     *     possible object is
     *     {@link AlvisnlpDoc.ModuleDoc }
     *     
     */
    public AlvisnlpDoc.ModuleDoc getModuleDoc() {
        return moduleDoc;
    }

    /**
     * Définit la valeur de la propriété moduleDoc.
     * 
     * @param value
     *     allowed object is
     *     {@link AlvisnlpDoc.ModuleDoc }
     *     
     */
    public void setModuleDoc(AlvisnlpDoc.ModuleDoc value) {
        this.moduleDoc = value;
    }

    /**
     * Obtient la valeur de la propriété author.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Définit la valeur de la propriété author.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthor(String value) {
        this.author = value;
    }

    /**
     * Obtient la valeur de la propriété date.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Définit la valeur de la propriété date.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Obtient la valeur de la propriété shortTarget.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortTarget() {
        return shortTarget;
    }

    /**
     * Définit la valeur de la propriété shortTarget.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortTarget(String value) {
        this.shortTarget = value;
    }

    /**
     * Obtient la valeur de la propriété target.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTarget() {
        return target;
    }

    /**
     * Définit la valeur de la propriété target.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTarget(String value) {
        this.target = value;
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
     *         &lt;element name="description">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="p">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="this" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
     *                             &lt;element name="param" maxOccurs="unbounded">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
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
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="param-doc" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="p" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="mandatory" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
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
        "description",
        "paramDoc"
    })
    public static class ModuleDoc {

        @XmlElement(required = true)
        protected AlvisnlpDoc.ModuleDoc.Description description;
        @XmlElement(name = "param-doc", required = true)
        protected List<AlvisnlpDoc.ModuleDoc.ParamDoc> paramDoc;

        /**
         * Obtient la valeur de la propriété description.
         * 
         * @return
         *     possible object is
         *     {@link AlvisnlpDoc.ModuleDoc.Description }
         *     
         */
        public AlvisnlpDoc.ModuleDoc.Description getDescription() {
            return description;
        }

        /**
         * Définit la valeur de la propriété description.
         * 
         * @param value
         *     allowed object is
         *     {@link AlvisnlpDoc.ModuleDoc.Description }
         *     
         */
        public void setDescription(AlvisnlpDoc.ModuleDoc.Description value) {
            this.description = value;
        }

        /**
         * Gets the value of the paramDoc property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the paramDoc property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getParamDoc().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AlvisnlpDoc.ModuleDoc.ParamDoc }
         * 
         * 
         */
        public List<AlvisnlpDoc.ModuleDoc.ParamDoc> getParamDoc() {
            if (paramDoc == null) {
                paramDoc = new ArrayList<AlvisnlpDoc.ModuleDoc.ParamDoc>();
            }
            return this.paramDoc;
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
         *         &lt;element name="p">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="this" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
         *                   &lt;element name="param" maxOccurs="unbounded">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
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
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "p"
        })
        public static class Description {

            @XmlElement(required = true)
            protected AlvisnlpDoc.ModuleDoc.Description.P p;

            /**
             * Obtient la valeur de la propriété p.
             * 
             * @return
             *     possible object is
             *     {@link AlvisnlpDoc.ModuleDoc.Description.P }
             *     
             */
            public AlvisnlpDoc.ModuleDoc.Description.P getP() {
                return p;
            }

            /**
             * Définit la valeur de la propriété p.
             * 
             * @param value
             *     allowed object is
             *     {@link AlvisnlpDoc.ModuleDoc.Description.P }
             *     
             */
            public void setP(AlvisnlpDoc.ModuleDoc.Description.P value) {
                this.p = value;
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
             *         &lt;element name="this" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
             *         &lt;element name="param" maxOccurs="unbounded">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
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
                "_this",
                "param"
            })
            public static class P {

                @XmlElement(name = "this", required = true)
                protected List<String> _this;
                @XmlElement(required = true)
                protected List<AlvisnlpDoc.ModuleDoc.Description.P.Param> param;

                /**
                 * Gets the value of the this property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the this property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getThis().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link String }
                 * 
                 * 
                 */
                public List<String> getThis() {
                    if (_this == null) {
                        _this = new ArrayList<String>();
                    }
                    return this._this;
                }

                /**
                 * Gets the value of the param property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the param property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getParam().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link AlvisnlpDoc.ModuleDoc.Description.P.Param }
                 * 
                 * 
                 */
                public List<AlvisnlpDoc.ModuleDoc.Description.P.Param> getParam() {
                    if (param == null) {
                        param = new ArrayList<AlvisnlpDoc.ModuleDoc.Description.P.Param>();
                    }
                    return this.param;
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
                 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "")
                public static class Param {

                    @XmlAttribute(name = "name")
                    protected String name;

                    /**
                     * Obtient la valeur de la propriété name.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getName() {
                        return name;
                    }

                    /**
                     * Définit la valeur de la propriété name.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setName(String value) {
                        this.name = value;
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
         *       &lt;sequence>
         *         &lt;element name="p" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *       &lt;attribute name="mandatory" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "p"
        })
        public static class ParamDoc {

            @XmlElement(required = true)
            protected String p;
            @XmlAttribute(name = "mandatory")
            protected String mandatory;
            @XmlAttribute(name = "name")
            protected String name;

            /**
             * Obtient la valeur de la propriété p.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getP() {
                return p;
            }

            /**
             * Définit la valeur de la propriété p.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setP(String value) {
                this.p = value;
            }

            /**
             * Obtient la valeur de la propriété mandatory.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMandatory() {
                return mandatory;
            }

            /**
             * Définit la valeur de la propriété mandatory.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMandatory(String value) {
                this.mandatory = value;
            }

            /**
             * Obtient la valeur de la propriété name.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getName() {
                return name;
            }

            /**
             * Définit la valeur de la propriété name.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setName(String value) {
                this.name = value;
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
     *         &lt;element name="p" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "p"
    })
    public static class Synopsis {

        @XmlElement(required = true)
        protected String p;

        /**
         * Obtient la valeur de la propriété p.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getP() {
            return p;
        }

        /**
         * Définit la valeur de la propriété p.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setP(String value) {
            this.p = value;
        }

    }

}
