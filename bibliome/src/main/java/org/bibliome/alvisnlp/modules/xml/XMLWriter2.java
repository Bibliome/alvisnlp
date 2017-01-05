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


package org.bibliome.alvisnlp.modules.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xml.utils.ListingErrorHandler;
import org.apache.xpath.NodeSet;
import org.bibliome.alvisnlp.converters.expression.parser.ExpressionParser;
import org.bibliome.alvisnlp.converters.expression.parser.ParseException;
import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.alvisnlp.modules.xml.XMLWriter2.XMLWriterResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.defaultmap.DefaultMap;
import org.bibliome.util.files.OutputDirectory;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.AnnotationComparator;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

@AlvisNLPModule(beta=true)
public class XMLWriter2 extends CorpusModule<XMLWriterResolvedObjects> {
	public static final String ALVISNLP_PROXY_NAMESPACE_URI = "http://bilbiome.jouy.inra.fr/alvisnlp/XMLReader2";
	
	private static final String ELEMENT_USER_DATA = "element";
	
	private OutputDirectory outDir;
	private Expression roots;
	private Expression fileName;
	private SourceStream xslTransform;
	private Boolean indent = true;
	
	static class XMLWriterResolvedObjects extends ResolvedObjects {
		private final Evaluator roots;
		private final Evaluator fileName;

		private XMLWriterResolvedObjects(ProcessingContext<Corpus> ctx, XMLWriter2 module) throws ResolverException {
			super(ctx, module);
			roots = rootResolver.resolveNullable(module.roots);
			fileName = module.fileName.resolveExpressions(rootResolver);
			libraryResolver = rootResolver;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesNullable(roots, defaultType);
			fileName.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Override
	protected XMLWriterResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new XMLWriterResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
	    	Logger logger = getLogger(ctx);
	    	EVALUATION_CONTEXT = new EvaluationContext(logger);
			Transformer transformer = getTransformer(ctx);

			XMLWriterResolvedObjects resObj = getResolvedObjects();
	        EvaluationContext evalCtx = new EvaluationContext(logger);
	        
	        outDir.mkdirs();
			for (Element root : Iterators.loop(getRoots(evalCtx, corpus))) {
				transformer.reset();
				
				Document doc = XMLUtils.docBuilder.newDocument();
				doc.setUserData(ELEMENT_USER_DATA, root, null);
//				org.w3c.dom.Element elt = getElementProxy(doc, root);
//				doc.appendChild(elt);
				Source source = new DOMSource(doc);
				
				String fileNameString = resObj.fileName.evaluateString(evalCtx, root);
				File outFile = new File(outDir, fileNameString);
				Result result = new StreamResult(outFile);

				doTransform(ctx, transformer, source, result);
			}
		}
		catch (DOMException|TransformerException|IOException e) {
			rethrow(e);
		}
	}
	
	@SuppressWarnings("static-method")
	@TimeThis(task="transform", category=TimerCategory.EXPORT)
	protected void doTransform(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, Transformer transformer, Source source, Result result) throws TransformerException {
		transformer.transform(source, result);
	}
	
	@TimeThis(task="read-xslt", category=TimerCategory.LOAD_RESOURCE)
	protected Transformer getTransformer(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx) throws IOException, TransformerConfigurationException {
		TransformerFactory factory = TransformerFactory.newInstance();
		try (InputStream is = xslTransform.getInputStream()) {
			Source transformerSource = new StreamSource(is);
			Transformer result = factory.newTransformer(transformerSource);
			result.setErrorListener(new ListingErrorHandler());
			if (indent) {
				result.setOutputProperty(OutputKeys.INDENT, "yes");
				result.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			}
			return result;
		}
	}
	
	private Iterator<Element> getRoots(EvaluationContext evalCtx, Corpus corpus) {
		if (roots == null)
			return Iterators.singletonIterator(corpus);
		XMLWriterResolvedObjects resObj = getResolvedObjects();
		return resObj.roots.evaluateElements(evalCtx, corpus);
	}

	@Param
	public OutputDirectory getOutDir() {
		return outDir;
	}

	@Param
	public Expression getRoots() {
		return roots;
	}

	@Param
	public Expression getFileName() {
		return fileName;
	}

	@Param
	public SourceStream getXslTransform() {
		return xslTransform;
	}

	@Param
	public Boolean getIndent() {
		return indent;
	}

	public void setIndent(Boolean indent) {
		this.indent = indent;
	}

	public void setOutDir(OutputDirectory outDir) {
		this.outDir = outDir;
	}

	public void setRoots(Expression roots) {
		this.roots = roots;
	}

	public void setFileName(Expression fileName) {
		this.fileName = fileName;
	}

	public void setXslTransform(SourceStream xslTransform) {
		this.xslTransform = xslTransform;
	}




	private static org.w3c.dom.Element getElementProxy(Document doc, Element element) {
		org.w3c.dom.Element result = doc.createElementNS(ALVISNLP_PROXY_NAMESPACE_URI, "element");
//		System.err.println("elt: " + element + ", proxy: " + result);
		for (String name : element.getFeatureKeys())
			result.setAttribute(name, element.getLastFeature(name));
		result.setUserData(ELEMENT_USER_DATA, element, null);
		return result;
	}
	
	private static LibraryResolver libraryResolver = null;
	private static final DefaultMap<String,Evaluator> expressionCache = new DefaultMap<String,Evaluator>(true, new WeakHashMap<String,Evaluator>()) {
		@Override
		protected Evaluator defaultValue(String key) {
			Reader r = new StringReader(key);
			ExpressionParser parser = new ExpressionParser(r);
			try {
				return parser.expression().resolveExpressions(libraryResolver);
			}
			catch (ParseException|ResolverException e) {
				throw new IllegalArgumentException(e);
			}
		}
	};
	
	private static EvaluationContext EVALUATION_CONTEXT = null;

	private static final Element getElement(Node node) {
		for (Node n = node; n != null; n = n.getParentNode()) {
			Object result = node.getUserData(ELEMENT_USER_DATA);
			if (result != null)
				return (Element) result;
		}
		return null;
	}
	
	public static final NodeList elements(ExpressionContext ctx, String exprStr) {
		Evaluator expr = expressionCache.safeGet(exprStr);
		NodeSet result = new NodeSet();
		Node node = ctx.getContextNode();
		Document doc = node instanceof Document ? (Document) node : node.getOwnerDocument();
//		System.err.println("node = "  + node);
		Element elt = getElement(node);
		if (elt != null)
			for (Element e : Iterators.loop(expr.evaluateElements(EVALUATION_CONTEXT, elt)))
				result.addElement(getElementProxy(doc, e));
		return result;
	}
	
	public static final String string(ExpressionContext ctx, String exprStr) {
		Evaluator expr = expressionCache.safeGet(exprStr);
		Node node = ctx.getContextNode();
		Element elt = getElement(node);
		if (elt == null)
			return "";
		return expr.evaluateString(EVALUATION_CONTEXT, elt);
	}
	
	public static final int integer(ExpressionContext ctx, String exprStr) {
		Evaluator expr = expressionCache.safeGet(exprStr);
		Node node = ctx.getContextNode();
		Element elt = getElement(node);
		if (elt == null)
			return Integer.MIN_VALUE;
		return expr.evaluateInt(EVALUATION_CONTEXT, elt);
	}
	
	public static final double number(ExpressionContext ctx, String exprStr) {
		Evaluator expr = expressionCache.safeGet(exprStr);
		Node node = ctx.getContextNode();
		Element elt = getElement(node);
		if (elt == null)
			return Double.MIN_VALUE;
		return expr.evaluateDouble(EVALUATION_CONTEXT, elt);
	}
	
	public static final NodeList features(ExpressionContext ctx) {
		NodeSet result = new NodeSet();
		Node node = ctx.getContextNode();
		Element elt = getElement(node);
		if (elt != null) {
			Document doc = node.getOwnerDocument();
			for (String name : elt.getFeatureKeys()) {
				for (String value : elt.getFeature(name)) {
					org.w3c.dom.Element e = doc.createElementNS(ALVISNLP_PROXY_NAMESPACE_URI, "feature");
					e.setAttribute("name", name);
					e.setAttribute("value", value);
					result.addElement(e);
				}
			}
		}
		return result;
	}
	
	private static final class InnerTag {
		private final Node node;
		private final Annotation annotation;
		private final InnerTag parent;
		
		private InnerTag(Node node, Annotation annotation, InnerTag parent) {
			super();
			this.node = node;
			this.annotation = annotation;
			this.parent = parent;
		}
	}
	
	public static final class InlineContext {
		private static final Document document = XMLUtils.docBuilder.newDocument();
		private final Evaluator expression;
		private Section section;
		private String contents;
		private int pos;
		private Layer layer;
		
		private InlineContext(Evaluator expression) {
			super();
			this.expression = expression;
		}

		private boolean init(Node contextNode) {
			section = null;
			contents = null;
			pos = 0;
			layer = null;
			Element elt = getElement(contextNode);
			if (elt == null)
				return false;
			section = DownCastElement.toSection(elt);
			if (section == null)
				return false;
			contents = section.getContents();
			layer = new Layer(section);
			for (Element e : Iterators.loop(expression.evaluateElements(EVALUATION_CONTEXT, elt))) {
				Annotation a = DownCastElement.toAnnotation(e);
				if (a == null)
					continue;
				layer.add(a);
			}
			layer.removeOverlaps(AnnotationComparator.byLength, false, false, true);
			return true;
		}
		
		private void makeText(Node parent, int pos) {
			if (pos <= this.pos)
				return;
			Text text = document.createTextNode(contents.substring(this.pos, pos));
			parent.appendChild(text);
			this.pos = pos;
		}
		
		private InnerTag proceed(InnerTag inner, Annotation a) {
			makeText(inner.node, a.getStart());
			Node n = getElementProxy(document, a);
			inner.node.appendChild(n);
			return new InnerTag(n, a, inner);
		}

		private NodeList inline() {
			DocumentFragment top = document.createDocumentFragment();
			InnerTag inner = new InnerTag(top, null, null);
			for (Annotation a : layer) {
				int end = a.getEnd();
				while (true) {
					if (inner.annotation == null)
						break;
					if (end <= inner.annotation.getEnd())
						break;
					inner = finish(inner);
				}
				inner = proceed(inner, a);
			}

			while (inner != null)
				inner = finish(inner);
			assert contents.length() == pos;

			return top.getChildNodes();
		}

		private InnerTag finish(InnerTag inner) {
			int end;
			if (inner.annotation == null)
				end = section.getContents().length();
			else
				end = inner.annotation.getEnd();
			makeText(inner.node, end);
			return inner.parent;
		}
	}
	
	private static final DefaultMap<String,InlineContext> inlineCache = new DefaultMap<String,InlineContext>(true, new WeakHashMap<String,InlineContext>()) {
		@Override
		protected InlineContext defaultValue(String key) {
			return new InlineContext(expressionCache.safeGet(key));
		}
	};

	public static final NodeList inline(ExpressionContext ctx, String exprStr) {
		InlineContext inlineContext = inlineCache.safeGet(exprStr);
		if (inlineContext.init(ctx.getContextNode()))
			return inlineContext.inline();
		return new NodeSet();
	}
}
