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


package org.bibliome.alvisnlp.modules.clone;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(beta=true)
public abstract class RemoveContents extends SectionModule<SectionResolvedObjects> implements AnnotationCreator, SectionCreator, TupleCreator {
	private String stripLayerName;

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		Map<Element,Element> map = new HashMap<Element,Element>();
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			cloneSection(logger, map, sec);
		}
		cloneArgs(map);
		removeOldSections(logger, map);
	}

	private void cloneSection(Logger logger, Map<Element,Element> map, Section sec) {
		Layer stripLayer = getStripLayer(logger, sec);
		String newContents = correctContents(stripLayer, sec.getContents());
		Section newSec = new Section(this, sec.getDocument(), sec.getName(), newContents);
		putMapping(map, sec, newSec);
		
		for (Layer layer : sec.getAllLayers()) {
			cloneLayer(map, stripLayer, newSec, layer);
		}
		
		for (Relation rel : sec.getAllRelations()) {
			cloneRelation(map, newSec, rel);
		}
	}
	
	private static void cloneArgs(Map<Element,Element> map) {
		for (Map.Entry<Element,Element> e : map.entrySet()) {
			Tuple oldT = DownCastElement.toTuple(e.getKey());
			if (oldT != null) {
				Tuple newT = DownCastElement.toTuple(e.getValue());
				for (String role : oldT.getRoles()) {
					Element oldArg = oldT.getArgument(role);
					newT.setArgument(role, map.containsKey(oldArg) ? map.get(oldArg) : oldArg);
				}
			}
		}
	}
	
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

	private void cloneRelation(Map<Element,Element> map, Section newSec, Relation rel) {
		Relation newRel = new Relation(this, newSec, rel.getName());
		putMapping(map, rel, newRel);
		for (Tuple t : rel.getTuples()) {
			Tuple newT = new Tuple(this, newRel);
			putMapping(map, t, newT);
		}
	}

	private void cloneLayer(Map<Element,Element> map, Layer stripLayer, Section newSec, Layer source) {
		Layer target = new Layer(newSec, source.getName());
		for (Annotation a : source) {
			int start = correctPosition(stripLayer, a.getStart());
			int end = correctPosition(stripLayer, a.getEnd());
			if (end > start || a.getLength() == 0) {
				Annotation newA = new Annotation(this, target, start, end);
				putMapping(map, a, newA);
			}
		}
	}

	private static void putMapping(Map<Element,Element> map, Element oldElt, Element newElt) {
		map.put(oldElt, newElt);
		newElt.addMultiFeatures(oldElt.getFeatures());
	}

	private static int correctPosition(Layer stripLayer, int position) {
		int result = position;
		for (Annotation a : stripLayer) {
			if (a.getStart() > position) {
				break;
			}
			result -= Math.min(position, a.getEnd()) - a.getStart();
		}
		return result;
	}
	
	private Layer getStripLayer(Logger logger, Section sec) {
		Layer stripLayer = sec.getLayer(stripLayerName);
		if (stripLayer.hasOverlaps()) {
			logger.warning("overlapping annotations, merging");
			return mergeOverlapping(stripLayer);
		}
		return stripLayer;
	}
	
	private static String correctContents(Layer stripLayer, String contents) {
		StringBuilder sb = new StringBuilder(contents.length());
		int reach = 0;
		for (Annotation a : stripLayer) {
			sb.append(contents.substring(reach, a.getStart()));
			reach = a.getEnd();
		}
		sb.append(contents.substring(reach));
		return sb.toString();
	}
	
	private Layer mergeOverlapping(Layer source) {
		Layer result = new Layer(source.getSection());
		int start = -1;
		int end = 0;
		for (Annotation a : source) {
			if (a.getStart() > end) {
				if (start > -1) {
					new Annotation(this, result, start, end);
				}
				start = a.getStart();
				end = a.getEnd();
				continue;
			}
			start = Math.min(end, a.getStart());
			end = Math.max(end, a.getEnd());
		}
		if (start > -1) {
			new Annotation(this, result, start, end);
		}
		return result;
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { stripLayerName };
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
	public String getStripLayerName() {
		return stripLayerName;
	}

	public void setStripLayerName(String stripLayerName) {
		this.stripLayerName = stripLayerName;
	}
}
