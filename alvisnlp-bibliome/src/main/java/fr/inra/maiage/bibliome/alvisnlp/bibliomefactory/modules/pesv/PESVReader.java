package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pesv;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
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
import fr.inra.maiage.bibliome.util.fragments.Fragment;
import fr.inra.maiage.bibliome.util.fragments.SimpleFragment;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule(beta=true)
public abstract class PESVReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator {
	private static final String[] DOCUMENT_FEATURES = {
			"url",
			"source",
			"lang",
			"probability_lang",
			"date_submitted",
			"date_aspirated",
			"id_rssfeed",
			"source_text",
			"source_title",
			"description",
			"created_at",
			"published_at",
			"content_type",
			"extension",
			"source_lang"
	};
	
	private SourceStream docStream;
	private SourceStream extractStream;
	private String tokenLayerName = "tokens";
	private String ordFeatureKey = "ord";
	private String sectionName = "text";
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			Logger logger = getLogger(ctx);
			loadDocuments(logger, corpus);
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}
	
	private void loadDocuments(Logger logger, Corpus corpus) throws IOException {
		try (Reader r = docStream.getReader()) {
			for (CSVRecord record : CSVFormat.MYSQL.withQuote('"').withFirstRecordAsHeader().parse(r)) {
				loadDocument(logger, corpus, record);
			}
		}
	}
	
	private void loadDocument(Logger logger, Corpus corpus, CSVRecord record) {
		if (!record.isConsistent()) {
			logger.warning("line " + record.getRecordNumber() + " has wrong number of columns, ignoring");
			return;
		}
		String docId = record.get("id");
		Document doc = Document.getDocument(this, corpus, docId);
		for (String col : DOCUMENT_FEATURES) {
			doc.addFeature(col, record.get(col));
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
		Section sec = new Section(this, doc, sectionName , content.toString());
		Layer layer = sec.ensureLayer(tokenLayerName);
		for (int i = 0; i < frags.size(); ++i) {
			Fragment f = frags.get(i);
			Annotation a = new Annotation(this, layer, f.getStart(), f.getEnd());
			a.addFeature(ordFeatureKey, Integer.toString(i));
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
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Param
	public SourceStream getDocStream() {
		return docStream;
	}

	public void setDocStream(SourceStream docStream) {
		this.docStream = docStream;
	}
}
