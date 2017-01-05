/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


package org.bibliome.alvisnlp.modules.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.files.OutputDirectory;
import org.bibliome.util.files.OutputFile;
import org.bibliome.util.fragments.FragmentTag;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(beta=true)
public class QuickHTML extends SectionModule<SectionResolvedObjects> {
	private static final String XPATH_DOMUMENT_TITLE = "/html/head/title";
	private static final String XPATH_TITLE_HEADING = "/html/body/h1";
	private static final String XPATH_PREVIOUS_DOCUMENT = "/html/body/div[@id = 'document-navigation']/div[@id = 'document-previous']";
	private static final String XPATH_NEXT_DOCUMENT = "/html/body/div[@id = 'document-navigation']/div[@id = 'document-next']";
	private static final String XPATH_DOCUMENT_DIV = "/html/body/div[@id = 'alvisnlp-document']";
	private static final String XPATH_FRAGMENTS = "//span[@alvisnlp-id != '']";
	private static final String XPATH_DOCUMENT_LIST = "/html/body/ul[@id = 'document-list']";
	private static final String DEFAULT_ANNOTATION_CLASS = "-default"; 
	
	
	private OutputDirectory outDir;
	private String[] layers;
	private String tagFeature;
	private String classFeature;
	private String[] features;
	private String[] colors = {
			"#FFFFFF",
			"#FFD3A5",
			"#8BB3C1",
			"#FC4B77",
			"#996992",
			"#AA9B8C",
	};

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			Logger logger = getLogger(ctx);
			EvaluationContext evalCtx = new EvaluationContext(logger);
			Set<String> classes = new TreeSet<String>();
			classes.add(DEFAULT_ANNOTATION_CLASS);
			Document docList = createDocumentList();
			if (!outDir.exists() && !outDir.mkdirs()) {
				processingException("could not create directory " + outDir.getAbsolutePath());
			}
			generateDocuments(logger, evalCtx, corpus, classes, docList);
			writeXHTMLDocument(docList, "index");
			copyResource(logger, "common.css");
			copyResource(logger, "jquery-1.11.1.min.js");
			copyResource(logger, "document.js");
			generateSpecificCSS(logger, classes);
		}
		catch (Exception e) {
			rethrow(e);
		}
	}
	
	private static Document createDocumentList() throws SAXException, IOException {
		// same ClassLoader as this class
		try (InputStream is = QuickHTML.class.getResourceAsStream("index.html")) {
			return XMLUtils.docBuilder.parse(is);
		}
	}
	
	private void generateDocuments(Logger logger, EvaluationContext evalCtx, Corpus corpus, Set<String> classes, Document docList) throws XPathExpressionException, IOException, SAXException, TransformerFactoryConfigurationError {
		logger.info("generating HTML documents");
		Element docListUL = XMLUtils.evaluateElement(XPATH_DOCUMENT_LIST, docList);
		Document docSkel = createDocumentSkeleton();
		Iterator<alvisnlp.corpus.Document> docIt = documentIterator(evalCtx, corpus);
		alvisnlp.corpus.Document prev = null;
		alvisnlp.corpus.Document next = docIt.hasNext() ? docIt.next() : null;
		while (next != null) {
			alvisnlp.corpus.Document doc = next;
			addDocumentListItem(docList, docListUL, doc);
			next = docIt.hasNext() ? docIt.next() : null;
			Document xmlDoc = createDocument(docSkel, doc, prev, next);
			HTMLBuilderFragmentTagIterator frit = new HTMLBuilderFragmentTagIterator(this, classes);
			for (Section sec : Iterators.loop(sectionIterator(evalCtx, doc))) {
				createSection(xmlDoc, sec, frit);
			}
			writeXHTMLDocument(xmlDoc, doc.getId());
			prev = doc;
		}
	}
	
	private static void addDocumentListItem(Document docList, Element docListUL, alvisnlp.corpus.Document doc) {
		Element li = XMLUtils.createElement(docList, docListUL, -1, "li");
		addClass(li, "documet-list-item");
		addLinkToDocument(li, doc);
	}
	
	private static void addLinkToDocument(Element parent, alvisnlp.corpus.Document doc) {
		String id = doc.getId();
		Element a = XMLUtils.createElement(parent.getOwnerDocument(), parent, -1, "a", id);
		a.setAttribute("href", id + ".html");
	}
	
	private static Document createDocumentSkeleton() throws IOException, SAXException {
		// same ClassLoader as this class
		try (InputStream is = QuickHTML.class.getResourceAsStream("document.html")) {
			return XMLUtils.docBuilder.parse(is);
		}
	}
	
	private OutputStream copyResource(Logger logger, String name) throws IOException {
		logger.info("copying " + name);
		try (InputStream is = QuickHTML.class.getResourceAsStream(name)) {
			OutputStream out = null;
			try {
				out = new FileOutputStream(new OutputFile(outDir, name));
				byte[] buf = new byte[1024];
				while (true) {
					int n = is.read(buf);
					if (n == -1) {
						break;
					}
					out.write(buf, 0, n);
				}
				return out;
			}
			catch (IOException e) {
				if (out != null) {
					out.close();
				}
				throw e;
			}
		}
	}

	private static Document createDocument(Document docSkel, alvisnlp.corpus.Document doc, alvisnlp.corpus.Document prev, alvisnlp.corpus.Document next) throws XPathExpressionException {
		Document result = (Document) docSkel.cloneNode(true);
		String title = "Document: " + doc.getId();
		Element docTitle = XMLUtils.evaluateElement(XPATH_DOMUMENT_TITLE, result);
		docTitle.setTextContent(title);
		Element titleHeading = XMLUtils.evaluateElement(XPATH_TITLE_HEADING, result);
		titleHeading.setTextContent(title);
		if (prev != null) {
			Element prevDiv = XMLUtils.evaluateElement(XPATH_PREVIOUS_DOCUMENT, result);
			addLinkToDocument(prevDiv, prev);
		}
		if (next != null) {
			Element nextDiv = XMLUtils.evaluateElement(XPATH_NEXT_DOCUMENT, result);
			addLinkToDocument(nextDiv, next);
		}
		return result;
	}
	
	private void createSection(Document xmlDoc, Section sec, HTMLBuilderFragmentTagIterator frit) throws XPathExpressionException {
		Element docDiv = XMLUtils.evaluateElement(XPATH_DOCUMENT_DIV, xmlDoc);
		Element secDiv = XMLUtils.createElement(xmlDoc, docDiv, 0, "div");
		secDiv.setAttribute("class", "alvisnlp-section");
		XMLUtils.createElement(xmlDoc, secDiv, -1, "h2", "Section: " + sec.getName());
		Element contentsDiv = XMLUtils.createElement(xmlDoc, secDiv, -1, "div");
		contentsDiv.setAttribute("class", "alvisnlp-contents");
		Layer annotations = getAnnotations(sec);
		frit.init(xmlDoc, contentsDiv);
		FragmentTag.iterateFragments(frit, sec.getContents(), STABLE_COMPARATOR, annotations, 0);
		stratify(xmlDoc);
	}
	
	private final Comparator<Annotation> STABLE_COMPARATOR = new Comparator<Annotation>() {
		@Override
		public int compare(Annotation o1, Annotation o2) {
			String class1 = getAnnotationClass(o1);
			String class2 = getAnnotationClass(o2);
			if (class1.equals(class2)) {
				return o1.hashCode() - o2.hashCode();
			}
			return class1.hashCode() - o2.hashCode();
		}
	};
	
	String getAnnotationClass(Annotation a) {
		if (a.hasFeature(classFeature)) {
			return a.getLastFeature(classFeature);
		}
		return DEFAULT_ANNOTATION_CLASS;
	}
	
	String getAnnotationTag(Annotation a) {
		if (tagFeature != null && a.hasFeature(tagFeature)) {
			return a.getLastFeature(tagFeature);
		}
		return "span";
	}
	
	Collection<String> getFeatureKeys(Annotation a) {
		if (features == null) {
			return a.getFeatureKeys();
		}
		return Arrays.asList(features);
	}

	private void stratify(Document doc) throws XPathExpressionException {
		Map<Element,Integer> cache = new HashMap<Element,Integer>();
		List<Element> fragments = XMLUtils.evaluateElements(XPATH_FRAGMENTS, doc);
		for (Element frag : fragments) {
			stratify(cache, frag);
		}
		Map<String,Integer> strates = new HashMap<String,Integer>();
		for (Element frag : fragments) {
			String id = frag.getAttribute("alvisnlp-id");
			int strate = cache.get(frag);
			if (!strates.containsKey(id) || strate > strates.get(id).intValue()) {
				strates.put(id, strate);
			}
		}
		for (Element frag : fragments) {
			String id = frag.getAttribute("alvisnlp-id");
			int strate = strates.get(id);
			addClass(frag, "strate-" + strate);
		}
	}
	
	static void addClass(Element elt, String klass) {
		klass = klass.trim();
		if (klass.isEmpty()) {
			return;
		}
		if (elt.hasAttribute("class")) {
			elt.setAttribute("class", elt.getAttribute("class") + ' ' + klass);
		}
		else {
			elt.setAttribute("class", klass);
		}
	}

	private int stratify(Map<Element,Integer> cache, Element frag) {
		if (cache.containsKey(frag)) {
			return cache.get(frag);
		}
		int result = 0;
		for (Element child : XMLUtils.childrenElements(frag)) {
			int strate = stratify(cache, child) + 1;
			if (strate > result) {
				result = strate;
			}
		}
		cache.put(frag, result);
		return result;
	}

	private Layer getAnnotations(Section sec) {
		if (layers == null) {
			return sec.getAllAnnotations();
		}
		Layer result = new Layer(sec);
		for (String name : layers) {
			if (sec.hasLayer(name)) {
				result.addAll(sec.getLayer(name));
			}
		}
		return result;
	}
	
	private void writeXHTMLDocument(Document xmlDoc, String name) throws TransformerFactoryConfigurationError {
		OutputFile file = new OutputFile(outDir, name + ".html");
		XMLUtils.writeDOMToFile(xmlDoc, null, file);
	}

	private void generateSpecificCSS(Logger logger, Set<String> classes) throws IOException {
		logger.info("generating specific.css");
		try (PrintStream out = new PrintStream(new File(outDir, "specific.css"))) {
			Iterator<String> colorIt = Arrays.asList(colors).iterator();
			for (String klass : classes) {
				if (!colorIt.hasNext()) {
					colorIt = Arrays.asList(colors).iterator();
				}
				String col = colorIt.next();
				out.format(".%s {\n\tbackground-color: %s;\n}\n\n", klass, col);
			}
			
			for (int strate = 0; strate < 10; ++strate) {
				int vPad = 1 + strate * 2;
				int hPad = 1 + strate * 1;
				out.format(".strate-%d {\n\tpadding-top: %dpx;\n\tpadding-bottom: %dpx;\n}\n\n", strate, vPad, vPad);
				out.format(".strate-%d.alvisnlp-first-fragment {\n\tpadding-left: %dpx;\n}\n\n", strate, hPad);
				out.format(".strate-%d.alvisnlp-last-fragment {\n\tpadding-right: %dpx;\n}\n\n", strate, hPad);
			}
		}
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

	@Param(mandatory=false, nameType=NameType.LAYER)
	public String[] getLayers() {
		return layers;
	}

	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String getTagFeature() {
		return tagFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getClassFeature() {
		return classFeature;
	}

	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String[] getFeatures() {
		return features;
	}

	@Param
	public String[] getColors() {
		return colors;
	}

	public void setColors(String[] colors) {
		this.colors = colors;
	}

	public void setOutDir(OutputDirectory outDir) {
		this.outDir = outDir;
	}

	public void setLayers(String[] layers) {
		this.layers = layers;
	}

	public void setTagFeature(String tagFeature) {
		this.tagFeature = tagFeature;
	}

	public void setClassFeature(String classFeature) {
		this.classFeature = classFeature;
	}

	public void setFeatures(String[] features) {
		this.features = features;
	}
}
