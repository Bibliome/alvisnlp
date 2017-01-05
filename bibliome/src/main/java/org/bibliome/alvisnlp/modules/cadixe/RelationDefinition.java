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


package org.bibliome.alvisnlp.modules.cadixe;

import java.util.Iterator;
import java.util.Map;

import org.bibliome.alvisnlp.modules.cadixe.ExportCadixeJSON.CadixeExportContext;
import org.bibliome.alvisnlp.modules.cadixe.RelationDefinition.Resolved;
import org.json.simple.JSONObject;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.Resolvable;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.types.EvaluatorMapping;
import alvisnlp.module.types.ExpressionMapping;

public class RelationDefinition extends AnnotationDefinition implements Resolvable<Resolved> {
	private final ExpressionMapping argsMap;
	private final Expression args;
	private final Expression role;
	
	RelationDefinition(
			Expression elements,
			ExpressionMapping propsMap,
			Expression properties,
			Expression propKey,
			Expression propValue,
			Expression sources,
			Expression sourceId,
			Expression sourceAnnotationSet,
			Expression type,
			ExpressionMapping argsMap,
			Expression args,
			Expression role) {
		super(elements, propsMap, properties, propKey, propValue, sources, sourceId, sourceAnnotationSet, type, 2);
		this.argsMap = argsMap;
		this.args = args;
		this.role = role;
	}

	public static class Resolved extends AnnotationDefinition.Resolved {
		private final EvaluatorMapping argsMap;
		private final Evaluator args;
		private final Evaluator role;

		public Resolved(LibraryResolver resolver, RelationDefinition aDef) throws ResolverException {
			super(resolver, aDef);
			this.argsMap = aDef.argsMap.resolveExpressions(resolver);
			this.args = resolver.resolveNullable(aDef.args);
			this.role = resolver.resolveNullable(aDef.role);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void fillObject(JSONObject object, Element elt, int offset, CadixeExportContext ctx) {
			JSONObject relation = new JSONObject();
			if (args != null) {
//				System.err.println("elt = " + elt);
				Iterator<Element> argIt = args.evaluateElements(ctx.evalCtx, elt);
				while (argIt.hasNext()) {
					Element arg = argIt.next();
					String role = this.role.evaluateString(ctx.evalCtx, arg);
//					System.err.println("  arg = " + arg);
//					System.err.println("  this.role = " + this.role);
//					System.err.println("  role = " + role);
//					System.err.println("  arg.@role = " + arg.getLastFeature("role"));
					relation.put(role, ctx.getAnnotationReference(arg).asJSON());
				}
			}
			for (Map.Entry<String,Evaluator> e : argsMap.entrySet()) {
				Evaluator expr = e.getValue();
				Iterator<Element> args = expr.evaluateElements(ctx.evalCtx, elt);
				if (args.hasNext()) {
					Element arg = args.next();
					relation.put(e.getKey(), ctx.getAnnotationReference(arg).asJSON());
				}
			}
			object.put("relation", relation);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			argsMap.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesNullable(args, defaultType);
			nameUsage.collectUsedNamesNullable(role, defaultType);
		}		
	}

	@Override
	public RelationDefinition.Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new RelationDefinition.Resolved(resolver, this);
	}
}
