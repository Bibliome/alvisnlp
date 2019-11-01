package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pesv;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

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
		catch (IOException | SAXException e) {
			throw new ProcessingException(e);
		}
	}
	
	private void loadDocuments(Logger logger, Corpus corpus) throws IOException, SAXException {
		try (Reader r = docStream.getReader()) {
			RECORD: for (CSVRecord record : CSVFormat.MYSQL.withQuote('"').withFirstRecordAsHeader().parse(r)) {
				if (!record.isConsistent()) {
					logger.warning("line " + record.getRecordNumber() + " has wrong number of columns, ignoring");
					continue;
				}
				String docId = record.get("id");
				Document doc = Document.getDocument(this, corpus, docId);
				Section title = createSection(doc, record, "title");
				Section text = createSection(doc, record, "text");
				for (String col : DOCUMENT_FEATURES) {
					doc.addFeature(col, record.get(col));
				}
				Section current = title;
				String content = current.getContents();
				Layer tokenLayer = title.ensureLayer(tokenLayerName);
				int from = 0;
				NodeList tokens = getTokens(record);
				for (int i = 0; i < tokens.getLength(); ++i) {
					Element t = (Element) tokens.item(i);
					String form = t.getTextContent();
					int start = content.indexOf(form, from);
					if (start == -1) {
						if (current == text) {
							logger.warning("reached end of text in " + docId + " at token " + form + ", ignoring everything");
							continue RECORD;
						}
						current = text;
						content = current.getContents();
						tokenLayer = title.ensureLayer(tokenLayerName);
						from = 0;
						start = content.indexOf(form, from);
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
		}
	}
	
	private static NodeList getTokens(CSVRecord record) throws SAXException, IOException {
		String tokensString = "<dummy>" + record.get("processed_text") + "</dummy>";
		org.w3c.dom.Document doc = XMLUtils.docBuilder.parse(new InputSource(new StringReader(tokensString)));
		return doc.getElementsByTagName("t");
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
