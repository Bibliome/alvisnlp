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



package alvisnlp.corpus;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bibliome.util.Iterators;
import org.bibliome.util.filters.Filters;
import org.bibliome.util.mappers.Mapper;
import org.bibliome.util.mappers.Mappers;

import alvisnlp.corpus.Corpus.SectionToLayerMapper;
import alvisnlp.corpus.creators.DocumentCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;

/**
 * A Document object represents a document to be annotated.
 */
public class Document extends AbstractElement {
    public final static long serialVersionUID = 1L;

    /**
     * Static feature representing the document identifier.
     */
    public static final String ID_FEATURE_NAME = "id";

    private final Corpus corpus;
    private final String     id;
    private List<Section>    sections         = null;
    private Set<String>      sectionNames     = null;

    private Document(DocumentCreator dc, Corpus corpus, String id, boolean autoLink) {
    	super(ID_FEATURE_NAME, dc);
    	this.corpus = corpus;
        this.id = id;
        if (autoLink)
        	corpus.addDocument(this);
        addFeatures(dc.getConstantDocumentFeatures());
    }

    /**
     * Returns a document with the specified id, or creates an empty one if the specified corpus does not contain it.
     * If the document is created, then all features in the document creator mapping are added to the result document.
     * @param dc document creator, typically a module
     * @param corpus the corpus to which the document belongs
     * @param id document identifier
     * @return the document
     */
    public static Document getDocument(DocumentCreator dc, Corpus corpus, String id, boolean autoLink) {
        if (corpus.hasDocument(id))
            return corpus.getDocument(id);
        return new Document(dc, corpus, id, autoLink);
    }

    public static Document getDocument(DocumentCreator dc, Corpus corpus, String id) {
    	return getDocument(dc, corpus, id, true);
    }

    /**
     * Returns the corpus to which belongs this document.
     */
    public Corpus getCorpus() {
		return corpus;
	}

	/**
     * Returns this document id.
     * @return this document id
     */
    public String getId() {
        return id;
    }

    @Override
	public String getStaticFeatureValue() {
    	return id;
	}

    int countSections() {
    	if (sections == null)
    		return 0;
    	return sections.size();
    }
    
    public void addSection(Section sec) {
        if (sections == null) {
            sections = new ArrayList<Section>();
            sectionNames = new HashSet<String>();
        }
        sections.add(sec);
        sectionNames.add(sec.getName());
    }

    /**
     * Checks either this document contains at least one section with the specified name.
     * @param name the name
     */
    public boolean hasSection(String name) {
        if (sectionNames == null)
            return false;
        return sectionNames.contains(name);
    }
    
    /**
     * Removes the specified section from this document.
     * Does nothing if the specified section does not belong to this document.
     * @param sec
     */
    public void removeSection(Section sec) {
    	if (sec.getDocument() == this) {
    		sections.remove(sec);
    	}
    }
    
    void toXML(PrintStream out) throws IOException {
    	write(out,"<document id=\"" + id + "\">");
    	featuresToXML(out, "feature", "name", "value");
    	for (Section sec : Iterators.loop(sectionIterator()))
    		sec.toXML(out);
    	write(out,"</document>");
    }

    /**
     * Iterator through sections in this document.
     */
	public Iterator<Section> sectionIterator() {
    	if (sections == null)
    		return Iterators.emptyIterator();
        return sections.iterator();
    }
	
    /**
     * Iterator through sections in this document with the specified name.
     */
	public Iterator<Section> sectionIterator(String name) {
        	if (sections == null)
        		return Iterators.emptyIterator();

		Collection<Section> c = new ArrayList<Section>();
		for (Section sec : sections)
			if (sec.getName().equals(name))
				c.add(sec);
		return c.iterator();
	}
    
    /**
     * Iterator through sections in this document that satisfy the specified filter.
     * @param filter
     */
    public Iterator<Section> sectionIterator(EvaluationContext ctx, Evaluator filter) {
        if (filter == null)
            return sectionIterator();
        return Filters.apply(filter.getFilter(ctx), sectionIterator());
    }
    
    /**
     * Iterator through layers with the specified name in this document.
     * Only layers that belong to sections that satisfy sectionFilter are iterated.
     * @param layerName
     */
    public Iterator<Layer> layerIterator(EvaluationContext ctx, String layerName, Evaluator sectionFilter) {
    	return Mappers.apply(new SectionToLayerMapper(layerName), sectionIterator(ctx, sectionFilter));
    }

    private static final class SectionCollector implements Mapper<Document,Iterator<Section>> {
    	private final EvaluationContext ctx;
        private final Evaluator filter;

        private SectionCollector(EvaluationContext ctx, Evaluator filter) {
            super();
            this.ctx = ctx;
            this.filter = filter;
        }

        @Override
        public Iterator<Section> map(Document doc) {
            return doc.sectionIterator(ctx, filter);
        }
    }
    
    static final Mapper<Document,Iterator<Section>> sectionCollector(EvaluationContext ctx, Evaluator filter) {
        return new SectionCollector(ctx, filter);
    }
    
    /**
     * Returns the total number of annotations in this document.
     */
    long countAnnotations() {
    	if (sections == null)
    		return 0;
    	long result = 0;
    	for (Section sec : sections)
    		result += sec.countAnnotations();
    	return result;
    }
    
    /**
     * Returns the sum of the size of all layers in this document.
     */
    long countPostings() {
    	long result = 0;
    	if (sections != null) {
    		for (Section sec : sections) {
    			result += sec.countPostings();
    		}
    	}
    	return result;
    }

	@Override
	public <R,P> R accept(ElementVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}

	public int size() {
		return sections.size();
	}

	@Override
	public ElementType getType() {
		return ElementType.DOCUMENT;
	}

	@Override
	public Element getParent() {
		return corpus;
	}

	@Override
	public Element getOriginal() {
		return this;
	}
}
