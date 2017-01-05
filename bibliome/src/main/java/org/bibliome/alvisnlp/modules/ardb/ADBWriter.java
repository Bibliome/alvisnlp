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


package org.bibliome.alvisnlp.modules.ardb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.alvisnlp.modules.ardb.ADBBinder.AnnotationKind;
import org.bibliome.util.Iterators;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.ElementType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
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

/**
 * This module stores Annotations in a relational database. <br><br>Examples of
 * module configuration to store every annotations:<br> <ul><li>when the
 * classical AlvisNLP TextBound Annotation representation is used
 * (=single-fragment Annotation)<br><br>
 * <code> &lt;module id="..." class="<b>ADBWrite</b>r"&gt;<br>
 * ...<br>
 * &lt;annotations&gt;<b>layer</b>&lt;/annotations&gt;<br>
 * ...<br>
 * &lt;module&gt;<br>
 * </code> </li> <br> <li>or alternatively, when the multi-fragments TextBound
 * Annotation representation is used, as introduced by
 * {@link org.bibliome.alvisnlp.modules.cadixe.AlvisAEReader2} <br><br>
 * <code> &lt;module id="..." class="<b>ADBWriter</b>"&gt;<br>
 * ...<br>
 * &lt;annotations&gt;<b>relations.tuples[args:frag0]</b>&lt;/annotations&gt;<br>
 * &lt;fragments&gt;<b>args</b>&lt;/fragments&gt;<br>
 * &lt;groups&gt;<b>relations.tuples[args:item0]</b>&lt;/groups&gt;<br>
 * &lt;relations&gt;<b>relations.tuples[not (args:item0) and not (args:frag0)]</b>&lt;/relations&gt;<br>
 * ...<br>
 * &lt;module&gt;<br>
 * </code> </li> </ul>
 * <pre>
 *
 * @author fpapazian
 */
@AlvisNLPModule(beta = true)
public class ADBWriter extends CorpusModule<ResolvedObjects> {

    private final ADBBinder dbContext = new ADBBinder();
    private Expression documents;
    private Expression sections;
    private Expression aspectId = null;
    private Expression annotations;
    private Expression fragments;
    private Expression annotationType = null;
    private Expression groups = null;
    private Expression relations = null;
    private Expression[] toDocScopeAnnotation = null;
    private String[] docScopeAnnType = null;

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

    //-- -------------------------------------------------------------------- -- 
    @Override
    public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
        Logger logger = getLogger(ctx);
        LibraryResolver resolver = getLibraryResolver(ctx);

        dbContext.setLogger(logger);
        dbContext.initConnection();

        try {
            try {
                dbContext.getConnection().setAutoCommit(false);
                dbContext.getConnection().setSavepoint();

                int corpus_id = dbContext.addCorpus(corpus);
                int nbDocuments = 0;

                Evaluator docsEvaluator = resolver.resolveNullable(documents);
                EvaluationContext docsEvalCtx = new EvaluationContext(logger);
                Evaluator sectionsEvaluator = resolver.resolveNullable(sections);
                EvaluationContext sectionsEvalCtx = new EvaluationContext(logger);

                Evaluator aspectIdEvaluator = null;
                EvaluationContext aspectIdEvalCtx = null;
                if (aspectId != null) {
                    aspectIdEvaluator = resolver.resolveNullable(aspectId);
                    aspectIdEvalCtx = new EvaluationContext(logger);
                }

                Evaluator annotationsEvaluator = resolver.resolveNullable(annotations);
                EvaluationContext annotationsEvalCtx = new EvaluationContext(logger);
                Evaluator fragmentsEvaluator = resolver.resolveNullable(fragments);
                EvaluationContext fragmentsEvalCtx = new EvaluationContext(logger);

                Evaluator annotationTypeEvaluator = resolver.resolveNullable(annotationType);
                EvaluationContext annotationTypeEvalCtx = new EvaluationContext(logger);

                Evaluator groupsEvaluator = resolver.resolveNullable(groups);
                EvaluationContext groupsEvalCtx = new EvaluationContext(logger);
                Evaluator relationsEvaluator = resolver.resolveNullable(relations);
                EvaluationContext relationsEvalCtx = new EvaluationContext(logger);

                Evaluator toDocScopeAnnotationEvaluator[] = null;
                EvaluationContext toDocScopeAnnotationEvalCtx = null;
                if (toDocScopeAnnotation != null) {
                    toDocScopeAnnotationEvaluator = resolver.resolveArray(toDocScopeAnnotation, Evaluator.class);
                    toDocScopeAnnotationEvalCtx = new EvaluationContext(logger);
                }

                int totalNbTextBoundAnnotations = 0, totalNbGroupAnnotations = 0, totalNbRelationAnnotations = 0;

                for (Element docElement : Iterators.loop(docsEvaluator.evaluateElements(docsEvalCtx, corpus))) {
                    if (!ElementType.DOCUMENT.equals(docElement.getType())) {
                        throw new ProcessingException("'documents' expression parameter did not evaluate to Document elements!");
                    }
                    Document doc = (Document) docElement;

                    int doc_id = dbContext.addDocument(doc);
                    dbContext.linkDocumentToCorpus(corpus_id, doc_id);
                    nbDocuments++;

//                    int nbSections = 0;
                    int nbTextBoundAnnotations = 0;
                    int nbGroupAnnotations = 0;
                    int nbRelationAnnotations = 0;
                    for (Element sectionElement : Iterators.loop(sectionsEvaluator.evaluateElements(sectionsEvalCtx, doc))) {
                        if (!ElementType.SECTION.equals(sectionElement.getType())) {
                            throw new ProcessingException("'sections' expression parameter did not evaluate to Section elements!");
                        }
                        Section section = (Section) sectionElement;

                        String asp_id = null;
                        if (aspectId == null) {
                            asp_id = section.getName();
                        } else {
                            asp_id = aspectIdEvaluator.evaluateString(aspectIdEvalCtx, section);
                            if (asp_id.isEmpty()) {
                                throw new ProcessingException("'aspectId' expression parameter did evaluate as an empty string!");
                            }
                        }
                        dbContext.addAspect(doc_id, asp_id, section);
//                        nbSections++;

                        Map<String, Integer> dbAnIdByAlvisAnnId = new HashMap<>();

                        //process primary Annotation (=text-bound Annotations)
                        for (Element annOrTupleElement : Iterators.loop(annotationsEvaluator.evaluateElements(annotationsEvalCtx, section))) {

                            String annTypeName = null;
                            List<Annotation> annFragments = new ArrayList<>();
                            if (ElementType.ANNOTATION.equals(annOrTupleElement.getType())) {
                                //single fragment annotation
                                annFragments.add((Annotation) annOrTupleElement);
                            } else if (ElementType.TUPLE.equals(annOrTupleElement.getType())) {
                                //multi-fragment Annotation, stored in Tuples
                                Tuple fragmentsHolder = (Tuple) annOrTupleElement;
                                //the actual annotation-type name is the name of the Relation storing fragments
                                annTypeName = fragmentsHolder.getRelation().getName();

                                for (Element fragmentElement : Iterators.loop(fragmentsEvaluator.evaluateElements(fragmentsEvalCtx, fragmentsHolder))) {
                                    if (!ElementType.ANNOTATION.equals(fragmentElement.getType())) {
                                        throw new ProcessingException("'fragments' expression parameter did not evaluate to Annotation elements!");
                                    }
                                    annFragments.add((Annotation) fragmentElement);
                                }
                            } else {
                                throw new ProcessingException("'annotations' expression parameter did not evaluate to Annotation or Tuple elements!");
                            }

                            if (!annFragments.isEmpty()) {

                                Annotation annotation = annFragments.get(0);

                                if (annotationType == null) {
                                    if (annTypeName == null) {
                                        annTypeName = ADBBinder.DEFAULT_ANNOTATIONTYPE;
                                    }
                                } else {
                                    annTypeName = annotationTypeEvaluator.evaluateString(annotationTypeEvalCtx, annotation);
                                    if (annTypeName.isEmpty()) {
                                        throw new ProcessingException("'annotationType' expression parameter did evaluate as an empty string!");
                                    }
                                }

                                int dbAnnId = dbContext.addTextBoundAnnotation(doc_id, asp_id, annFragments, annTypeName);
                                dbAnIdByAlvisAnnId.put(annOrTupleElement.getStringId(), dbAnnId);
                                nbTextBoundAnnotations++;
                            }
                        }


                        List<Tuple> secondaryAnnotations = new ArrayList<>();

                        //process secondary Annotations (=Relations and Groups)
                        //1. Create naked Relations and Groups (=without any references to Group's components or Relation's arguments)
                        if (groups != null) {
                            for (Element groupTupleElement : Iterators.loop(groupsEvaluator.evaluateElements(groupsEvalCtx, section))) {
                                if (!ElementType.TUPLE.equals(groupTupleElement.getType())) {
                                    throw new ProcessingException("'groups' expression parameter did evaluate as Tuple elements!");
                                }
                                Tuple groupComponentsHolder = (Tuple) groupTupleElement;

                                String groupTypeName = groupComponentsHolder.getRelation().getName();
                                int dbAnnId = dbContext.addNakedSecondaryAnnotation(AnnotationKind.Group, groupTypeName, groupComponentsHolder.getFeatures());
                                dbAnIdByAlvisAnnId.put(groupComponentsHolder.getStringId(), dbAnnId);
                                secondaryAnnotations.add(groupComponentsHolder);
                                nbGroupAnnotations++;
                            }
                        }
                        if (relations != null) {
                            for (Element relationTupleElement : Iterators.loop(relationsEvaluator.evaluateElements(relationsEvalCtx, section))) {
                                if (!ElementType.TUPLE.equals(relationTupleElement.getType())) {
                                    throw new ProcessingException("'relations' expression parameter did evaluate as Tuple elements!");
                                }
                                Tuple relationArgumentsHolder = (Tuple) relationTupleElement;

                                String relationTypeName = relationArgumentsHolder.getRelation().getName();
                                int dbAnnId = dbContext.addNakedSecondaryAnnotation(AnnotationKind.Relation, relationTypeName, relationArgumentsHolder.getFeatures());
                                dbAnIdByAlvisAnnId.put(relationArgumentsHolder.getStringId(), dbAnnId);
                                secondaryAnnotations.add(relationArgumentsHolder);
                                nbRelationAnnotations++;
                            }
                        }

                        if (groups != null || relations != null) {
                            //2. Enrich naked Relations and Groups with references to other annotations
                            for (Tuple secAnnReferencesHolder : secondaryAnnotations) {
                                int dbAnnId = dbAnIdByAlvisAnnId.get(secAnnReferencesHolder.getStringId());
                                dbContext.addAnnReferencesToNakedSecondaryAnnotation(dbAnnId, secAnnReferencesHolder, dbAnIdByAlvisAnnId);
                            }
                        }

                    }

                    if (toDocScopeAnnotation != null && toDocScopeAnnotation.length > 0) {
                        if (docScopeAnnType == null) {
                            throw new ProcessingException("'docScopeAnnType' array parameter not specified!");
                        } else if (docScopeAnnType.length != toDocScopeAnnotationEvaluator.length) {
                            throw new ProcessingException("'docScopeAnnType' array parameter length differs from 'toDocScopeAnnotation' array parameter length!");
                        }
                        for (int i = 0; i < toDocScopeAnnotationEvaluator.length; i++) {
                            for (Element element : Iterators.loop(toDocScopeAnnotationEvaluator[i].evaluateElements(toDocScopeAnnotationEvalCtx, doc))) {
                                dbContext.addDocumentScopeAnnotation(doc_id, docScopeAnnType[i], element.getFeatures());
                                nbTextBoundAnnotations++;
                            }
                        }
                    }

                    //logger.log(Level.FINEST, String.format("Processed  %d Annotation(s) / %d Group(s) / %d Relation(s) in %d Section(s) for Document %s", nbTextBoundAnnotations, nbGroupAnnotations, nbRelationAnnotations, nbSections, doc.getId()));
                    totalNbTextBoundAnnotations += nbTextBoundAnnotations;
                    totalNbGroupAnnotations += nbGroupAnnotations;
                    totalNbRelationAnnotations += nbRelationAnnotations;
                }
                dbContext.getConnection().commit();

                logger.log(Level.INFO, String.format("%d Document(s) stored in database [%d Annotation(s) / %d Group(s) / %d Relation(s)].", nbDocuments, totalNbTextBoundAnnotations, totalNbGroupAnnotations, totalNbRelationAnnotations));
            } catch (SQLException ex) {
            }

        } finally {
            dbContext.finishConnection();
        }
    }
    //-- -------------------------------------------------------------------- -- 

    /**
     *
     * @return the Expression to select Documents to be stored in DB
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
     * @return the Expression to select Sections to be stored in DB. <br> this
     * expression is evaluated in the context of selected Documents
     */
    @Param
    public Expression getSections() {
        return sections;
    }

    public void setSections(Expression sections) {
        this.sections = sections;
    }

    /**
     *
     * @return the Expression to compute the identifier of the Aspect create for
     * the corresponding section. <br> this expression is evaluated in the
     * context of selected Sections
     */
    @Param
    public Expression getAspectId() {
        return aspectId;
    }

    public void setAspectId(Expression aspectId) {
        this.aspectId = aspectId;
    }

    /**
     *
     * @return the Expression to select Annotations to be stored in DB. <br>
     * this expression is evaluated in the context of selected Sections and can
     * either returns Annotation or Tuple Elements (for single fragment
     * Annotation or multi-fragments Annotation respectively
     *
     */
    @Param(mandatory = false)
    public Expression getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Expression annotation) {
        this.annotations = annotation;
    }

    /**
     *
     * @return the Expression to select Annotations corresponding to the
     * fragments of the text-bound Annotation. <br> this expression is evaluated
     * in the context of the Element selected by getAnnotations()
     */
    @Param(mandatory = false)
    public Expression getFragments() {
        return fragments;
    }

    public void setFragments(Expression fragments) {
        this.fragments = fragments;
    }

    /**
     *
     * @return the Expression used to compute the annotation type name for
     * storage. <br> this expression evaluated as a String in the selected
     * Annotation context
     */
    @Param(mandatory = false)
    public Expression getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(Expression annotationType) {
        this.annotationType = annotationType;
    }

    /**
     *
     * @return
     *
     * <br>this expression is evaluated in the context of selected Sections
     * returns Tuple Elements
     */
    @Param(mandatory = false)
    public Expression getGroups() {
        return groups;
    }

    public void setGroups(Expression groups) {
        this.groups = groups;
    }

    /**
     *
     * @return <br>this expression is evaluated in the context of selected
     * Sections returns Tuple Elements
     */
    @Param(mandatory = false)
    public Expression getRelations() {
        return relations;
    }

    public void setRelations(Expression relations) {
        this.relations = relations;
    }

    /**
     * The elements matching this expression will be converted as Annotations
     * whose scope is the whole document. The created Annotation will inherit
     * the element's features
     *
     * @return <br>this expression is evaluated in the context of selected
     * Document.
     */
    @Param(mandatory = false)
    public Expression[] getToDocScopeAnnotation() {
        return toDocScopeAnnotation;
    }

    public void setToDocScopeAnnotation(Expression[] toDocScopeAnnotation) {
        this.toDocScopeAnnotation = toDocScopeAnnotation;
    }

    /**
     *
     * @return the Annotation type name used for annotations generated through
     * conversion of elements matching {@link #getToDocScopeAnnotation()}
     *
     */
    @Param(mandatory = false)
    public String[] getDocScopeAnnType() {
        return docScopeAnnType;
    }

    public void setDocScopeAnnType(String[] docScopeAnnType) {
        this.docScopeAnnType = docScopeAnnType;
    }

    //-- -------------------------------------------------------------------- -- 
    /**
     *
     * @return the JDBC url of the database used for storage
     */
    @Param
    public String getUrl() {
        return dbContext.getUrl();
    }

    public void setUrl(String url) {
        dbContext.setUrl(url);
    }

    /**
     *
     * @return the name of the database Schema used for storage
     */
    @Param(mandatory = false)
    public String getSchema() {
        return dbContext.getSchema();
    }

    public void setSchema(String schema) {
        dbContext.setSchema(schema);
    }

    /**
     *
     * @return the user name used to connect to the database
     */
    @Param
    public String getUsername() {
        return dbContext.getUsername();
    }

    public void setUsername(String username) {
        dbContext.setUsername(username);
    }

    /**
     *
     * @return the password used to connect to the database
     */
    @Param
    public String getPassword() {
        return dbContext.getPassword();
    }

    public void setPassword(String password) {
        dbContext.setPassword(password);
    }
}
