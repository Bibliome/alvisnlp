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


package org.bibliome.alvisnlp.modules.alvisdb;

import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.bibliome.alvisdb.impl.lucene.LuceneUtils;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

public class ADBEntities extends ADBElements {
	private final Expression path;
	private final Expression pathItemId;
	private final Expression ancestors;
	private final Expression ancestorId;
	private final Expression children;
	private final Expression childId;
	private final Expression synonyms;
	private final Expression synonymForm;

	public ADBEntities(Expression items, Expression id, Expression name, Expression type, Expression path, Expression pathItemId, Expression ancestors, Expression ancestorId, Expression children, Expression childId, Expression synonyms, Expression synonymForm) {
		super(items, id, name, type);
		this.path = path;
		this.pathItemId = pathItemId;
		this.ancestors = ancestors;
		this.ancestorId = ancestorId;
		this.children = children;
		this.childId = childId;
		this.synonyms = synonyms;
		this.synonymForm = synonymForm;
	}
	
	@Override
	public Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new Resolved(resolver, this);
	}

	public static final class Resolved extends ADBElements.Resolved {
		private final Evaluator  path;
		private final Evaluator  pathItemId;
		private final Evaluator  ancestors;
		private final Evaluator  ancestorId;
		private final Evaluator  children;
		private final Evaluator  childId;
		private final Evaluator  synonyms;
		private final Evaluator  synonymForm;

		public Resolved(LibraryResolver resolver, ADBEntities entities) throws ResolverException {
			super(resolver, entities);
			this.path = resolver.resolveNullable(entities.path);
			this.pathItemId = resolver.resolveNullable(entities.pathItemId);
			this.ancestors = resolver.resolveNullable(entities.ancestors);
			this.ancestorId = resolver.resolveNullable(entities.ancestorId);
			this.children = resolver.resolveNullable(entities.children);
			this.childId = resolver.resolveNullable(entities.childId);
			this.synonyms = entities.synonyms.resolveExpressions(resolver);
			this.synonymForm = entities.synonymForm.resolveExpressions(resolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesNullable(path, defaultType);
			nameUsage.collectUsedNamesNullable(pathItemId, defaultType);
			nameUsage.collectUsedNamesNullable(ancestors, defaultType);
			nameUsage.collectUsedNamesNullable(ancestorId, defaultType);
			nameUsage.collectUsedNamesNullable(children, defaultType);
			nameUsage.collectUsedNamesNullable(childId, defaultType);
			synonyms.collectUsedNames(nameUsage, defaultType);
			synonymForm.collectUsedNames(nameUsage, defaultType);
		}
		
		@Override
		protected Document fillDocument(Logger logger, Document result, EvaluationContext ctx, Element elt) {
			addFields(result, LuceneUtils.Fields.SYNONYMS, synonyms, synonymForm, ctx, elt);
			addFields(result, LuceneUtils.Fields.CHILDREN, children, childId, ctx, elt);
			addFields(result, LuceneUtils.Fields.PATH, path, pathItemId, ctx, elt);
			addFields(result, LuceneUtils.Fields.ANCESTORS, ancestors, ancestorId, ctx, elt);
			return result;
		}
	}
}
