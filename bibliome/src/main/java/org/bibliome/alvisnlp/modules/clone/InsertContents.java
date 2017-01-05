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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.clone.InsertContents.InsertContentsResolvedObjects;
import org.bibliome.util.Iterators;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(beta=true)
public abstract class InsertContents extends SectionModule<InsertContentsResolvedObjects> implements SectionCreator, AnnotationCreator, TupleCreator {
	private Expression points;
	private Expression offset;
	private Expression insert;
	
	public static class InsertContentsResolvedObjects extends SectionResolvedObjects {
		private final Evaluator points;
		private final Evaluator offset;
		private final Evaluator insert;

		private InsertContentsResolvedObjects(ProcessingContext<Corpus> ctx, InsertContents module) throws ResolverException {
			super(ctx, module);
			this.points = module.points.resolveExpressions(rootResolver);
			this.offset = module.offset.resolveExpressions(rootResolver);
			this.insert = module.insert.resolveExpressions(rootResolver);
		}
	}
	
	@Override
	protected InsertContentsResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new InsertContentsResolvedObjects(ctx, this);
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		Map<Element,Element> map = new HashMap<Element,Element>();
		EvaluationContext evalCtx = new EvaluationContext(logger);
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			cloneSection(logger, sec, map);
		}
		cloneArgs(map);
		removeOldSections(logger, map);
	}
	
	private void cloneSection(Logger logger, Section oldSec, Map<Element,Element> map) {
		InsertPoint[] points = getInsertPoints(logger, oldSec);
		String newContents = getNewContents(points, oldSec);

		Section newSec = new Section(this, oldSec.getDocument(), oldSec.getName(), newContents);
		putMapping(map, oldSec, newSec);

		for (Layer layer : oldSec.getAllLayers()) {
			cloneLayer(newSec, points, layer, map);
		}
		
		for (Relation oldRel : oldSec.getAllRelations()) {
			cloneRelation(newSec, oldRel, map);
		}
	}
	
	private static class InsertPoint implements Comparable<InsertPoint> {
		private final int offset;
		private final String insert;
		private int cumulativeOffset;
		
		private InsertPoint(int offset, String insert) {
			super();
			this.offset = offset;
			this.insert = insert;
		}

		@Override
		public int compareTo(InsertPoint o) {
			return Integer.compare(offset, o.offset);
		}
	}
	
	private InsertPoint[] getInsertPoints(Logger logger, Section sec) {
		List<InsertPoint> list = createInsertPoints(logger, sec);
		Collections.sort(list);
		int secLen = sec.getContents().length();
		Iterator<InsertPoint> it = list.iterator();
		InsertPoint prevPoint = null;
		while (it.hasNext()) {
			InsertPoint point = it.next();
			if (point.offset < 0) {
				logger.warning("negative offset");
				it.remove();
				continue;
			}
			if (point.offset > secLen) {
				logger.warning("offset > section contents");
				it.remove();
				continue;
			}
			if (prevPoint == null) {
				point.cumulativeOffset = point.insert.length();
			}
			else {
				if (prevPoint.offset == point.offset) {
					logger.warning("conflicting offsets");
					it.remove();
					continue;
				}
				point.cumulativeOffset = prevPoint.cumulativeOffset + point.insert.length();
			}
			prevPoint = point;
		}
		Collections.reverse(list);
		return list.toArray(new InsertPoint[list.size()]);		
	}

	private List<InsertPoint> createInsertPoints(Logger logger, Section sec) {
		InsertContentsResolvedObjects resObj = getResolvedObjects();
		List<InsertPoint> result = new ArrayList<InsertPoint>();
		EvaluationContext evalCtx = new EvaluationContext(logger);
		for (Element elt : Iterators.loop(resObj.points.evaluateElements(evalCtx, sec))) {
			int offset = resObj.offset.evaluateInt(evalCtx, elt);
			String insert = resObj.insert.evaluateString(evalCtx, elt);
			InsertPoint point = new InsertPoint(offset, insert);
			result.add(point);
		}
		return result;
	}
	
	private static String getNewContents(InsertPoint[] points, Section sec) {
		String oldContents = sec.getContents();
		StringBuilder sb = new StringBuilder();
		int last = 0;
		for (int i = points.length - 1; i >= 0; --i) {
			InsertPoint point = points[i];
			sb.append(oldContents.substring(last, point.offset));
			sb.append(point.insert);
			last = point.offset;
		}
		sb.append(oldContents.substring(last));
		return sb.toString();
	}
	
	private static void putMapping(Map<Element,Element> map, Element oldElt, Element newElt) {
		map.put(oldElt, newElt);
		newElt.addMultiFeatures(oldElt.getFeatures());
	}

	private void cloneLayer(Section newSec, InsertPoint[] points, Layer oldLayer, Map<Element,Element> map) {
		Layer newLayer = new Layer(newSec, oldLayer.getName());
		for (Annotation oldA : oldLayer) {
			int start = getOffset(points, oldA.getStart(), true);
			int end = getOffset(points, oldA.getEnd(), false);
			Annotation newA = new Annotation(this, newLayer, start, end);
			putMapping(map, oldA, newA);
		}
	}

	private static int getOffset(InsertPoint[] points, int offset, boolean start) {
		for (InsertPoint point : points) {
			if (offset > point.offset || (start && offset == point.offset)) {
				return offset + point.cumulativeOffset;
			}
		}
		return offset;
	}
	
	private void cloneRelation(Section newSec, Relation oldRel, Map<Element,Element> map) {
		Relation newRel = new Relation(this, newSec, oldRel.getName());
		putMapping(map, oldRel, newRel);
		for (Tuple oldT : oldRel.getTuples()) {
			Tuple newT = new Tuple(this, newRel);
			putMapping(map, oldT, newT);
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

	@Param
	public Expression getPoints() {
		return points;
	}

	@Param
	public Expression getOffset() {
		return offset;
	}

	@Param
	public Expression getInsert() {
		return insert;
	}

	public void setPoints(Expression points) {
		this.points = points;
	}

	public void setOffset(Expression offset) {
		this.offset = offset;
	}

	public void setInsert(Expression insert) {
		this.insert = insert;
	}
}
