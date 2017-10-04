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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import alvisnlp.corpus.creators.TupleCreator;

/**
 * Tuple.
 */
public class Tuple extends AbstractElement {
    private static final long serialVersionUID = 1L;

    private final Relation     relation;
    private final Map<String,WeakReference<Element>> arguments = new LinkedHashMap<String,WeakReference<Element>>();

    /**
     * Creates a new tuple.
     * All values in the creator mapping will be added to this tuple features.
     * @param tc tuple creator, typically a module
     * @param relation relation to which this tuple belongs
     */
    public Tuple(TupleCreator tc, Relation relation, boolean autoLink) {
        super(null, tc);
        this.relation = relation;
        if (autoLink)
        	relation.addTuple(this);
        addFeatures(tc.getConstantTupleFeatures());
    }

    public Tuple(TupleCreator tc, Relation relation) {
    	this(tc, relation, true);
    }
    
    @Override
	public String getStaticFeatureValue() {
		return "";
	}
    
    private void clean() {
    	Collection<WeakReference<Element>> values = arguments.values();
    	Iterator<WeakReference<Element>> it = values.iterator();
    	while (it.hasNext()) {
    		WeakReference<Element> ref = it.next();
    		if (ref.get() == null)
    			it.remove();
    	}
    }

	/**
     * Returns the arity of this tuple.
     */
    public int getArity() {
    	clean();
        return arguments.size();
    }

    /**
     * Returns all arguments.
     */
    public Collection<Element> getAllArguments() {
    	List<Element> result = new ArrayList<Element>(arguments.size());
    	for (WeakReference<Element> ref : arguments.values()) {
    		Element elt = ref.get();
    		if (elt != null)
    			result.add(elt);
    	}
    	Collections.sort(result, ElementComparator.INSTANCE);
        return result;
    }

    /**
     * Returns the argument corresponding to the specified role name.
     * @param role
     */
    public Element getArgument(String role) {
    	clean();
    	return arguments.get(role).get();
    }

    /**
     * Returns either this tuple has an argument with the specified role.
     * @param role
     */
    public boolean hasArgument(String role) {
    	clean();
    	return arguments.containsKey(role);
    }
    
    public boolean hasArgument(Element elt) {
    	for (WeakReference<Element> ref : arguments.values())
    		if (ref.get() == elt)
    			return true;
    	return false;
    }
    
    /**
     * Returns the roles for which there is an argument.
     */
    public Collection<String> getRoles() {
    	clean();
    	return arguments.keySet();
    }
    
    /**
     * Set an argument.
     * If arg is null, then the argument with the specified role is removed.
     * @param role
     * @param arg
     */
    public void setArgument(String role, Element arg) {
    	if (arg == null)
    		arguments.remove(role);
    	else
    		arguments.put(role, new WeakReference<Element>(arg));
    }

    /**
     * Returns the relation this tuple belongs to.
     */
    public Relation getRelation() {
        return relation;
    }

    void toXML(PrintStream out) throws IOException {
    	out.print("<t>");
    	featuresToXML(out, "f", "n", "v");
    	for (Map.Entry<String,WeakReference<Element>> e : arguments.entrySet()) {
    		Element elt = e.getValue().get();
    		if (elt != null)
    			out.print("<g r=\"" + e.getKey() + "\" a=\"" + elt.hashCode() + "\"/>");
    	}
    	out.print("</t>");
    }

	@Override
	public <R,P> R accept(ElementVisitor<R,P> visitor, P param) {
		clean();
		return visitor.visit(this, param);
	}

	@Override
	public ElementType getType() {
		return ElementType.TUPLE;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(relation.getName());
		sb.append(" : {");
		boolean notFirst = false;
		for (Map.Entry<String,WeakReference<Element>> e : arguments.entrySet()) {
			if (notFirst)
				sb.append(", ");
			else
				notFirst = true;
			sb.append(e.getKey());
			sb.append(": ");
			sb.append(e.getValue().get());
		}
		sb.append('}');
		return sb.toString();
	}

	@Override
	public Element getParent() {
		return relation;
	}

	@Override
	public Element getOriginal() {
		return this;
	}
}
