package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pesv;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.DocumentCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.fragments.Fragment;
import fr.inra.maiage.bibliome.util.fragments.SimpleFragment;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule
public abstract class PESVReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator {
	private SourceStream docStream;
	private SourceStream entitiesStream;
	private String tokenLayer = "tokens";
	private String ordFeature = "ord";
	private String section = DefaultNames.getDefaultSectionName();
	private String entityLayer = "entities";
	private String propertiesFeature = "properties";

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		try {
			Logger logger = getLogger(ctx);
			loadDocuments(logger, corpus);
			loadEntities(logger, corpus);
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

	private void loadEntities(Logger logger, Corpus corpus) throws IOException {
		Iterator<Reader> readers = entitiesStream.getReaders();
		for (Reader r : Iterators.loop(readers)) {
			String name = entitiesStream.getStreamName(r);
			logger.info("reading " + name);
			for (CSVRecord record : CSVFormat.MYSQL.builder().setQuote('"').setHeader().setSkipHeaderRecord(true).build().parse(r)) {
				loadEntities(logger, corpus, record);
			}
		}
	}

	private void loadEntities(Logger logger, Corpus corpus, CSVRecord record) {
		if (!record.isConsistent()) {
			logger.warning("line " + record.getRecordNumber() + " has wrong number of columns");
		}
		String docId = record.get("id_articleweb");
		if (!corpus.hasDocument(docId)) {
			logger.warning("line " + record.getRecordNumber() + ": no document " + docId + ", ignoring record");
			return;
		}
		Document doc = corpus.getDocument(docId);
		Section sec = doc.sectionIterator(section).next();
		Annotation a = createAnnotation(sec, record);
		for (String col : record.getParser().getHeaderNames()) {
			String value = record.get(col);
			a.addFeature(col, value);
			a.addFeature(propertiesFeature, value);
		}
	}

	private Annotation createAnnotation(Section sec, CSVRecord record) {
		Layer tokens = sec.ensureLayer(tokenLayer);
		String firstTokenIndexStr = record.get("token_index");
		String lastTokenIndexStr = getLastTokenIndexStr(firstTokenIndexStr, record);
		Annotation firstToken = lookupToken(tokens, firstTokenIndexStr);
		Annotation lastToken = lookupToken(tokens, lastTokenIndexStr);
		int start = firstToken.getStart();
		int end = lastToken.getEnd();
		Layer entities = sec.ensureLayer(entityLayer);
		return new Annotation(this, entities, start, end);
	}

	private static String getLastTokenIndexStr(String firstTokenIndexStr, CSVRecord record) {
		int firstTokenIndex = Integer.parseInt(firstTokenIndexStr);
		int entityLength = Integer.parseInt(record.get("length"));
		int lastTokenIndex = firstTokenIndex + entityLength - 1;
		return Integer.toString(lastTokenIndex);
	}

	private Annotation lookupToken(Layer tokens, String tokenIndexStr) {
		for (Annotation t : tokens) {
			if (tokenIndexStr.equals(t.getLastFeature(ordFeature))) {
				return t;
			}
		}
		return null;
	}

	private void loadDocuments(Logger logger, Corpus corpus) throws IOException {
		Iterator<Reader> readers = docStream.getReaders();
		for (Reader r : Iterators.loop(readers)) {
			String name = docStream.getStreamName(r);
			logger.info("reading " + name);
			for (CSVRecord record : CSVFormat.MYSQL.builder().setQuote('"').setHeader().setSkipHeaderRecord(true).build().parse(r)) {
				loadDocument(logger, corpus, record);
			}
		}
	}

	private void loadDocument(Logger logger, Corpus corpus, CSVRecord record) {
		if (!record.isConsistent()) {
			logger.warning("line " + record.getRecordNumber() + " has wrong number of columns");
		}
		String docId = record.get("id");
		Document doc = Document.getDocument(this, corpus, docId);
		Map<String,String> features = record.toMap();
		for (Map.Entry<String,String> e : features.entrySet()) {
			String key = e.getKey();
			if (!key.equals("id")) {
				doc.addFeature(key, e.getValue());
			}
		}
		List<String> tokens = getTokens(record);
		List<Fragment> frags = new ArrayList<Fragment>(tokens.size());
		StringBuilder content = new StringBuilder();
		for (String t : tokens) {
			if (content.length() > 0) {
				content.append(' ');
			}
			int start = content.length();
			content.append(t);
			int end = content.length();
			Fragment f = new SimpleFragment(start, end);
			frags.add(f);
		}
		Section sec = new Section(this, doc, section , content.toString());
		Layer layer = sec.ensureLayer(tokenLayer);
		for (int i = 0; i < frags.size(); ++i) {
			Fragment f = frags.get(i);
			Annotation a = new Annotation(this, layer, f.getStart(), f.getEnd());
			a.addFeature(ordFeature, Integer.toString(i));
		}
	}

	private static final Pattern TOKEN_PATTERN = Pattern.compile("<t>(.+?)</t>");
	private static List<String> getTokens(CSVRecord record) {
		List<String> result = new ArrayList<String>();
		String s = record.get("processed_text");
		Matcher m = TOKEN_PATTERN.matcher(s);
		while (m.find()) {
			String t = m.group(1);
			if (t.equals("<br />")) {
				t = "\n";
			}
			result.add(t);
		}
		return result;
	}

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Param
	public SourceStream getDocStream() {
		return docStream;
	}

	@Param
	public SourceStream getEntitiesStream() {
		return entitiesStream;
	}

	@Param(nameType=NameType.LAYER)
	public String getTokenLayer() {
	    return this.tokenLayer;
	};

	public void setTokenLayer(String tokenLayer) {
	    this.tokenLayer = tokenLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getTokenLayerName() {
		return tokenLayer;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getOrdFeatureKey() {
		return ordFeature;
	}

	@Deprecated
	@Param(nameType=NameType.SECTION)
	public String getSectionName() {
		return section;
	}

	@Param(nameType=NameType.LAYER)
	public String getEntityLayer() {
	    return this.entityLayer;
	};

	public void setEntityLayer(String entityLayer) {
	    this.entityLayer = entityLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getEntityLayerName() {
		return entityLayer;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getPropertiesFeatureKey() {
		return propertiesFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getOrdFeature() {
		return ordFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getPropertiesFeature() {
		return propertiesFeature;
	}

	@Param(nameType=NameType.SECTION)
	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public void setOrdFeature(String ordFeature) {
		this.ordFeature = ordFeature;
	}

	public void setPropertiesFeature(String propertiesFeature) {
		this.propertiesFeature = propertiesFeature;
	}

	public void setEntitiesStream(SourceStream entitiesStream) {
		this.entitiesStream = entitiesStream;
	}

	public void setTokenLayerName(String tokenLayer) {
		this.tokenLayer = tokenLayer;
	}

	public void setOrdFeatureKey(String ordFeatureKey) {
		this.ordFeature = ordFeatureKey;
	}

	public void setSectionName(String sectionName) {
		this.section = sectionName;
	}

	public void setEntityLayerName(String entityLayer) {
		this.entityLayer = entityLayer;
	}

	public void setPropertiesFeatureKey(String propertiesFeatureKey) {
		this.propertiesFeature = propertiesFeatureKey;
	}

	public void setDocStream(SourceStream docStream) {
		this.docStream = docStream;
	}
}
