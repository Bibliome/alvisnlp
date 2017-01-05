/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

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



package org.bibliome.alvisnlp.modules.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibliome.util.Strings;
import org.xml.sax.Attributes;
import org.xml.sax.ext.DefaultHandler2;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.module.ProcessingContext;

// TODO: Auto-generated Javadoc
/**
 * The Class XMLHandler.
 */
public class XMLHandler extends DefaultHandler2 {

    /** The corpus. */
    private final Corpus                  corpus;

    private final ProcessingContext<Corpus> ctx;
    
    /** The owner module. */
    private XMLReader                     ownerModule;

    /** The document. */
    private Document                      document         = null;

    /** The section. */
    private Section                       section          = null;

    /** The section id. */
    private String                        sectionId        = null;

    /** The layer. */
    private Layer                         layer            = null;

    /** The annot. */
    private Annotation                    annot            = null;

    /** The relation. */
    private Relation                      relation         = null;

    /** The tuple. */
    private Tuple                         tuple            = null;

    /** The innermost element. */
    private Element                       innermostElement = null;

    /** The sb. */
    private StringBuilder                 sb               = new StringBuilder();

    /** The keep char. */
    private boolean                       keepChar         = false;

    /** The annotation map. */
    private final Map<Integer,Annotation> annotationMap    = new HashMap<Integer,Annotation>();

    /**
     * Instantiates a new corpus xml handler.
     * @param ctx TODO
     * @param ownerModule
     *            the owner module
     * @param corpus
     *            the corpus
     */
    public XMLHandler(ProcessingContext<Corpus> ctx, XMLReader ownerModule, Corpus corpus) {
        super();
        this.ctx = ctx;
        this.corpus = corpus;
        this.ownerModule = ownerModule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if ("alvisnlp-corpus".equals(qName)) {
        	document = null;
            section = null;
            sectionId = null;
            layer = null;
            annot = null;
            sb.setLength(0);
            keepChar = false;
            annotationMap.clear();
            return;
        }
        if ("processed-by".equals(qName)) {
            corpus.hasBeenProcessedBy(attributes.getValue("module"));
            return;
        }
        if ("document".equals(qName)) {
        	String id = attributes.getValue("id");
        	ownerModule.getLogger(ctx).finer("building document " + id);
            document = Document.getDocument(ownerModule, corpus, id);
            innermostElement = document;
            return;
        }
        if ("feature".equals(qName)) {
            innermostElement.addFeature(attributes.getValue("name"), attributes.getValue("value"));
            return;
        }
        if ("f".equals(qName)) {
            innermostElement.addFeature(attributes.getValue("n"), attributes.getValue("v"));
            return;
        }
        if ("section".equals(qName)) {
            sectionId = attributes.getValue("name");
            return;
        }
        if ("contents".equals(qName)) {
            sb.setLength(0);
            keepChar = true;
            return;
        }
        if ("a".equals(qName)) {
        	annot = new Annotation(ownerModule, section, Integer.parseInt(attributes.getValue("s")), Integer.parseInt(attributes.getValue("e")));
        	String layerString = attributes.getValue("l");
        	if (layerString != null)
        		for (String l : Strings.split(layerString, ',', -1))
        			section.ensureLayer(l.trim()).add(annot);
        	String id = attributes.getValue("id");
        	if (id != null)
        		annotationMap.put(Integer.parseInt(id), annot);
            innermostElement = annot;
            return;
        }
        if ("layer".equals(qName)) {
            layer = new Layer(section, attributes.getValue("name"));
            for (String a : Strings.split(attributes.getValue("annotations"), ',', 0)) {
                if ("".equals(a))
                    continue;
                layer.add(annotationMap.get(Integer.parseInt(a)));
            }
            return;
        }
        if ("relation".equals(qName)) {
            relation = new Relation(ownerModule, section, attributes.getValue("name"));
            innermostElement = relation;
            return;
        }
        if ("t".equals(qName)) {
            List<String> sargs = Strings.split(attributes.getValue("a"), ',', 0);
            Annotation[] args = new Annotation[sargs.size()];
            for (int i = 0; i < sargs.size(); i++)
                args[i] = annotationMap.get(Integer.parseInt(sargs.get(i)));
            tuple = new Tuple(ownerModule, relation);
            innermostElement = tuple;
            return;
        }
        if ("g".equals(qName)) {
        	String role = attributes.getValue("r");
        	String annotationId = attributes.getValue("a");
        	Annotation a = annotationMap.get(Integer.parseInt(annotationId));
        	tuple.setArgument(role, a);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
	public void endElement(String uri, String localName, String qName) {
        if ("document".equals(qName)) {
            document = null;
            innermostElement = null;
            return;
        }
        if ("section".equals(qName)) {
            section = null;
            innermostElement = document;
            return;
        }
        if ("contents".equals(qName)) {
            section = new Section(ownerModule, document, sectionId, sb.toString());
            innermostElement = section;
            return;
        }
        if ("a".equals(qName)) {
            annot = null;
            innermostElement = section;
            return;
        }
        if ("relation".equals(qName)) {
            relation = null;
            innermostElement = null;
        }
        if ("t".equals(qName)) {
            tuple = null;
            innermostElement = relation;
            return;
        }
    }

    @Override
	public void characters(char[] ch, int start, int length) {
        if (keepChar)
            sb.append(ch, start, length);
    }
}
