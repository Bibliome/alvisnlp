package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.streams.AcceptAllFiles;
import fr.inra.maiage.bibliome.util.streams.CompressionFilter;
import fr.inra.maiage.bibliome.util.streams.DirectorySourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.StreamFactory;

@AlvisNLPModule(beta = true)
public abstract class TagTogReader extends AbstractXMLReader<ResolvedObjects> {
	public static final String XSL_TRANSFORM_SOURCE = "res://fr/inra/maiage/bibliome/alvisnlp/bibliomefactory/resources/XMLReader/tagtog2alvisnlp.xslt";
	public static final String HTML_DIRECTORY = "plain.html";
	public static final String LEGEND_FILE = "annotations-legend.json";
	public static final String ANNOTATION_DIRECTORY = "ann.json";

	private InputDirectory source;
	private String entitiesLayer = "entities";
	private String typeFeature = "type";
	private String annotatorFeature = "who";

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			processDocuments(ctx, corpus);
			Mapping annotationTypes = readAnnotationTypes();
			Logger logger = getLogger(ctx);
			logger.info("annotation types:");
			for (Map.Entry<String,String> e : annotationTypes.entrySet()) {
				logger.info(e.getKey() + " -> " + e.getValue());
			}
			readAnnotations(ctx, corpus, annotationTypes);
		}
		catch (IOException | SAXException | ParserConfigurationException | TransformerException | ParseException e) {
			throw new ProcessingException(e);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Mapping readAnnotationTypes() throws FileNotFoundException, IOException, ParseException {
		InputFile legendFile = new InputFile(source, LEGEND_FILE);
		try (Reader reader = new FileReader(legendFile)) {
			JSONParser parser = new JSONParser();
			Map obj = (Map) parser.parse(reader);
			Mapping result = new Mapping();
			for (Map.Entry e : (Iterable<Map.Entry>) obj.entrySet()) {
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				result.put(key, value);
			}
			return result;
		}
	}
	
	private void readAnnotations(ProcessingContext<Corpus> ctx, Corpus corpus, Mapping annotationTypes) throws IOException, ParseException {
		Logger logger = getLogger(ctx);
		SourceStream annotationSources = getAnnotationSources();
		for (BufferedReader reader : Iterators.loop(annotationSources.getBufferedReaders())) {
			String docId = getDocumentId(annotationSources, reader, corpus);
			Document doc = corpus.getDocument(docId);
			if (doc != null) {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(reader);
				JSONArray entities = (JSONArray) obj.get("entities");
				for (Object oEnt : entities) {
					JSONObject jEnt = (JSONObject) oEnt;
					String classId = (String) jEnt.get("classId");
					String annotationType;
					if (annotationTypes.containsKey(classId)) {
						annotationType = annotationTypes.get(classId);
					}
					else {
						annotationType = classId;
						logger.warning("unknown annotation type: " + classId);
					}
					String part = (String) jEnt.get("part");
					if (!doc.hasSection(part)) {
						logger.warning("unknown part: " + part);
						continue;
					}
					Section sec = doc.sectionIterator(part).next();
					Layer layer = sec.ensureLayer(entitiesLayer);
					JSONArray offsets = (JSONArray) jEnt.get("offsets");
					if (offsets.size() > 1) {
						logger.warning("discontinuous entity");
					}
					for (Object oOff : offsets) {
						JSONObject jOff = (JSONObject) oOff;
						int start = (int) (long) jOff.get("start");
						String text = (String) jOff.get("text");
						int end = start + text.length();
						Annotation a = new Annotation(this, layer, start, end);
						if (!a.getForm().equals(text)) {
							logger.warning("text mismatch, expected " + a.getForm() + ", got " + text);
						}
						a.addFeature(typeFeature, annotationType);
						JSONObject confidence = (JSONObject) jEnt.get("confidence");
						JSONArray who = (JSONArray) confidence.get("who");
						for (Object w : who) {
							a.addFeature(annotatorFeature, (String) w);
						}
					}
				}
			}
			else {
				logger.warning("annotations for unknown document: " + docId);
			}
		}
	}
	
	private String getDocumentId(SourceStream annotationSources, Reader reader, Corpus corpus) {
		String name = annotationSources.getStreamName(reader);
		String baseName = new File(name).getName();
		return baseName.replace(".ann.json", "");
	}

	private SourceStream getAnnotationSources() {
		InputDirectory jsonDir = new InputDirectory(source, ANNOTATION_DIRECTORY);
		return new DirectorySourceStream("UTF-8", CompressionFilter.NONE, jsonDir, true, AcceptAllFiles.INSTANCE);
	}

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public SourceStream getXslTransform() {
		StreamFactory sf = new StreamFactory();
		try {
			return sf.getSourceStream(XSL_TRANSFORM_SOURCE);
		}
		catch (IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Mapping getStringParams() {
		return null;
	}

	@Override
	public SourceStream getXMLSource() {
		InputDirectory htmlDir = new InputDirectory(source, HTML_DIRECTORY);
		return new DirectorySourceStream("UTF-8", CompressionFilter.NONE, htmlDir, true, AcceptAllFiles.INSTANCE);
	}

	@Override
	public Boolean getHtml() {
		return false;
	}

	@Override
	public Boolean getRawTagNames() {
		return false;
	}

	@Param
	public InputDirectory getSource() {
		return source;
	}

	public void setSource(InputDirectory source) {
		this.source = source;
	}
}
