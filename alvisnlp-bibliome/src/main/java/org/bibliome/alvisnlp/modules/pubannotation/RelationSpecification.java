package org.bibliome.alvisnlp.modules.pubannotation;

import java.util.Iterator;

import org.bibliome.alvisnlp.converters.expression.parser.ExpressionParser;
import org.bibliome.alvisnlp.modules.DefaultExpressions;
import org.bibliome.alvisnlp.modules.pubannotation.RelationSpecification.Resolved;
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

public class RelationSpecification implements Resolvable<Resolved> {
	private final Expression instances;
	private final Expression pred;
	private final Expression subj;
	private final Expression obj;
	
	public RelationSpecification(Expression instances, Expression pred, Expression subj, Expression obj) {
		super();
		this.instances = instances;
		this.pred = pred;
		this.subj = subj;
		this.obj = obj;
	}

	public RelationSpecification() {
		this(DefaultExpressions.SECTION_TUPLES, DefaultExpressions.feature("type"), ExpressionParser.parseUnsafe("args{0}"), ExpressionParser.parseUnsafe("args{1}"));
	}

	@Override
	public Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new Resolved(resolver, this);
	}
	
	public static class Resolved implements NameUser {
		private final Evaluator instances;
		private final Evaluator pred;
		private final Evaluator subj;
		private final Evaluator obj;

		private Resolved(LibraryResolver resolver, RelationSpecification spec) throws ResolverException {
			this.instances = spec.instances.resolveExpressions(resolver);
			this.pred = spec.pred.resolveExpressions(resolver);
			this.subj = spec.subj.resolveExpressions(resolver);
			this.obj = spec.obj.resolveExpressions(resolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			instances.collectUsedNames(nameUsage, defaultType);
			pred.collectUsedNames(nameUsage, defaultType);
			subj.collectUsedNames(nameUsage, defaultType);
			obj.collectUsedNames(nameUsage, defaultType);
		}
		
		@SuppressWarnings("unchecked")
		void addRelations(EvaluationContext ctx, Section sec, JSONArray relations) {
			for (Element e : Iterators.loop(instances.evaluateElements(ctx, sec))) {
				JSONObject j = convertRelation(ctx, e);
				relations.add(j);
			}
		}

		@SuppressWarnings("unchecked")
		private JSONObject convertRelation(EvaluationContext ctx, Element e) {
			JSONObject result = new JSONObject();
			result.put("id", e.getStringId());
			result.put("pred", pred.evaluateString(ctx, e));
			result.put("subj", getArgument(ctx, e, subj));
			result.put("obj", getArgument(ctx, e, obj));
			return result;
		}
		
		private static String getArgument(EvaluationContext ctx, Element e, Evaluator eval) {
			Iterator<Element> it = eval.evaluateElements(ctx, e);
			if (it.hasNext()) {
				Element arg = it.next();
				return arg.getStringId();
			}
			return null;
		}
	}
}
