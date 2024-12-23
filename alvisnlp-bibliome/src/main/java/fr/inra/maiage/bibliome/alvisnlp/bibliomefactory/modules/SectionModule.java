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

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.converters.expression.parser.ExpressionParser;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ConstantsLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;

/**
 * Base class of modules that iterate over sections.
 */
public abstract class SectionModule<T extends SectionResolvedObjects> extends DocumentModule<T> {
    private Expression sectionFilter = ConstantsLibrary.TRUE;

    public static class SectionResolvedObjects extends DocumentResolvedObjects {
    	private final Evaluator sectionFilter;
    	
    	public SectionResolvedObjects(ProcessingContext ctx, SectionModule<? extends SectionResolvedObjects> module) throws ResolverException {
    		super(ctx, module);
    		sectionFilter = rootResolver.resolveNullable(module.getSectionFilter());
    	}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			sectionFilter.collectUsedNames(nameUsage, defaultType);
		}

		public Evaluator getSectionFilter() {
			return sectionFilter;
		}
    }
    
    /**
     * Instantiates a new section module.
     */
    public SectionModule() {
        super();
    }

    /**
     * Returns the section filter.
     * @return the section filter
     */
    @Param
    public Expression getSectionFilter() {
    	Expression filter = null;
    	String[] layerNames = addLayersToSectionFilter();
    	if (layerNames != null)
    		for (String layerName : layerNames) {
    			if (layerName == null)
    				continue;
    			Expression layerFilter = ExpressionParser.parseUnsafe("layer:" + layerName);
    			if (filter == null) {
    				filter = layerFilter;
    			}
    			else {
    				filter = and(filter, layerFilter);
    			}
    		}

    	String[] features = addFeaturesToSectionFilter();
    	if (features != null)
    		for (String feat : features) {
    			Expression featureFilter = ExpressionParser.parseUnsafe("@" + feat);
    			if (filter == null) {
    				filter = featureFilter;
    			}
    			else {
    				filter = and(filter, featureFilter);
    			}
    		}

    	if (filter == null)
    		return sectionFilter;
    	return and(sectionFilter, filter);
    }

    /**
     * Sets the section filter.
     * @param sectionFilter
     */
    public void setSectionFilter(Expression sectionFilter) {
        this.sectionFilter = sectionFilter;
    }

    /**
     * Adds layer names check to the section filter.
     * @return the string[]
     */
    protected abstract String[] addLayersToSectionFilter();

    /**
     * Adds features check to section filter.
     * @return the string[]
     */
    protected abstract String[] addFeaturesToSectionFilter();

    /**
     * Returns all sections in the specified document that satisfy sectionFilter.
     * @param doc
     * @return all sections in the specified document that satisfy sectionFilter
     */
    public Iterator<Section> sectionIterator(EvaluationContext ctx, Document doc) {
        return doc.sectionIterator(ctx, getResolvedObjects().getSectionFilter());
    }

    /**
     * Returns all sections that satisfy sectionFilter in documents that satisfy documentFilter in the specified corpus.
     * @param corpus
     * @return all sections that satisfy sectionFilter in documents that satisfy documentFilter in the specified corpus
     */
    public Iterator<Section> sectionIterator(EvaluationContext ctx, Corpus corpus) {
    	T resObj = getResolvedObjects();
        return corpus.sectionIterator(ctx, resObj.getDocumentFilter(), resObj.getSectionFilter());
    }
}
