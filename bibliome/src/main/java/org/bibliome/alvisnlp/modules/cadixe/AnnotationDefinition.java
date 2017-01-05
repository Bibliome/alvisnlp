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
import org.bibliome.util.Iterators;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;
import alvisnlp.module.types.EvaluatorMapping;
import alvisnlp.module.types.ExpressionMapping;

public abstract class AnnotationDefinition {
	private final Expression elements;
	private final ExpressionMapping propsMap;
	private final Expression properties;
	private final Expression propKey;
	private final Expression propValue;
	private final Expression sources;
	private final Expression sourceId;
	private final Expression sourceAnnotationSet;
	private final Expression type;
	private final int kind;
	
	protected AnnotationDefinition(Expression elements, ExpressionMapping propsMap, Expression properties, Expression propKey, Expression propValue, Expression sources, Expression sourceId, Expression sourceAnnotationSet, Expression type, int kind) {
		super();
		this.elements = elements;
		this.propsMap = propsMap;
		this.properties = properties;
		this.propKey = propKey;
		this.propValue = propValue;
		this.sources = sources;
		this.sourceId = sourceId;
		this.sourceAnnotationSet = sourceAnnotationSet;
		this.type = type;
		this.kind = kind;
	}
	
	public static abstract class Resolved implements NameUser {
		private final Evaluator elements;
		private final EvaluatorMapping propsMap;
		private final Evaluator properties;
		private final Evaluator propKey;
		private final Evaluator propValue;
		private final Evaluator sources;
		private final Evaluator sourceId;
		private final Evaluator sourceAnnotationSet;
		private final Evaluator type;
		private final int kind;
		
		private Resolved(Evaluator elements, EvaluatorMapping propsMap, Evaluator properties, Evaluator propKey, Evaluator propValue, Evaluator sources, Evaluator sourceId, Evaluator sourceAnnotationSet, Evaluator type, int kind) {
			super();
			this.elements = elements;
			this.propsMap = propsMap;
			this.properties = properties;
			this.propKey = propKey;
			this.propValue = propValue;
			this.sources = sources;
			this.sourceId = sourceId;
			this.sourceAnnotationSet = sourceAnnotationSet;
			this.type = type;
			this.kind = kind;
		}
		
		protected Resolved(LibraryResolver resolver, AnnotationDefinition aDef) throws ResolverException {
			this(
					aDef.elements.resolveExpressions(resolver),
					aDef.propsMap.resolveExpressions(resolver),
					resolver.resolveNullable(aDef.properties),
					aDef.propKey.resolveExpressions(resolver),
					aDef.propValue.resolveExpressions(resolver),
					aDef.sources.resolveExpressions(resolver),
					aDef.sourceId.resolveExpressions(resolver),
					aDef.sourceAnnotationSet.resolveExpressions(resolver),
					aDef.type.resolveExpressions(resolver),
					aDef.kind);
		}

		Iterator<Element> getElements(EvaluationContext ctx, Section sec) {
			return elements.evaluateElements(ctx, sec);
		}

		@SuppressWarnings("unchecked")
		JSONObject toJSON(Element elt, int offset, CadixeExportContext ctx) {
			JSONObject result = new JSONObject();
			result.put("id", ctx.getAnnotationReference(elt).getId());
			result.put("kind", kind);
			result.put("type", type.evaluateString(ctx.evalCtx, elt));
			JSONObject props = new JSONObject();
			for (Map.Entry<String,Evaluator> e : propsMap.entrySet()) {
				String item = e.getValue().evaluateString(ctx.evalCtx, elt);
				if (item.isEmpty())
					continue;
				JSONArray value = new JSONArray();
				value.add(item);
				props.put(e.getKey(), value);
			}
			if (properties != null) {
				for (Element e : Iterators.loop(properties.evaluateElements(ctx.evalCtx, elt))) {
					String key = propKey.evaluateString(ctx.evalCtx, e);
					String value = propValue.evaluateString(ctx.evalCtx, e);
					JSONArray values;
					if (props.containsKey(key)) {
						values = (JSONArray) props.get(key);
					}
					else {
						values = new JSONArray();
						props.put(key, values);
					}
					values.add(value);
				}
			}
			result.put("properties", props);
			JSONArray sources = new JSONArray();
			for (Element e : Iterators.loop(this.sources.evaluateElements(ctx.evalCtx, elt))) {
				String id = sourceId.evaluateString(ctx.evalCtx, e);
				int asetId = sourceAnnotationSet.evaluateInt(ctx.evalCtx, e);
				JSONObject srcObj = new JSONObject();
				srcObj.put("ann_id", id);
				srcObj.put("set_id", asetId);
				srcObj.put("status", 1);
				sources.add(srcObj);
			}
			result.put("sources", sources);
			fillObject(result, elt, offset, ctx);
			return result;
		}

		protected abstract void fillObject(JSONObject object, Element elt, int offset, CadixeExportContext ctx);
		
		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			nameUsage.collectUsedNamesNullable(elements, defaultType);
			propsMap.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesNullable(properties, defaultType);
			propKey.collectUsedNames(nameUsage, defaultType);
			propValue.collectUsedNames(nameUsage, defaultType);
			sources.collectUsedNames(nameUsage, defaultType);
			sourceId.collectUsedNames(nameUsage, defaultType);
			sourceAnnotationSet.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesNullable(type, defaultType);
		}
	}
}
