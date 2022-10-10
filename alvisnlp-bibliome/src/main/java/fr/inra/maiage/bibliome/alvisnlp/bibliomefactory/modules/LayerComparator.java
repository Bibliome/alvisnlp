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

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Timer;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

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
    private String[] referenceLayer = null;

    /** Layer containing predicted annotations. */
    private String[] predictedLayer = null;

    /** Path to the file where to write the results. */
    private TargetStream   outFile            = null;

    /** Character encoding of the results file. */
//    private String charset            = "UTF-8";

    /*
     * (non-Javadoc)
     *
     * @see fr.inra.maiage.bibliome.alvisnlp.core.module.lib.SectionModule#addFeaturesToSectionFilter()
     */
    @Override
    public String[] addFeaturesToSectionFilter() {
        return new String[] {};
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.inra.maiage.bibliome.alvisnlp.core.module.lib.SectionModule#addLayersToSectionFilter()
     */
    @Override
    public String[] addLayersToSectionFilter() {
        return null;
    }

    @Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

    private static Layer fillLayer(Section sec, String[] names) {
    	Layer result = new Layer(sec);
    	for (String name : names) {
    		if (sec.hasLayer(name)) {
    			result.addAll(sec.getLayer(name));
    		}
    	}
    	return result;
    }

	/*
     * (non-Javadoc)
     *
     * @see
     * fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ModuleBase#process(fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext,
     * fr.inra.maiage.bibliome.alvisnlp.core.document.Corpus)
     */
    @Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
    	Logger logger = getLogger(ctx);
    	EvaluationContext evalCtx = new EvaluationContext(logger);
    	try (PrintStream ps = outFile.getPrintStream()) {
    		int corpusTP = 0;
    		int relaxedCorpusTP = 0;
    		int corpusRef = 0;
    		int corpusPred = 0;
    		Timer<TimerCategory> writeTimer = getTimer(ctx, "write-txt", TimerCategory.PREPARE_DATA, false);
    		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
    			Layer referenceLayer = fillLayer(sec, getReferenceLayerName());
    			Layer predictedLayer = fillLayer(sec, getPredictedLayerName());
    			int tp = 0;
    			int relaxedTP = 0;
    			writeTimer.start();
    			ps.printf("Document %s, section %s\n    False positives:\n", sec.getDocument().getId(), sec.getName());
    			writeTimer.stop();
    			for (Annotation pred : predictedLayer) {
    				if (referenceLayer.span(pred).size() > 0) {
    					tp++;
    					relaxedTP++;
    				}
    				else {
        				if (referenceLayer.overlapping(pred).size() > 0) {
        					relaxedTP++;
        				}
    					printAnnotation(writeTimer, ps, pred);
    				}
    			}
    			writeTimer.start();
    			ps.print("    False negatives:\n");
    			writeTimer.stop();
    			for (Annotation ref : referenceLayer) {
    				Layer matches = predictedLayer.span(ref);
    				if (matches.size() == 0)
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
    			printResults(writeTimer, ps, tp, nRef, nPred, "Strict");
    			printResults(writeTimer, ps, relaxedTP, nRef, nPred, "Relaxed");
    			corpusTP += tp;
    			relaxedCorpusTP += relaxedTP;
    			corpusRef += nRef;
    			corpusPred += nPred;
    		}
    		writeTimer.start();
    		ps.print("Global scores:\n");
    		writeTimer.stop();
    		printResults(writeTimer, ps, corpusTP, corpusRef, corpusPred, "Strict");
    		printResults(writeTimer, ps, relaxedCorpusTP, corpusRef, corpusPred, "Relaxed");
    	}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
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
    private static void printResults(Timer<TimerCategory> writeTimer, PrintStream ps, int tp, int ref, int pred, String prefix) {
        double h = 100.0 * tp;
        writeTimer.start();
        double rec = h/ref;
        double pre = h/pred;
        double f1 = 2 * rec * pre / (rec + pre);
        ps.printf("    %s Recall      %2.6f%%\n    %s Precision   %2.6f%%\n    %s F1          %2.6f\n\n", prefix, rec, prefix, pre, prefix, f1);
        writeTimer.stop();
    }

    /**
     * Gets the reference layer name.
     *
     * @return the referenceLayer
     */
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the reference layer.")
    public String[] getReferenceLayer() {
        return this.referenceLayer;
    };

    public void setReferenceLayer(String[] referenceLayer) {
        this.referenceLayer = referenceLayer;
    };

    @Deprecated
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the reference layer.")
    public String[] getReferenceLayerName() {
        return referenceLayer;
    }

    /**
     * Sets the reference layer name.
     *
     * @param referenceLayer
     *            the referenceLayer to set
     */
    public void setReferenceLayerName(String[] referenceLayer) {
        this.referenceLayer = referenceLayer;
    }

    /**
     * Gets the predicted layer name.
     *
     * @return the predictedLayer
     */
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the source layer.")
    public String[] getPredictedLayer() {
        return this.predictedLayer;
    };

    public void setPredictedLayer(String[] predictedLayer) {
        this.predictedLayer = predictedLayer;
    };

    @Deprecated
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the source layer.")
    public String[] getPredictedLayerName() {
        return predictedLayer;
    }

    /**
     * Sets the predicted layer name.
     *
     * @param predictedLayer
     *            the predictedLayer to set
     */
    public void setPredictedLayerName(String[] predictedLayer) {
        this.predictedLayer = predictedLayer;
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
