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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.bibliome.util.Iterators;
import org.bibliome.util.filters.Filters;
import org.bibliome.util.mappers.Mapper;
import org.bibliome.util.mappers.Mappers;

import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.module.Annotable;

/**
 * A Corpus object represents a set of documents to annotate.
 */
public final class Corpus extends AbstractElement implements Annotable {
    public final static long           serialVersionUID = 1L;
    
    private final Map<String,Document> documents        = new LinkedHashMap<String,Document>();
    private final Set<String>          processedBy      = new LinkedHashSet<String>();

    /**
     * Creates a new empty corpus.
     */
    public Corpus() {
    	super(null, null);
    }

    @Override
	public String getStaticFeatureValue() {
    	return "";
    }

	@Override
	public <R, P> R accept(ElementVisitor<R, P> visitor, P param) {
		return visitor.visit(this, param);
	}

	/**
     * Iterator through documents in this corpus.
     */
    public Iterator<Document> documentIterator() {
        return documents.values().iterator();
    }
    
    /**
     * Iterator through documents in this corpus that satisfy the specified filter.
     * @param ctx TODO
     * @param filter
     */
    public Iterator<Document> documentIterator(EvaluationContext ctx, Evaluator filter) {
        if (filter == null)
            return documentIterator();
        return Filters.apply(filter.getFilter(ctx), documentIterator());
    }
    
    /**
     * Iterator through sections in this corpus that satisfy the specified filter.
     * Only sections that belong to documents that satisfy docFilter are iterated.
     * @param docFilter
     * @param secFilter
     */
    public Iterator<Section> sectionIterator(EvaluationContext ctx, Evaluator docFilter, final Evaluator secFilter) {
        return Iterators.flatten(Mappers.apply(Document.sectionCollector(ctx, secFilter), documentIterator(ctx, docFilter)));
    }

    static final class SectionToLayerMapper implements Mapper<Section,Layer> {
		private final String layerName;
		
		SectionToLayerMapper(String layerName) {
			super();
			this.layerName = layerName;
		}

		@Override
		public Layer map(Section sec) {
			return sec.ensureLayer(layerName);
		}
	}

    /**
     * Checks for document.
     * @param id
     * @return true, if successful
     */
    public boolean hasDocument(String id) {
        return documents.containsKey(id);
    }

    /**
     * Returns the document in this corpus with the specified id.
     * @param id identifier of the document to get
     * 
     * @return the document in this corpus with the specified id, or null if no document has the specified id
     */
    public Document getDocument(String id) {
        return documents.get(id);
    }
 
    /**
     * Removes the specified document from this corpus.
     * Does nothing if the specified docupment does not belong to this corpus.
     * @param doc
     */
    public void removeDocument(Document doc) {
    	if (doc.getCorpus() == this)
    		documents.remove(doc.getId());
    }

    /**
     * Adds a new document to this corpus.
     * @param doc document to add
     */
    public void addDocument(Document doc) {
        String id = doc.getId();
        if (documents.containsKey(id))
            throw new IllegalArgumentException("corpus already contains a document with id " + id);
        documents.put(id, doc);
    }

    @Override
    public boolean wasProcessedBy(String modulePath) {
        return processedBy.contains(modulePath);
    }
    
    @Override
    public Collection<String> wasProcessedBy() {
    	return Collections.unmodifiableCollection(processedBy);
    }

    @Override
	public void hasBeenProcessedBy(String modulePath) {
        processedBy.add(modulePath);
    }

    /**
     * XML serialization of this corpus.
     * @param out stream where to write the XML
     * @throws IOException 
     */
    public void toXML(PrintStream out) throws IOException {
        out.print("<alvisnlp-corpus>");
        for (String modulePath : processedBy) {
        	out.print("<processed-by module=\"");
        	out.print(modulePath);
        	out.print("\"/>");
        }
        for (Document doc : Iterators.loop(documentIterator()))
            doc.toXML(out);
        out.print("</alvisnlp-corpus>");
    }

    public int countDocuments() {
    	return documents.size();
    }
    
    /**
     * Returns the total number of annotations in this corpus.
     * This method traverses all sections of all documents.
     */
    public long countAnnotations() {
    	long result = 0;
    	for (Document doc : documents.values())
    		result += doc.countAnnotations();
    	return result;
    }

    /**
     * Returns the sum of the size of all layers in this corpus.
     * This method traverses all sections of all documents.
     */
    public long countPostings() {
    	long result = 0;
    	for (Document doc : documents.values())
    		result += doc.countPostings();
    	return result;
    }

	@Override
	public ElementType getType() {
		return ElementType.CORPUS;
	}

	@Override
	public Element getParent() {
		return this;
	}

	@Override
	public Element getOriginal() {
		return this;
	}
}
