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



package org.bibliome.alvisnlp.modules.mapper;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.Timer;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.Param;

// TODO: Auto-generated Javadoc
/**
 * Maps features to other atttributes.
 */
public abstract class Mapper extends SectionModule<SectionResolvedObjects> {
    private String                          mappedLayerName = DefaultNames.getWordLayer();
    private String                           sourceFeature   = Annotation.FORM_FEATURE_NAME;
    private String[]                         targetFeatures  = null;
    protected Boolean ignoreCase = false;
    
    public Mapper() {
        super();
    }

    @Override
    public String[] addLayersToSectionFilter() {
        return new String[] {
            mappedLayerName
        };
    }

    @Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
    	Timer<TimerCategory> dictTimer = getTimer(ctx, "load-dictionary", TimerCategory.LOAD_RESOURCE, true);
    	Map<String,List<List<String>>> mapping = getMapping(ctx);
        dictTimer.stop();
        int n = 0;
        int m = 0;
        for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
            for (Annotation annot : sec.getLayer(mappedLayerName)) {
                if (!annot.hasFeature(sourceFeature)) {
                    continue;
                }
                ++n;
                String sourceFeatureValue = annot.getLastFeature(sourceFeature);
                if (ignoreCase)
                	sourceFeatureValue = sourceFeatureValue.toLowerCase();
                if (!mapping.containsKey(sourceFeatureValue))
                    continue;
                ++m;
                for (List<String> v : mapping.get(sourceFeatureValue)) {
                    for (int i = 0; i < v.size() && i < targetFeatures.length; i++) {
                    	if (targetFeatures[i].isEmpty())
                    		continue;
                    	String s = v.get(i);
                    	if (s == null)
                    		continue;
                    	if (s.isEmpty())
                    		continue;
                    	annot.addFeature(targetFeatures[i], s);
                    }
                }
            }
        }
        if (n == 0) {
        	logger.warning("no annotations visited");
        }
        else if (m == 0) {
        	logger.warning(String.format("no annotations mapped (%d visited)", n));
        }
        else {
        	logger.fine(String.format("mapped %d/%d annotations", m, n));
        }
    }

    /**
     * Gets the mapping.
     * @param ctx TODO
     * 
     * @return the mapping
     * 
     * @throws ProcessingException
     *             the processing exception
     */
    public abstract Map<String,List<List<String>>> getMapping(ProcessingContext<Corpus> ctx) throws ProcessingException;

    /**
     * Gets the mapped layer name.
     * 
     * @return the mappedLayerName
     */
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing the annotations to map.")
    public String getMappedLayerName() {
        return mappedLayerName;
    }

    /**
     * Gets the source feature.
     * 
     * @return the sourceFeature
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Name of the feature to search in the mapping.")
    public String getSourceFeature() {
        return sourceFeature;
    }

    /**
     * Gets the target features.
     * 
     * @return the targetFeatures
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Names of features to fill with the mapping.")
    public String[] getTargetFeatures() {
        return targetFeatures;
    }

    @Param
    public Boolean getIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(Boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	/**
     * Sets the mapped layer name.
     * 
     * @param mappedLayerName
     *            the mappedLayerName to set
     */
    public void setMappedLayerName(String mappedLayerName) {
        this.mappedLayerName = mappedLayerName;
    }

    /**
     * Sets the source feature.
     * 
     * @param sourceFeature
     *            the sourceFeature to set
     */
    public void setSourceFeature(String sourceFeature) {
        this.sourceFeature = sourceFeature;
    }

    /**
     * Sets the target features.
     * 
     * @param targetFeatures
     *            the targetFeatures to set
     */
    public void setTargetFeatures(String[] targetFeatures) {
        this.targetFeatures = targetFeatures;
    }
}
