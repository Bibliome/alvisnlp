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

import java.util.ArrayList;
import java.util.Collection;
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
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;

// TODO: Auto-generated Javadoc
/**
 * Creates a layer containing annotations in a set of source layers.
 */
@AlvisNLPModule
public class MergeLayers extends SectionModule<SectionResolvedObjects> {
    private String[]             sourceLayers     = null;
    private String               targetLayer      = null;

    @Override
    public String[] addFeaturesToSectionFilter() {
        return new String[] {};
    }

    @Override
    public String[] addLayersToSectionFilter() {
        return null;
    }

    @Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
        int n = 0;
        for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
        	Collection<Annotation> toAdd = new ArrayList<Annotation>();
            for (String sln : sourceLayers) {
                if (!sec.hasLayer(sln))
                    continue;
                Layer sl = sec.getLayer(sln);
                toAdd.addAll(sl);
            }
            Layer layer = sec.ensureLayer(targetLayer);
            layer.addAll(toAdd);
            n += toAdd.size();
        }
        if (n == 0) {
        	logger.warning("moved no annotations");
        }
        else {
        	logger.info("moved " + n + " annotations");
        }
    }

    /**
     * Gets the source layer names.
     *
     * @return the sourceLayers
     */
    @Param(nameType=NameType.LAYER)
    public String[] getSourceLayers() {
        return this.sourceLayers;
    };

    public void setSourceLayers(String[] sourceLayers) {
        this.sourceLayers = sourceLayers;
    };

    @Deprecated
    @Param(nameType=NameType.LAYER)
    public String[] getSourceLayerNames() {
        return sourceLayers;
    }

    /**
     * Sets the source layer names.
     *
     * @param sourceLayers
     *            the sourceLayers to set
     */
    public void setSourceLayerNames(String[] sourceLayers) {
        this.sourceLayers = sourceLayers;
    }

    /**
     * Gets the target layer name.
     *
     * @return the targetLayer
     */
    @Param(nameType=NameType.LAYER)
    public String getTargetLayer() {
        return this.targetLayer;
    };

    public void setTargetLayer(String targetLayer) {
        this.targetLayer = targetLayer;
    };

    @Deprecated
    @Param(nameType=NameType.LAYER)
    public String getTargetLayerName() {
        return targetLayer;
    }

    /**
     * Sets the target layer name.
     *
     * @param targetLayer
     *            the targetLayer to set
     */
    public void setTargetLayerName(String targetLayer) {
        this.targetLayer = targetLayer;
    }
}
