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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;

@Library("path")
public abstract class PathLibrary extends FunctionLibrary {
	private static final class PathNode {
		private final Element elt;
		private final PathNode prev;
		
		private PathNode(Element elt, PathNode prev) {
			this.elt = elt;
			this.prev = prev;
		}
		
		private PathNode(Element elt) {
			this(elt, null);
		}
	}
	
	private static final List<Element> shortestPath(EvaluationContext ctx, Evaluator vert, Element a, Element b) {
		PathNode root = new PathNode(a);
		Collection<PathNode> pathNodes = new ArrayList<PathNode>();
		pathNodes.add(root);
		Set<Element> seen = new HashSet<Element>();
		seen.add(a);
		while (true) {
			Collection<PathNode> nextPathNodes = new ArrayList<PathNode>();			
			for (PathNode n : pathNodes) {
				for (Element e : Iterators.loop(vert.evaluateElements(ctx, n.elt))) {
					if (e == b) {
						List<Element> result = new ArrayList<Element>();
						result.add(b);
						for (PathNode s = n; s != null; s = s.prev)
							result.add(s.elt);
						Collections.reverse(result);
						return result;
					}
					if (seen.contains(e))
						continue;
					seen.add(e);
					PathNode nn = new PathNode(e, n);
					nextPathNodes.add(nn);
				}
			}
			if (nextPathNodes.isEmpty())
				break;
			pathNodes = nextPathNodes;
		}
		return Collections.emptyList();
	}
	
	@Function
	public static final List<Element> to(EvaluationContext ctx, Element elt, Iterator<Element> to, Evaluator vert) {
		if (!to.hasNext())
			return Collections.emptyList();
		return shortestPath(ctx, vert, elt, to.next());
	}
	
	@Function
	public static final List<Element> between(EvaluationContext ctx, @SuppressWarnings("unused") Element elt, Iterator<Element> from, Iterator<Element> to, Evaluator vert) {
		if (!(from.hasNext() && to.hasNext()))
			return Collections.emptyList();
		return shortestPath(ctx, vert, from.next(), to.next());
	}
}
