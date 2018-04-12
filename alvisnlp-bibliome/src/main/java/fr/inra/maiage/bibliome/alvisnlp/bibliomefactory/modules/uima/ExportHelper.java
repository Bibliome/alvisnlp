package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.FSCollectionFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AnnotationProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.ArgumentProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.DocumentProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.FeatureProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.LayerProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.RelationProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.TupleProxy;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.StringCat;

public class ExportHelper {
	private final XMIExport module;
	private final Logger logger;
	private final EvaluationContext evalCtx;
	private final JCas jcas;
	private final Map<Element,TOP> argumentMap = new HashMap<Element,TOP>();
	
	ExportHelper(XMIExport module, Logger logger, EvaluationContext evalCtx) throws UIMAException {
		super();
		this.module = module;
		this.logger = logger;
		this.evalCtx = evalCtx;
		this.jcas = JCasFactory.createJCas();
	}
	
	void reset() {
		jcas.reset();
		argumentMap.clear();
	}
	
	CAS getCas() {
		return jcas.getCas();
	}

	DocumentProxy convertDocument(Document doc) {
		reset();
		jcas.setDocumentText(convertContents(doc));
		DocumentProxy result = new DocumentProxy(jcas);
		result.setId(doc.getId());
		result.setFeatures(convertFeatures(doc));
		result.setSections(convertSections(doc));
		return result;
	}

	private String convertContents(Document doc) {
		StringCat strcat = new StringCat();
		for (Section sec : Iterators.loop(module.sectionIterator(evalCtx, doc))) {
			strcat.append(sec.getContents());
		}
		return strcat.toString();
	}

	private FSArray convertFeatures(Element elt) {
		List<FeatureProxy> result = new ArrayList<FeatureProxy>();
		for (String key : elt.getFeatureKeys()) {
			if (elt.isStaticFeatureKey(key)) {
				continue;
			}
			for (String value : elt.getFeature(key)) {
				FeatureProxy f = new FeatureProxy(jcas);
				f.setKey(key);
				f.setValue(value);
				result.add(f);
			}
		}
		return FSCollectionFactory.createFSArray(jcas, result);
	}
	
	private FSArray convertSections(Document doc) {
		List<SectionProxy> result = new ArrayList<SectionProxy>();
		int offset = 0;
		for (Section sec : Iterators.loop(module.sectionIterator(evalCtx, doc))) {
			result.add(convertSection(sec, offset));
			offset += sec.getContents().length();
		}
		return FSCollectionFactory.createFSArray(jcas, result);
	}

	private SectionProxy convertSection(Section sec, int offset) {
		SectionProxy result = new SectionProxy(jcas, offset, offset + sec.getContents().length());
		result.setName(sec.getName());
		result.setFeatures(convertFeatures(sec));
		Map<Annotation,AnnotationProxy> annotationMap = new HashMap<Annotation,AnnotationProxy>();
		result.setLayers(convertLayers(annotationMap, sec, offset));
		argumentMap.putAll(annotationMap);
		result.setRelations(convertRelations(sec));
		return result;
	}
	
	private FSArray convertLayers(Map<Annotation,AnnotationProxy> annotationMap, Section sec, int offset) {
		List<LayerProxy> result = new ArrayList<LayerProxy>();
		for (Layer layer : sec.getAllLayers()) {
			result.add(convertLayer(annotationMap, layer, offset));
		}
		return FSCollectionFactory.createFSArray(jcas, result);
	}

	private LayerProxy convertLayer(Map<Annotation,AnnotationProxy> annotationMap, Layer layer, int offset) {
		List<AnnotationProxy> annotProxies = new ArrayList<AnnotationProxy>(layer.size());
		for (Annotation a : layer) {
			annotProxies.add(convertAnnotation(annotationMap, a, offset));
		}
		LayerProxy result = new LayerProxy(jcas);
		result.setName(layer.getName());
		result.setAnnotations(FSCollectionFactory.createFSArray(jcas, annotProxies));
		return result;
	}
	
	private AnnotationProxy convertAnnotation(Map<Annotation,AnnotationProxy> annotationMap, Annotation a, int offset) {
		if (annotationMap.containsKey(a)) {
			return annotationMap.get(a);
		}
		AnnotationProxy result = new AnnotationProxy(jcas, offset + a.getStart(), offset + a.getEnd());
		result.setFeatures(convertFeatures(a));
		annotationMap.put(a, result);
		return result;
	}
	
	private FSArray convertRelations(Section sec) {
		List<RelationProxy> result = new ArrayList<RelationProxy>();
		for (Relation rel : sec.getAllRelations()) {
			result.add(convertRelation(rel));
		}
		for (Relation rel : sec.getAllRelations()) {
			for (Tuple t : rel.getTuples()) {
				updateArgumentProxies(t);
			}
		}
		return FSCollectionFactory.createFSArray(jcas, result);
	}
	
	private RelationProxy convertRelation(Relation rel) {
		RelationProxy result = new RelationProxy(jcas);
		result.setName(rel.getName());
		result.setFeatures(convertFeatures(rel));
		result.setTuples(convertTuples(rel));
		return result;
	}
	
	private FSArray convertTuples(Relation rel) {
		List<TupleProxy> result = new ArrayList<TupleProxy>(rel.size());
		for (Tuple t : rel.getTuples()) {
			result.add(convertTuple(t));
		}
		return FSCollectionFactory.createFSArray(jcas, result);
	}
	
	private TupleProxy convertTuple(Tuple t) {
		TupleProxy result = new TupleProxy(jcas);
		result.setFeatures(convertFeatures(t));
		argumentMap.put(t, result);
		return result;
	}

	private void updateArgumentProxies(Tuple t) {
		List<ArgumentProxy> argProxies = new ArrayList<ArgumentProxy>();
		for (String role : t.getRoles()) {
			Element arg = t.getArgument(role);
			ArgumentProxy argProxy = convertArgumentProxy(role, arg);
			if (argProxy != null) {
				argProxies.add(argProxy);
			}
		}
		TupleProxy tupleProxy = (TupleProxy) argumentMap.get(t);
		tupleProxy.setArguments(FSCollectionFactory.createFSArray(jcas, argProxies));
	}
	
	private ArgumentProxy convertArgumentProxy(String role, Element arg) {
		if (!argumentMap.containsKey(arg)) {
			logger.warning("tuple argument is neither annotation or tuple, skip");
			return null;
		}
		TOP aarg = argumentMap.get(arg);
		ArgumentProxy result = new ArgumentProxy(jcas);
		result.setRole(role);
		result.setArgument(aarg);
		return result;
	}
}
