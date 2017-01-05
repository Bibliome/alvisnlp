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

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.bibliome.alvisdb.impl.lucene.LuceneUtils;
import org.bibliome.alvisnlp.modules.alvisdb.ADBElements.Resolved;
import org.bibliome.util.Iterators;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.Resolvable;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;

public abstract class ADBElements implements Resolvable<Resolved> {
	private final Expression items;
	private final Expression id;
	private final Expression name; 
	private final Expression type;
	
	protected ADBElements(Expression items, Expression id, Expression name, Expression type) {
		super();
		this.items = items;
		this.id = id;
		this.name = name;
		this.type = type;
	}

	protected static abstract class Resolved implements NameUser {
		private final Evaluator  items;
		private final Evaluator  id;
		private final Evaluator  name;
		private final Evaluator  type;
		
		protected Resolved(LibraryResolver resolver, ADBElements elements) throws ResolverException {
			super();
			this.items = elements.items.resolveExpressions(resolver);
			this.id = elements.id.resolveExpressions(resolver);
			this.name = elements.name.resolveExpressions(resolver);
			this.type = elements.type.resolveExpressions(resolver);
		}
		
		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			items.collectUsedNames(nameUsage, defaultType);
			id.collectUsedNames(nameUsage, defaultType);
			name.collectUsedNames(nameUsage, defaultType);
			type.collectUsedNames(nameUsage, defaultType);
		}

		private Document createDocument(Logger logger, EvaluationContext ctx, Element elt) {
			Document result = new Document();
			addField(result, LuceneUtils.Fields.ID, id, ctx, elt);
			addField(result, LuceneUtils.Fields.NAME, name, ctx, elt);
			addField(result, LuceneUtils.Fields.TYPE, type, ctx, elt);
			return fillDocument(logger, result, ctx, elt);
		}

		protected abstract Document fillDocument(Logger logger, Document result, EvaluationContext ctx, Element elt);
		
		void indexElements(Logger logger, IndexWriter writer, EvaluationContext ctx, Corpus corpus) throws IOException {
			for (Element item : Iterators.loop(items.evaluateElements(ctx, corpus))) {
				Document doc = createDocument(logger, ctx, item);
				if (doc != null) {
					writer.addDocument(doc);
				}
			}
		}
		
		protected static void addField(Document doc, String name, String value) {
			Field field = new Field(name, value, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
			doc.add(field);
		}
		
		protected static void addField(Document doc, String name, Evaluator eval, EvaluationContext ctx, Element elt) {
			if (eval != null) {
				String value = eval.evaluateString(ctx, elt);
				addField(doc, name, value);
			}
		}
		
		protected static void addIntField(Document doc, String name, Evaluator eval, EvaluationContext ctx, Element elt) {
			if (eval != null) {
				int intValue = eval.evaluateInt(ctx, elt);
				String value = Integer.toString(intValue);
				addField(doc, name, value);
			}
		}
		
		protected static void addFields(Document doc, String name, Evaluator eval, Evaluator form, EvaluationContext ctx, Element elt) {
			if (eval != null && form != null) {
				for (Element e : Iterators.loop(eval.evaluateElements(ctx, elt))) {
					addField(doc, name, form, ctx, e);
				}
			}
		}
	}
}
