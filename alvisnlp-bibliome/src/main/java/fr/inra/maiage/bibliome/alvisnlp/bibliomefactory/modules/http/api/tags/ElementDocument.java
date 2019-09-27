package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.tags;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ElementVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;

public enum ElementDocument implements ElementVisitor<Document,Void> {
	INSTANCE;

	@Override
	public Document visit(Annotation a, Void param) {
		return a.getSection().getDocument();
	}

	@Override
	public Document visit(Corpus corpus, Void param) {
		return null;
	}

	@Override
	public Document visit(Document doc, Void param) {
		return doc;
	}

	@Override
	public Document visit(Relation rel, Void param) {
		return rel.getSection().getDocument();
	}

	@Override
	public Document visit(Section sec, Void param) {
		return sec.getDocument();
	}

	@Override
	public Document visit(Tuple t, Void param) {
		return t.getRelation().getSection().getDocument();
	}

	@Override
	public Document visit(Element e, Void param) {
		return e.getParent().accept(this, param);
	}
}
