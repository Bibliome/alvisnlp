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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DownCastElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.DocumentCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Pair;

@AlvisNLPModule(beta=true)
public abstract class SplitSections extends SectionModule<SectionResolvedObjects> implements AnnotationCreator, DocumentCreator, SectionCreator, TupleCreator {
	private String selectLayerName;
	private Boolean mergeOverlapping = false;
	private Boolean splitDocuments = false;
	private String croppedAnnotationFeatureName = "cropped";

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		ElementMapping map = new ElementMapping();
		Collection<Document> documents = Iterators.fill(documentIterator(evalCtx, corpus), new ArrayList<Document>());
		for (Document doc : documents) {
			int nSelect = 0;
			Collection<Section> sections = Iterators.fill(sectionIterator(evalCtx, doc), new ArrayList<Section>());
			for (Section sec : sections) {
				Layer selectLayer = getSelectLayer(logger, sec);
				for (Annotation selectAnnotation : selectLayer) {
					Document newDoc = getNewDocument(map, doc, nSelect++);
					clone(map, selectAnnotation, newDoc);
				}
			}
		}
		cloneArgs(map);
	}
	
	@SuppressWarnings("serial")
	private static class ElementMapping extends LinkedHashMap<Pair<Document,Element>,Element> {
		private ElementMapping() {
			super();
		}

		private void put(Document newDoc, Element oldElt, Element newElt, boolean copyFeatures) {
			put(new Pair<Document,Element>(newDoc, oldElt), newElt);
			if (copyFeatures) {
				newElt.addMultiFeatures(oldElt.getFeatures());
			}
		}
		
		private void put(Document newDoc, Element oldElt, Element newElt) {
			put(newDoc, oldElt, newElt, true);
		}
		
		private Element get(Document newDoc, Element oldElt) {
			return get(new Pair<Document,Element>(newDoc, oldElt));
		}
	}

	private Layer getSelectLayer(Logger logger, Section sec) {
		Layer selectLayer = sec.getLayer(selectLayerName);
		if (mergeOverlapping && selectLayer.hasOverlaps()) {
			logger.warning("overlapping annotations, merging");
			return selectLayer.mergeOverlaps(this);
		}
		return selectLayer;
	}

	private Document getNewDocument(ElementMapping map, Document doc, int nSelect) {
		if (splitDocuments) {
			String id = doc.getId();
			String newId = String.format("%s__%03d", id, nSelect);
			Document result = Document.getDocument(this, doc.getCorpus(), newId);
			map.put(result, doc, result);
			return result;
		}
		map.put(doc, doc, doc, false);
		return doc;
	}

	private void clone(ElementMapping map, Annotation selectAnnotation, Document newDoc) {
		Section sec = selectAnnotation.getSection();
		String newContents = selectAnnotation.getForm();
		Section newSec = new Section(this, newDoc, sec.getName(), newContents);
		map.put(newDoc, sec, newSec);
		
		for (Layer layer : sec.getAllLayers()) {
			cloneLayer(map, selectAnnotation, newSec, layer);
		}
		
		for (Relation rel : sec.getAllRelations()) {
			cloneRelation(map, newSec, rel);
		}
	}

	private void cloneLayer(ElementMapping map, Annotation selectAnnotation, Section newSec, Layer source) {
		Document newDoc = newSec.getDocument();
		Layer target = new Layer(newSec, source.getName());
		int selectStart = selectAnnotation.getStart();
		int selectEnd = selectAnnotation.getEnd();
		for (Annotation a : source) {
			int start = a.getStart();
			int end = a.getEnd();
			if (end <= selectStart) {
				continue;
			}
			if (start >= selectEnd) {
				break;
			}
			int newStart = start < selectStart ? 0 : start - selectStart;
			int newEnd = end > selectEnd ? selectEnd - selectStart : end - selectStart;
			Annotation newA = new Annotation(this, target, newStart, newEnd);
			map.put(newDoc, a, newA);
			if ((start < selectStart) || (end > selectEnd)) {
				newA.addFeature(croppedAnnotationFeatureName, "true");
			}
		}
	}

	private void cloneRelation(ElementMapping map, Section newSec, Relation rel) {
		Document newDoc = newSec.getDocument();
		Relation newRel = new Relation(this, newSec, rel.getName());
		map.put(newDoc, rel, newRel);
		for (Tuple t : rel.getTuples()) {
			Tuple newT = new Tuple(this, newRel);
			map.put(newDoc, t, newT);
		}
	}
	
	private static void cloneArgs(ElementMapping map) {
		for (Map.Entry<Pair<Document,Element>,Element> e : map.entrySet()) {
			Tuple oldT = DownCastElement.toTuple(e.getKey().second);
			if (oldT != null) {
				Element elt = e.getValue();
				Tuple newT = DownCastElement.toTuple(elt);
				Document newDoc = newT.getRelation().getSection().getDocument();
				for (String role : oldT.getRoles()) {
					Element oldArg = oldT.getArgument(role);
					Element newArg = map.get(newDoc, oldArg);
					newT.setArgument(role, newArg);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private static void removeOldSections(Logger logger, Map<Element,Element> map) {
		int n = 0;
		for (Map.Entry<Element,Element> e : map.entrySet()) {
			Section oldSec = DownCastElement.toSection(e.getKey());
			if (oldSec != null) {
				Document oldDoc = oldSec.getDocument();
				oldDoc.removeSection(oldSec);
				n++;
			}
		}
		logger.info("removed " + n + " sections");
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { selectLayerName };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Param(nameType=NameType.LAYER)
	public String getSelectLayerName() {
		return selectLayerName;
	}

	@Param
	public Boolean getSplitDocuments() {
		return splitDocuments;
	}

	@Param
	public Boolean getMergeOverlapping() {
		return mergeOverlapping;
	}

	@Param(nameType=NameType.FEATURE)
	public String getCroppedAnnotationFeatureName() {
		return croppedAnnotationFeatureName;
	}

	public void setCroppedAnnotationFeatureName(String croppedAnnotationFeatureName) {
		this.croppedAnnotationFeatureName = croppedAnnotationFeatureName;
	}

	public void setMergeOverlapping(Boolean mergeOverlapping) {
		this.mergeOverlapping = mergeOverlapping;
	}

	public void setSelectLayerName(String selectLayerName) {
		this.selectLayerName = selectLayerName;
	}

	public void setSplitDocuments(Boolean splitDocuments) {
		this.splitDocuments = splitDocuments;
	}
}
