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



package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules;

import java.util.Iterator;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.standard.BooleanLibrary;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DocumentModule.DocumentResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;

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
    @Param
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
