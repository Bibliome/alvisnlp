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


package org.bibliome.alvisnlp.modules.INIST;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
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
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

/**
 * This module implementation is a duplicate of XMLWriter2, with the difference that it
 * does NOT use Layer to store Annotation.
 *
 * This module is intended to output, with minimum loss, the content of arbitrary
 * XML file (read by XMLReader2) enriched by user's annotations coming from 
 * AlvisAE. 
 * This implementation is able to preserve exact location of null sized 
 * annotations (from the XML only) thanks to 2 extra features, position and level, 
 * which must be set when the XML file is read.
 * Currently, level is automatically set by XMLReader2, but position must be set 
 * thanks to the XSLT style sheet used by XMLReader2
 *
 * @author fpapazian
 */
@AlvisNLPModule(beta = true)
public class XMLWriter2ForINIST extends CorpusModule<ResolvedObjects> {

    public static final String ALVISNLP_PROXY_NAMESPACE_URI = "http://bilbiome.jouy.inra.fr/alvisnlp/XMLReader2";
    private static final String ELEMENT_USER_DATA = "element";

    private static Integer getIntegerFeatureOrNull(Annotation annotation, String key) {
        Integer value = null;
        if (annotation != null && annotation.hasFeature(key)) {
            try {
                value = Integer.valueOf(annotation.getFeature(key).get(0));
            } catch (NumberFormatException e) {
            }
        }
        return value;
    }

    public static class ByOrderAndPostionAnnotationComparator implements Comparator<Annotation> {

        Map<Annotation, Integer> position = new HashMap<Annotation, Integer>();

        @Override
        public int compare(Annotation a1, Annotation a2) {
            int s1 = a1.getStart();
            int s2 = a2.getStart();
            if (s1 != s2) {
                return s1 - s2;
            }

            Integer pos1;
            if (position.containsKey(a1)) {
                pos1 = position.get(a1);
            } else {
                pos1 = getIntegerFeatureOrNull(a1, "position");
                position.put(a1, pos1);
            }
            Integer pos2;
            if (position.containsKey(a2)) {
                pos2 = position.get(a2);
            } else {
                pos2 = getIntegerFeatureOrNull(a2, "position");
                position.put(a2, pos2);
            }

            //Note: if both positions are set, use them to determine order whatever the size of the annotation
            //Indeed, Comment or Processing-instructions have start==end, because their content is outside the document text!!
            if (pos1 != null && pos2 != null) {

                return pos1 - pos2;

            } else {
                int e1 = a1.getEnd();
                int e2 = a2.getEnd();

                if (e1 != e2) {

                    return e2 - e1;

                } else if (pos1 != null) {

                    return -1;

                } else if (pos2 != null) {

                    return 1;

                } else {
                    //2 annotations not coming from XML with exact same coordinate
                    //return Integer.compare(a1.hashCode(), a2.hashCode());
                    return 0;
                }
            }

        }
    }
    private OutputDirectory outDir;
    private Expression roots;
    private Expression fileName;
    private SourceStream xslTransform;

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
    public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
        try {
        	Logger logger = getLogger(ctx);
            TransformerFactory factory = TransformerFactory.newInstance();
            InputStream is = xslTransform.getInputStream();
            Source transformerSource = new StreamSource(is);
            Transformer transformer = factory.newTransformer(transformerSource);
            is.close();
            transformer.setErrorListener(new ListingErrorHandler());

            libraryResolver = getLibraryResolver(ctx);
            Evaluator roots = libraryResolver.resolveNullable(this.roots);
            Evaluator fileName = this.fileName.resolveExpressions(libraryResolver);
            EvaluationContext evalCtx = new EvaluationContext(logger);

            for (Element root : Iterators.loop(getRoots(roots, evalCtx, corpus))) {
                transformer.reset();

                Document doc = XMLUtils.docBuilder.newDocument();
                doc.setUserData(ELEMENT_USER_DATA, root, null);
//				org.w3c.dom.Element elt = getElementProxy(doc, root);
//				doc.appendChild(elt);
                Source source = new DOMSource(doc);

                String fileNameString = fileName.evaluateString(evalCtx, root);
//                System.out.println("creating file: " + fileNameString);
                File outFile = new File(outDir, fileNameString);
                Result result = new StreamResult(outFile);

                transformer.transform(source, result);
            }
        } catch (DOMException | TransformerException | IOException e) {
            rethrow(e);
        }
    }

    private static Iterator<Element> getRoots(Evaluator roots, EvaluationContext evalCtx, Corpus corpus) {
        if (roots == null) {
            return Iterators.singletonIterator(corpus);
        }
        return roots.evaluateElements(evalCtx, corpus);
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
        for (String name : element.getFeatureKeys()) {
            result.setAttribute(name, element.getLastFeature(name));
        }
        result.setUserData(ELEMENT_USER_DATA, element, null);
        return result;
    }
    private static LibraryResolver libraryResolver = null;
    private static final DefaultMap<String, Evaluator> expressionCache = new DefaultMap<String, Evaluator>(true, new WeakHashMap<String, Evaluator>()) {
        @Override
        protected Evaluator defaultValue(String key) {
            Reader r = new StringReader(key);
            ExpressionParser parser = new ExpressionParser(r);
            try {
                return parser.expression().resolveExpressions(libraryResolver);
            } catch (ParseException | ResolverException e) {
                throw new IllegalArgumentException(e);
            }
        }
    };
    private static EvaluationContext EVALUATION_CONTEXT = null;

    private static final Element getElement(Node node) {
        for (Node n = node; n != null; n = n.getParentNode()) {
            Object result = node.getUserData(ELEMENT_USER_DATA);
            if (result != null) {
                return (Element) result;
            }
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
        if (elt != null) {
            for (Element e : Iterators.loop(expr.evaluateElements(EVALUATION_CONTEXT, elt))) {
                result.addElement(getElementProxy(doc, e));
            }
        }
        return result;
    }

    public static final String string(ExpressionContext ctx, String exprStr) {
        Evaluator expr = expressionCache.safeGet(exprStr);
        Node node = ctx.getContextNode();
        Element elt = getElement(node);
        if (elt == null) {
            return "";
        }
        return expr.evaluateString(EVALUATION_CONTEXT, elt);
    }

    public static final int integer(ExpressionContext ctx, String exprStr) {
        Evaluator expr = expressionCache.safeGet(exprStr);
        Node node = ctx.getContextNode();
        Element elt = getElement(node);
        if (elt == null) {
            return Integer.MIN_VALUE;
        }
        return expr.evaluateInt(EVALUATION_CONTEXT, elt);
    }

    public static final double number(ExpressionContext ctx, String exprStr) {
        Evaluator expr = expressionCache.safeGet(exprStr);
        Node node = ctx.getContextNode();
        Element elt = getElement(node);
        if (elt == null) {
            return Double.MIN_VALUE;
        }
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
        private List<Annotation> layer;

        private InlineContext(Evaluator expression) {
            super();
            this.expression = expression;
        }

        private boolean init(Node contextNode) {
//            System.out.println("");
            section = null;
            contents = null;
            pos = 0;
            layer = null;
            Element elt = getElement(contextNode);
            if (elt == null) {
                return false;
            }
            section = DownCastElement.toSection(elt);
            if (section == null) {
                return false;
            }
            contents = section.getContents();
            layer = new LinkedList<Annotation>();
            for (Element e : Iterators.loop(expression.evaluateElements(EVALUATION_CONTEXT, elt))) {
                Annotation a = DownCastElement.toAnnotation(e);
                if (a == null) {
                    continue;
                }
                layer.add(a);
            }

            Collections.sort(layer, new ByOrderAndPostionAnnotationComparator());
            //layer.removeOverlaps(AnnotationComparator.byLength, false, false, true);
            return true;
        }

        private void makeText(Node parent, int pos) {
            if (pos <= this.pos) {
                return;
            }
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

                @SuppressWarnings("unused") // XXX
				Integer position = getIntegerFeatureOrNull(a, "position");
                int annotationSize = end - a.getStart();
                Integer annotationLevel = getIntegerFeatureOrNull(a, "level");
                //
//                String annotationType = "<?>";
//                if (a.hasFeature("tag")) {
//                    annotationType = "<" + a.getFeature("tag").get(0) + ">";
//                }
//                if (a.hasFeature("type")) {
//                    annotationType = "[" + a.getFeature("type").get(0) + "]";
//                }
//                System.out.print("Placing " + annotationType + " pos" + position + " lvl(" + annotationLevel + ")");
//                System.out.println("[" + a.getStart() + ", " + a.getEnd() + "]" + " s=" + annotationSize);
                //

                while (true) {
                    if (inner.annotation == null) {
                        break;
                    }

                    if (end <= inner.annotation.getEnd()) {

                        //
                        Integer innerLevel = getIntegerFeatureOrNull(inner.annotation, "level");
                        Integer atOrBelowlevel = getIntegerFeatureOrNull(inner.annotation, "atOrBelow");

                        //annotation element must be above the current annotation (specific handling when having a null sized annotation which can not be located precisely by character position only)
                        if ((annotationLevel == null)
                                || (innerLevel == null && annotationSize > 0)
                                || (innerLevel == null && atOrBelowlevel == null)
                                || (innerLevel == null && atOrBelowlevel != null && annotationSize == 0 && atOrBelowlevel <= annotationLevel)
                                || (innerLevel != null && innerLevel < annotationLevel)) {

//                            System.out.println("\tSelected Inner pos:" + getIntegerFeatureOrNull(inner.annotation, "position") + " lvl(" + innerLevel + ")" + " @" + atOrBelowlevel);

                            break;
                        } else {
//                            System.out.println("\tSkipped Inner pos:" + getIntegerFeatureOrNull(inner.annotation, "position") + " lvl(" + innerLevel + ")" + " @" + atOrBelowlevel);
                        }
                    }
                    //search for higher element to check if it is the one englobing the current annotation
                    inner = finish(inner);
                }
                
                {
                    Annotation innerAnnotation = inner.annotation;
                    if (innerAnnotation != null) {
                        Integer innerLevel = getIntegerFeatureOrNull(innerAnnotation, "level");
                        //
//                        String innerType = "<?>";
//                        if (innerAnnotation.hasFeature("tag")) {
//                            innerType = "<" + innerAnnotation.getFeature("tag").get(0) + ">";
//                        }
//                        if (innerAnnotation.hasFeature("type")) {
//                            innerType = "[" + innerAnnotation.getFeature("type").get(0) + "]";
//                        }
//                        System.out.print("==> Inner " + innerType + " (" + innerLevel + ")");
//                        System.out.println(" [" + innerAnnotation.getStart() + ", " + innerAnnotation.getEnd() + "]");
                        //

                        //NOTE: Although user's annotation have no level feature, once they are attached to a parent node, their actual level is necessary to correctly put in other annotations such as null sized ones coming from XML
                        if (!a.hasFeature("level") && innerLevel != null) {
                            a.addFeature("atOrBelow", String.valueOf(innerLevel + 1));
                        }
                    }
                }

                //make annotation a child on inner
                inner = proceed(inner, a);
//                System.out.println("Proceed with Inner:" + getIntegerFeatureOrNull(inner.annotation, "position") + "(" + getIntegerFeatureOrNull(inner.annotation, "level") + ")");
//                System.out.println();
            }

            while (inner != null) {
                inner = finish(inner);
            }
            assert contents.length() == pos;

            return top.getChildNodes();
        }

        private InnerTag finish(InnerTag inner) {
            int end;
            if (inner.annotation == null) {
                end = section.getContents().length();
            } else {
                end = inner.annotation.getEnd();
            }
            makeText(inner.node, end);
            return inner.parent;
        }
    }
    private static final DefaultMap<String, InlineContext> inlineCache = new DefaultMap<String, InlineContext>(true, new WeakHashMap<String, InlineContext>()) {
        @Override
        protected InlineContext defaultValue(String key) {
            return new InlineContext(expressionCache.safeGet(key));
        }
    };

    public static final NodeList inline(ExpressionContext ctx, String exprStr) {
        InlineContext inlineContext = inlineCache.safeGet(exprStr);
        if (inlineContext.init(ctx.getContextNode())) {
            return inlineContext.inline();
        }
        return new NodeSet();
    }
}
