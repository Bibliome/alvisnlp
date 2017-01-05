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

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.Timer;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

// TODO: Auto-generated Javadoc
/**
 * The layer comparator compares the annotations in a layer against another
 * reference layer. The result is given in terms of precision and recall for
 * each section and for the whole corpus. The annotation comparison is exact
 * same span.
 */
@AlvisNLPModule
public class LayerComparator extends SectionModule<SectionResolvedObjects> {

    /** Layer containing reference annotations. */
    private String referenceLayerName = null;

    /** Layer containing predicted annotations. */
    private String predictedLayerName = null;

    /** Path to the file where to write the results. */
    private TargetStream   outFile            = null;

    /** Character encoding of the results file. */
//    private String charset            = "UTF-8";

    /*
     * (non-Javadoc)
     * 
     * @see alvisnlp.module.lib.SectionModule#addFeaturesToSectionFilter()
     */
    @Override
    public String[] addFeaturesToSectionFilter() {
        return new String[] {};
    }

    /*
     * (non-Javadoc)
     * 
     * @see alvisnlp.module.lib.SectionModule#addLayersToSectionFilter()
     */
    @Override
    public String[] addLayersToSectionFilter() {
        return null;
    }

    @Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	/*
     * (non-Javadoc)
     * 
     * @see
     * alvisnlp.module.lib.ModuleBase#process(alvisnlp.module.ProcessingContext,
     * alvisnlp.document.Corpus)
     */
    @Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
        PrintStream ps = null;
        try {
//            ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(outFile)), false, charset);
            ps = outFile.getPrintStream();
        }
        catch (IOException ioe) {
            rethrow(ioe);
        }
        int corpusTP = 0;
        int corpusRef = 0;
        int corpusPred = 0;
        Timer<TimerCategory> writeTimer = getTimer(ctx, "write-txt", TimerCategory.PREPARE_DATA, false);
        for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
            Layer referenceLayer = sec.ensureLayer(referenceLayerName);
            Layer predictedLayer = sec.ensureLayer(predictedLayerName);
            int tp = 0;
            writeTimer.start();
            ps.printf("Document %s, section %s\n    False positives:\n", sec.getDocument().getId(), sec.getName());
            writeTimer.stop();
            for (Annotation pred : predictedLayer) {
            	if (referenceLayer.span(pred).size() > 0)
            		tp++;
            	else
            		printAnnotation(writeTimer, ps, pred);
            }
            writeTimer.start();
            ps.print("    False negatives:\n");
            writeTimer.stop();
            for (Annotation ref : referenceLayer) {
            	if (predictedLayer.span(ref).size() == 0)
            		printAnnotation(writeTimer, ps, ref);
            }
            writeTimer.start();
            ps.printf("    True positives: %d\n", tp);
            ps.print("    Span mismatches:\n");
            writeTimer.stop();
            for (Annotation ref : referenceLayer) {
            	for (Annotation pred : predictedLayer.overlapping(ref))
            		printSpanMismatch(writeTimer, ps, ref, pred);
            }
            int nPred = predictedLayer.size();
            int nRef = referenceLayer.size();
            printResults(writeTimer, ps, tp, nRef, nPred);
            corpusTP += tp;
            corpusRef += nRef;
            corpusPred += nPred;
        }
        writeTimer.start();
        ps.print("Global scores:\n");
        writeTimer.stop();
        printResults(writeTimer, ps, corpusTP, corpusRef, corpusPred);
        ps.close();
    }

    /**
     * Prints the annotation.
     * @param ps
     *            the ps
     * @param annot
     *            the annot
     */
    private static void printAnnotation(Timer<TimerCategory> writeTimer, PrintStream ps, Annotation annot) {
    	writeTimer.start();
    	ps.printf("        '%s' at %d\n", annot.getForm(), annot.getStart());
    	writeTimer.stop();
    }

    /**
     * Prints the span mismatch.
     * @param ps
     *            the ps
     * @param ref
     *            the ref
     * @param pred
     *            the pred
     */
    private static void printSpanMismatch(Timer<TimerCategory> writeTimer, PrintStream ps, Annotation ref, Annotation pred) {
        if (ref.sameSpan(pred))
            return;
        writeTimer.start();
        ps.printf("        '%s' at %d should be '%s' at %d\n", pred.getForm(), pred.getStart(), ref.getForm(), ref.getStart());
        writeTimer.stop();
    }

    /**
     * Prints the results.
     * @param ps
     *            the ps
     * @param tp
     *            the tp
     * @param ref
     *            the ref
     * @param pred
     *            the pred
     */
    private static void printResults(Timer<TimerCategory> writeTimer, PrintStream ps, int tp, int ref, int pred) {
        double h = 100.0 * tp;
        writeTimer.start();
        ps.printf("    Recall      %2.6f%%\n    Precision   %2.6f%%\n\n", h / ref, h / pred);
        writeTimer.stop();
    }

    /**
     * Gets the reference layer name.
     * 
     * @return the referenceLayerName
     */
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the reference layer.")
    public String getReferenceLayerName() {
        return referenceLayerName;
    }

    /**
     * Sets the reference layer name.
     * 
     * @param referenceLayerName
     *            the referenceLayerName to set
     */
    public void setReferenceLayerName(String referenceLayerName) {
        this.referenceLayerName = referenceLayerName;
    }

    /**
     * Gets the predicted layer name.
     * 
     * @return the predictedLayerName
     */
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the source layer.")
    public String getPredictedLayerName() {
        return predictedLayerName;
    }

    /**
     * Sets the predicted layer name.
     * 
     * @param predictedLayerName
     *            the predictedLayerName to set
     */
    public void setPredictedLayerName(String predictedLayerName) {
        this.predictedLayerName = predictedLayerName;
    }

    /**
     * Gets the out file.
     * 
     * @return the outFile
     */
    @Param(defaultDoc = "Path to the file where to store results.")
    public TargetStream getOutFile() {
        return outFile;
    }

    /**
     * Sets the out file.
     * 
     * @param outFile
     *            the outFile to set
     */
    public void setOutFile(TargetStream outFile) {
        this.outFile = outFile;
    }
}
