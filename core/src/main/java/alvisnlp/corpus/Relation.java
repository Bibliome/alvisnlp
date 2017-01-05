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
import java.util.LinkedHashSet;

import alvisnlp.corpus.creators.RelationCreator;

/**
 * Relation.
 */
public class Relation extends AbstractElement {
    private static final long serialVersionUID = 1L;

    public static final String NAME_FEATURE_NAME = "name";
    
    private final Section section;
    private final String              name;
    private final Collection<Tuple>   tuples      = new LinkedHashSet<Tuple>();
    
    /**
     * Creates a new relation.
     * All values in the relation creator mapping will be added to this relation featires.
     * @param rc relation creator, typically a module
     * @param section section to which this relation belongs
     * @param name name of this relation
     */
    public Relation(RelationCreator rc, Section section, String name, boolean autoLink) {
        super(NAME_FEATURE_NAME, rc);
        this.section = section;
        this.name = name;
        if (autoLink)
        	section.addRelation(this);
        addFeatures(rc.getConstantRelationFeatures());
    }

    public Relation(RelationCreator rc, Section section, String name) {
    	this(rc, section, name, true);
    }
    
    /**
     * Returns the name of this relation.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the section to which this relation belongs.
     */
    public Section getSection() {
		return section;
	}

	@Override
	public String getStaticFeatureValue() {
    	return name;
	}

	/**
     * Returns all the tuples in this relation.
     * @return the tuples
     */
    public Collection<Tuple> getTuples() {
        return Collections.unmodifiableCollection(tuples);
    }
    
    /**
     * Remove the specified tuple from this relation.
     * Does nothing if the specified tuple does not belong to this relation.
     * @param t
     */
    public void removeTuple(Tuple t) {
    	if (t.getRelation() == this)
    		tuples.remove(t);
    }
    
    public void removeTuples(Collection<Tuple> c) {
    	tuples.removeAll(c);
    }

    /**
     * Adds a tuple to this relation.
     * @param tuple
     */
    public void addTuple(Tuple tuple) {
        tuples.add(tuple);
    }

    public int size() {
    	return tuples.size();
    }
    
    /**
     * XML serialization of this relation.
     * @param out
     * @throws IOException 
     */
    void toXML(PrintStream out) throws IOException {
    	write(out,"<relation name=\"" + name + "\">");
    	featuresToXML(out, "feat", "name", "value");
    	for (Tuple t : tuples)
    		t.toXML(out);
    	write(out,"</relation>");
    }

	@Override
	public <R,P> R accept(ElementVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}

	@Override
	public ElementType getType() {
		return ElementType.RELATION;
	}

	@Override
	public Element getParent() {
		return section;
	}

	@Override
	public Element getOriginal() {
		return this;
	}
}
