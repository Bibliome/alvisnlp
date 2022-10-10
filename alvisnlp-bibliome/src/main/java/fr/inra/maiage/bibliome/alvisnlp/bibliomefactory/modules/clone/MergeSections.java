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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.clone;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.StringCat;
import fr.inra.maiage.bibliome.util.mappers.Mappers;

@AlvisNLPModule
public abstract class MergeSections extends SectionModule<SectionResolvedObjects> implements SectionCreator, AnnotationCreator, TupleCreator {
	private String sectionSeparator = "";
	private String fragmentSeparator = "";
	private String targetSection;
	private Boolean removeSections = true;
	private String sectionsLayer;
	private FragmentSelection fragmentSelection = FragmentSelection.EXLUDE;
	private String fragmentLayer;

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		for (Document doc : Iterators.loop(documentIterator(evalCtx, corpus))) {
			List<Section> sections = getSections(evalCtx, doc);
			List<Layer> keepLayers = Mappers.apply(fragmentSelection, fragmentLayer, sections, new ArrayList<Layer>());
			String newContents = getContents(keepLayers);
			Map<Annotation,Integer> offsets = computeOffsets(keepLayers);
			Section newSection = new Section(this, doc, targetSection, newContents);

			Map<Element,Element> mapping = new LinkedHashMap<Element,Element>();
			for (int i = 0; i < sections.size(); ++i)
				cloneSection(mapping, sections.get(i), keepLayers.get(i), newSection, offsets);

			for (Section sec : sections)
				cloneTupleArgs(mapping, sec);

			if (removeSections)
				for (Section sec : sections)
					doc.removeSection(sec);
		}
	}

	private Map<Annotation,Integer> computeOffsets(List<Layer> keepLayers) {
		Map<Annotation,Integer> result = new LinkedHashMap<Annotation,Integer>();
		int offset = 0;
		boolean notFirstSection = false;
		for (Layer layer : keepLayers) {
			if (notFirstSection)
				offset += sectionSeparator.length();
			else
				notFirstSection = true;
			boolean notFirstFragment = false;
			for (Annotation a : layer) {
				if (notFirstFragment)
					offset += fragmentSeparator.length();
				else
					notFirstFragment = true;
				result.put(a, offset);
				offset += a.getLength();
			}
		}
		return result;
	}

	private void createSectionAnnotation(Map<Element,Element> mapping, Section sec, Layer keepLayer, Section newSection, Map<Annotation,Integer> offsets) {
//		System.err.println("offsets = " + offsets);
//		System.err.println("keepLayer = " + keepLayer);
//		System.err.println("first = " + keepLayer.first());
		int start = offsets.get(keepLayer.first());
		Annotation last = keepLayer.last();
		int end = offsets.get(last) + last.getLength();
		Annotation a = new Annotation(this, newSection.ensureLayer(sectionsLayer), start, end);
		clone(mapping, sec, a);
	}

	private void cloneSection(Map<Element,Element> mapping, Section sec, Layer keepLayer, Section newSection, Map<Annotation,Integer> offsets) {
		createSectionAnnotation(mapping, sec, keepLayer, newSection, offsets);

		for (Layer layer : sec.getAllLayers()) {
			Layer newLayer = newSection.ensureLayer(layer.getName());
			for (Annotation a : layer) {
				Layer zones = keepLayer.overlapping(a);
				if (zones.isEmpty()) {
					mapping.put(a, null);
					continue;
				}
				Annotation z = zones.first();
				int offset = offsets.get(z) - z.getStart();
				clone(mapping, a, new Annotation(this, newLayer, offset + a.getStart(), offset + a.getEnd()));
			}
		}

		for (Relation rel : sec.getAllRelations()) {
			Relation newRelation = clone(mapping, rel, newSection.ensureRelation(this, rel.getName()));
			for (Tuple t : rel.getTuples())
				clone(mapping, t, new Tuple(this, newRelation));
		}
	}

	private static void cloneTupleArgs(Map<Element,Element> mapping, Section sec) {
		for (Relation rel : sec.getAllRelations()) {
			for (Tuple t : rel.getTuples()) {
				Tuple newTuple = (Tuple) mapping.get(t);
				for (String role : t.getRoles()) {
					Element arg = t.getArgument(role);
					if (mapping.containsKey(arg))
						arg = mapping.get(arg);
					newTuple.setArgument(role, arg);
				}
			}
		}
	}

	private static <E extends Element> E clone(Map<Element,Element> mapping, Element old, E newElement) {
		newElement.addMultiFeatures(old.getFeatures());
		mapping.put(old, newElement);
		return newElement;
	}

	private String getContents(List<Layer> keepLayers) {
		StringCat strcat = new StringCat();
		boolean notFirstSection = false;
		for (Layer l : keepLayers) {
			if (notFirstSection)
				strcat.append(sectionSeparator);
			else
				notFirstSection = true;
			boolean notFirstFragment = false;
			for (Annotation a : l) {
				if (notFirstFragment)
					strcat.append(fragmentSeparator);
				else
					notFirstFragment = true;
				strcat.append(a.getForm());
			}
		}
		return strcat.toString();
	}

	private List<Section> getSections(EvaluationContext evalCtx, Document doc) {
		List<Section> result = new ArrayList<Section>();
		Iterators.fill(sectionIterator(evalCtx, doc), result);
		return result;
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Param
	public String getSectionSeparator() {
		return sectionSeparator;
	}

	@Param
	public String getFragmentSeparator() {
		return fragmentSeparator;
	}

	@Deprecated
	@Param(nameType=NameType.SECTION)
	public String getTargetSectionName() {
		return targetSection;
	}

	@Param
	public Boolean getRemoveSections() {
		return removeSections;
	}

	@Param(nameType=NameType.LAYER, mandatory=false)
	public String getSectionsLayer() {
	    return this.sectionsLayer;
	};

	public void setSectionsLayer(String sectionsLayer) {
	    this.sectionsLayer = sectionsLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER, mandatory=false)
	public String getSectionsLayerName() {
		return sectionsLayer;
	}

	@Param
	public FragmentSelection getFragmentSelection() {
		return fragmentSelection;
	}

	@Param(nameType=NameType.LAYER, mandatory=false)
	public String getFragmentLayer() {
	    return this.fragmentLayer;
	};

	public void setFragmentLayer(String fragmentLayer) {
	    this.fragmentLayer = fragmentLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER, mandatory=false)
	public String getFragmentLayerName() {
		return fragmentLayer;
	}

	@Param(nameType=NameType.SECTION)
	public String getTargetSection() {
		return targetSection;
	}

	public void setTargetSection(String targetSection) {
		this.targetSection = targetSection;
	}

	public void setFragmentLayerName(String fragmentLayer) {
		this.fragmentLayer = fragmentLayer;
	}

	public void setSectionSeparator(String sectionSeparator) {
		this.sectionSeparator = sectionSeparator;
	}

	public void setFragmentSeparator(String fragmentSeparator) {
		this.fragmentSeparator = fragmentSeparator;
	}

	public void setTargetSectionName(String targetSectionName) {
		this.targetSection = targetSectionName;
	}

	public void setRemoveSections(Boolean removeSections) {
		this.removeSections = removeSections;
	}

	public void setSectionsLayerName(String sectionsLayer) {
		this.sectionsLayer = sectionsLayer;
	}

	public void setFragmentSelection(FragmentSelection fragmentSelection) {
		this.fragmentSelection = fragmentSelection;
	}
}
