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

import java.util.Iterator;
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

public class ADBRelations extends ADBElements {
	private final Expression args;
	private final Expression argId;
	private final Expression argName;
	private final Expression argAncestors;
	private final Expression argAncestorId;
	private final Expression argDoc;
	private final Expression argSec;
	private final Expression argStart;
	private final Expression argEnd;

	public ADBRelations(Expression items, Expression id, Expression name, Expression type, Expression args, Expression argId, Expression argName, Expression argAncestors, Expression argAncestorId, Expression argDoc, Expression argSec, Expression argStart, Expression argEnd) {
		super(items, id, name, type);
		this.args = args;
		this.argId = argId;
		this.argName = argName;
		this.argAncestors = argAncestors;
		this.argAncestorId = argAncestorId;
		this.argDoc = argDoc;
		this.argSec = argSec;
		this.argStart = argStart;
		this.argEnd = argEnd;
	}

	@Override
	public Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new Resolved(resolver, this);
	}

	public static class Resolved extends ADBElements.Resolved {
		private final Evaluator  args;
		private final Evaluator  argId;
		private final Evaluator  argName;
		private final Evaluator  argAncestors;
		private final Evaluator  argAncestorId;
		private final Evaluator  argDoc;
		private final Evaluator  argSec;
		private final Evaluator  argStart;
		private final Evaluator  argEnd;
		
		public Resolved(LibraryResolver resolver, ADBRelations relations) throws ResolverException {
			super(resolver, relations);
			this.args = relations.args.resolveExpressions(resolver);
			this.argId = relations.argId.resolveExpressions(resolver);
			this.argName = resolver.resolveNullable(relations.argName);
			this.argAncestors = resolver.resolveNullable(relations.argAncestors);
			this.argAncestorId = resolver.resolveNullable(relations.argAncestorId);
			this.argDoc = relations.argDoc.resolveExpressions(resolver);
			this.argSec = relations.argSec.resolveExpressions(resolver);
			this.argStart = relations.argStart.resolveExpressions(resolver);
			this.argEnd = relations.argEnd.resolveExpressions(resolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			args.collectUsedNames(nameUsage, defaultType);
			argId.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesNullable(argName, defaultType);
			nameUsage.collectUsedNamesNullable(argAncestors, defaultType);
			nameUsage.collectUsedNamesNullable(argAncestorId, defaultType);
			argDoc.collectUsedNames(nameUsage, defaultType);
			argSec.collectUsedNames(nameUsage, defaultType);
			argStart.collectUsedNames(nameUsage, defaultType);
			argEnd.collectUsedNames(nameUsage, defaultType);
		}
		
		private void fillArgFields(Document doc, LuceneUtils.Fields.ArgSide argSide, EvaluationContext ctx, Element arg) {
			addField(doc, argSide.ENTITY, argId, ctx, arg);
			addField(doc, argSide.NAME, argName, ctx, arg);
			addField(doc, argSide.DOCUMENT, argDoc, ctx, arg);
			addField(doc, argSide.SECTION, argSec, ctx, arg);
			addFields(doc, argSide.ANCESTORS, argAncestors, argAncestorId, ctx, arg);
			addIntField(doc, argSide.START, argStart, ctx, arg);
			addIntField(doc, argSide.END, argEnd, ctx, arg);
		}
		
		@Override
		protected Document fillDocument(Logger logger, Document result, EvaluationContext ctx, Element elt) {
			Iterator<Element> args = this.args.evaluateElements(ctx, elt);
			if (!args.hasNext()) {
				logger.warning("relation has no arguments (ignoring the relation)");
				return null;
			}
			Element left = args.next();
			if (!args.hasNext()) {
				logger.warning("relation has only one argument (ignoring the relation)");
				return null;
			}
			Element right = args.next();
			if (args.hasNext()) {
				logger.warning("relation has more than two arguments (ignoring the extra arguments)");
			}
			fillArgFields(result, LuceneUtils.Fields.ArgSide.LEFT, ctx, left);
			fillArgFields(result, LuceneUtils.Fields.ArgSide.RIGHT, ctx, right);
			return result;
		}
	}
}
