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


package org.bibliome.alvisnlp.library;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bibliome.util.Iterators;
import org.bibliome.util.defaultmap.DefaultMap;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.AbstractIteratorEvaluator;
import alvisnlp.corpus.expressions.AbstractListEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;

@Library("cpath")
public abstract class PathLibrary2 extends FunctionLibrary {
	private static final class PathNode {
		private final PathNode prev;
		private final Element element;

		private PathNode(PathNode prev, Element element) {
			super();
			this.prev = prev;
			this.element = element;
		}
		
		private List<Element> getList() {
			List<Element> result = new ArrayList<Element>();
			for (PathNode n = this; n != null; n = n.prev)
				result.add(n.element);
			Collections.reverse(result);
			return result;
		}
	}
	
	private static final class FromPathCache extends DefaultMap<Element,List<Element>> {
		private FromPathCache() {
			super(false, new HashMap<Element,List<Element>>());
		}

		@Override
		protected List<Element> defaultValue(Element key) {
			return Collections.emptyList();
		}
	}
	
	private static final class PathCache extends DefaultMap<Element,FromPathCache> {
		private PathCache() {
			super(true, new HashMap<Element,FromPathCache>());
		}

		@Override
		protected FromPathCache defaultValue(Element key) {
			return new FromPathCache();
		}
	}
	
	private static Collection<List<Element>> subLists(List<Element> list) {
		Collection<List<Element>> result = new ArrayList<List<Element>>();
		final int len = list.size();
		for (int i = 0; i < len - 1; ++i)
			result.add(list.subList(i, len));
		return result;
	}
	
	private static Collection<PathNode> getPathTree(EvaluationContext ctx, Evaluator arcs, Element from) {
		Map<Element,PathNode> result = new HashMap<Element,PathNode>();
		PathNode root = new PathNode(null, from);
		Collection<PathNode> pathNodes = new ArrayList<PathNode>();
		pathNodes.add(root);
		result.put(from, root);
		while (!pathNodes.isEmpty()) {
			Collection<PathNode> nextPathNodes = new ArrayList<PathNode>();
			for (PathNode n : pathNodes) {
				for (Element e : Iterators.loop(arcs.evaluateElements(ctx, n.element))) {
					if (result.containsKey(e))
						continue;
					PathNode nn = new PathNode(n, e);
					result.put(e, nn);
					nextPathNodes.add(nn);
				}
			}
			pathNodes = nextPathNodes;
		}
		return result.values();
	}

	private static List<Element> getShortestPath(EvaluationContext ctx, Evaluator arcs, PathCache cache, Element from, Element to) {
		List<Element> result = cache.safeGet(from).safeGet(to);
		if (!result.isEmpty())
			return result;
		Collection<PathNode> shortestPaths = getPathTree(ctx, arcs, from);
		for (PathNode n : shortestPaths) {
			List<Element> path = n.getList();
			for (List<Element> subPath : subLists(path)) {
				Element a = subPath.get(0);
				Element b = subPath.get(subPath.size() - 1);
				FromPathCache fromCache = cache.safeGet(a);
				if (!fromCache.containsKey(b))
					fromCache.put(b, subPath);
			}
		}
		return cache.safeGet(from).safeGet(to);
	}
	
	protected static abstract class AbstractPathEvaluator extends AbstractListEvaluator {
		private final PathCache cache = new PathCache();
		protected final Evaluator cacheExpression;
		protected final Evaluator arcs;
		private List<Element> cacheElements = Collections.emptyList();
		private EvaluationContext cacheContext;

		protected AbstractPathEvaluator(Evaluator cacheExpression, Evaluator arcs) {
			super();
			this.cacheExpression = cacheExpression;
			this.arcs = arcs;
		}

		private boolean isCacheObsolete(EvaluationContext ctx, Element elt) {
			if (ctx != cacheContext)
				return true;
			if (cacheExpression == null)
				return false;
			List<Element> elements = cacheExpression.evaluateList(ctx, elt);
			if (elements.size() != cacheElements.size())
				return true;
			for (int i = 0; i < elements.size(); ++i)
				if (elements.get(i) != cacheElements.get(i)) {
					cacheElements = elements;
					return true;
				}
			return false;
		}

		@Override
		public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
			if (isCacheObsolete(ctx, elt)) {
				cache.clear();
				cacheContext = ctx;
			}
			Element from = getFrom(ctx, elt);
			if (from == null)
				return Collections.emptyList();
			Element to = getTo(ctx, elt);
			if (to == null)
				return Collections.emptyList();
			return getShortestPath(ctx, arcs, cache, from, to);
		}
		
		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			cacheExpression.collectUsedNames(nameUsage, defaultType);
			arcs.collectUsedNames(nameUsage, defaultType);
		}

		protected abstract Element getFrom(EvaluationContext ctx, Element elt);
		
		protected abstract Element getTo(EvaluationContext ctx, Element elt);
		
		protected static Element evaluateVertex(Evaluator expr, EvaluationContext ctx, Element elt) {
			Iterator<Element> result = expr.evaluateElements(ctx, elt);
			if (result.hasNext())
				return result.next();
			return null;
		}
	}
	
	private static final class BetweenPathEvaluator extends AbstractPathEvaluator {
		private final Evaluator from;
		private final Evaluator to;
		
		private BetweenPathEvaluator(Evaluator cacheExpression, Evaluator arcs, Evaluator from, Evaluator to) {
			super(cacheExpression, arcs);
			this.from = from;
			this.to = to;
		}

		@Override
		protected Element getFrom(EvaluationContext ctx, Element elt) {
			return evaluateVertex(from, ctx, elt);
		}

		@Override
		protected Element getTo(EvaluationContext ctx, Element elt) {
			return evaluateVertex(to, ctx, elt);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			from.collectUsedNames(nameUsage, defaultType);
			to.collectUsedNames(nameUsage, defaultType);
		}
	}

	private static final class ToPathEvaluator extends AbstractPathEvaluator {
		private final Evaluator to;

		private ToPathEvaluator(Evaluator cacheExpression, Evaluator arcs, Evaluator to) {
			super(cacheExpression, arcs);
			this.to = to;
		}

		@Override
		protected Element getFrom(EvaluationContext ctx, Element elt) {
			return elt;
		}

		@Override
		protected Element getTo(EvaluationContext ctx, Element elt) {
			return evaluateVertex(to, ctx, elt);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			to.collectUsedNames(nameUsage, defaultType);
		}
	}

	private static final class FromPathEvaluator extends AbstractPathEvaluator {
		private final Evaluator from;

		private FromPathEvaluator(Evaluator cacheExpression, Evaluator arcs, Evaluator from) {
			super(cacheExpression, arcs);
			this.from = from;
		}

		@Override
		protected Element getFrom(EvaluationContext ctx, Element elt) {
			return evaluateVertex(from, ctx, elt);
		}

		@Override
		protected Element getTo(EvaluationContext ctx, Element elt) {
			return elt;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			from.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Function
	public static final Evaluator to(Evaluator arcs, Evaluator to) {
		return new ToPathEvaluator(null, arcs, to);
	}
	
	@Function
	public static final Evaluator to(Evaluator arcs, Evaluator to, Evaluator cache) {
		return new ToPathEvaluator(cache, arcs, to);
	}
	
	@Function
	public static final Evaluator from(Evaluator arcs, Evaluator from) {
		return new FromPathEvaluator(null, arcs, from);
	}
	
	@Function
	public static final Evaluator from(Evaluator arcs, Evaluator from, Evaluator cache) {
		return new FromPathEvaluator(cache, arcs, from);
	}
	
	@Function
	public static final Evaluator between(Evaluator arcs, Evaluator from, Evaluator to) {
		return new BetweenPathEvaluator(null, arcs, from, to);
	}

	@Function
	public static final Evaluator between(Evaluator arcs, Evaluator from, Evaluator to, Evaluator cache) {
		return new BetweenPathEvaluator(cache, arcs, from, to);
	}
	
	@Function
	public static final Evaluator connected(Evaluator arcs) {
		return new ConnectedEvaluator(arcs);
	}
	
	private static final class ConnectedEvaluator extends AbstractIteratorEvaluator {
		private final Evaluator arcs;

		private ConnectedEvaluator(Evaluator arcs) {
			super();
			this.arcs = arcs;
		}

		@Override
		public Iterator<Element> evaluateElements(EvaluationContext ctx, Element elt) {
			Collection<Element> result = new LinkedHashSet<Element>();
			Deque<Element> queue = new LinkedList<Element>();
			queue.addLast(elt);
//			System.err.println("elt = " + elt);
//			System.err.println("arc = " + arc);
			while (!queue.isEmpty()) {
				Element e = queue.removeFirst();
				result.add(e);
//				System.err.println("e = " + e);
				for (Element n : Iterators.loop(arcs.evaluateElements(ctx, e))) {
//					System.err.println("n = " + n);
					if (!result.contains(n)) {
						queue.addLast(n);
					}
				}
			}
			return result.iterator();
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			arcs.collectUsedNames(nameUsage, defaultType);
		}
	}
}
