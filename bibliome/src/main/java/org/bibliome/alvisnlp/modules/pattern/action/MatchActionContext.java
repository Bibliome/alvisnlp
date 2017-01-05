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


package org.bibliome.alvisnlp.modules.pattern.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.pattern.EvaluatorFilterProxy;
import org.bibliome.alvisnlp.modules.pattern.PatternMatcher;
import org.bibliome.util.pattern.CapturingGroup;
import org.bibliome.util.pattern.SequenceMatcher;
import org.bibliome.util.pattern.SequencePattern;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.corpus.expressions.VariableLibrary;
import alvisnlp.corpus.expressions.VariableLibrary.Variable;

public class MatchActionContext {
	private final Map<String,Integer> groupNameMap = new HashMap<String,Integer>();
	private final String matchedLayerName;
	private final Collection<Element> toAdd = new ArrayList<Element>();
	private final Collection<Element> toRemove = new ArrayList<Element>();
	private final PatternMatcher owner;
	private final EvaluationContext evalCtx;
	private final VariableLibrary groupLib;
	private Logger logger;

	public MatchActionContext(PatternMatcher owner, SequencePattern<Element,EvaluationContext,EvaluatorFilterProxy> pattern, String matchedLayerName) {
		this.matchedLayerName = matchedLayerName;
		this.owner = owner;
		List<CapturingGroup<Element,EvaluationContext,EvaluatorFilterProxy>> capturingGroups = pattern.getCapturingGroups();
		int n = 0;
		groupLib = new VariableLibrary("group");
		for (CapturingGroup<Element,EvaluationContext,EvaluatorFilterProxy> group : capturingGroups) {
			String name = group.getName();
			if (name == null)
				continue;
			groupLib.newVariable(name);
			if (groupNameMap.containsKey(name)) {
				logger.warning("duplicate group name: " + name);
				continue;
			}
			groupNameMap.put(name, ++n);
		}
		groupLib.newVariable("match");
		groupNameMap.put("match", 0);
		groupNameMap.remove(null);
		evalCtx = new EvaluationContext(logger);
	}

	public LibraryResolver getGroupLibraryResolver(LibraryResolver parent) throws ResolverException {
		return groupLib.newLibraryResolver(parent);
	}

	public String getMatchedLayerName() {
		return matchedLayerName;
	}
	
	public void addAnnotation(Element a) {
		toAdd.add(a);
	}
		
	public void removeAnnotation(Element a) {
		toRemove.add(a);
	}
		
	public void commit(Section section) {
		Layer matchedLayer = section.getLayer(matchedLayerName);
		matchedLayer.removeAll(toRemove);
		for (Element elt : toAdd) {
			Annotation a = DownCastElement.toAnnotation(elt);
			if (a != null)
				matchedLayer.add(a);
		}
		toRemove.clear();
		toAdd.clear();
	}
	
	public Integer getGroupIndex(String name) {
		return groupNameMap.get(name);
	}

	public Logger getLogger() {
		return logger;
	}
	
	public PatternMatcher getOwner() {
		return owner;
	}

	public EvaluationContext getEvaluationContext() {
		return evalCtx;
	}

	public void updateGroupContents(SequenceMatcher<Element> matcher) {
		for (Map.Entry<String,Integer> e : groupNameMap.entrySet()) {
			String groupName = e.getKey();
			int groupIndex = e.getValue();
			List<Element> value;
			if ("match".equals(groupName)) {
				value = matcher.getMatchedElements();
			}
			else {
				value = matcher.getMatchedElements(groupIndex);
			}
			//evalCtx.setReference(groupName, value);
			Variable var = groupLib.getVariable(groupName);
			var.set(value);
		}
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
