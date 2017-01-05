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


package alvisnlp.corpus.expressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

import org.bibliome.util.Pair;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.ElementVisitor;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.creators.DocumentCreator;
import alvisnlp.corpus.creators.RelationCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.module.ActionInterface;

public class EvaluationContext {
	private final Logger logger;
	private boolean allowSetFeature;
	private boolean allowSetArgument;
	private boolean allowDeleteElement;
	private boolean allowAddAnnotation;
	private boolean allowRemoveAnnotation;
	private DocumentCreator documentCreator;
	private SectionCreator sectionCreator;
	private AnnotationCreator annotationCreator;
	private RelationCreator relationCreator;
	private TupleCreator tupleCreator;
	private final Collection<Element> createdElements = new LinkedHashSet<Element>();
	private final Collection<Pair<Annotation,String>> addedAnnotations = new ArrayList<Pair<Annotation,String>>();
	private final Collection<Element> deletedElements = new HashSet<Element>();
	private final Collection<Pair<Annotation,String>> removedAnnotations = new ArrayList<Pair<Annotation,String>>();
	private final Collection<ArgumentTriplet> argumentsSet = new ArrayList<ArgumentTriplet>();
	private final Collection<FeatureTriplet> featuresSet = new ArrayList<FeatureTriplet>();
	private final Collection<Pair<Element,String>> removedFeatures = new ArrayList<Pair<Element,String>>();

	public EvaluationContext(Logger logger) {
		super();
		this.logger = logger;
	}
	
	public EvaluationContext(Logger logger, ActionInterface action) {
		this(logger);
		setAllowDeleteElement(action.getDeleteElements());
		setAllowSetArgument(action.getSetArguments());
		setAllowSetFeature(action.getSetFeatures());
		if (action.getCreateDocuments()) {
			setDocumentCreator(action);
		}
		if (action.getCreateSections()) {
			setSectionCreator(action);
		}
		if (action.getCreateAnnotations()) {
			setAnnotationCreator(action);
		}
		if (action.getCreateRelations()) {
			setRelationCreator(action);
		}
		if (action.getCreateTuples()) {
			setTupleCreator(action);
		}
		setAllowAddAnnotation(action.getAddToLayer());
		setAllowRemoveAnnotation(action.getRemoveFromLayer());
	}

	public Logger getLogger() {
		return logger;
	}

	public DocumentCreator getDocumentCreator() {
		if (documentCreator == null) {
			throw new RuntimeException("document creation is not allowed");
		}
		return documentCreator;
	}

	public SectionCreator getSectionCreator() {
		if (sectionCreator == null) {
			throw new RuntimeException("section creation is not allowed");
		}
		return sectionCreator;
	}

	public AnnotationCreator getAnnotationCreator() {
		if (annotationCreator == null) {
			throw new RuntimeException("annotation creation is not allowed");
		}
		return annotationCreator;
	}

	public RelationCreator getRelationCreator() {
		if (relationCreator == null) {
			throw new RuntimeException("relation creation is not allowed");
		}
		return relationCreator;
	}

	public TupleCreator getTupleCreator() {
		if (tupleCreator == null) {
			throw new RuntimeException("tuple creation is not allowed");
		}
		return tupleCreator;
	}

	public boolean isAllowSetFeature() {
		return allowSetFeature;
	}

	public boolean isAllowSetArgument() {
		return allowSetArgument;
	}

	public boolean isAllowDeleteElement() {
		return allowDeleteElement;
	}

	public boolean isAllowAddAnnotation() {
		return allowAddAnnotation;
	}

	public boolean isAllowRemoveAnnotation() {
		return allowRemoveAnnotation;
	}

	public void setAllowAddAnnotation(boolean allowAddAnnotation) {
		this.allowAddAnnotation = allowAddAnnotation;
	}

	public void setAllowRemoveAnnotation(boolean allowRemoveAnnotation) {
		this.allowRemoveAnnotation = allowRemoveAnnotation;
	}

	public void setAllowSetFeature(boolean allowSetFeature) {
		this.allowSetFeature = allowSetFeature;
	}

	public void setAllowSetArgument(boolean allowSetArgument) {
		this.allowSetArgument = allowSetArgument;
	}

	public void setAllowDeleteElement(boolean allowDeleteElement) {
		this.allowDeleteElement = allowDeleteElement;
	}

	public void setDocumentCreator(DocumentCreator documentCreator) {
		this.documentCreator = documentCreator;
	}

	public void setSectionCreator(SectionCreator sectionCreator) {
		this.sectionCreator = sectionCreator;
	}

	public void setAnnotationCreator(AnnotationCreator annotationCreator) {
		this.annotationCreator = annotationCreator;
	}

	public void setRelationCreator(RelationCreator relationCreator) {
		this.relationCreator = relationCreator;
	}

	public void setTupleCreator(TupleCreator tupleCreator) {
		this.tupleCreator = tupleCreator;
	}

	public void registerCreateElement(Element e) {
		createdElements.add(e);
	}
	
	public void registerAddAnnotation(Annotation a, String ln) {
		if (!allowAddAnnotation) {
			throw new RuntimeException("adding annotations to layers is not allowed");
		}
		addedAnnotations.add(new Pair<Annotation,String>(a, ln));
	}
	
	public void registerDeleteElement(Element e) {
		if (!allowDeleteElement) {
			throw new RuntimeException("element deletion is not allowed");
		}
		deletedElements.add(e);
	}
	
	public void registerRemoveAnnotation(Annotation a, String ln) {
		if (!allowRemoveAnnotation) {
			throw new RuntimeException("removing annotations from layers is not allowed");
		}
		removedAnnotations.add(new Pair<Annotation,String>(a, ln));
	}
	
	public void registerSetArgument(Tuple t, String role, Element arg) {
		if (!allowSetArgument) {
			throw new RuntimeException("setting tuple arguments is not allowed");
		}
		ArgumentTriplet triplet = new ArgumentTriplet(t, role, arg);
		argumentsSet.add(triplet);
	}
	
	public void registerSetFeature(Element elt, String key, String value) {
		if (!allowSetFeature) {
			throw new RuntimeException("setting element features is not allowed");
		}
		FeatureTriplet triplet = new FeatureTriplet(elt, key, value);
		featuresSet.add(triplet);
	}
	
	public void registerRemoveFeature(Element elt, String key) {
		if (!allowSetFeature) {
			throw new RuntimeException("setting element features is not allowed");
		}
		Pair<Element,String> pair = new Pair<Element,String>(elt, key);
		removedFeatures.add(pair);
	}

	private static final class ArgumentTriplet {
		private final Tuple t;
		private final String role;
		private final Element arg;
		
		private ArgumentTriplet(Tuple t, String role, Element arg) {
			super();
			this.t = t;
			this.role = role;
			this.arg = arg;
		}
	}
	
	private static final class FeatureTriplet {
		private final Element elt;
		private final String key;
		private final String value;
		
		private FeatureTriplet(Element elt, String key, String value) {
			super();
			this.elt = elt;
			this.key = key;
			this.value = value;
		}
	}
	
	private void logInfo(String msg) {
		if (logger != null) {
			logger.info(msg);
		}
	}

	public void commit() {
		boolean collect = (!removedAnnotations.isEmpty()) || (!deletedElements.isEmpty());
		
		int n = 0;
		for (Element e : createdElements)
			if (e.accept(commitCreate, this))
				n++;
		createdElements.clear();
		if (n > 0) {
			logInfo("created elements: " + n);
		}

		n = 0;
		for (Pair<Annotation,String> p : addedAnnotations) {
			Annotation a = p.first;
			if (deletedElements.contains(a))
				continue;
			Section sec = a.getSection();
			Layer layer = sec.ensureLayer(p.second);
			if (layer.add(a))
				n++;
		}
		addedAnnotations.clear();
		if (n > 0) {
			logInfo("annotations added: " + n);
		}
		
		n = 0;
		for (Pair<Annotation,String> p : removedAnnotations) {
			Annotation a = p.first;
			if (deletedElements.contains(a))
				continue;
			Section sec = a.getSection();
			if (deletedElements.contains(sec))
				continue;
			Layer layer = sec.ensureLayer(p.second);
			if (layer.remove(a))
				n++;
		}
		removedAnnotations.clear();
		if (n > 0) {
			logInfo("annotations removed: " + n);
		}
		
		n = 0;
		for (ArgumentTriplet triplet : argumentsSet) {
			if (deletedElements.contains(triplet.t))
				continue;
			if (triplet.arg == null) {
				triplet.t.setArgument(triplet.role, null);
			}
			else {
				triplet.t.setArgument(triplet.role, triplet.arg.getOriginal());
			}
		}
		n += argumentsSet.size();
		argumentsSet.clear();
		if (n > 0) {
			logInfo("tuple arguments set: " + n);
		}
		
		n = 0;
		for (FeatureTriplet triplet : featuresSet) {
			if (deletedElements.contains(triplet.elt))
				continue;
			triplet.elt.addFeature(triplet.key, triplet.value);
		}
		n += featuresSet.size();
		featuresSet.clear();
		if (n > 0) {
			logInfo("element features set: " + n);
		}
		
		n = 0;
		for (Pair<Element,String> pair : removedFeatures) {
			if (deletedElements.contains(pair.first)) {
				continue;
			}
			pair.first.removeFeatures(pair.second);
		}
		n += removedFeatures.size();
		if (n > 0) {
			logInfo("element features removed: " + n);
		}

		n = 0;
		for (Element e : deletedElements) {
			e.accept(commitDelete, this);
		}
		n += deletedElements.size();
		deletedElements.clear();
		if (n > 0) {
			logInfo("deleted elements: " + n);
		}
		
		if (collect)
			System.gc();
	}

	private static final ElementVisitor<Boolean,EvaluationContext> commitCreate = new ElementVisitor<Boolean,EvaluationContext>() {
		@Override
		public Boolean visit(Annotation a, EvaluationContext param) {
			return true;
		}

		@Override
		public Boolean visit(Corpus corpus, EvaluationContext param) {
			throw new RuntimeException();
		}

		@Override
		public Boolean visit(Document doc, EvaluationContext param) {
			Corpus corpus = doc.getCorpus();
			if (!corpus.hasDocument(doc.getId())) {
				corpus.addDocument(doc);
				return true;
			}
			return false;
		}

		@Override
		public Boolean visit(Relation rel, EvaluationContext param) {
			Section sec = rel.getSection();
			if (!param.deletedElements.contains(sec))
				if (!sec.hasRelation(rel.getName())) {
					sec.addRelation(rel);
					return true;
				}
			return false;
		}

		@Override
		public Boolean visit(Section sec, EvaluationContext param) {
			Document doc = sec.getDocument();
			if (!param.deletedElements.contains(doc)) {
				doc.addSection(sec);
				return true;
			}
			return false;
		}

		@Override
		public Boolean visit(Tuple t, EvaluationContext param) {
			Relation rel = t.getRelation();
			if (!param.deletedElements.contains(rel)) {
				rel.addTuple(t);
				return true;
			}
			return false;
		}

		@Override
		public Boolean visit(Element e, EvaluationContext param) {
			throw new RuntimeException();
		}
	};
	
	private static final ElementVisitor<Void,EvaluationContext> commitDelete = new ElementVisitor<Void,EvaluationContext>() {
		@Override
		public Void visit(Annotation a, EvaluationContext param) {
			Section sec = a.getSection();
			if (!param.deletedElements.contains(sec))
				for (Layer layer : sec.getAllLayers()) {
					layer.remove(a);
				}
			return null;
		}

		@Override
		public Void visit(Corpus corpus, EvaluationContext param) {
			throw new RuntimeException();
		}

		@Override
		public Void visit(Document doc, EvaluationContext param) {
			Corpus corpus = doc.getCorpus();
			corpus.removeDocument(doc);
			return null;
		}

		@Override
		public Void visit(Relation rel, EvaluationContext param) {
			Section sec = rel.getSection();
			if (!param.deletedElements.contains(sec))
				sec.removeRelation(rel);
			return null;
		}

		@Override
		public Void visit(Section sec, EvaluationContext param) {
			Document doc = sec.getDocument();
			if (!param.deletedElements.contains(doc))
				doc.removeSection(sec);
			return null;
		}

		@Override
		public Void visit(Tuple t, EvaluationContext param) {
			Relation rel = t.getRelation();
			if (!param.deletedElements.contains(rel)) {
				rel.removeTuple(t);
			}
			return null;
		}

		@Override
		public Void visit(Element e, EvaluationContext param) {
			throw new RuntimeException();
		}
	};
}
