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


package org.bibliome.alvisnlp.modules.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibliome.util.Strings;
import org.bibliome.util.fragments.FragmentTag;
import org.bibliome.util.fragments.FragmentTagIterator;
import org.bibliome.util.fragments.FragmentTagType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import alvisnlp.corpus.Annotation;

class HTMLBuilderFragmentTagIterator implements FragmentTagIterator<String,Annotation> {
	private final QuickHTML owner;
	private Document document;
	private Element contentsDiv;
	private final Deque<StackElement> stack = new LinkedList<StackElement>();
	private final Set<String> classes;
	private Element previousAnnotation = null;

	HTMLBuilderFragmentTagIterator(QuickHTML owner, Set<String> classes) {
		super();
		this.owner = owner;
		this.classes = classes;
	}
	
	void init(Document document, Element contentsDiv) {
		this.document = document;
		this.contentsDiv = contentsDiv;
		this.stack.clear();
	}

	private void append(Node node) {
		StackElement se = stack.peek();
		Element parent = se == null ? contentsDiv : se.element;
		parent.appendChild(node);
	}
	
	private String getAnnotationClass(Annotation a) {
		String result = owner.getAnnotationClass(a);
		classes.add(result);
		return result;
	}
	
	private Element push(Annotation a) {
		Element elt = document.createElement(owner.getAnnotationTag(a));
		elt.setAttribute("alvisnlp-id", a.getStringId());
		QuickHTML.addClass(elt, "alvisnlp-fragment");
		QuickHTML.addClass(elt, getAnnotationClass(a));
		append(elt);
		StackElement se = new StackElement(elt, a);
		stack.push(se);
		return elt;
	}

	@Override
	public void handleTag(String param, FragmentTag<Annotation> tag) {
		FragmentTagType tagType = tag.getTagType();
		switch (tagType) {
			case EMPTY: break; // ignore empty annotations
			case OPEN: {
				Annotation a = tag.getFragment();
				Element elt = push(a);
				if (previousAnnotation != null) {
					previousAnnotation.setAttribute("alvisnlp-next", elt.getAttribute("alvisnlp-id"));
					elt.setAttribute("alvisnlp-prev", previousAnnotation.getAttribute("alvisnlp-id"));
				}
				QuickHTML.addClass(elt, "alvisnlp-first-fragment");
				Collection<String> keys = owner.getFeatureKeys(a);
				elt.setAttribute("alvisnlp-feature-keys", Strings.join(keys, ' '));
				for (String key : keys) {
					elt.setAttribute("alvisnlp-feature-value-" + key, a.getLastFeature(key));
				}
				previousAnnotation = elt;
				break;
			}
			case CLOSE: {
				Annotation a = tag.getFragment();
				List<Annotation> split = new ArrayList<Annotation>();
				while (true) {
					StackElement se = stack.pop();
					if (a == se.annotation) {
						QuickHTML.addClass(se.element, "alvisnlp-last-fragment");
						Collections.reverse(split);
						for (Annotation sa : split) {
							push(sa);
						}
						break;
					}
					else {
						split.add(se.annotation);
					}
				}
				break;
			}
		}
	}

	private void addText(String s, int from, int to) {
		Text txt = document.createTextNode(s.substring(from, to));
		append(txt);
	}
	
	@Override
	public void handleGap(String param, int from, int to) {
		addText(param, from, to);
	}

	@Override
	public void handleHead(String param, int to) {
		addText(param, 0, to);
	}

	@Override
	public void handleTail(String param, int from) {
		addText(param, from, param.length());
	}

	private static class StackElement {
		private final Element element;
		private final Annotation annotation;
		
		private StackElement(Element element, Annotation annotation) {
			super();
			this.element = element;
			this.annotation = annotation;
		}
	}
}
