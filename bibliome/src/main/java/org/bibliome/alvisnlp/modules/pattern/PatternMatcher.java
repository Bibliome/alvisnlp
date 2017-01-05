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


package org.bibliome.alvisnlp.modules.pattern;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.pattern.PatternMatcher.PatternMatcherResolvedObjects;
import org.bibliome.alvisnlp.modules.pattern.action.MatchAction;
import org.bibliome.alvisnlp.modules.pattern.action.MatchActionContext;
import org.bibliome.util.Iterators;
import org.bibliome.util.Timer;
import org.bibliome.util.pattern.SequenceMatcher;
import org.bibliome.util.pattern.SequencePattern;

import alvisnlp.corpus.AnnotationComparator;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule
public abstract class PatternMatcher extends SectionModule<PatternMatcherResolvedObjects> implements AnnotationCreator, TupleCreator {
	private String layerName = DefaultNames.getWordLayer();
	private ElementPattern pattern;
	private MatchAction[] actions;
	private OverlappingBehaviour overlappingBehaviour = OverlappingBehaviour.REMOVE_OVERLAPS;
	private AnnotationComparator annotationComparator = AnnotationComparator.byLength;

	static class PatternMatcherResolvedObjects extends SectionResolvedObjects {
		private final SequencePattern<Element,EvaluationContext,EvaluatorFilterProxy> pattern;
		private final MatchActionContext matchCtx;
		private final MatchAction[] actions;

		private PatternMatcherResolvedObjects(ProcessingContext<Corpus> ctx, PatternMatcher module) throws ResolverException {
			super(ctx, module);
			pattern = module.pattern.resolveExpressions(rootResolver);
			matchCtx = new MatchActionContext(module, pattern, module.layerName);
			LibraryResolver actionResolver = matchCtx.getGroupLibraryResolver(module.getLibraryResolver(ctx));
			actions = actionResolver.resolveArray(module.actions, MatchAction.class);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesArray(actions, defaultType);
			for (EvaluatorFilterProxy filter : pattern.getFilters()) {
				filter.collectUsedNames(nameUsage, defaultType);
			}
		}
	}
	
	@Override
	protected PatternMatcherResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new PatternMatcherResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		PatternMatcherResolvedObjects resObj = getResolvedObjects();
		Logger logger = getLogger(ctx);
		resObj.matchCtx.setLogger(logger);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		int nMatches = 0;
		Timer<TimerCategory> matchTimer = getTimer(ctx, "match", TimerCategory.MODULE, false);
		Timer<TimerCategory> commitTimer = getTimer(ctx, "commit", TimerCategory.MODULE, false);
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			Layer layer = sec.getLayer(layerName);
			for (List<Element> sequence : getSequences(ctx, layer)) {
				matchTimer.start();
				SequenceMatcher<Element> matcher = resObj.pattern.getMatcher(sequence, evalCtx);
				while (matcher.next()) {
					nMatches++;
					resObj.matchCtx.updateGroupContents(matcher);
					for (MatchAction action : resObj.actions)
						action.process(resObj.matchCtx, sec, matcher);
				}
				matchTimer.stop();
				commitTimer.start();
				resObj.matchCtx.commit(sec);
				commitTimer.stop();
			}
		}
		if (nMatches == 0) {
			logger.warning("found no matches");
		}
		else {
			logger.info("found " + nMatches + " matches");
		}
	}
	
	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { layerName };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	private Collection<List<Element>> getSequences(ProcessingContext<Corpus> ctx, Layer layer) {
		if (layer.hasOverlaps())
			return overlappingBehaviour.getSequences(getLogger(ctx), layer, annotationComparator);
		return Collections.singleton(layer.asElementList());
	}
	
	@Param(nameType=NameType.LAYER, defaultDoc="Layer where to find tokens.")
	public String getLayerName() {
		return layerName;
	}

	@Param(defaultDoc="Annotation pattern.")
	public ElementPattern getPattern() {
		return pattern;
	}

	@Param(defaultDoc="Actions.")
	public MatchAction[] getActions() {
		return actions;
	}

	@Param
	public OverlappingBehaviour getOverlappingBehaviour() {
		return overlappingBehaviour;
	}

	@Param
	public AnnotationComparator getAnnotationComparator() {
		return annotationComparator;
	}

	public void setOverlappingBehaviour(OverlappingBehaviour overlappingBehaviour) {
		this.overlappingBehaviour = overlappingBehaviour;
	}

	public void setAnnotationComparator(AnnotationComparator annotationComparator) {
		this.annotationComparator = annotationComparator;
	}

	public void setActions(MatchAction[] actions) {
		this.actions = actions;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public void setPattern(ElementPattern pattern) {
		this.pattern = pattern;
	}
}
