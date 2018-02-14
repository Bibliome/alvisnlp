package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.admin.CASAdminException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.xml.sax.SAXException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisAnnotation;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisDocument;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisFeature;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisLayer;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisSection;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisTuple;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.TupleArgument;
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

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			JCas jcas = JCasFactory.createJCas();
			for (InputStream is : Iterators.loop(source.getInputStreams())) {
				XmiCasDeserializer.deserialize(is, jcas.getCas());
				convertDocument(corpus, jcas);
				jcas.reset();
			}
		}
		catch (UIMAException|CASAdminException|IOException|SAXException e) {
			rethrow(e);
		}
	}
	
	private static void convertFeatures(Element elt, FSArray fsa) {
		for (FeatureStructure f : fsa) {
			AlvisFeature af = (AlvisFeature) f;
			String key = af.getKey();
			String value = af.getValue();
			elt.addFeature(key, value);
		}
	}

	private void convertDocument(Corpus corpus, JCas jcas) {
		String docContents = jcas.getDocumentText();
		AlvisDocument ad = jcas.getAllIndexedFS(AlvisDocument.class).get();
		Document doc = Document.getDocument(this, corpus, ad.getId());
		convertFeatures(doc, ad.getFeatures());
		int offset = 0;
		for (FeatureStructure fss : ad.getSections()) {
			AlvisSection as = (AlvisSection) fss;
			Section sec = new Section(this, doc, as.getName(), docContents.substring(as.getBegin(), as.getEnd()));
			convertFeatures(sec, as.getFeatures());
			Map<AlvisAnnotation,Annotation> annotationMap = new HashMap<AlvisAnnotation,Annotation>();
			convertLayers(annotationMap, offset, sec, as);
			Map<FeatureStructure,Element> argumentMap = new HashMap<FeatureStructure,Element>(annotationMap);
			convertRelations(argumentMap, sec, as);
			convertArguments(argumentMap, as);
			offset += sec.getContents().length();
		}
	}
	
	private static void convertArguments(Map<FeatureStructure,Element> argumentMap, AlvisSection as) {
		for (FeatureStructure fsr : as.getRelations()) {
			convertArguments(argumentMap, (AlvisRelation) fsr);
		}
	}

	private static void convertArguments(Map<FeatureStructure,Element> argumentMap, AlvisRelation ar) {
		for (FeatureStructure fst : ar.getTuples()) {
			convertArguments(argumentMap, (AlvisTuple) fst);
		}
	}

	private static void convertArguments(Map<FeatureStructure,Element> argumentMap, AlvisTuple fst) {
		Tuple t = (Tuple) argumentMap.get(fst);
		for (FeatureStructure fsa : fst.getArguments()) {
			convertArgument(argumentMap, t, (TupleArgument) fsa);
		}
	}

	private static void convertArgument(Map<FeatureStructure,Element> argumentMap, Tuple t, TupleArgument fsa) {
		String role = fsa.getRole();
		Element arg = argumentMap.get(fsa.getArgument());
		System.err.println("fsa = " + fsa);
		System.err.println("arg = " + arg);
		t.setArgument(role, arg);
	}

	private void convertRelations(Map<FeatureStructure,Element> argumentMap, Section sec, AlvisSection as) {
		for (FeatureStructure fsr : as.getRelations()) {
			convertRelation(argumentMap, sec, (AlvisRelation) fsr);
		}
	}

	private void convertRelation(Map<FeatureStructure,Element> argumentMap, Section sec, AlvisRelation ar) {
		Relation rel = new Relation(this, sec, ar.getName());
		convertFeatures(rel, ar.getFeatures());
		for (FeatureStructure fst : ar.getTuples()) {
			convertTuple(argumentMap, rel, (AlvisTuple) fst);
		}
	}

	private void convertTuple(Map<FeatureStructure,Element> argumentMap, Relation rel, AlvisTuple fst) {
		Tuple t = new Tuple(this, rel);
		convertFeatures(t, fst.getFeatures());
		argumentMap.put(fst, t);
	}

	private void convertLayers(Map<AlvisAnnotation,Annotation> annotationMap, int offset, Section sec, AlvisSection as) {
		for (FeatureStructure fsl : as.getLayers()) {
			convertLayer(annotationMap, offset, sec, (AlvisLayer) fsl);
		}
	}
	
	private void convertLayer(Map<AlvisAnnotation,Annotation> annotationMap, int offset, Section sec, AlvisLayer al) {
		Layer layer = sec.ensureLayer(al.getName());
		for (FeatureStructure fsa : al.getAnnotations()) {
			convertAnnotation(annotationMap, offset, layer, (AlvisAnnotation) fsa);
		}
	}
	
	private void convertAnnotation(Map<AlvisAnnotation,Annotation> annotationMap, int offset, Layer layer, AlvisAnnotation aa) {
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
