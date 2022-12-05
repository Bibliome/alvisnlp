package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json;

import org.json.simple.JSONArray;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DownCastElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;

public enum AnnotationOffset implements JsonValue, JsonValue.Resolved {
	INSTANCE {
		@SuppressWarnings("unchecked")
		@Override
		public Object create(EvaluationContext evalCtx, Element elt) {
			Annotation a = DownCastElement.toAnnotation(elt);
			JSONArray result = new JSONArray();
			result.add(a.getStart());
			result.add(a.getEnd());
			return result;
		}

		@Override
		public Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
			return this;
		}
	}
}
