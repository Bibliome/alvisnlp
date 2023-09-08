package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json.helper.JArray;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json.helper.JObject;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml.TagTogReader.TagTogResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.streams.AcceptAllFiles;
import fr.inra.maiage.bibliome.util.streams.CompressionFilter;
import fr.inra.maiage.bibliome.util.streams.DirectorySourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.StreamFactory;

@AlvisNLPModule(beta = true)
public abstract class TagTogReader extends AbstractXMLReader<TagTogResolvedObjects> {
	public static final String XSL_TRANSFORM_SOURCE = "res://fr/inra/maiage/bibliome/alvisnlp/bibliomefactory/resources/XMLReader/tagtog2alvisnlp.xslt";
	public static final String HTML_DIRECTORY = "plain.html";
	public static final String LEGEND_FILE = "annotations-legend.json";
	public static final String ANNOTATION_DIRECTORY = "ann.json";

	private InputFile zipFile;
	private String entitiesLayer = "entities";
	private String relation = "relations";
	private String typeFeature = "type";
	private String annotatorFeature = "annotator";
	private String argumentPrefix = "arg";

	public static class TagTogResolvedObjects extends ResolvedObjects {
		private InputDirectory rootDir;
		
		public TagTogResolvedObjects(ProcessingContext ctx, TagTogReader module) throws ResolverException {
			super(ctx, module);
		}

		public InputDirectory getRootDir() {
			return rootDir;
		}

		public void setRootDir(InputDirectory rootDir) {
			this.rootDir = rootDir;
		}
	}
	
	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		try {
			extract(ctx);
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
	
	private void extract(ProcessingContext ctx) throws IOException {
		File tmpDir = getTempDir(ctx);
		Path tmpPath = tmpDir.toPath();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
        	Path rootName = null;
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
            	if (rootName == null) {
            		rootName = new File(zipEntry.getName()).toPath().getName(0);
            	}
                boolean isDirectory = zipEntry.getName().endsWith(File.separator);
                Path newPath = zipSlipProtect(zipEntry, tmpPath);
                if (isDirectory) {
                    Files.createDirectories(newPath);
                }
                else {
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
    		TagTogResolvedObjects resObj = getResolvedObjects();
    		resObj.setRootDir(new InputDirectory(tmpDir, rootName.toString()));
        }
	}

	private static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir) throws IOException {
		Path targetDirResolved = targetDir.resolve(zipEntry.getName());
		Path normalizePath = targetDirResolved.normalize();
		if (!normalizePath.startsWith(targetDir)) {
			throw new IOException("Bad zip entry: " + zipEntry.getName());
		}
		return normalizePath;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Mapping readAnnotationTypes() throws FileNotFoundException, IOException, ParseException {
		TagTogResolvedObjects resObj = getResolvedObjects();
		InputFile legendFile = new InputFile(resObj.getRootDir(), LEGEND_FILE);
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
	
	private void readAnnotations(ProcessingContext ctx, Corpus corpus, Mapping annotationTypes) throws IOException, ParseException {
		Logger logger = getLogger(ctx);
		SourceStream annotationSources = getAnnotationSources();
		for (BufferedReader reader : Iterators.loop(annotationSources.getBufferedReaders())) {
			String docId = getDocumentId(annotationSources, reader, corpus);
			Document doc = corpus.getDocument(docId);
			if (doc != null) {
				JObject obj = JObject.parse(reader);
				JArray entities = obj.getArray("entities");
				for (JObject jEnt : entities.asObjectArray()) {
					String annotationType = getAnnotationType(logger, annotationTypes, jEnt);
					Section sec = getSection(logger, doc, jEnt);
					if (sec == null) {
						continue;
					}
					Layer layer = sec.ensureLayer(entitiesLayer);
					createEntityAnnotation(logger, layer, jEnt, annotationType);
				}
				JArray relations = obj.getArray("relations");
				for (JObject jRel : relations.asObjectArray()) {
					String annotationType = getAnnotationType(logger, annotationTypes, jRel);
					JArray jArgs = jRel.getArray("entities");
					Tuple t = null;
					int argN = 0;
					for (String sArg : jArgs.asStringArray()) {
						Annotation arg = getArgument(logger, annotationTypes, doc, sArg);
						if (arg == null) {
							continue;
						}
						if (t == null) {
							Section sec = arg.getSection();
							Relation rel = sec.ensureRelation(this, relation);
							t = new Tuple(this, rel);
							t.addFeature(typeFeature, annotationType);
							addAnnotatorFeature(jRel, t);
						}
						argN++;
						t.setArgument(argumentPrefix + argN, arg);
					}
				}
			}
			else {
				logger.warning("annotations for unknown document: " + docId);
			}
			reader.close();
		}
	}
	
	private Annotation getArgument(Logger logger, Mapping annotationTypes, Document doc, String sArg) {
		List<String> argInfo = Strings.split(sArg, '|', -1);
		String part = argInfo.get(0);
		if (!doc.hasSection(part)) {
			logger.warning("unknown part: " + part);
			return null;
		}
		Section argSec = getSection(logger, doc, part);
		Layer layer = argSec.ensureLayer(entitiesLayer);
		String type = getAnnotationType(logger, annotationTypes, argInfo.get(1));
		List<String> off = Strings.split(argInfo.get(2), ',', -1);
		int start = Integer.parseInt(off.get(0));
		int end = Integer.parseInt(off.get(1)) + 1;
		for (Annotation a : layer.span(start, end)) {
			if (a.getLastFeature(typeFeature).equals(type)) {
				return a;
			}
		}
		logger.warning("could not find entity: " + argInfo);
		return null;
	}
	
	private String getDocumentId(SourceStream annotationSources, Reader reader, Corpus corpus) {
		String name = annotationSources.getStreamName(reader);
		String baseName = new File(name).getName();
		return baseName.replace(".ann.json", "");
	}

	private static String getAnnotationType(Logger logger, Mapping annotationTypes, String classId) {
		if (annotationTypes.containsKey(classId)) {
			return annotationTypes.get(classId);
		}
		logger.warning("unknown annotation type: " + classId);
		return classId;
	}
	
	private static String getAnnotationType(Logger logger, Mapping annotationTypes, JObject jAnn) {
		String classId = jAnn.getString("classId");
		return getAnnotationType(logger, annotationTypes, classId);
	}
	
	private static Section getSection(Logger logger, Document doc, String part) {
		if (doc.hasSection(part)) {
			return doc.sectionIterator(part).next();
		}
		logger.warning("unknown part: " + part);
		return null;
	}

	private static Section getSection(Logger logger, Document doc, JObject jEnt) {
		String part = jEnt.getString("part");
		return getSection(logger, doc, part);
	}
	
	private void createEntityAnnotation(Logger logger, Layer layer, JObject jEnt, String annotationType) {
		JArray offsets = jEnt.getArray("offsets");
		if (offsets.size() > 1) {
			logger.warning("discontinuous entity");
		}
		for (JObject jOff : offsets.asObjectArray()) {
			int start = jOff.getInt("start");
			String text = jOff.getString("text");
			int end = start + text.length();
			Annotation a = new Annotation(this, layer, start, end);
			if (!a.getForm().equals(text)) {
				logger.warning("text mismatch, expected " + a.getForm() + ", got " + text);
			}
			a.addFeature(typeFeature, annotationType);
			addAnnotatorFeature(jEnt, a);
		}
	}
	
	private void addAnnotatorFeature(JObject jEnt, Element elt) {
		JObject confidence = jEnt.getObject("confidence");
		JArray who = confidence.getArray("who");
		for (String w : who.asStringArray()) {
			elt.addFeature(annotatorFeature, w);
		}
	}

	private SourceStream getAnnotationSources() {
		TagTogResolvedObjects resObj = getResolvedObjects();
		InputDirectory jsonDir = new InputDirectory(resObj.getRootDir(), ANNOTATION_DIRECTORY);
		return new DirectorySourceStream("UTF-8", CompressionFilter.NONE, jsonDir, true, AcceptAllFiles.INSTANCE);
	}

	@Override
	protected TagTogResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new TagTogResolvedObjects(ctx, this);
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
		TagTogResolvedObjects resObj = getResolvedObjects();
		InputDirectory htmlDir = new InputDirectory(resObj.getRootDir(), HTML_DIRECTORY);
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
	public InputFile getZipFile() {
		return zipFile;
	}

	@Param(nameType = NameType.LAYER)
	public String getEntitiesLayer() {
		return entitiesLayer;
	}

	@Param(nameType = NameType.RELATION)
	public String getRelation() {
		return relation;
	}

	@Param(nameType = NameType.FEATURE)
	public String getTypeFeature() {
		return typeFeature;
	}

	@Param(nameType = NameType.FEATURE)
	public String getAnnotatorFeature() {
		return annotatorFeature;
	}

	@Param(nameType = NameType.ARGUMENT)
	public String getArgumentPrefix() {
		return argumentPrefix;
	}

	public void setEntitiesLayer(String entitiesLayer) {
		this.entitiesLayer = entitiesLayer;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public void setTypeFeature(String typeFeature) {
		this.typeFeature = typeFeature;
	}

	public void setAnnotatorFeature(String annotatorFeature) {
		this.annotatorFeature = annotatorFeature;
	}

	public void setArgumentPrefix(String argumentPrefix) {
		this.argumentPrefix = argumentPrefix;
	}

	public void setZipFile(InputFile zipFile) {
		this.zipFile = zipFile;
	}
}
