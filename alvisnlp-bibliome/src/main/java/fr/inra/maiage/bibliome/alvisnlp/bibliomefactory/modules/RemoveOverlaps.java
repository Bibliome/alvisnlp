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

import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;

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
	private String layer;
	private AnnotationComparator annotationComparator = AnnotationComparator.byLength;
	private Boolean removeEqual = true;
	private Boolean removeIncluded = true;
	private Boolean removeOverlapping = true;

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		int removed = 0;
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			Layer layer = sec.getLayer(this.layer);
			int n = layer.size();
			layer.removeOverlaps(annotationComparator, removeEqual, removeIncluded, removeOverlapping);
			removed += n - layer.size();
		}
		if (removed > 0) {
			System.gc();
		}
		getLogger(ctx).info("removed " + removed + " annotations");
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { layer };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getLayerName() {
		return layer;
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

	@Param(nameType=NameType.LAYER)
	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
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
		this.layer = layerName;
	}

	public void setAnnotationComparator(AnnotationComparator annotationComparator) {
		this.annotationComparator = annotationComparator;
	}
}
