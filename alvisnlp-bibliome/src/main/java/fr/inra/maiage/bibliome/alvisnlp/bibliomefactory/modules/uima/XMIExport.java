package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.admin.CASAdminException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.FSCollectionFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;
import org.xml.sax.SAXException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
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
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.StringCat;
import fr.inra.maiage.bibliome.util.files.OutputDirectory;

@AlvisNLPModule(beta=true)
public class XMIExport extends SectionModule<SectionResolvedObjects> {
	private OutputDirectory outDir;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		try {
			JCas jcas = JCasFactory.createJCas();
			for (Document doc : Iterators.loop(documentIterator(evalCtx, corpus))) {
				jcas.setDocumentText(convertContents(evalCtx, doc));
				AlvisDocument ad = convertDocument(logger, evalCtx, jcas, doc);
				ad.addToIndexes();
				try (OutputStream os = openDocumentFile(doc)) {
					XmiCasSerializer.serialize(jcas.getCas(), null, os, true, null);
				}
				jcas.reset();
			}
		}
		catch (CASRuntimeException|UIMAException|CASAdminException|SAXException|IOException e) {
			rethrow(e);
		}
	}
	
	private OutputStream openDocumentFile(Document doc) throws FileNotFoundException {
		File file = new File(outDir, doc.getId() + ".xmi");
		File dir = file.getParentFile();
		dir.mkdirs();
		return new FileOutputStream(file);
	}

	private String convertContents(EvaluationContext evalCtx, Document doc) {
		StringCat strcat = new StringCat();
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, doc))) {
			strcat.append(sec.getContents());
		}
		return strcat.toString();
	}
	
	private static FSArray convertFeatures(JCas jcas, Element elt) {
		List<AlvisFeature> result = new ArrayList<AlvisFeature>();
		for (String key : elt.getFeatureKeys()) {
			if (elt.isStaticFeatureKey(key)) {
				continue;
			}
			for (String value : elt.getFeature(key)) {
				AlvisFeature f = new AlvisFeature(jcas);
				f.setKey(key);
				f.setValue(value);
				result.add(f);
			}
		}
		return FSCollectionFactory.createFSArray(jcas, result);
	}
	
	private AlvisDocument convertDocument(Logger logger, EvaluationContext evalCtx, JCas jcas, Document doc) {
		AlvisDocument result = new AlvisDocument(jcas);
		result.setId(doc.getId());
		result.setFeatures(convertFeatures(jcas, doc));
		result.setSections(convertSections(logger, evalCtx, jcas, doc));
		return result;
	}
	
	private FSArray convertSections(Logger logger, EvaluationContext evalCtx, JCas jcas, Document doc) {
		List<AlvisSection> result = new ArrayList<AlvisSection>();
		int offset = 0;
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, doc))) {
			result.add(convertSection(logger, jcas, sec, offset));
			offset += sec.getContents().length();
		}
		return FSCollectionFactory.createFSArray(jcas, result);
	}

	private static AlvisSection convertSection(Logger logger, JCas jcas, Section sec, int offset) {
		AlvisSection result = new AlvisSection(jcas, offset, offset + sec.getContents().length());
		result.setName(sec.getName());
		result.setFeatures(convertFeatures(jcas, sec));
		Map<Element,TOP> argumentMap = new HashMap<Element,TOP>();
		Map<Annotation,AlvisAnnotation> annotationMap = new HashMap<Annotation,AlvisAnnotation>();
		result.setLayers(convertLayers(argumentMap, annotationMap, jcas, sec, offset));
		result.setRelations(convertRelations(logger, argumentMap, jcas, sec));
		return result;
	}
	
	private static FSArray convertLayers(Map<Element,TOP> argumentMap, Map<Annotation,AlvisAnnotation> annotationMap, JCas jcas, Section sec, int offset) {
		List<AlvisLayer> result = new ArrayList<AlvisLayer>();
		for (Layer layer : sec.getAllLayers()) {
			result.add(convertLayer(argumentMap, annotationMap, jcas, layer, offset));
		}
		return FSCollectionFactory.createFSArray(jcas, result);
	}

	private static AlvisLayer convertLayer(Map<Element,TOP> argumentMap, Map<Annotation,AlvisAnnotation> annotationMap, JCas jcas, Layer layer, int offset) {
		List<AlvisAnnotation> aas = new ArrayList<AlvisAnnotation>(layer.size());
		for (Annotation a : layer) {
			aas.add(convertAnnotation(argumentMap, annotationMap, jcas, a, offset));
		}
		AlvisLayer result = new AlvisLayer(jcas);
		result.setName(layer.getName());
		result.setAnnotations(FSCollectionFactory.createFSArray(jcas, aas));
		return result;
	}
	
	private static AlvisAnnotation convertAnnotation(Map<Element,TOP> argumentMap, Map<Annotation,AlvisAnnotation> annotationMap, JCas jcas, Annotation a, int offset) {
		if (annotationMap.containsKey(a)) {
			return annotationMap.get(a);
		}
		AlvisAnnotation result = new AlvisAnnotation(jcas, offset + a.getStart(), offset + a.getEnd());
		result.setFeatures(convertFeatures(jcas, a));
		annotationMap.put(a, result);
		argumentMap.put(a, result);
		return result;
	}
	
	private static FSArray convertRelations(Logger logger, Map<Element,TOP> argumentMap, JCas jcas, Section sec) {
		List<AlvisRelation> result = new ArrayList<AlvisRelation>();
		for (Relation rel : sec.getAllRelations()) {
			result.add(convertRelation(argumentMap, jcas, rel));
		}
		for (Relation rel : sec.getAllRelations()) {
			for (Tuple t : rel.getTuples()) {
				updateTupleArguments(logger, argumentMap, jcas, t);
			}
		}
		return FSCollectionFactory.createFSArray(jcas, result);
	}
	
	private static AlvisRelation convertRelation(Map<Element,TOP> argumentMap, JCas jcas, Relation rel) {
		AlvisRelation result = new AlvisRelation(jcas);
		result.setName(rel.getName());
		result.setFeatures(convertFeatures(jcas, rel));
		result.setTuples(convertTuples(argumentMap, jcas, rel));
		return result;
	}
	
	private static FSArray convertTuples(Map<Element,TOP> argumentMap, JCas jcas, Relation rel) {
		List<AlvisTuple> result = new ArrayList<AlvisTuple>(rel.size());
		for (Tuple t : rel.getTuples()) {
			result.add(convertTuple(argumentMap, jcas, t));
		}
		return FSCollectionFactory.createFSArray(jcas, result);
	}
	
	private static AlvisTuple convertTuple(Map<Element,TOP> argumentMap, JCas jcas, Tuple t) {
		AlvisTuple result = new AlvisTuple(jcas);
		result.setFeatures(convertFeatures(jcas, t));
		argumentMap.put(t, result);
		return result;
	}

	private static void updateTupleArguments(Logger logger, Map<Element,TOP> argumentMap, JCas jcas, Tuple t) {
		List<TupleArgument> tas = new ArrayList<TupleArgument>();
		for (String role : t.getRoles()) {
			Element arg = t.getArgument(role);
			TupleArgument ta = convertTupleArgument(logger, argumentMap, jcas, role, arg);
			if (ta != null) {
				tas.add(ta);
			}
		}
		AlvisTuple at = (AlvisTuple) argumentMap.get(t);
		at.setArguments(FSCollectionFactory.createFSArray(jcas, tas));
	}
	
	private static TupleArgument convertTupleArgument(Logger logger, Map<Element,TOP> argumentMap, JCas jcas, String role, Element arg) {
		if (!argumentMap.containsKey(arg)) {
			logger.warning("tuple argument is neither annotation or tuple, skip");
			return null;
		}
		TOP aarg = argumentMap.get(arg);
		TupleArgument ta = new TupleArgument(jcas);
		ta.setRole(role);
		ta.setArgument(aarg);
		return ta;
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Param
	public OutputDirectory getOutDir() {
		return outDir;
	}

	public void setOutDir(OutputDirectory outDir) {
		this.outDir = outDir;
	}
}
