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
		Section title = createSection(doc, record, "title");
		Section text = createSection(doc, record, "text");
		Section current = title;
		String content = current.getContents();
		Layer tokenLayer = title.ensureLayer(tokenLayerName);
		int from = 0;
		List<String> tokens = getTokens(record);
		for (int i = 0; i < tokens.size(); ++i) {
			String form = tokens.get(i);
			int start = lookupToken(logger, content, from, form);
			if (start == -1) {
				if (current == text) {
					logger.warning("reached end of text in " + docId + " at token " + form + ", ignoring everything");
					return;
				}
				current = text;
				content = current.getContents();
				tokenLayer = current.ensureLayer(tokenLayerName);
				from = 0;
				start = lookupToken(logger, content, from, form);
			}
			if (start == -1) {
				logger.warning("in " + docId + ", could not find token " + form + ", ignoring it");
				continue;
			}
			from = start + form.length();
			Annotation a = new Annotation(this, tokenLayer, start, from);
			a.addFeature(ordFeatureKey, Integer.toString(i));
		}
	}
	
	private static int lookupToken(Logger logger, String content, int from, String form) {
		int result = content.indexOf(form, from);
		if (result != -1) {
			return result;
		}
		Pattern pat = tokenToPattern(form);
		Matcher m = pat.matcher(content);
		if (m.find(from)) {
			logger.warning("force-fit " + form + " to " + m.group());
			return m.start();
		}
		return -1;
	}

	private static Pattern tokenToPattern(String form) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < form.length(); ++i) {
			char c = form.charAt(i);
			if (Character.isWhitespace(c) || c == '_') {
				sb.append("[\\s_]");
			}
			else {
				sb.append("[^\\s_]");
			}
		}
		return Pattern.compile(sb.toString());
	}

	private static final Pattern TOKEN_PATTERN = Pattern.compile("<t>(.+?)</t>");
	private static List<String> getTokens(CSVRecord record) {
		List<String> result = new ArrayList<String>();
		String s = record.get("processed_text");
		Matcher m = TOKEN_PATTERN.matcher(s);
		while (m.find()) {
			String t = m.group(1);
			result.add(t);
		}
		return result;
	}
	
	

	private Section createSection(Document doc, CSVRecord record, String column) {
		return new Section(this, doc, column, record.get(column));
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
