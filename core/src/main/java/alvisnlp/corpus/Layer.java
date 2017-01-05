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
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.bibliome.util.Strings;
import org.bibliome.util.defaultmap.DefaultArrayListHashMap;
import org.bibliome.util.defaultmap.DefaultMap;
import org.bibliome.util.mappers.Mapper;
import org.bibliome.util.mappers.Mappers;


/**
 * A layer is an annotation container. Each layer belongs to a single section,
 * all annotations in a layer belong to the same section. An annotation can be
 * present in several layers. Layers can be named, the layer name is unique
 * within a section. There can be an unlimited number of anonymous layers in a
 * section.
 * Annotation order is maintained.
 * @author rbossy
 */
public class Layer extends AbstractCollection<Annotation> implements Serializable {
    private static final long      serialVersionUID   = 1L;

    private final String           name;
    private final Section          section;
    private final List<Annotation> annotations;
    private Annotation             sentenceAnnotation = null;

    private Layer(Section section, String name, List<Annotation> annotations) {
        this.name = name;
        this.section = section;
        this.annotations = new ArrayList<Annotation>(annotations);
        Collections.sort(this.annotations, AnnotationComparator.byOrder);
        if (name != null)
            section.addLayer(this);
    }

    /**
     * Constructs a new named empty layer.
     * @param section section to which this layer belongs
     * @param name name of this layer
     */
    public Layer(Section section, String name) {
        this(section, name, new ArrayList<Annotation>());
    }

    /**
     * Constructs a new anonymous empty layer.
     * Anonymous layers cannot be retrieved from the section.
     * @param section section to which this layer belongs
     */
    public Layer(Section section) {
        this(section, null);
    }

    /**
     * Returns the name of this layer, or null if this layer is anonymous.
     * @return the name of this layer, or null if this layer is anonymous
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the section to which this layer belongs.
     * @return the section to which this layer belongs
     */
    public Section getSection() {
        return section;
    }

    /**
     * Return an anonymous copy of this layer.
     */
    public Layer getAnonymousCopy() {
    	return new Layer(section, null, annotations);
    }

    @Override
	public int size() {
        return annotations.size();
    }

    @Override
	public Iterator<Annotation> iterator() {
        return annotations.iterator();
    }

    /**
     * Return the index of the specified annotation.
     * @param annot
     * @return the index of the specified annotation, or -(insersionPoint + 1) if the specified annotation is not in this layer.
     */
    public int index(Annotation annot) {
        return Collections.binarySearch(annotations, annot, AnnotationComparator.byOrder);
    }

    @Override
	public boolean add(Annotation annot) {
        if (annot.getSection() != section)
            throw new IllegalArgumentException();
        if (annotations.isEmpty()) {
            annotations.add(annot);
            return true;
        }
        int i = index(annot);
        if (i >= 0) {
            if (annotations.get(i) == annot) // annotation already there
                return false;
        }
        else
            i = -(i + 1);
        annotations.add(i, annot);
        return true;
    }

    /**
     * Removes the specified annotation from this layer.
     * @param annot annotation to remove
     * @return either if this layer contents has been changed
     */
    public boolean remove(Annotation annot) {
    	int start = annot.getStart();
    	for (int i = searchStartLeft(start); i < annotations.size(); ++i) {
    		Annotation a = annotations.get(i);
    		if (a == annot) {
    			annotations.remove(i);
    			return true;
    		}
    		if (a.getStart() > start) {
    			break;
    		}
    	}
    	return false;
    }

    /**
     * Returns true iff there are at least two overlapping annotations in this layer.
     */
    public boolean hasOverlaps() {
    	int reach = 0;
    	for (Annotation annot : annotations) {
    		if (annot.getStart() < reach)
    			return true;
    		if (annot.getEnd() > reach)
    			reach = annot.getEnd();
    	}
    	return false;
    }

    private int searchStartLeft(int pos) {
        int lo = 0;
        int hi = annotations.size();
        while (lo < hi) {
            int mid = (lo + hi) / 2;
            if (annotations.get(mid).getStart() < pos)
                lo = mid + 1;
            else
                hi = mid;
        }
        return lo;
    }

    private int searchStartRight(int pos) {
        int lo = 0;
        int hi = annotations.size();
        while (lo < hi) {
            int mid = (lo + hi) / 2;
            if (annotations.get(mid).getStart() > pos)
                hi = mid;
            else
                lo = mid + 1;
        }
        return lo;
    }

    private Layer subLayer(List<Annotation> list) {
        return new Layer(section, null, list);
    }
    
    /**
     * Returns a layer containing annotations in this layer at the specified positions.
     * @param from
     * @param to
     */
    public Layer subLayer(int from, int to) {
    	return subLayer(annotations.subList(from, to));
    }

    /**
     * Returns all annotations completely contained in the specified span.
     * Annotations in the returned layer have start points between from
     * (inclusive) and to (exclusive), and end points between from and to (both
     * inclusive).
     * @param from lower bound of the returned layer
     * @param to higher bound of the returned layer
     * @return all annotations completely contained in the specified span
     */
    public Layer between(int from, int to) {
        int fromi = searchStartLeft(from);
        int toi = searchStartRight(to);
        List<Annotation> list;
        if (hasOverlaps()) {
            list = new ArrayList<Annotation>();
            for (Annotation annot : annotations.subList(fromi, toi))
                if (annot.getEnd() <= to)
                    list.add(annot);
        }
        else {
            if ((toi > 0) && (annotations.get(toi - 1).getEnd() > to))
                toi--;
            if (toi < fromi)
                list = Collections.emptyList();
            else
                list = annotations.subList(fromi, toi);
        }
        return subLayer(list);
    }

    /**
     * Returns all annotations in this layer completely contained in the
     * specified annotation. If the specified annotation belongs to the same
     * section as this layer, then it will necessarily be included in th
     * returned layer.
     * @param annot 
     * @return all annotations in this layer completely contained in the specified annotation
     */
    public Layer between(Annotation annot) {
        return between(annot.getStart(), annot.getEnd());
    }
    
    private static final class BetweenCollector implements Mapper<Annotation,Layer> {
        private final Layer layer;

        public BetweenCollector(Layer layer) {
            super();
            this.layer = layer;
        }

        @Override
        public Layer map(Annotation sent) {
            Layer result = layer.between(sent);
            result.setSentenceAnnotation(sent);
            return result;
        }
    }
    
    Mapper<Annotation,Layer> betweenCollector() {
        return new BetweenCollector(this);
    }

    /**
     * Returns all annotations that start at or after the specified position.
     * @param from minimum start position
     * @return all annotations that start at or after the specified position
     */
    public Layer after(int from) {
        return subLayer(annotations.subList(searchStartRight(from), annotations.size()));
    }

    /**
     * Returns all annotations that end at or before the specified position.
     * @param to maximum end position
     * @return all annotations that end at or before the specified position
     */
    public Layer before(int to) {
        int toi = searchStartRight(to);
        List<Annotation> list;
        if (hasOverlaps()) {
            list = new ArrayList<Annotation>();
            for (Annotation annot : annotations.subList(0, toi))
                if (annot.getEnd() <= to)
                    list.add(annot);
        }
        else {
            if ((toi > 0) && (annotations.get(toi - 1).getEnd() > to))
                toi--;
            list = annotations.subList(0, toi);
        }
        return subLayer(list);
    }

    /**
     * Returns all annotations that overlap the specified span.
     * @param from
     * @param to
     * @return all annotations that overlap the specified span
     */
    public Layer overlapping(int from, int to) {
        int fromi = searchStartLeft(from);
        int toi = searchStartRight(to);
        List<Annotation> list;
        if (hasOverlaps()) {
            list = new ArrayList<Annotation>();
            for (Annotation annot : annotations.subList(0, fromi))
                if (annot.getEnd() > from)
                    list.add(annot);
            for (Annotation annot : annotations.subList(fromi, toi))
                list.add(annot);
        }
        else {
            if ((fromi > 0) && (annotations.get(fromi - 1).getEnd() > from))
                fromi--;
            list = annotations.subList(fromi, toi);
        }
        return subLayer(list);
    }

    /**
     * Returns all annotations in this layer that overlap the specified
     * annotation. If the specified annotation belongs to the same section than
     * this layer, then it will necessarily be included in the returned value.
     * @param annot
     * @return all annotations in this layer that overlap the specified annotation
     */
    public Layer overlapping(Annotation annot) {
        return overlapping(annot.getStart(), annot.getEnd());
    }

    /**
     * Returns all annotation that includes the specified span.
     * @param from
     * @param to
     */
    public Layer including(int from, int to) {
    	Layer result = new Layer(section);
    	int n = searchStartRight(from);
    	for (int i = 0; i < n; ++i) {
    		Annotation a = get(i);
    		if (a.getEnd() >= to)
    			result.add(a);
    	}
    	return result;
    }
    
    /**
     * Returns all annotations that include the specified annotation.
     * If the specified annotation belongs to the same section as this layer, then the specified annotation is included in the result.
     * @param a
     */
    public Layer including(Annotation a) {
    	return including(a.getStart(), a.getEnd());
    }

    /**
     * Returns all annotations in this layer that have the same span as the
     * specified annotation. If the specified annotation belongs to the same
     * section as this layer then it is included in the result.
     * @param annot
     * @return all annotations in this layer that have the same span as the specified annotation
     */
    public Layer span(Annotation annot) {
        int i = index(annot);
        List<Annotation> list;
        if (i < 0)
            list = Collections.emptyList();
        else if (hasOverlaps()) {
            list = new ArrayList<Annotation>();
            ListIterator<Annotation> lit = annotations.listIterator(i);
            while (lit.hasNext()) {
                Annotation a = lit.next();
                if (!annot.sameSpan(a))
                    break;
                list.add(a);
            }
            lit = annotations.listIterator(i);
            while (lit.hasPrevious()) {
                Annotation a = lit.previous();
                if (!annot.sameSpan(a))
                    break;
                list.add(a);
            }
        }
        else
            list = annotations.subList(i, i + 1);
        return subLayer(list);
    }

    /**
     * Returns all annotations that have the specified position.
     * @param from
     * @param to
     */
    public Layer span(int from, int to) {
    	Layer result = new Layer(section);
    	for (int i = searchStartLeft(from); i < annotations.size(); ++i) {
    		Annotation a = annotations.get(i);
    		if (a.getStart() > from)
    			break;
    		if (a.getEnd() == to)
    			result.add(a);
    	}
    	return result;
    }

    @Override
    public String toString() {
    	return section.toString() + ", layer " + name;
    }
    
    private static enum OverlapType {
    	EQUAL,
    	INCLUDED,
    	STRICT;
    }
    
    private static OverlapType getOverlapType(Annotation a1, Annotation a2) {
    	if (a1.sameSpan(a2))
    		return OverlapType.EQUAL;
    	if (a1.includes(a2))
    		return OverlapType.INCLUDED;
    	if (a2.includes(a1))
    		return OverlapType.INCLUDED;
    	if (a1.overlaps(a2))
    		return OverlapType.STRICT;
    	return null;
    }
    
    /**
     * Removes overlapping annotations in this layer.
     * @param comp comparator used to chose between overlapping annotations
     * @param removeEqual either to remove annotations with the exact same span
     * @param removeIncluded either to remove annotations included
     * @param removeOverlapping either to remove strict overlapping annotations
     */
	public void removeOverlaps(Comparator<Annotation> comp, boolean removeEqual, boolean removeIncluded, boolean removeOverlapping) {
        if (!hasOverlaps()) {
            return;
        }
        DefaultMap<Annotation,List<Annotation>> clusters = new DefaultArrayListHashMap<Annotation,Annotation>();
        for (int i = 0; i < annotations.size(); i++) {
            Annotation a1 = annotations.get(i);
            for (int j = i + 1; j < annotations.size(); j++) {
                Annotation a2 = annotations.get(j);
                if (a2.getStart() >= a1.getEnd())
                    break;
                boolean remove = false;
                OverlapType ot = getOverlapType(a1, a2);
                switch (ot) {
                case EQUAL: remove = removeEqual; break;
                case INCLUDED: remove = removeIncluded; break;
                case STRICT: remove = removeOverlapping; break;
                }
                if (!remove)
                	continue;
                int c = comp.compare(a1, a2);
                if (c >= 0)
                	clusters.safeGet(a1).add(a2);
                if (c <= 0)
                	clusters.safeGet(a2).add(a1);
            }
        }
        List<Annotation> sortedAnnotations = new ArrayList<Annotation>(annotations);
        Collections.sort(sortedAnnotations, comp);
        Collections.reverse(sortedAnnotations);
        for (Annotation a : sortedAnnotations) {
        	Collection<Annotation> toRemove = new ArrayList<Annotation>(clusters.safeGet(a));
        	for (Annotation a2 : toRemove) {
        		annotations.remove(a2);
        		clusters.safeGet(a2).clear();
        	}
        }
    }

	public void removeOverlaps(Comparator<Annotation> comp) {
		removeOverlaps(comp, true, true, true);
	}
	
    private final static Mapper<Annotation,String> annotationIdMapper = new Mapper<Annotation,String>() {
        @Override
        public String map(Annotation x) {
            return Integer.toString(x.hashCode());
        }
    };
    
    /**
     * Serializes this layer in XML format into the specified stream.
     * @param out
     * @throws IOException 
     */
    void toXML(PrintStream out) throws IOException {
        out.print("<layer name=\"");
        out.print(Strings.escapeXML(name.toString()));
        out.print("\" annotations=\"");
        Strings.join(out, Mappers.mappedList(annotationIdMapper, annotations), ',');
        out.print("\"/>");
    }

    /**
     * Returns the first annotation in this layer.
     * @return the first annotation in this layer
     */
    public Annotation first() {
        if (annotations.isEmpty())
            return null;
        return annotations.get(0);
    }

    /**
     * Returns the last annotation in this layer.
     * @return the last annotation in this layer
     */
    public Annotation last() {
        if (annotations.isEmpty())
        	return null;
        return annotations.get(annotations.size() - 1);
    }

    /**
     * Returns the ith annotation in canonical order.
     * @param i
     */
    public Annotation get(int i) {
        return annotations.get(i);
    }

    public void setSentenceAnnotation(Annotation sentenceAnnotation) {
        this.sentenceAnnotation = sentenceAnnotation;
    }

    /**
     * Gets the sentence annotation if this layer has been build by Section.getSentences().
     * @return the sentenceAnnotation
     */
    public Annotation getSentenceAnnotation() {
        return sentenceAnnotation;
    }
    
    public List<Element> asElementList() {
    	return Collections.unmodifiableList((List<? extends Element>) annotations);
    }
}
