package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.admin.CASAdminException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
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
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.DocumentCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule(beta=true)
public abstract class XMIImport extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
	private SourceStream source;
	private String defaultSectionName = "text";
    private Boolean baseNameId = false;
    private Boolean ignoreMalformedXMI = false;
    
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		try {
			JCas jcas = JCasFactory.createJCas();
			for (InputStream is : Iterators.loop(source.getInputStreams())) {
				String sourceName = source.getStreamName(is);
				logger.info("reading " + sourceName);
				try {
					XmiCasDeserializer.deserialize(is, jcas.getCas(), true);
				}
				catch (Exception e) {
					if (ignoreMalformedXMI) {
						logger.warning("ignoring " + sourceName);
						continue;
					}
					rethrow(e);
				}
				convertDocument(corpus, jcas, sourceName);
				jcas.reset();
			}
		}
		catch (UIMAException|CASAdminException|IOException e) {
			rethrow(e);
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

	private void convertDocument(Corpus corpus, JCas jcas, String sourceName) {
		FSIterator<DocumentProxy> adit = jcas.getAllIndexedFS(DocumentProxy.class);
		if (adit.hasNext()) {
			convertAnnotatedDocument(corpus, jcas, adit.get());
		}
		else {
			convertEmptyDocument(corpus, jcas, sourceName);
		}
	}
	
	private void convertEmptyDocument(Corpus corpus, JCas jcas, String sourceName) {
		Document doc = Document.getDocument(this, corpus, getDocumentId(sourceName));
		new Section(this, doc, defaultSectionName, jcas.getDocumentText());
	}
	
	private String getDocumentId(String sourceName) {
		if (baseNameId) {
			int slash = sourceName.lastIndexOf(File.separatorChar) + 1;
			int dot = sourceName.lastIndexOf('.');
			if (dot == -1 || dot < slash)
				dot = sourceName.length();
			return sourceName.substring(slash, dot);
		}
		return sourceName;
	}
	
	private void convertAnnotatedDocument(Corpus corpus, JCas jcas, DocumentProxy docProxy) {
		Document doc = Document.getDocument(this, corpus, docProxy.getId());
		convertFeatures(doc, docProxy.getFeatures());
		String docContents = jcas.getDocumentText();
		int offset = 0;
		for (FeatureStructure fsSection : docProxy.getSections()) {
			SectionProxy sectionProxy = (SectionProxy) fsSection;
			Section sec = new Section(this, doc, sectionProxy.getName(), docContents.substring(sectionProxy.getBegin(), sectionProxy.getEnd()));
			convertFeatures(sec, sectionProxy.getFeatures());
			Map<AnnotationProxy,Annotation> annotationMap = new HashMap<AnnotationProxy,Annotation>();
			convertLayers(annotationMap, offset, sec, sectionProxy);
			Map<FeatureStructure,Element> argumentMap = new HashMap<FeatureStructure,Element>(annotationMap);
			convertRelations(argumentMap, sec, sectionProxy);
			convertArguments(argumentMap, sectionProxy);
			offset += sec.getContents().length();
		}
	}
	
	private static void convertArguments(Map<FeatureStructure,Element> argumentMap, SectionProxy sectionProxy) {
		for (FeatureStructure fsRelation : sectionProxy.getRelations()) {
			convertArguments(argumentMap, (RelationProxy) fsRelation);
		}
	}

	private static void convertArguments(Map<FeatureStructure,Element> argumentMap, RelationProxy relationProxy) {
		for (FeatureStructure fsTuple : relationProxy.getTuples()) {
			convertArguments(argumentMap, (TupleProxy) fsTuple);
		}
	}

	private static void convertArguments(Map<FeatureStructure,Element> argumentMap, TupleProxy tupleProxy) {
		Tuple t = (Tuple) argumentMap.get(tupleProxy);
		for (FeatureStructure fsArg : tupleProxy.getArguments()) {
			convertArgument(argumentMap, t, (ArgumentProxy) fsArg);
		}
	}

	private static void convertArgument(Map<FeatureStructure,Element> argumentMap, Tuple t, ArgumentProxy argProxy) {
		String role = argProxy.getRole();
		Element arg = argumentMap.get(argProxy.getArgument());
		t.setArgument(role, arg);
	}

	private void convertRelations(Map<FeatureStructure,Element> argumentMap, Section sec, SectionProxy sectionProxy) {
		for (FeatureStructure fsRelation : sectionProxy.getRelations()) {
			convertRelation(argumentMap, sec, (RelationProxy) fsRelation);
		}
	}

	private void convertRelation(Map<FeatureStructure,Element> argumentMap, Section sec, RelationProxy relationProxy) {
		Relation rel = new Relation(this, sec, relationProxy.getName());
		convertFeatures(rel, relationProxy.getFeatures());
		for (FeatureStructure fsTuple : relationProxy.getTuples()) {
			convertTuple(argumentMap, rel, (TupleProxy) fsTuple);
		}
	}

	private void convertTuple(Map<FeatureStructure,Element> argumentMap, Relation rel, TupleProxy tupleProxy) {
		Tuple t = new Tuple(this, rel);
		convertFeatures(t, tupleProxy.getFeatures());
		argumentMap.put(tupleProxy, t);
	}

	private void convertLayers(Map<AnnotationProxy,Annotation> annotationMap, int offset, Section sec, SectionProxy sectionProxy) {
		for (FeatureStructure fsLayer : sectionProxy.getLayers()) {
			convertLayer(annotationMap, offset, sec, (LayerProxy) fsLayer);
		}
	}
	
	private void convertLayer(Map<AnnotationProxy,Annotation> annotationMap, int offset, Section sec, LayerProxy layerProxy) {
		Layer layer = sec.ensureLayer(layerProxy.getName());
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
			a = new Annotation(this, layer, annotProxy.getBegin() - offset, annotProxy.getEnd() - offset);
			convertFeatures(a, annotProxy.getFeatures());
			annotationMap.put(annotProxy, a);
		}
	}

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Param
	public SourceStream getSource() {
		return source;
	}

	@Param(nameType=NameType.SECTION)
	public String getDefaultSectionName() {
		return defaultSectionName;
	}

	@Param
	public Boolean getBaseNameId() {
		return baseNameId;
	}

	@Param
	public Boolean getIgnoreMalformedXMI() {
		return ignoreMalformedXMI;
	}

	public void setIgnoreMalformedXMI(Boolean ignoreMalformedXMI) {
		this.ignoreMalformedXMI = ignoreMalformedXMI;
	}

	public void setDefaultSectionName(String defaultSectionName) {
		this.defaultSectionName = defaultSectionName;
	}

	public void setBaseNameId(Boolean baseNameId) {
		this.baseNameId = baseNameId;
	}

	public void setSource(SourceStream source) {
		this.source = source;
	}
	
}
