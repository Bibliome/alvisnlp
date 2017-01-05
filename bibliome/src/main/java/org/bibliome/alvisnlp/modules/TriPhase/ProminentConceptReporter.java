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


package org.bibliome.alvisnlp.modules.TriPhase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.util.Iterators;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.ElementType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.types.Mapping;

/**
 * Module specific to TriPhase project. This module is used to reveal the
 * prominent concepts based on the result of the ontology projection (Prominent
 * concepts are stored as new Section(s))
 *
 *
 * @author fpapazian
 */
@AlvisNLPModule(beta = true)
public class ProminentConceptReporter extends CorpusModule<ResolvedObjects> implements SectionCreator {

    private static final int NbProminentConcepts = 5;
    private Expression documents;
    private Expression conceptAnnotations;
    private Expression conceptId;
    private String sectionName;

    @Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	/**
     * This basic implementation merely choose the first 5 most frequent
     * concepts just by counting occurrences within all sections.
     *
     * @param ctx
     * @param corpus
     * @throws ModuleException
     */
    @Override
    public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
        Logger logger = getLogger(ctx);

        LibraryResolver resolver = getLibraryResolver(ctx);

        Evaluator docsEvaluator = resolver.resolveNullable(documents);
        EvaluationContext docsEvalCtx = new EvaluationContext(logger);

        Evaluator conceptAnnotationsEvaluator = resolver.resolveNullable(conceptAnnotations);
        EvaluationContext conceptAnnotationsEvalCtx = new EvaluationContext(logger);

        Evaluator conceptIdEvaluator = resolver.resolveNullable(conceptId);
        EvaluationContext conceptIdEvalCtx = new EvaluationContext(logger);


        int nbDocuments = 0;
        int nbConceptAnnotations = 0;
        for (Element docElement : Iterators.loop(docsEvaluator.evaluateElements(docsEvalCtx, corpus))) {
            if (!ElementType.DOCUMENT.equals(docElement.getType())) {
                throw new ProcessingException("'documents' expression parameter did not evaluate to Document elements!");
            }
            Document doc = (Document) docElement;
            nbDocuments++;


            Map<String, Integer> conceptCount = new HashMap<String, Integer>();
            Map<String, Map<String, List<String>>> conceptFeatures = new HashMap<String, Map<String, List<String>>>();

            for (Element conceptAnnotationElement : Iterators.loop(conceptAnnotationsEvaluator.evaluateElements(conceptAnnotationsEvalCtx, doc))) {
                if (!ElementType.ANNOTATION.equals(conceptAnnotationElement.getType())) {
                    throw new ProcessingException("'conceptAnnotations' expression parameter did not evaluate to Annotation elements!");
                }
                Annotation annotation = (Annotation) conceptAnnotationElement;
                String concept_id = conceptIdEvaluator.evaluateString(conceptIdEvalCtx, annotation);
                if (concept_id.isEmpty()) {
                    throw new ProcessingException("'conceptId' expression parameter did evaluate as an empty string!");
                }
                nbConceptAnnotations++;
                if (!conceptCount.containsKey(concept_id)) {
                    conceptCount.put(concept_id, 0);
                    conceptFeatures.put(concept_id, annotation.getFeatures());
                }
                conceptCount.put(concept_id, conceptCount.get(concept_id) + 1);
            }

            List<Map.Entry<String, Integer>> sorted = new ArrayList<Map.Entry<String, Integer>>(conceptCount.entrySet());
            Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });

            int i = 0;
            while (i < sorted.size() && i < NbProminentConcepts) {
                Section sec = new Section(this, doc, sectionName, "");
                sec.addFeature("rank", String.valueOf(i + 1));
                Map<String, List<String>> conceptFeats = conceptFeatures.get(sorted.get(i).getKey());
                sec.addMultiFeatures(conceptFeats);

                i++;
            }

        }
        logger.log(Level.INFO, String.format("%d Document(s) processed with %d concept's occurences.", nbDocuments, nbConceptAnnotations));

    }

    @Override
    public Mapping getConstantSectionFeatures() {
        return null;
    }

    @Override
    public void setConstantSectionFeatures(Mapping constantSectionFeatures) {
    }

    /**
     *
     * @return the Expression to select Documents to be processed
     */
    @Param
    public Expression getDocuments() {
        return documents;
    }

    public void setDocuments(Expression documents) {
        this.documents = documents;
    }

    /**
     *
     * @return the Expression to select Annotations created through Ontology
     * projection
     *
     */
    @Param
    public Expression getConceptAnnotations() {
        return conceptAnnotations;
    }

    public void setConceptAnnotations(Expression annotation) {
        this.conceptAnnotations = annotation;
    }

    /**
     *
     * @return the Expression which evaluates to the conceptId in the context of
     * the annotation projection
     *
     */
    @Param
    public Expression getConceptId() {
        return conceptId;
    }

    public void setConceptId(Expression conceptId) {
        this.conceptId = conceptId;
    }

    /**
     *
     * @return name of the Section(s) which will be created for each of the
     * prominent concepts. The section will inherit the Annotation's features.
     *
     */
    @Param
    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
}
