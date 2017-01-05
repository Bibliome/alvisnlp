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

import org.bibliome.alvisnlp.modules.cadixe.ExportCadixeJSON.CadixeExportContext;
import org.bibliome.alvisnlp.modules.cadixe.GroupDefinition.Resolved;
import org.bibliome.util.Iterators;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.Resolvable;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.types.ExpressionMapping;

public class GroupDefinition extends AnnotationDefinition implements Resolvable<Resolved> {
	private final Expression items;

	GroupDefinition(Expression elements, ExpressionMapping propsMap, Expression properties, Expression propKey, Expression propValue, Expression sources, Expression sourceId, Expression sourceAnnotationSet, Expression type, Expression items) {
		super(elements, propsMap, properties, propKey, propValue, sources, sourceId, sourceAnnotationSet, type, 1);
		this.items = items;
	}

	public static class Resolved extends AnnotationDefinition.Resolved {
		private final Evaluator items;

		public Resolved(LibraryResolver resolver, GroupDefinition aDef) throws ResolverException {
			super(resolver, aDef);
			this.items = aDef.items.resolveExpressions(resolver);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void fillObject(JSONObject object, Element elt, int offset, CadixeExportContext ctx) {
			JSONArray group = new JSONArray();
			for (Element item : Iterators.loop(items.evaluateElements(ctx.evalCtx, elt))) {
				AnnotationReference aRef = ctx.getAnnotationReference(item);
				group.add(aRef.asJSON());
			}
			object.put("group", group);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			if (items != null) {
				items.collectUsedNames(nameUsage, defaultType);
			}
		}		
	}

	@Override
	public GroupDefinition.Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new GroupDefinition.Resolved(resolver, this);
	}
}
