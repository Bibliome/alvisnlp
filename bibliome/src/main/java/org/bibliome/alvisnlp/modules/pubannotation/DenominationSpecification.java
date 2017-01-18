package org.bibliome.alvisnlp.modules.pubannotation;

import org.bibliome.alvisnlp.modules.DefaultExpressions;
import org.bibliome.alvisnlp.modules.pubannotation.DenominationSpecification.Resolved;
import org.bibliome.util.Iterators;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.Resolvable;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;

public class DenominationSpecification implements Resolvable<Resolved> {
	private final Expression instances;
	private final Expression begin;
	private final Expression end;
	private final Expression obj;

	public DenominationSpecification(Expression instances, Expression begin, Expression end, Expression obj) {
		super();
		this.instances = instances;
		this.begin = begin;
		this.end = end;
		this.obj = obj;
	}

	public DenominationSpecification(Expression obj) {
		this(DefaultExpressions.SECTION_ANNOTATIONS, DefaultExpressions.ANNOTATION_START, DefaultExpressions.ANNOTATION_END, obj);
	}

	public DenominationSpecification() {
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

		private Resolved(LibraryResolver resolver, DenominationSpecification spec) throws ResolverException {
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
		void addDenominations(EvaluationContext ctx, Section sec, JSONArray denominations) {
			for (Element e : Iterators.loop(instances.evaluateElements(ctx, sec))) {
				JSONObject j = convertDenomination(ctx, e);
				denominations.add(j);
			}
		}

		@SuppressWarnings("unchecked")
		private JSONObject convertDenomination(EvaluationContext ctx, Element e) {
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
