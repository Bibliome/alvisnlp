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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.NodeSet;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.DocumentCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.StringCat;
import fr.inra.maiage.bibliome.util.filters.AcceptAll;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

@AlvisNLPModule
public abstract class XMLReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
	private static final org.w3c.dom.Document document;
	static {
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		}
		catch (ParserConfigurationException pce) {
			throw new RuntimeException(pce);
		}
	}
    public static final String SOURCE_PATH_PARAMETER = "source-path";
    public static final String SOURCE_BASENAME_PARAMETER = "source-basename";
    private static final String XML_READER_CONTEXT_PARAMETER = "xml-reader-context";
    public static final String INLINE_NAMESPACE = "http://bibliome.jouy.inra.fr/alvisnlp/bibliome-module-factory/inline";

    public static final String START_ATTRNAME = "start";
    public static final String END_ATTRNAME = "end";
    public static final String LEVEL_ATTRNAME = "level";
    public static final String WRAPPER_ELMTNAME = "wrapper";

    private SourceStream xslTransform;
    private TransformerSelector[] defaultTransformSelectors = new TransformerSelector[0];
    private TransformerSelector[] transformSelectors = new TransformerSelector[0];
    private SourceStream defaultTransform;
    private Mapping stringParams;
    private Boolean html = false;
    private SourceStream sourcePath;
    private Boolean rawTagNames = false;

    @Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}
    
    private List<TransformerSelector> getEffectiveTransformSelectors() {
    	if (xslTransform != null) {
    		TransformerSelector ts = new TransformerSelector(new AcceptAll<Document>(), xslTransform);
    		return Collections.singletonList(ts);
    	}
    	List<TransformerSelector> result = new ArrayList<TransformerSelector>();
    	result.addAll(Arrays.asList(transformSelectors));
    	result.addAll(Arrays.asList(defaultTransformSelectors));
    	if (defaultTransform != null) {
    		result.add(new TransformerSelector(new AcceptAll<Document>(), defaultTransform));
    	}
    	return result;
    }
    
	@TimeThis(task="load-xslt", category=TimerCategory.LOAD_RESOURCE)
    protected Transformer getTransformer(ProcessingContext<Corpus> ctx, TransformerFactory transformerFactory, List<TransformerSelector> transformerSelectors, Document doc) throws TransformerConfigurationException, IOException {
    	Logger logger = getLogger(ctx);
    	for (TransformerSelector selector : transformerSelectors) {
    		if (selector.accept(doc)) {
    			return selector.getTransformer(logger, transformerFactory);
    		}
    	}
    	return null;
    }

    @TimeThis(task="read-file", category=TimerCategory.LOAD_RESOURCE)
	protected Document getSource(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, InputStream file) throws SAXException, IOException, ParserConfigurationException {
		if (html) {
	        DOMParser parser = new DOMParser();
	        parser.setFeature("http://xml.org/sax/features/namespaces", false);
	        parser.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);
	        parser.setFeature("http://cyberneko.org/html/features/parse-noscript-content", false);
	        parser.setProperty("http://cyberneko.org/html/properties/default-encoding", sourcePath.getCharset());
	        if (rawTagNames) {
	        	parser.setProperty("http://cyberneko.org/html/properties/names/elems", "match");
	        }
	        else {
	        	parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
	        }
	        parser.parse(new InputSource(file));
	        return parser.getDocument();
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		db.setEntityResolver(new EntityResolver() {
	        @Override
			public InputSource resolveEntity(String pid, String sid) throws SAXException {
	            return new InputSource(new ByteArrayInputStream(new byte[] {}));
	        }
	    });
		return db.parse(file);
	}
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		try {
			List<TransformerSelector> transformSelectors = getEffectiveTransformSelectors();
			for (InputStream is : Iterators.loop(sourcePath.getInputStreams())) {
				processFile(ctx, logger, corpus, is, transformSelectors, transformerFactory);
				is.close();
			}
		}
		catch (IOException e) {
			rethrow(e);
		}
	}

	private void processFile(ProcessingContext<Corpus> ctx, Logger logger, Corpus corpus, InputStream file, List<TransformerSelector> transformSelectors, TransformerFactory transformerFactory) throws ModuleException, IOException {
    	try {
    		String name = sourcePath.getStreamName(file);
    		logger.finer("reading: " + name);
    		Document doc = getSource(ctx, file);
    		Transformer transformer = getTransformer(ctx, transformerFactory, transformSelectors, doc);
    		if (transformer == null) {
    			processingException("could not find transformer for " + name);
    		}
    		transformer.reset();
	        if (stringParams != null) {
	            for (Map.Entry<String,String> e : stringParams.entrySet()) {
	                transformer.setParameter(e.getKey(), e.getValue());
	            }
	        }
    		transformer.setParameter(SOURCE_PATH_PARAMETER, name);
    		transformer.setParameter(SOURCE_BASENAME_PARAMETER, new File(name).getName());
    		transformer.setParameter(XML_READER_CONTEXT_PARAMETER, new XMLReaderContext(this, corpus));
    		Source source = new DOMSource(doc);
    		doTransform(ctx, transformer, source);
    	}
    	catch (TransformerException|SAXException|ParserConfigurationException e) {
    		rethrow(e);
		}
	}
	
	@SuppressWarnings("static-method")
	@TimeThis(task="transform")
	protected void doTransform(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, Transformer transformer, Source source) throws TransformerException {
		Result result = new SAXResult(new DefaultHandler());
		transformer.transform(source, result);
	}

	@Param
	public SourceStream getXslTransform() {
		return xslTransform;
	}

	@Param(mandatory=false)
	public Mapping getStringParams() {
		return stringParams;
	}

	@Param
	public Boolean getHtml() {
		return html;
	}

	@Param
	public SourceStream getSourcePath() {
		return sourcePath;
	}

	@Param
	public Boolean getRawTagNames() {
		return rawTagNames;
	}

	public void setRawTagNames(Boolean rawTagNames) {
		this.rawTagNames = rawTagNames;
	}

	public void setSourcePath(SourceStream sourcePath) {
		this.sourcePath = sourcePath;
	}

	public void setHtml(Boolean html) {
		this.html = html;
	}

	public void setXslTransform(SourceStream xslTransform) {
		this.xslTransform = xslTransform;
	}

	public void setStringParams(Mapping stringParams) {
		this.stringParams = stringParams;
	}

    


	public static final String concat(NodeList nodes) {
		StringCat strcat = new StringCat();
		for (Node node : XMLUtils.getListOfNodes(nodes))
			strcat.append(node.getTextContent());
		return strcat.toString();
	}
	
	public static final NodeList inline(ExpressionContext ctx) {
		NodeSet nodeSet = new NodeSet();
		Node node = ctx.getContextNode();
		nodeSet.addElement(node);
		return inline(nodeSet);
	}

	public static final NodeList inline(NodeList nodes) {
		NodeSet result = new NodeSet();
		int position = 0;
		for (Node node : XMLUtils.getListOfNodes(nodes))
			position = inline(result, position, node, 0);
		return result;
	}
	
	private static final org.w3c.dom.Element copyNodeAndAttributes(Node node) {
		org.w3c.dom.Element result = document.createElement(node.getNodeName());
		NamedNodeMap attributes = node.getAttributes();
		for (int i = 0; i < attributes.getLength(); ++i) {
			Node a = attributes.item(i);
			String name = a.getNodeName();
			String value = a.getNodeValue();
			result.setAttribute(name, value);
		}
		return result;
	}
        
        private static final org.w3c.dom.Element wrapCPICopy(int position, Node node, int level) {
            
            org.w3c.dom.Node embedded;
            String nodeType;
            switch (node.getNodeType()) {

                case Node.PROCESSING_INSTRUCTION_NODE:                
                    embedded = document.createProcessingInstruction(node.getNodeName(), node.getNodeValue());
                    nodeType = "processing-instruction";
                    break;
                    
                case Node.COMMENT_NODE:
                    embedded = document.createComment(node.getNodeValue());
                    nodeType = "comment";
                    break;
                    
                default:
                    return null;
            }
            
            org.w3c.dom.Element wrapper = document.createElementNS(INLINE_NAMESPACE, WRAPPER_ELMTNAME);
            wrapper.appendChild(embedded);
            wrapper.setAttributeNS(INLINE_NAMESPACE, "embedded-node-type", nodeType);
            wrapper.setAttributeNS(INLINE_NAMESPACE, LEVEL_ATTRNAME, Integer.toString(level));
            wrapper.setAttributeNS(INLINE_NAMESPACE, START_ATTRNAME, Integer.toString(position));
            wrapper.setAttributeNS(INLINE_NAMESPACE, END_ATTRNAME, Integer.toString(position));
            return wrapper;
        }

	private static final int inline(NodeSet result, int position, Node node, int level) {
		short type = node.getNodeType();
		switch (type) {
		case Node.DOCUMENT_NODE:
			org.w3c.dom.Document doc = (org.w3c.dom.Document)node;
			return inline(result, position, doc.getDocumentElement(), level+1);
		case Node.ELEMENT_NODE:
			if (node.getNodeName().isEmpty())
				break;
			org.w3c.dom.Element elt = copyNodeAndAttributes(node);
                        elt.setAttributeNS(INLINE_NAMESPACE, LEVEL_ATTRNAME, Integer.toString(level));
			result.addElement(elt);
			elt.setAttributeNS(INLINE_NAMESPACE, START_ATTRNAME, Integer.toString(position));
			for (Node child : XMLUtils.childrenNodes(node))
				position = inline(result, position, child, level+1);
			elt.setAttributeNS(INLINE_NAMESPACE, END_ATTRNAME, Integer.toString(position));
			return position;
                case Node.COMMENT_NODE:
                case Node.PROCESSING_INSTRUCTION_NODE:
                        result.addElement(wrapCPICopy(position, node, level));
			return position;
		case Node.TEXT_NODE:
		case Node.CDATA_SECTION_NODE:
			return position + node.getNodeValue().length();
		case Node.ENTITY_REFERENCE_NODE:
			for (Node child : XMLUtils.childrenNodes(node))
				position = inline(result, position, child, level+1);
			return position;
		}
		return position;
	}
	
	private static final String getAttributeStringValue(XSLProcessorContext ctx, ElemExtensionCall call, String exprAttr, String valueAttr) throws TransformerException {
		Node node = ctx.getContextNode();
		String expr = call.getAttribute(exprAttr);
		if (expr == null || expr.isEmpty()) {
			return call.getAttribute(valueAttr);
		}
		XPathContext xpctx = ctx.getTransformer().getXPathContext();
		XPath xp = new XPath(expr, call, xpctx.getNamespaceContext(), XPath.SELECT);
		XObject xobj = xp.execute(xpctx, node, call);
		return xobj.str();
	}
	
	private static final int getAttributeIntValue(XSLProcessorContext ctx, ElemExtensionCall call, String attr) throws TransformerException {
		Node node = ctx.getContextNode();
		String expr = call.getAttribute(attr);
		XPathContext xpctx = ctx.getTransformer().getXPathContext();
		XPath xp = new XPath(expr, call, xpctx.getNamespaceContext(), XPath.SELECT);
		XObject xobj = xp.execute(xpctx, node, call);
		return (int) xobj.num();
	}
	
	private static final boolean getAttributeBooleanValue(XSLProcessorContext ctx, ElemExtensionCall call, String attr, boolean defaultValue) throws TransformerException {
		Node node = ctx.getContextNode();
		String expr = call.getAttribute(attr);
		if (expr == null || expr.isEmpty())
			return defaultValue;
		XPathContext xpctx = ctx.getTransformer().getXPathContext();
		XPath xp = new XPath(expr, call, xpctx.getNamespaceContext(), XPath.SELECT);
		XObject xobj = xp.execute(xpctx, node, call);
		String value = xobj.str();
		if (value == null)
			return defaultValue;
		if (value.equals("yes"))
			return true;
		if (value.equals("true"))
			return true;
		if (value.equals("no"))
			return false;
		if (value.equals("false"))
			return false;
		return defaultValue;
	}
	
	public static void document(XSLProcessorContext ctx, ElemExtensionCall call) throws TransformerException {
		TransformerImpl transformer = ctx.getTransformer();
		XMLReaderContext xrctx = (XMLReaderContext) transformer.getParameter(XML_READER_CONTEXT_PARAMETER);
		String id = getAttributeStringValue(ctx, call, "xpath-id", "id");
		xrctx.startDocument(id);
		transformer.executeChildTemplates(call, false);
		xrctx.endDocument();
	}
	
	public static void section(XSLProcessorContext ctx, ElemExtensionCall call) throws TransformerException {
		TransformerImpl transformer = ctx.getTransformer();
		XMLReaderContext xrctx = (XMLReaderContext) transformer.getParameter(XML_READER_CONTEXT_PARAMETER);
		String name = getAttributeStringValue(ctx, call, "xpath-name", "name");
		String contents = getAttributeStringValue(ctx, call, "xpath-contents", "contents");
		boolean referenceScope = getAttributeBooleanValue(ctx, call, "references", true);
		xrctx.startSection(name, contents);
		if (referenceScope)
			xrctx.startRefScope();
		transformer.executeChildTemplates(call, false);
		if (referenceScope)
			xrctx.endRefScope();
		xrctx.endSection();
	}
	
	private static final Pattern WHITESPACE = Pattern.compile("\\s+");
	
	public static void annotation(XSLProcessorContext ctx, ElemExtensionCall call) throws TransformerException {
		TransformerImpl transformer = ctx.getTransformer();
		XMLReaderContext xrctx = (XMLReaderContext) transformer.getParameter(XML_READER_CONTEXT_PARAMETER);
		int start = getAttributeIntValue(ctx, call, START_ATTRNAME);
		int end = getAttributeIntValue(ctx, call, END_ATTRNAME);
		String layers0 = getAttributeStringValue(ctx, call, "xpath-layers", "layers");
		//System.err.println("a: " + start + "-" + end + " (" + layers0 + ")");
		if (layers0 == null)
			return;
		layers0 = layers0.trim();
		if (layers0.isEmpty())
			return;
		String[] layers = WHITESPACE.split(layers0);
		String ref = getAttributeStringValue(ctx, call, "xpath-ref", "ref");
		xrctx.startAnnotation(start, end, layers, ref);
		transformer.executeChildTemplates(call, false);
		xrctx.endAnnotation();
	}
	
	public static final void relation(XSLProcessorContext ctx, ElemExtensionCall call) throws TransformerException {
		TransformerImpl transformer = ctx.getTransformer();
		XMLReaderContext xrctx = (XMLReaderContext) transformer.getParameter(XML_READER_CONTEXT_PARAMETER);
		String name = getAttributeStringValue(ctx, call, "xpath-name", "name");
		xrctx.startRelation(name);
		transformer.executeChildTemplates(call, false);
		xrctx.endRelation();
	}
	
	public static final void tuple(XSLProcessorContext ctx, ElemExtensionCall call) throws TransformerException {
		TransformerImpl transformer = ctx.getTransformer();
		XMLReaderContext xrctx = (XMLReaderContext) transformer.getParameter(XML_READER_CONTEXT_PARAMETER);
		xrctx.startTuple();
		transformer.executeChildTemplates(call, false);
		xrctx.endTuple();
	}
	
	public static final void arg(XSLProcessorContext ctx, ElemExtensionCall call) throws TransformerException {
		TransformerImpl transformer = ctx.getTransformer();
		XMLReaderContext xrctx = (XMLReaderContext) transformer.getParameter(XML_READER_CONTEXT_PARAMETER);
		String role = getAttributeStringValue(ctx, call, "xpath-role", "role");
		String ref = getAttributeStringValue(ctx, call, "xpath-ref", "ref");
		xrctx.setArgument(role, ref);
	}
	
	public static void feature(XSLProcessorContext ctx, ElemExtensionCall call) throws TransformerException {
		TransformerImpl transformer = ctx.getTransformer();
		XMLReaderContext xrctx = (XMLReaderContext) transformer.getParameter(XML_READER_CONTEXT_PARAMETER);
		String name = getAttributeStringValue(ctx, call, "xpath-name", "name");
		String value = getAttributeStringValue(ctx, call, "xpath-value", "value");
		xrctx.setFeature(name, value);
		transformer.executeChildTemplates(call, false);
	}
	
	public static void references(XSLProcessorContext ctx, ElemExtensionCall call) throws TransformerException {
		TransformerImpl transformer = ctx.getTransformer();
		XMLReaderContext xrctx = (XMLReaderContext) transformer.getParameter(XML_READER_CONTEXT_PARAMETER);
		xrctx.startRefScope();
		transformer.executeChildTemplates(call, false);
		xrctx.endRefScope();
	}
}
