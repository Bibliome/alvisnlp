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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bibliome.util.Strings;
import org.bibliome.util.filters.Filters;
import org.bibliome.util.mappers.Mappers;

import alvisnlp.corpus.creators.RelationCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;

/**
 * A Section object represents a distinct document part.
 */
public final class Section extends AbstractElement {
    public final static long           serialVersionUID = 1L;

    /**
     * Static feature representing the section name.
     */
    public static final String NAME_FEATURE_NAME = "name";
    
    private final Document             document;
    private final int                  ord;
    private final String               name;
    private final String               contents;
    private final Map<String,Layer>    layers           = new HashMap<String,Layer>();
    private final Map<String,Relation> relations        = new LinkedHashMap<String,Relation>();
    private final String               fileName;

    /**
     * Creates a new section.
     * All values in the creator mapping will be added to this section features.
     * @param sc section creator, typically a module
     * @param doc document to which this section belongs
     * @param name name of this section
     * @param contents contents of this section
     */
    public Section(SectionCreator sc, Document doc, String name, String contents, boolean autoLink) {
        super(NAME_FEATURE_NAME, sc);
        this.name = name;
        ord = doc.countSections();
        if (autoLink)
        	doc.addSection(this);
        this.document = doc;
        this.contents = contents;
        fileName = Strings.join(new String[] { document.getId(), Integer.toString(ord), name }, "__");
        addFeatures(sc.getConstantSectionFeatures());
    }

    public Section(SectionCreator sc, Document doc, String name, String contents) {
    	this(sc, doc, name, contents, true);
    }

    /**
     * Returns this section name.
     * @return this section name
     */
    public String getName() {
        return name;
    }

    @Override
	public String getStaticFeatureValue() {
    	return name;
	}

	/**
     * Returns this section order in the parent document.
     */
    public int getOrder() {
    	return ord;
    }
    
    /**
     * Returns the document this section belongs to.
     * @return the document this section belongs to
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Returns this section text.
     * @return this section text
     */
    public String getContents() {
        return contents;
    }

    /**
     * Returns all annotations in this section.
     */
    public Layer getAllAnnotations() {
        Layer result = new Layer(this);
        for (Layer layer : layers.values())
            for (Annotation ann : layer)
                result.add(ann);
        return result;
    }

    /**
     * Returns true if this section has a layer with the specified name.
     * @param name
     * @return true if this section has a layer with the specified name
     */
    public boolean hasLayer(String name) {
        if (layers == null)
            return false;
        return layers.containsKey(name);
    }

    /**
     * Returns this section layer with the specified name.
     * @param name name of the layer to get
     * @return this section layer with the specified name, or null there is no layer named <code>name</code>
     */
    public Layer getLayer(String name) {
        if (layers == null)
            return null;
        return layers.get(name);
    }
    
    /**
     * Returns a layer containing all annotations in the layer with the specified name but only annotations that satisfy the specified filter.
     * @param name
     * @param filter
     */
    public Layer getLayer(EvaluationContext ctx, String name, Evaluator filter) {
        Layer layer = getLayer(name);
        if (filter == null)
            return layer;
        Layer result = new Layer(this);
        Filters.apply(filter.getFilter(ctx), layer, result);
        return result;
    }

    void addLayer(Layer layer) {
    	String name = layer.getName();
        if (layers.containsKey(name))
            throw new IllegalArgumentException(name.toString());
        layers.put(name, layer);
    }

    /**
     * Returns this section layer with the specified name, create an empty layer if it does not exist.
     * @param name name of the layer to get
     * @return this section layer with the specified name, create an empty layer if it does not exist
     */
    public Layer ensureLayer(String name) {
        if (hasLayer(name))
            return getLayer(name);
        return new Layer(this, name);
    }

    /**
     * Returns a collection of anonymous sentence layers. Each layer corresponds
     * to an annotation from the sentenceLayerName layer in this section, and
     * contains all annotations in tokenLayerName layer contained in the
     * sentence.
     * @param tokenLayerName the token layer name
     * @param sentenceLayerName the sentence layer name
     * @return the sentences
     */
    public Collection<Layer> getSentences(String tokenLayerName, String sentenceLayerName) {
        if (!hasLayer(tokenLayerName))
            return Collections.emptyList();
        final Layer tokens = getLayer(tokenLayerName);
        if ((sentenceLayerName == null) || !hasLayer(sentenceLayerName))
            return Collections.singleton(tokens);
        Layer sentences = getLayer(sentenceLayerName);
        return Mappers.mappedCollection(tokens.betweenCollector(), sentences);
    }

    /**
     * Returns all the named layers in this section.
     * @return the layers
     */
    public Collection<Layer> getAllLayers() {
        return Collections.unmodifiableCollection(layers.values());
    }

    @Override
    public String toString() {
        return name + " of " + document.getId();
    }

    public void addRelation(Relation rel) {
        String name = rel.getName();
        if (relations.containsKey(name))
            throw new IllegalArgumentException("duplicate relation " + name);
        relations.put(name, rel);
    }

    /**
     * Returns all relations in this section.
     */
    public Collection<Relation> getAllRelations() {
        return Collections.unmodifiableCollection(relations.values());
    }

    /**
     * Returns the relation with the specified name.
     * @param name
     */
    public Relation getRelation(String name) {
        return relations.get(name);
    }
    
    /**
     * Returns either this section has a relation with the specified name.
     * @param name
     */
    public boolean hasRelation(String name) {
    	return relations.containsKey(name);
    }
    
    /**
     * Removes the specified relation from this section.
     * Does nothing if the specified relation does not belong to this section.
     * @param relation
     */
    public void removeRelation(Relation relation) {
    	if (relation.getSection() == this)
    		relations.remove(relation.getName());
    }
    
    /**
     * Returns the relation of this section with the specified name.
     * If this section has no relation with the specified name then an empty one is created.
     * @param rc
     * @param name
     */
    public Relation ensureRelation(RelationCreator rc, String name, boolean autoLink) {
    	if (relations.containsKey(name))
    		return relations.get(name);
    	return new Relation(rc, this, name, autoLink);
    }
    
    public Relation ensureRelation(RelationCreator rc, String name) {
    	return ensureRelation(rc, name, true);
    }
    
    void toXML(PrintStream out) throws IOException {
    	write(out,"<section name=\"" + Strings.escapeXML(name.toString()) + "\"><contents>" + Strings.escapeXML(contents) + "</contents>");
    	featuresToXML(out, "feature", "name", "value");
    	for (Annotation ann : getAllAnnotations())
    		ann.toXML(out);
    	for (Relation rel : getAllRelations())
    		rel.toXML(out);
    	if (layers != null)
    		for (Layer layer : layers.values())
    			layer.toXML(out);
    	write(out,"</section>");
    }

    /**
     * Returns a file name build with this section name, its document id and its order (in its document).
     * @return the tempFileName
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Returns the total number of annotations.
     */
    int countAnnotations() {
    	Set<Annotation> set = new HashSet<Annotation>();
    	for (Layer layer : layers.values())
    		set.addAll(layer);
    	return set.size();
    }
    
    /**
     * Returns the sum of the size of all layers in this section.
     */
    long countPostings() {
    	long result = 0;
    	for (Layer layer : layers.values())
    		result += layer.size();
    	return result;
    }

	@Override
	public <R,P> R accept(ElementVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}

	@Override
	public ElementType getType() {
		return ElementType.SECTION;
	}

	@Override
	public Element getParent() {
		return document;
	}

	@Override
	public Element getOriginal() {
		return this;
	}
}
