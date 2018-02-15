package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima;

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
import org.xml.sax.SAXException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AnnotationProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.DocumentProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.FeatureProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.LayerProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.RelationProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.TupleProxy;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.ArgumentProxy;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
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

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		try {
			JCas jcas = JCasFactory.createJCas();
			for (InputStream is : Iterators.loop(source.getInputStreams())) {
				String sourceName = source.getStreamName(is);
				logger.info("reading " + sourceName);
				XmiCasDeserializer.deserialize(is, jcas.getCas(), true);
				convertDocument(corpus, jcas, sourceName);
				jcas.reset();
			}
		}
		catch (UIMAException|CASAdminException|IOException|SAXException e) {
			rethrow(e);
		}
	}
	
	private static void convertFeatures(Element elt, FSArray fsa) {
		for (FeatureStructure f : fsa) {
			FeatureProxy af = (FeatureProxy) f;
			String key = af.getKey();
			String value = af.getValue();
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
		Document doc = Document.getDocument(this, corpus, sourceName);
		new Section(this, doc, defaultSectionName, jcas.getDocumentText());
	}
	
	private void convertAnnotatedDocument(Corpus corpus, JCas jcas, DocumentProxy ad) {
		Document doc = Document.getDocument(this, corpus, ad.getId());
		convertFeatures(doc, ad.getFeatures());
		String docContents = jcas.getDocumentText();
		int offset = 0;
		for (FeatureStructure fss : ad.getSections()) {
			SectionProxy as = (SectionProxy) fss;
			Section sec = new Section(this, doc, as.getName(), docContents.substring(as.getBegin(), as.getEnd()));
			convertFeatures(sec, as.getFeatures());
			Map<AnnotationProxy,Annotation> annotationMap = new HashMap<AnnotationProxy,Annotation>();
			convertLayers(annotationMap, offset, sec, as);
			Map<FeatureStructure,Element> argumentMap = new HashMap<FeatureStructure,Element>(annotationMap);
			convertRelations(argumentMap, sec, as);
			convertArguments(argumentMap, as);
			offset += sec.getContents().length();
		}
	}
	
	private static void convertArguments(Map<FeatureStructure,Element> argumentMap, SectionProxy as) {
		for (FeatureStructure fsr : as.getRelations()) {
			convertArguments(argumentMap, (RelationProxy) fsr);
		}
	}

	private static void convertArguments(Map<FeatureStructure,Element> argumentMap, RelationProxy ar) {
		for (FeatureStructure fst : ar.getTuples()) {
			convertArguments(argumentMap, (TupleProxy) fst);
		}
	}

	private static void convertArguments(Map<FeatureStructure,Element> argumentMap, TupleProxy fst) {
		Tuple t = (Tuple) argumentMap.get(fst);
		for (FeatureStructure fsa : fst.getArguments()) {
			convertArgument(argumentMap, t, (ArgumentProxy) fsa);
		}
	}

	private static void convertArgument(Map<FeatureStructure,Element> argumentMap, Tuple t, ArgumentProxy fsa) {
		String role = fsa.getRole();
		Element arg = argumentMap.get(fsa.getArgument());
		t.setArgument(role, arg);
	}

	private void convertRelations(Map<FeatureStructure,Element> argumentMap, Section sec, SectionProxy as) {
		for (FeatureStructure fsr : as.getRelations()) {
			convertRelation(argumentMap, sec, (RelationProxy) fsr);
		}
	}

	private void convertRelation(Map<FeatureStructure,Element> argumentMap, Section sec, RelationProxy ar) {
		Relation rel = new Relation(this, sec, ar.getName());
		convertFeatures(rel, ar.getFeatures());
		for (FeatureStructure fst : ar.getTuples()) {
			convertTuple(argumentMap, rel, (TupleProxy) fst);
		}
	}

	private void convertTuple(Map<FeatureStructure,Element> argumentMap, Relation rel, TupleProxy fst) {
		Tuple t = new Tuple(this, rel);
		convertFeatures(t, fst.getFeatures());
		argumentMap.put(fst, t);
	}

	private void convertLayers(Map<AnnotationProxy,Annotation> annotationMap, int offset, Section sec, SectionProxy as) {
		for (FeatureStructure fsl : as.getLayers()) {
			convertLayer(annotationMap, offset, sec, (LayerProxy) fsl);
		}
	}
	
	private void convertLayer(Map<AnnotationProxy,Annotation> annotationMap, int offset, Section sec, LayerProxy al) {
		Layer layer = sec.ensureLayer(al.getName());
		for (FeatureStructure fsa : al.getAnnotations()) {
			convertAnnotation(annotationMap, offset, layer, (AnnotationProxy) fsa);
		}
	}
	
	private void convertAnnotation(Map<AnnotationProxy,Annotation> annotationMap, int offset, Layer layer, AnnotationProxy aa) {
		Annotation a;
		if (annotationMap.containsKey(aa)) {
			a = annotationMap.get(aa);
			layer.add(a);
		}
		else {
			a = new Annotation(this, layer, aa.getBegin() - offset, aa.getEnd() - offset);
			convertFeatures(a, aa.getFeatures());
			annotationMap.put(aa, a);
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

	public void setSource(SourceStream source) {
		this.source = source;
	}
	
}
