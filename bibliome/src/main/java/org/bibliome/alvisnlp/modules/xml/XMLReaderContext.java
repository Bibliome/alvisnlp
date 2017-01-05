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


package org.bibliome.alvisnlp.modules.xml;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;

class XMLReaderContext {
	private final XMLReader2 owner;
	private final Corpus corpus;
	private final Deque<Element> elementStack = new ArrayDeque<Element>();
	private final Deque<Document> documentStack = new ArrayDeque<Document>();
	private final Deque<Section> sectionStack = new ArrayDeque<Section>();
	private final Deque<Annotation> annotationStack = new ArrayDeque<Annotation>();
	private final Deque<Relation> relationStack = new ArrayDeque<Relation>();
	private final Deque<Tuple> tupleStack = new ArrayDeque<Tuple>();
	private final Deque<Map<String,Annotation>> refScope = new ArrayDeque<Map<String,Annotation>>();
	
	XMLReaderContext(XMLReader2 owner, Corpus corpus) {
		super();
		this.owner = owner;
		this.corpus = corpus;
		elementStack.addLast(corpus);
	}
	
	void startDocument(String id) {
		Document doc = Document.getDocument(owner, corpus, id);
		documentStack.addLast(doc);
		elementStack.addLast(doc);
	}
	
	void endDocument() {
		documentStack.removeLast();
		elementStack.removeLast();
	}
	
	void startSection(String name, String contents) {
		Document doc = documentStack.getLast();
		Section sec = new Section(owner, doc, name, contents);
		sectionStack.addLast(sec);
		elementStack.addLast(sec);
	}
	
	void endSection() {
		sectionStack.removeLast();
		elementStack.removeLast();
	}
	
	void startRelation(String name) {
		Section sec = sectionStack.getLast();
		Relation rel = new Relation(owner, sec, name);
		relationStack.addLast(rel);
		elementStack.addLast(rel);
	}
	
	void endRelation() {
		relationStack.removeLast();
		elementStack.removeLast();
	}
	
	void startTuple() {
		Relation rel = relationStack.getLast();
		Tuple t = new Tuple(owner, rel);
		tupleStack.addLast(t);
		elementStack.addLast(t);
	}
	
	void endTuple() {
		tupleStack.removeLast();
		elementStack.removeLast();
	}
	
	void setArgument(String role, String aRef) {
		Tuple t = tupleStack.getLast();
		Map<String,Annotation> aMap = refScope.getLast();
		if (!aMap.containsKey(aRef))
			return;
		Annotation a = aMap.get(aRef);
		t.setArgument(role, a);
	}
	
	void startAnnotation(int start, int end, String[] layers, String ref) {
		Section sec = sectionStack.getLast();
		Annotation a = new Annotation(owner, sec, start, end);
		for (String ln : layers) {
			Layer layer = sec.ensureLayer(ln);
			layer.add(a);
		}
		if (ref != null) {
			Map<String,Annotation> aMap = refScope.getLast();
			aMap.put(ref, a);
		}
		annotationStack.addLast(a);
		elementStack.addLast(a);
	}
	
	void endAnnotation() {
		annotationStack.removeLast();
		elementStack.removeLast();
	}
	
	void setFeature(String name, String value) {
		Element elt = elementStack.getLast();
		elt.addFeature(name, value);
	}
	
	void startRefScope() {
		Map<String,Annotation> aMap = new HashMap<String,Annotation>();
		refScope.addLast(aMap);
	}
	
	void endRefScope() {
		refScope.removeLast();
	}
}