package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro.DKProDependencyImporter;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro.DKProNamedEntityImporter;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro.DKProSentenceImporter;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro.DKProTokenImporter;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AnnotationProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.ArgumentProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.DocumentProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.FeatureProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.LayerProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.RelationProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.TupleProxy;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.util.Iterators;

public class ImportHelper {
	private final Map<FeatureStructure,Element> argumentMap = new HashMap<FeatureStructure,Element>();
	private final NavigableMap<Integer,Section> sectionOffsets = new TreeMap<Integer,Section>();
	private final XMIImport creator;
	private final Corpus corpus;
	private final JCas jcas;
	private Document currentDocument = null;

	ImportHelper(XMIImport creator, Corpus corpus, JCas jcas) {
		super();
		this.creator = creator;
		this.corpus = corpus;
		this.jcas = jcas;
	}

	void reset() {
		jcas.reset();
		argumentMap.clear();
		sectionOffsets.clear();
		currentDocument = null;
	}

	void convertDocument(String sourceName) {
		FSIterator<DocumentProxy> adit = jcas.getAllIndexedFS(DocumentProxy.class);
		if (adit.hasNext()) {
			DocumentProxy docProxy = adit.get();
			Map<SectionProxy,Section> sectionMap = convertDocumentsAndSections(docProxy);
			if (creator.getDkproCompatibility()) {
				convertDKProCompatibility();
			}
			convertAnnotatedSections(docProxy, sectionMap);
		}
		else {
			convertEmptyDocument(sourceName);
			if (creator.getDkproCompatibility()) {
				convertDKProCompatibility();
			}
		}
	}

	private void convertEmptyDocument(String sourceName) {
		currentDocument = Document.getDocument(creator, corpus, getDocumentId(sourceName));
		createSection();
	}
	
	private String getDocumentId(String sourceName) {
		if (creator.getBaseNameId()) {
			int slash = sourceName.lastIndexOf(File.separatorChar) + 1;
			int dot = sourceName.lastIndexOf('.');
			if (dot == -1 || dot < slash)
				dot = sourceName.length();
			return sourceName.substring(slash, dot);
		}
		return sourceName;
	}
	
	private Map<SectionProxy,Section> convertDocumentsAndSections(DocumentProxy docProxy) {
		currentDocument = Document.getDocument(creator, corpus, docProxy.getId());
		convertFeatures(currentDocument, docProxy.getFeatures());
		Map<SectionProxy,Section> result = new HashMap<SectionProxy,Section>();
		for (FeatureStructure fsSection : docProxy.getSections()) {
			SectionProxy sectionProxy = (SectionProxy) fsSection;
			Section sec = createSection(sectionProxy);
			result.put(sectionProxy, sec);
		}
		return result;
	}
	
	private void convertAnnotatedSections(DocumentProxy docProxy, Map<SectionProxy,Section> sectionMap) {
		int offset = 0;
		for (FeatureStructure fsSection : docProxy.getSections()) {
			SectionProxy sectionProxy = (SectionProxy) fsSection;
			Section sec = sectionMap.get(sectionProxy);
			Map<AnnotationProxy,Annotation> annotationMap = new HashMap<AnnotationProxy,Annotation>();
			convertLayers(annotationMap, offset, sec, sectionProxy);
			argumentMap.putAll(annotationMap);
			convertRelations(sec, sectionProxy);
			convertArguments(sectionProxy);
			offset += sec.getContents().length();
		}
	}
	
	private static void convertFeatures(Element elt, FSArray fsFeatures) {
		for (FeatureStructure f : fsFeatures) {
			FeatureProxy featureProxy = (FeatureProxy) f;
			String key = featureProxy.getKey();
			String value = featureProxy.getValue();
			elt.addFeature(key, value);
		}
	}

	private Section createSection(String name, int begin, int end) {
		String docContents = jcas.getDocumentText();
		Section result = new Section(creator, currentDocument, name, docContents.substring(begin, end));
		sectionOffsets.put(begin, result);
		return result;
	}
	
	private Section createSection() {
		String docContents = jcas.getDocumentText();
		return createSection(creator.getDefaultSectionName(), 0, docContents.length());
	}
	
	private Section createSection(SectionProxy sectionProxy) {
		Section result = createSection(sectionProxy.getName(), sectionProxy.getBegin(), sectionProxy.getEnd());
		convertFeatures(result, sectionProxy.getFeatures());
		return result;
	}

	private void convertLayers(Map<AnnotationProxy,Annotation> annotationMap, int offset, Section sec, SectionProxy sectionProxy) {
		for (FeatureStructure fsLayer : sectionProxy.getLayers()) {
			convertLayer(annotationMap, offset, sec, (LayerProxy) fsLayer);
		}
	}
	
	private void convertLayer(Map<AnnotationProxy,Annotation> annotationMap, int offset, Section sec, LayerProxy layerProxy) {
		String layerName = layerProxy.getName();
		Layer layer = sec.ensureLayer(layerName);
		for (FeatureStructure fsAnnotation : layerProxy.getAnnotations()) {
			convertAnnotation(annotationMap, offset, layer, (AnnotationProxy) fsAnnotation);
		}
	}
	
	private void convertAnnotation(Map<AnnotationProxy,Annotation> annotationMap, int offset, Layer layer, AnnotationProxy annotProxy) {
		Annotation a;
		if (annotationMap.containsKey(annotProxy)) {
			a = annotationMap.get(annotProxy);
			layer.add(a);
		}
		else {
			a = new Annotation(creator, layer, annotProxy.getBegin() - offset, annotProxy.getEnd() - offset);
			convertFeatures(a, annotProxy.getFeatures());
			annotationMap.put(annotProxy, a);
		}
	}

	private void convertRelations(Section sec, SectionProxy sectionProxy) {
		for (FeatureStructure fsRelation : sectionProxy.getRelations()) {
			convertRelation(sec, (RelationProxy) fsRelation);
		}
	}

	private void convertRelation(Section sec, RelationProxy relationProxy) {
		Relation rel = new Relation(creator, sec, relationProxy.getName());
		convertFeatures(rel, relationProxy.getFeatures());
		for (FeatureStructure fsTuple : relationProxy.getTuples()) {
			convertTuple(rel, (TupleProxy) fsTuple);
		}
	}

	private void convertTuple(Relation rel, TupleProxy tupleProxy) {
		Tuple t = new Tuple(creator, rel);
		convertFeatures(t, tupleProxy.getFeatures());
		argumentMap.put(tupleProxy, t);
	}
	
	private void convertArguments(SectionProxy sectionProxy) {
		for (FeatureStructure fsRelation : sectionProxy.getRelations()) {
			convertArguments((RelationProxy) fsRelation);
		}
	}

	private void convertArguments(RelationProxy relationProxy) {
		for (FeatureStructure fsTuple : relationProxy.getTuples()) {
			convertArguments((TupleProxy) fsTuple);
		}
	}

	private void convertArguments(TupleProxy tupleProxy) {
		Tuple t = (Tuple) argumentMap.get(tupleProxy);
		for (FeatureStructure fsArg : tupleProxy.getArguments()) {
			convertArgument(t, (ArgumentProxy) fsArg);
		}
	}

	private void convertArgument(Tuple t, ArgumentProxy argProxy) {
		String role = argProxy.getRole();
		Element arg = argumentMap.get(argProxy.getArgument());
		t.setArgument(role, arg);
	}
	
	private <A extends org.apache.uima.jcas.tcas.Annotation> void importCompatibilityAnnotations(CompatibilityAnnotationImporter<A> annotationImporter) {
		Class<A> klass = annotationImporter.getAnnotationClass();
		FSIterator<A> it = jcas.getAllIndexedFS(klass);
		for (A annotation : Iterators.loop(it)) {
			importCompatibilityAnnotation(annotationImporter, annotation);
		}
	}

	private <A extends org.apache.uima.jcas.tcas.Annotation> void importCompatibilityAnnotation(CompatibilityAnnotationImporter<A> annotationImporter, A annotation) {
		System.err.println("sectionOffsets = " + sectionOffsets);
		System.err.println("annotation = " + annotation);
		int rawBegin = annotation.getBegin();
		System.err.println("rawBegin = " + rawBegin);
		Map.Entry<Integer,Section> e = sectionOffsets.floorEntry(rawBegin); 
		System.err.println("e = " + e);
		int offset = e.getKey();
		Section section = e.getValue();
		Annotation alvisAnnotation = new Annotation(creator, section, rawBegin - offset, annotation.getEnd() - offset);
		for (String layerName : annotationImporter.getLayerNames(annotation)) {
			Layer layer = section.ensureLayer(layerName);
			layer.add(alvisAnnotation);
		}
		annotationImporter.setFeatures(alvisAnnotation, annotation);
		argumentMap.put(annotation, alvisAnnotation);
	}

	private <A extends org.apache.uima.jcas.tcas.Annotation> void importCompatibilityTuples(CompatibilityTupleImporter<A> tupleImporter) {
		Class<A> klass = tupleImporter.getAnnotationClass();
		FSIterator<A> it = jcas.getAllIndexedFS(klass);
		for (A annotation : Iterators.loop(it)) {
			importCompatibilityTuple(tupleImporter, annotation);
		}
	}

	private <A extends org.apache.uima.jcas.tcas.Annotation> void importCompatibilityTuple(CompatibilityTupleImporter<A> tupleImporter, A annotation) {
		int rawBegin = annotation.getBegin();
		Map.Entry<Integer,Section> e = sectionOffsets.lowerEntry(rawBegin); 
		Section section = e.getValue();
		String relName = tupleImporter.getRelationName(annotation);
		Relation relation = section.ensureRelation(creator, relName);
		Tuple tuple = new Tuple(creator, relation);
		tupleImporter.setFeatures(tuple, annotation);
		tupleImporter.setArguments(argumentMap, tuple, annotation);
	}
	
	private void convertDKProCompatibility() {
		importCompatibilityAnnotations(DKProTokenImporter.INSTANCE);
		importCompatibilityAnnotations(DKProSentenceImporter.INSTANCE);
		importCompatibilityAnnotations(DKProNamedEntityImporter.INSTANCE);
		importCompatibilityTuples(DKProDependencyImporter.INSTANCE);
	}
}
