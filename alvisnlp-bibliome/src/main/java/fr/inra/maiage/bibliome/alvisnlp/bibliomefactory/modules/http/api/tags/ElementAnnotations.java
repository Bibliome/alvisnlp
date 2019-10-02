package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.tags;

import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ElementVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;

public enum ElementAnnotations implements ElementVisitor<List<Annotation>,List<Annotation>> {
	INSTANCE;

	@Override
	public List<Annotation> visit(Annotation a, List<Annotation> param) {
		param.add(a);
		return param;
	}

	@Override
	public List<Annotation> visit(Corpus corpus, List<Annotation> param) {
		return param;
	}

	@Override
	public List<Annotation> visit(Document doc, List<Annotation> param) {
		return param;
	}

	@Override
	public List<Annotation> visit(Relation rel, List<Annotation> param) {
		return param;
	}

	@Override
	public List<Annotation> visit(Section sec, List<Annotation> param) {
		return param;
	}

	@Override
	public List<Annotation> visit(Tuple t, List<Annotation> param) {
		for (Element arg : t.getAllArguments()) {
			arg.accept(this, param);
		}
		return param;
	}

	@Override
	public List<Annotation> visit(Element e, List<Annotation> param) {
		return param;
	}
}
