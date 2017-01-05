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



package org.bibliome.alvisnlp.modules;

import java.util.Iterator;

import org.bibliome.alvisnlp.library.standard.BooleanLibrary;
import org.bibliome.alvisnlp.modules.DocumentModule.DocumentResolvedObjects;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.Param;

/**
 * Base class for modules that iterate over documents.
 */
public abstract class DocumentModule<T extends DocumentResolvedObjects> extends CorpusModule<T> {
    private Expression documentFilter = DefaultExpressions.TRUE;

    /**
     * Constructs a new DocumentModule object.
     */
    public DocumentModule() {
        super();
    }
    
    protected static Expression and(Expression a, Expression b) {
    	return new Expression(BooleanLibrary.NAME, "and", a, b);
    }

    public static class DocumentResolvedObjects extends ResolvedObjects {
    	private final Evaluator documentFilter;
    	
    	protected DocumentResolvedObjects(ProcessingContext<Corpus> ctx, DocumentModule<? extends DocumentResolvedObjects> module) throws ResolverException {
    		super(ctx, module);
    		documentFilter = rootResolver.resolveNullable(module.getDocumentFilter());
    	}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			documentFilter.collectUsedNames(nameUsage, defaultType);
		}

		public Evaluator getDocumentFilter() {
			return documentFilter;
		}
    }
    
    /**
     * Returns the document filter.
     * @return the documentFilter
     */
    @Param(defaultDoc = "Only process document that satisfy this filter.")
    public Expression getDocumentFilter() {
        return documentFilter;
    }

    /**
     * Sets the document filter.
     * @param documentFilter
     */
    public void setDocumentFilter(Expression documentFilter) {
        this.documentFilter = documentFilter;
    }

    /**
     * Returns all documents in the specified corpus that satisfy documentFilter.
     * @param corpus
     * @return all documents in the specified corpus that satisfy documentFilter
     */
    public Iterator<Document> documentIterator(EvaluationContext ctx, Corpus corpus) {
        return corpus.documentIterator(ctx, getResolvedObjects().getDocumentFilter());
    }
}
