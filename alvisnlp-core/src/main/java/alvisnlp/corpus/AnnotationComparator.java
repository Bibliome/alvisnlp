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

import java.util.Arrays;
import java.util.Comparator;

import org.bibliome.util.Strings;

/**
 * Annotation comparators are used for ordering annotations in layers or
 * choosing annotations when removing overlaps.
 */
public abstract class AnnotationComparator implements Comparator<Annotation> {

    /** Annotation comparator by ascending start position. */
	public static final AnnotationComparator byStart  = new AnnotationComparator() {
		@Override
		public int compare(Annotation a1, Annotation a2) {
			return a1.getStart() - a2.getStart();
		}

		@Override
		public String toString() {
			return "start";
		}
	};

	/** Annotation comparator by ascending end position. */
	public static final AnnotationComparator byEnd    = new AnnotationComparator() {
		@Override
		public int compare(Annotation a1, Annotation a2) {
			return a1.getEnd() - a2.getEnd();
		}

		@Override
		public String toString() {
			return "end";
		}
	};

    /** Annotation comparator by ascending length. */
    public static final AnnotationComparator byLength = new AnnotationComparator() {
		@Override
		public int compare(Annotation a1, Annotation a2) {
			return a1.getLength() - a2.getLength();
		}

		@Override
		public String toString() {
			return "length";
		}
    };

    /**
     * Returns an annotation comparator by ascending attribute value with the specified key.
     * @param name name of the attribute to compare
     * @return an annotation comparator by ascending attribute value with the specified key
     */
    public static AnnotationComparator byFeature(final String name) {
        return new AnnotationComparator() {
    		@Override
    		public int compare(Annotation a1, Annotation a2) {
    			String v1 = a1.getLastFeature(name);
    			String v2 = a2.getLastFeature(name);
    			if (v1 == null) {
    				if (v2 == null)
    					return 0;
    				return -1;
    			}
    			if (v2 == null)
    				return 1;
    			return v1.compareTo(v2);
    		}

    		@Override
    		public String toString() {
    			return name;
    		}
        };
    }
    
    public static final AnnotationComparator byIntFeature(final String name) {
        return new AnnotationComparator() {
    		@Override
            public int compare(Annotation a1, Annotation a2) {
                String v1 = a1.getLastFeature(name);
                String v2 = a2.getLastFeature(name);
                if (v1 == null) {
                    if (v2 == null)
                        return 0;
                    return -1;
                }
                if (v2 == null)
                    return 1;
                //return Integer.compare(Strings.getInteger(v1, Integer.MIN_VALUE), Strings.getInteger(v2, Integer.MIN_VALUE));
                return new Integer(Strings.getInteger(v1, Integer.MIN_VALUE)).compareTo(Strings.getInteger(v2, Integer.MIN_VALUE));
            }

            @Override
			public String toString() {
                return name;
            }
        };
    }
    
    public static final AnnotationComparator byDoubleFeature(final String name) {
        return new AnnotationComparator() {
    		@Override
            public int compare(Annotation a1, Annotation a2) {
                String v1 = a1.getLastFeature(name);
                String v2 = a2.getLastFeature(name);
                if (v1 == null) {
                    if (v2 == null)
                        return 0;
                    return -1;
                }
                if (v2 == null)
                    return 1;
                return new Double(Strings.getDouble(v1, Double.MIN_VALUE)).compareTo(Strings.getDouble(v2, Double.MIN_VALUE));
            }

            @Override
			public String toString() {
                return name;
            }
        };
    }

    /**
     * Returns an annotation comparator exploiting a sequence of comparators.
     * When comparing two annotations, the comparator will try each comparator in the given order.
     * The result will be the result of the first comparator that returns a non-zero value.
     * If the last comparator returns zero, the comparator returns zero.
     * @param comps the comps
     * @return an annotation comparator exploiting a sequence of comparators
     */
    public static AnnotationComparator multiple(final AnnotationComparator... comps) {
        return new AnnotationComparator() {
            @Override
			public int compare(Annotation a1, Annotation a2) {
                int c;
                for (AnnotationComparator comp : comps) {
                    c = comp.compare(a1, a2);
                    if (c != 0)
                        return c;
                }
                return 0;
            }

            @Override
			public String toString() {
                return Strings.joinStrings(Arrays.asList(comps), ',');
            }
        };
    }

    /**
     * Annotation comparator by ascending start position, then by descending
     * length, then by an arbitrary order based on annotations hash codes.
     */
    public static final AnnotationComparator byOrder = new AnnotationComparator() {
		@Override
    	public int compare(Annotation a1, Annotation a2) {
    		int s1 = a1.getStart();
    		int s2 = a2.getStart();
    		if (s1 != s2)
    			return s1 - s2;
    		return a2.getEnd() - a1.getEnd();
    	}

    	@Override
    	public String toString() {
    		return "order";
    	}
    };

    /**
     * Returns a reverse comparator of the specified comparator.
     * @param comp original comparator
     * @return a reverse comparator of the specified comparator
     */
    public static AnnotationComparator reverse(final AnnotationComparator comp) {
        return new AnnotationComparator() {
    		@Override
            public int compare(Annotation a1, Annotation a2) {
                return comp.compare(a2, a1);
            }

            @Override
			public String toString() {
                return "reverse-" + comp.toString();
            }
        };
    }
}
