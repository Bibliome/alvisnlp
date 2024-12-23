package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pubannotation;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DefaultExpressions;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pubannotation.DenotationSpecification.Resolved;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Resolvable;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUser;
import fr.inra.maiage.bibliome.util.Iterators;

public class DenotationSpecification implements Resolvable<Resolved> {
	private final Expression instances;
	private final Expression begin;
	private final Expression end;
	private final Expression obj;

	public DenotationSpecification(Expression instances, Expression begin, Expression end, Expression obj) {
		super();
		this.instances = instances;
		this.begin = begin;
		this.end = end;
		this.obj = obj;
	}

	public DenotationSpecification(Expression obj) {
		this(DefaultExpressions.SECTION_ANNOTATIONS, DefaultExpressions.ANNOTATION_START, DefaultExpressions.ANNOTATION_END, obj);
	}

	public DenotationSpecification() {
		this(DefaultExpressions.feature("ref"));
	}

	@Override
	public Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new Resolved(resolver, this);
	}

	public static class Resolved implements NameUser {
		private final Evaluator instances;
		private final Evaluator begin;
		private final Evaluator end;
		private final Evaluator obj;

		private Resolved(LibraryResolver resolver, DenotationSpecification spec) throws ResolverException {
			this.instances = spec.instances.resolveExpressions(resolver);
			this.begin = spec.begin.resolveExpressions(resolver);
			this.end = spec.end.resolveExpressions(resolver);
			this.obj = spec.obj.resolveExpressions(resolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			instances.collectUsedNames(nameUsage, defaultType);
			begin.collectUsedNames(nameUsage, defaultType);
			end.collectUsedNames(nameUsage, defaultType);
			obj.collectUsedNames(nameUsage, defaultType);
		}
		
		@SuppressWarnings("unchecked")
		void addDenotations(EvaluationContext ctx, Section sec, JSONArray denotations) {
			for (Element e : Iterators.loop(instances.evaluateElements(ctx, sec))) {
				JSONObject j = convertDenotation(ctx, e);
				denotations.add(j);
			}
		}

		@SuppressWarnings("unchecked")
		private JSONObject convertDenotation(EvaluationContext ctx, Element e) {
			JSONObject result = new JSONObject();
			result.put("id", e.getStringId());
			result.put("span", convertSpan(ctx, e));
			result.put("obj", obj.evaluateString(ctx, e));
			return result;
		}

		@SuppressWarnings("unchecked")
		private JSONObject convertSpan(EvaluationContext ctx, Element e) {
			JSONObject result = new JSONObject();
			result.put("begin", begin.evaluateInt(ctx, e));
			result.put("end", end.evaluateInt(ctx, e));
			return result;
		}
	}
}
