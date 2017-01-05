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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.types.Mapping;

/**
 *
 * @author fpapazian
 */
@AlvisNLPModule(beta = true)
public class SplitOverlaps extends SectionModule<SectionResolvedObjects> implements AnnotationCreator {

    //all Annotations from these Layers will be compared to find overlaps
    private String[] checkedlayerNames;
    //only Annotations from this Layer will be splitted to remove overlaps
    private String modifiedlayerName;
    //optional feature name used to store the index of splitted annotation parts
    private String indexFeatureName = null;
	
	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

    @Override
    public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
        Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);

        for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
//            logger.info("Processing Section '" + sec.getName() + "'");

            int nbSplit = 0;

            //layer containing annotation that can be splitted
            Layer layerToModify = sec.getLayer(modifiedlayerName);
            if (layerToModify == null) {
//                logger.warning("Missing layer to modify '" + modifiedlayerName + "' in Section '" + sec.getName() + "' - skipping...");
                continue;
            }

            //anonymous layer containing all non-modifiable annotation
            Layer readOnlyAnnLayer = new Layer(sec);

            Set<Layer> checkedLayers = new HashSet<Layer>();
            for (String checked : checkedlayerNames) {
                Layer layer = sec.getLayer(checked);
                if (layer != null) {
                    checkedLayers.add(layer);
                    if (layer != layerToModify) {
                        readOnlyAnnLayer.addAll(layer);
                    }
                }

            }
            if (checkedLayers.add(layerToModify)) {
//                logger.info("Layer to modify '" + modifiedlayerName + "' added to checked layer");
            }
            if (checkedLayers.isEmpty()) {
//                logger.warning("Missing layer to be checked '" + modifiedlayerName + "' in Section '" + sec.getName() + "' - skipping...");
                continue;
            }
            if (layerToModify.isEmpty()) {
//                logger.warning("Layer to modify '" + modifiedlayerName + "' is empty - skipping...");
                continue;
            }

            //anonymous layer containing all annotation to process
            Layer allAnnLayer = new Layer(sec);
            allAnnLayer.addAll(readOnlyAnnLayer);
            allAnnLayer.addAll(layerToModify);


            int startFrom = 0;
            boolean checkAgain = true;

            while (checkAgain) {
                checkAgain = false;

                //Note: Annotations are maintained sorted by position+size ("byOrder") in Layer
                Outer:
                for (int i = startFrom; i < allAnnLayer.size(); i++) {
                    Annotation outerAnn = allAnnLayer.get(i);

                    for (int j = i + 1; j < allAnnLayer.size(); j++) {
                        Annotation innerAnn = allAnnLayer.get(j);

                        if (innerAnn.getStart() >= outerAnn.getEnd()) {
                            break;
                        } else if (innerAnn.getEnd() > outerAnn.getEnd()) {


                            boolean outerReadOnly = readOnlyAnnLayer.contains(outerAnn);
                            boolean innerReadOnly = readOnlyAnnLayer.contains(innerAnn);

                            if (outerReadOnly && innerReadOnly) {
                                logger.severe("Overlapping annotations belonging to readonly layer :" + outerAnn.toString() + " / " + innerAnn.toString());
                                return;
                            }

                            Annotation untouched;
                            Annotation toSplit;
                            int splitPos;
                            if (innerReadOnly) {
                                untouched = innerAnn;
                                toSplit = outerAnn;
                                splitPos = untouched.getStart();

                            } else {
                                untouched = outerAnn;
                                toSplit = innerAnn;
                                splitPos = untouched.getEnd();
                            }
                            int leftAnnStart = toSplit.getStart();
                            int rightAnnEnd = toSplit.getEnd();

                            //split the inner annotation and replicate the features on each one
//                            logger.fine("Spliting annotation " + toSplit + " at position " + splitPos);

                            nbSplit++;

                            Annotation newLeftAnn = new Annotation(this, layerToModify, leftAnnStart, splitPos);
                            Annotation newRightAnn = new Annotation(this, layerToModify, splitPos, rightAnnEnd);
                            newLeftAnn.addMultiFeatures(toSplit.getFeatures());
                            newRightAnn.addMultiFeatures(toSplit.getFeatures());

                            //optionnaly store the index of the split annotation in the specified feature
                            if (indexFeatureName != null) {
                                int index = 0;
                                //retrieve index already stored in the annotation to split
                                String strPreviousIndex = toSplit.getFirstFeature(indexFeatureName);
                                if (strPreviousIndex == null) {
                                    //splitted annotations indexes start from 1
                                    newLeftAnn.addFeature(indexFeatureName, String.valueOf(++index));
                                } else {
                                    try {
                                        index = Integer.valueOf(strPreviousIndex);
                                    } catch (NumberFormatException ex) {
                                        newLeftAnn.addFeature(indexFeatureName, String.valueOf(++index));
                                    }
                                }
                                newRightAnn.addFeature(indexFeatureName, String.valueOf(++index));
                            }

                            //remove split annotation from modified layer
                            layerToModify.remove(toSplit);

                            //update the global layer containing all annotations
                            allAnnLayer.remove(toSplit);
                            allAnnLayer.add(newLeftAnn);
                            allAnnLayer.add(newRightAnn);

                            //because the splitting action caused order change from the current outer annotation onward, 
                            //so the processing need to resume with annotation preceeding the current outer one
                            startFrom = i >= 0 ? i - 1 : i;
                            checkAgain = true;
                            break Outer;
                        }
                    }
                }
            }
            if (nbSplit > 0) {
                logger.info("document " + sec.getDocument().getId() + " : " + nbSplit + " split event(s) occured in Section '" + sec.getName() + "'");
            }

        }
    }

    @Override
    protected String[] addLayersToSectionFilter() {
        return null;
    }

    @Override
    protected String[] addFeaturesToSectionFilter() {
        return null;
    }

    @Param(nameType=NameType.LAYER)
    public String[] getCheckedlayerNames() {
        return checkedlayerNames;
    }

    public void setCheckedlayerNames(String[] checkedlayerNames) {
        this.checkedlayerNames = checkedlayerNames;
    }

    @Param(nameType=NameType.LAYER)
    public String getModifiedlayerName() {
        return modifiedlayerName;
    }

    public void setModifiedlayerName(String modifiedlayerName) {
        this.modifiedlayerName = modifiedlayerName;
    }

    @Param(nameType=NameType.FEATURE, mandatory = false)
    public String getIndexFeatureName() {
        return indexFeatureName;
    }

    public void setIndexFeatureName(String indexFeatureName) {
        this.indexFeatureName = indexFeatureName;
    }

    @Override
    public Mapping getConstantAnnotationFeatures() {
        return null;
    }

    @Override
    public void setConstantAnnotationFeatures(Mapping mpng) {
    }
}
