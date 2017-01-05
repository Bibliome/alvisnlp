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

/**
 * Down casts an Element to one of the Element subclasses.
 * @author rbossy
 *
 * @param <E>
 */
public abstract class DownCastElement<E extends Element> implements ElementVisitor<E,Void> {
	private DownCastElement() {}
	
	@Override
	public E visit(Annotation a, Void param) {
		return null;
	}

	@Override
	public E visit(Corpus corpus, Void param) {
		return null;
	}

	@Override
	public E visit(Document doc, Void param) {
		return null;
	}

	@Override
	public E visit(Relation rel, Void param) {
		return null;
	}

	@Override
	public E visit(Section sec, Void param) {
		return null;
	}

	@Override
	public E visit(Tuple t, Void param) {
		return null;
	}
	
	@Override
	public E visit(Element e, Void param) {
		return null;
	}

	private static final DownCastElement<Annotation> toAnnotation = new DownCastElement<Annotation>() {
		@Override
		public Annotation visit(Annotation a, Void param) {
			return a;
		}
	};
	
	/**
	 * Returns the specified element if it is an annotation, otherwise null.
	 * @param elt
	 * @return the specified element if it is an annotation, otherwise null
	 */
	public static final Annotation toAnnotation(Element elt) {
		return elt.accept(toAnnotation, null);
	}
	
	private static final DownCastElement<Corpus> toCorpus = new DownCastElement<Corpus>() {
		@Override
		public Corpus visit(Corpus corpus, Void param) {
			return corpus;
		}
	};
		
	/**
	 * Returns the specified element if it is a corpus, otherwise null.
	 * @param elt
	 * @return the specified element if it is a corpus, otherwise null
	 */
	public static final Corpus toCorpus(Element elt) {
		return elt.accept(toCorpus, null);
	}
	
	private static final DownCastElement<Document> toDocument = new DownCastElement<Document>() {
		@Override
		public Document visit(Document doc, Void param) {
			return doc;
		}
	};
	
	
	/**
	 * Returns the specified element if it is a document, otherwise null.
	 * @param elt
	 * @return the specified element if it is a document, otherwise null
	 */
	public static final Document toDocument(Element elt) {
		return elt.accept(toDocument, null);
	}
	
	private static final DownCastElement<Relation> toRelation = new DownCastElement<Relation>() {
		@Override
		public Relation visit(Relation rel, Void param) {
			return rel;
		}
	};
	
	
	/**
	 * Returns the specified element if it is a relation, otherwise null.
	 * @param elt
	 * @return the specified element if it is a relation, otherwise null
	 */
	public static final Relation toRelation(Element elt) {
		return elt.accept(toRelation, null);
	}
	
	private static final DownCastElement<Section> toSection = new DownCastElement<Section>() {
		@Override
		public Section visit(Section sec, Void param) {
			return sec;
		}
	};
	
	
	/**
	 * Returns the specified element if it is a section, otherwise null.
	 * @param elt
	 * @return the specified element if it is a section, otherwise null
	 */
	public static final Section toSection(Element elt) {
		return elt.accept(toSection, null);
	}
	
	private static final DownCastElement<Tuple> toTuple = new DownCastElement<Tuple>() {
		@Override
		public Tuple visit(Tuple t, Void param) {
			return t;
		}
	};
	
	
	/**
	 * Returns the specified element if it is a tuple, otherwise null.
	 * @param elt
	 * @return the specified element if it is a tuple, otherwise null
	 */
	public static final Tuple toTuple(Element elt) {
		return elt.accept(toTuple, null);
	}
}
