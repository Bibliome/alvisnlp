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

import org.bibliome.util.fragments.Fragment;

import alvisnlp.corpus.creators.AnnotationCreator;

/**
 * An annotation is a fragment of text typically created by modules.
 */
public final class Annotation extends AbstractElement implements Fragment {
    public final static long   serialVersionUID  = 1L;

    /**
     * The annotation form is the section contents substring at this annotation's position.
     */
    public static final String FORM_FEATURE_NAME = "form";

    private final Section      section;
    private final int          start;
    private final int          end;

    /**
     * Creates a new annotation.
     * All features in the annotation creator mapping will be added to this annotation.
     * @param ac annotation creator, typically the module that requires the creation of this annotation
     * @param section section to which belongs this annotation
     * @param start start position
     * @param end end position
     * @throws IllegalArgumentException if start > end, or start < 0, or end < 0, or end > section contents length, or start > section contents length
     */
    public Annotation(AnnotationCreator ac, Section section, int start, int end) {
        super(FORM_FEATURE_NAME, ac);
        if (start > end)
            throw new IllegalArgumentException("illegal Annotation boundaries: " + start + " > " + end);
        if (start < 0)
        	throw new IllegalArgumentException("illegal Annotation start: " + start + " < 0");
        if (end < 0)
        	throw new IllegalArgumentException("illegal Annotation end: " + start + " < 0");
        int secLen = section.getContents().length();
        if (end > secLen)
        	throw new IllegalArgumentException("illegal Annotation end: " + end + " > " + secLen + " (" + section + ")");
        if (start > secLen)
        	throw new IllegalArgumentException("illegal Annotation start: " + start + " > " + secLen + " (" + section + ")");
        this.start = start;
        this.end = end;
        this.section = section;
        addFeatures(ac.getConstantAnnotationFeatures());
    }

    /**
     * Creates a new annotation and adds it into the specified layer.
     * This annotation belongs to the same section than the specified layer.
     * All features in the annotation creator mapping will be added to this annotation.
     * @param ac annotation creator, typically the module that requires the creation of this annotation
     * @param layer layer where to add this annotation
     * @param start start position
     * @param end end position
     * @throws IllegalArgumentException if start > end, or start < 0, or end < 0, or end > section contents length, or start > section contents length
     */
    public Annotation(AnnotationCreator ac, Layer layer, int start, int end) {
        this(ac, layer.getSection(), start, end);
        layer.add(this);
    }

    /**
     * Returns the section to which this annotation belongs.
     * 
     * @return the section to which this annotation belongs
     */
    public Section getSection() {
        return section;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    /**
     * Returns the length of this annotation.
     * @return the length of this annotation
     */
    public int getLength() {
        return end - start;
    }

    /**
     * Checks if this annotation includes the specified annotation.
     * @param that query annotation
     * @return true if <code>that</code> is fully included in this annotation
     */
    public boolean includes(Annotation that) {
        return (getStart() <= that.getStart()) && (getEnd() >= that.getEnd());
    }

    /**
     * Checks if this annotation has the same boundaries as the specified
     * annotation.
     * @param that query annotation
     * @return true if this annotation and <code>that</code> have the same start and end positions
     */
    public boolean sameSpan(Annotation that) {
        return (getStart() == that.getStart()) && (getEnd() == that.getEnd());
    }

    /**
     * Checks if this annotation overlaps the specified annotation.
     * @param that query annotation
     * @return true if this annotation and <code>that</code> overlap
     */
    public boolean overlaps(Annotation that) {
        return (that.getStart() < getEnd()) && (getStart() < that.getEnd());
    }

    /**
     * Returns the surface form of the annotation.
     * @return the surface form of the annotation
     */
    public String getForm() {
        return section.getContents().substring(getStart(), getEnd());
    }

    @Override
	public String getStaticFeatureValue() {
    	return getForm();
	}

	/**
     * XML serialization of this annotation.
     * @param out stream where to serialize
     * @throws IOException 
     */
    void toXML(PrintStream out) throws IOException {
    	write(out,"<a id=\"" + hashCode() + "\" s=\"" + getStart() + "\" e=\"" + getEnd() + "\"");
    	if (isFeatureless()) {
    		write(out,"/>");
    		return;
    	}
    	write(out,">");
    	featuresToXML(out, "f", "n", "v");
    	write(out,"</a>");
    }

    @Override
    public String toString() {
        return getForm() + ":" + start + "-" + end;
    }

	@Override
	public <R,P> R accept(ElementVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}

	@Override
	public ElementType getType() {
		return ElementType.ANNOTATION;
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
