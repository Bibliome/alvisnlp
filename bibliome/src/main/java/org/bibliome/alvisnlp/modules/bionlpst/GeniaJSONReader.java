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


package org.bibliome.alvisnlp.modules.bionlpst;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.streams.SourceStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.creators.DocumentCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(beta=true)
public abstract class GeniaJSONReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
	private SourceStream source;
	private String annotationsLayerName = "annotations";
	private String instanceIdFeature = "instance-id";

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		JSONParser parser = new JSONParser();
		try {
			for (BufferedReader br : Iterators.loop(source.getBufferedReaders())) {
				try {
					JSONObject json = (JSONObject) parser.parse(br);
					Section sec = getSection(logger, corpus, json);
					Map<String, Element> eltMap = new HashMap<String, Element>();
					fillAnnotations(logger, sec, json, eltMap);
					fillInstances(logger, sec, json, eltMap);
					fillectionRelations(logger, sec, json, eltMap);
				}
				finally {
					br.close();
				}
			}
		}
		catch (IOException|ParseException e) {
			rethrow(e);
		}
	}
	
	private void fillectionRelations(Logger logger, Section sec, JSONObject json, Map<String, Element> eltMap) {
		JSONArray relanns = (JSONArray) json.get("relanns");
		if (relanns == null) {
			return;
		}
		for (Object o : relanns) {
			JSONObject ra = (JSONObject) o;
			String id = (String) ra.get("id");
			String type = (String) ra.get("type");
			String subject = (String) ra.get("subject");
			String object = (String) ra.get("object");
			Element subjectElt = getElement(logger, sec, eltMap, subject);
			if (subjectElt == null) {
				continue;
			}
			Element objectElt = getElement(logger, sec, eltMap, object);
			if (objectElt == null) {
				continue;
			}
			Relation rel = sec.ensureRelation(this, type);
			Tuple t = new Tuple(this, rel);
			t.addFeature("type", type);
			t.addFeature("id", id);
			t.setArgument("subject", subjectElt);
			t.setArgument("object", objectElt);
			eltMap.put(id, t);
		}
	}

	private static Element getElement(Logger logger, Section sec, Map<String, Element> eltMap, String id) {
		if (eltMap.containsKey(id)) {
			return eltMap.get(id);
		}
		logger.warning("could not find annotation '" + id + "' in " + sec.getDocument().getId());
		return null;
	}

	private void fillInstances(Logger logger, Section sec, JSONObject json, Map<String, Element> eltMap) {
		JSONArray insanns = (JSONArray) json.get("insanns");
		if (insanns == null) {
			return;
		}
		for (Object o : insanns) {
			JSONObject ia = (JSONObject) o;
			String id = (String) ia.get("id");
			String type = (String) ia.get("type");
			String object = (String) ia.get("object");
			if (!type.equals("instanceOf")) {
				logger.warning("instance annotation " + id + " has type '" + type + "', expected 'instanceOf' in " + sec.getDocument().getId());
				continue;
			}
			Element elt = getElement(logger, sec, eltMap, object);
			if (elt != null) {
				elt.addFeature(instanceIdFeature, id);
				eltMap.put(id, elt);
			}
		}
	}

	private void fillAnnotations(@SuppressWarnings("unused") Logger logger, Section sec, JSONObject json, Map<String,Element> eltMap) {
		JSONArray catanns = (JSONArray) json.get("catanns");
		if (catanns == null) {
			return;
		}
		Layer annotations = sec.ensureLayer(annotationsLayerName);
		for (Object o : catanns) {
			JSONObject ca = (JSONObject) o;
			String id = (String) ca.get("id");
			JSONObject span = (JSONObject) ca.get("span");
			int begin = (int) (long) span.get("begin");
			int end = (int) (long) span.get("end");
			String category = (String) ca.get("category");
			Layer layer = sec.ensureLayer(category);
			Annotation a = new Annotation(this, layer, begin, end);
			annotations.add(a);
			a.addFeature("id", id);
			a.addFeature("category", category);
			eltMap.put(id, a);
		}
	}

	private Section getSection(@SuppressWarnings("unused") Logger logger, Corpus corpus, JSONObject json) {
		String source_db = (String) json.get("source_db");
		String source_id = (String) json.get("source_id");
		int division_id = (int) (long) json.get("division_id");
		String section = (String) json.get("section");
		String docId = String.format("%s-%s-%02d-%s", source_db, source_id, division_id, section);
		Document doc = Document.getDocument(this, corpus, docId);
		if (!doc.hasFeature("source_db")) {
			doc.addFeature("source_db", source_db);
			doc.addFeature("source_id", source_id);
			doc.addFeature("section", section);
			doc.addFeature("division_id", Integer.toString(division_id));
		}
		String text = (String) json.get("text");
		return new Section(this, doc, section, text);
	}

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Param
	public SourceStream getSource() {
		return source;
	}

	@Param(nameType=NameType.LAYER)
	public String getAnnotationsLayerName() {
		return annotationsLayerName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getInstanceIdFeature() {
		return instanceIdFeature;
	}

	public void setSource(SourceStream source) {
		this.source = source;
	}

	public void setAnnotationsLayerName(String annotationsLayerName) {
		this.annotationsLayerName = annotationsLayerName;
	}

	public void setInstanceIdFeature(String instanceIdFeature) {
		this.instanceIdFeature = instanceIdFeature;
	}
}
