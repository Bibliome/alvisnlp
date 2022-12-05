package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json.JsonValue.Resolved;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Resolvable;

public interface JsonValue extends Resolvable<Resolved> {
	public static interface Resolved {
		Object create(EvaluationContext evalCtx, Element elt);
	}
}
