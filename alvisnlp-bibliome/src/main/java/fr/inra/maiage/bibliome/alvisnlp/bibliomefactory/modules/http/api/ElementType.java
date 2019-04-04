package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DownCastElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;

abstract class ElementType<P extends Element> {
	private final String name;
	
	public ElementType(String name) {
		super();
		this.name = name;
	}

	protected abstract P cast(Element elt);
	
	String getName() {
		return name;
	}
	
	static final ElementType<Corpus> CORPUS = new ElementType<Corpus>("corpus") {
		@Override
		protected Corpus cast(Element elt) {
			return DownCastElement.toCorpus(elt);
		}
	};
	
	static final ElementType<Document> DOCUMENT = new ElementType<Document>("document") {
		@Override
		protected Document cast(Element elt) {
			return DownCastElement.toDocument(elt);
		}
	};
	
	static final ElementType<Section> SECTION = new ElementType<Section>("section") {
		@Override
		protected Section cast(Element elt) {
			return DownCastElement.toSection(elt);
		}
	};
	
	static final ElementType<Relation> RELATION = new ElementType<Relation>("relation") {
		@Override
		protected Relation cast(Element elt) {
			return DownCastElement.toRelation(elt);
		}
	};

	static final ElementType<Tuple> TUPLE = new ElementType<Tuple>("tuple") {
		@Override
		protected Tuple cast(Element elt) {
			return DownCastElement.toTuple(elt);
		}
	};
	
	static final ElementType<Element> ANY = new ElementType<Element>("element") {
		@Override
		protected Element cast(Element elt) {
			return elt;
		}
	};
}
