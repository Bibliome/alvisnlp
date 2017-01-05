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


package org.bibliome.alvisnlp.modules.alvisre;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bibliome.alvisnlp.modules.SectionModule;

import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;

public class SectionsMerger {
	private final SectionModule<?> module;
	private final String separator;
	private Document document;
	private Iterator<Section> sectionIterator;
	private Section section;
	private final StringBuilder contents = new StringBuilder();
	private int offset;
	private Map<Document,SectionsUnmerger> unmergers = new HashMap<Document,SectionsUnmerger>();
	private SectionsUnmerger unmerger;
	
	public SectionsMerger(SectionModule<?> module, String separator) {
		super();
		this.module = module;
		this.separator = separator;
	}
	
	public Document getDocument() {
		return document;
	}

	public boolean setDocument(EvaluationContext evalCtx, Document document) {
		this.document = document;
		sectionIterator = module.sectionIterator(evalCtx, document);
		contents.setLength(0);
		unmerger = new SectionsUnmerger();
		unmergers.put(document, unmerger);
		return nextSection();
	}
	
	public Section getSection() {
		return section;
	}

	public boolean nextSection() {
		if (sectionIterator.hasNext()) {
			if (section != null) {
				contents.append(separator);
			}
			offset = contents.length();
			section = sectionIterator.next();
			contents.append(section.getContents());
			unmerger.setOffset(offset, section);
			return true;
		}
		section = null;
		return false;
	}

	public String getContents() {
		return contents.toString();
	}
	
	public int getOffset() {
		return offset;
	}
	
	public SectionModule<?> getModule() {
		return module;
	}

	public int correctOffset(int pos) {
		return pos + offset;
	}
	
	String addToken(Element elt) {
		return unmerger.addToken(elt);
	}
	
	String addRelation(Element elt) {
		return unmerger.addRelation(elt);
	}
	
	String getId(Element elt) {
		return unmerger.getId(elt);
	}
	
	public SectionsUnmerger getSectionsUnmerger(Document doc) {
		if (unmergers.containsKey(doc)) {
			return unmergers.get(doc);
		}
		throw new IllegalArgumentException("no info for document " + doc);
	}
}
