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

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro.DependencyExporter;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro.NamedEntityExporter;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro.SentenceExporter;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro.TokenExporter;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AnnotationProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.ArgumentProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.DocumentProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.FeatureProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.LayerProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.RelationProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.TupleProxy;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
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
			LayerProxy layerProxy = convertLayer(annotationMap, layer, offset);
			if (layerProxy != null) {
				result.add(layerProxy);
			}
		}
		return FSCollectionFactory.createFSArray(jcas, result);
	}

	private LayerProxy convertLayer(Map<Annotation,AnnotationProxy> annotationMap, Layer layer, int offset) {
		if (module.getDkproCompatibility()) {
			String layerName = layer.getName();
			if (layerName.equals(DefaultNames.getWordLayer())) {
				convertCompatibilityLayer(TokenExporter.INSTANCE, layer, offset);
				return null;
			}
			if (layerName.equals(DefaultNames.getSentenceLayer())) {
				convertCompatibilityLayer(SentenceExporter.INSTANCE, layer, offset);
				return null;
			}
			if (layerName.equals(DefaultNames.getNamedEntityLayer())) {
				convertCompatibilityLayer(NamedEntityExporter.INSTANCE, layer, offset);
				return null;
			}
		}
		return convertNativeLayer(annotationMap, layer, offset);
	}

	private <A extends org.apache.uima.jcas.tcas.Annotation> void convertCompatibilityLayer(CompatibilityAnnotationExporter<A> exporter, Layer layer, int offset) {
		for (Annotation a : layer) {
			convertCompatibilityAnnotation(exporter, a, offset);
		}
	}

	private <A extends org.apache.uima.jcas.tcas.Annotation> void convertCompatibilityAnnotation(CompatibilityAnnotationExporter<A> exporter, Annotation alvisAnnotation, int offset) {
		int begin = offset + alvisAnnotation.getStart();
		int end = offset + alvisAnnotation.getEnd();
		A annotation = exporter.create(jcas, alvisAnnotation, begin, end);
		argumentMap.put(alvisAnnotation, annotation);
		annotation.addToIndexes();
	}

	private LayerProxy convertNativeLayer(Map<Annotation,AnnotationProxy> annotationMap, Layer layer, int offset) {
		List<AnnotationProxy> annotProxies = new ArrayList<AnnotationProxy>(layer.size());
		for (Annotation a : layer) {
			annotProxies.add(convertNativeAnnotation(annotationMap, a, offset));
		}
		LayerProxy result = new LayerProxy(jcas);
		result.setName(layer.getName());
		result.setAnnotations(FSCollectionFactory.createFSArray(jcas, annotProxies));
		return result;
	}
	
	private AnnotationProxy convertNativeAnnotation(Map<Annotation,AnnotationProxy> annotationMap, Annotation a, int offset) {
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
			RelationProxy relationProxy = convertRelation(rel);
			if (relationProxy != null) {
				result.add(relationProxy);
			}
		}
		for (Relation rel : sec.getAllRelations()) {
			if (module.getDkproCompatibility()) {
				String relName = rel.getName();
				if (relName.equals(DefaultNames.getDependencyRelationName())) {
					continue;
				}
			}
			for (Tuple t : rel.getTuples()) {
				updateArgumentProxies(t);
			}
		}
		return FSCollectionFactory.createFSArray(jcas, result);
	}
	
	private RelationProxy convertRelation(Relation rel) {
		if (module.getDkproCompatibility()) {
			String relName = rel.getName();
			if (relName.equals(DefaultNames.getDependencyRelationName())) {
				convertCompatibilityRelation(DependencyExporter.INSTANCE, rel);
				return null;
			}
		}
		return convertNativeRelation(rel);
	}
	
	private <A extends org.apache.uima.jcas.tcas.Annotation> void convertCompatibilityRelation(CompatibilityTupleExporter<A> exporter, Relation rel) {
		for (Tuple t : rel.getTuples()) {
			convertCompatibilityTuple(exporter, t);
		}
	}

	private <A extends org.apache.uima.jcas.tcas.Annotation> void convertCompatibilityTuple(CompatibilityTupleExporter<A> exporter, Tuple t) {
		A annotation = exporter.create(argumentMap, jcas, t);
		annotation.addToIndexes();
	}

	private RelationProxy convertNativeRelation(Relation rel) {
		RelationProxy result = new RelationProxy(jcas);
		result.setName(rel.getName());
		result.setFeatures(convertFeatures(rel));
		result.setTuples(convertNativeTuples(rel));
		return result;
	}
	
	private FSArray convertNativeTuples(Relation rel) {
		List<TupleProxy> result = new ArrayList<TupleProxy>(rel.size());
		for (Tuple t : rel.getTuples()) {
			result.add(convertNativeTuple(t));
		}
		return FSCollectionFactory.createFSArray(jcas, result);
	}
	
	private TupleProxy convertNativeTuple(Tuple t) {
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
