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

import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.AnnotationComparator;
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

@AlvisNLPModule
public class RemoveOverlaps extends SectionModule<SectionResolvedObjects> {
	private String layerName;
	private AnnotationComparator annotationComparator = AnnotationComparator.byLength;
	private Boolean removeEqual = true;
	private Boolean removeIncluded = true;
	private Boolean removeOverlapping = true;
	
	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		int removed = 0;
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			Layer layer = sec.getLayer(layerName);
			int n = layer.size();
			layer.removeOverlaps(annotationComparator, removeEqual, removeIncluded, removeOverlapping);
			removed += n - layer.size();
		}
		getLogger(ctx).info("removed " + removed + " annotations");
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { layerName };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Param(nameType=NameType.LAYER)
	public String getLayerName() {
		return layerName;
	}

	@Param
	public AnnotationComparator getAnnotationComparator() {
		return annotationComparator;
	}

	@Param
	public Boolean getRemoveEqual() {
		return removeEqual;
	}

	@Param
	public Boolean getRemoveIncluded() {
		return removeIncluded;
	}

	@Param
	public Boolean getRemoveOverlapping() {
		return removeOverlapping;
	}

	public void setRemoveEqual(Boolean removeEqual) {
		this.removeEqual = removeEqual;
	}

	public void setRemoveIncluded(Boolean removeIncluded) {
		this.removeIncluded = removeIncluded;
	}

	public void setRemoveOverlapping(Boolean removeOverlapping) {
		this.removeOverlapping = removeOverlapping;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public void setAnnotationComparator(AnnotationComparator annotationComparator) {
		this.annotationComparator = annotationComparator;
	}
}
