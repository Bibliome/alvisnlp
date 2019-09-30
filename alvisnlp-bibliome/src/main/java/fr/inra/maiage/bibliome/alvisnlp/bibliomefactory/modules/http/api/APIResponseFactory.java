package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.bouncycastle.util.Strings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.converters.expression.parser.ExpressionParser;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.converters.expression.parser.ParseException;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.standard.NavigationLibrary;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.ResponseFactory;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.Server;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.tags.ContentViewCreator;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.tags.ElementDocument;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.treeview.ElementToChildrenTreeviewNodes;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.treeview.TreeviewConstants;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.treeview.TreeviewElementNode;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.treeview.TreeviewFeatureNode;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.treeview.TreeviewNode;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DownCastElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

public class APIResponseFactory extends ResponseFactory {
	private final LibraryResolver libraryResolver;
	private final Corpus corpus;
	private final Map<String,Element> cache = new HashMap<String,Element>();
	private final ExpressionParser expressionParser = new ExpressionParser((Reader) null);

	public APIResponseFactory(Logger logger, LibraryResolver librearyResolver, Corpus corpus) {
		super(logger);
		this.libraryResolver = librearyResolver;
		this.corpus = corpus;
	}

	@Override
	protected Response createResponse(IHTTPSession session, List<String> path) throws Exception {
		if (path.isEmpty()) {
			return createNotFoundResponse(session);
		}
		String cmd = path.remove(0);
		switch (cmd) {
			case "treeview": {
				return treeviewResponse(session);
			}
			case "docinfo": {
				return documentInfoResponse(session);
			}
			case "contentview":{
				return contentviewResponse(session);
			}
			default: {
				ItemsRetriever<?,?> retriever = getRetriever(cmd);
				if (retriever == null) {
					return createNotFoundResponse(session);
				}
				return createRetrieverResponse(session, path, retriever);
			}
		}
	}

	private ItemsRetriever<?,?> getRetriever(String cmd) {
		switch (cmd) {
			case "features":
				return ItemsRetriever.ELEMENT_FEATURES;
			case "corpus":
				return ElementsRetriever.THE_CORPUS;
			case "documents":
				return ElementsRetriever.CORPUS_DOCUMENTS;
			case "sections":
				return ElementsRetriever.DOCUMENT_SECTIONS;
			case "layers":
				return ItemsRetriever.SECTION_LAYERS;
			case "annotations":
				return ElementsRetriever.SECTION_ANNOTATIONS;
			case "relations":
				return ElementsRetriever.SECTION_RELATIONS;
			case "tuples":
				return ElementsRetriever.RELATION_TUPLES;
			case "arguments":
				return ElementsRetriever.TUPLE_ARGUMENTS;
			case "ancestors":
				return ElementsRetriever.ELEMENT_ANCESTORS;
			case "evaluate":
				return evaluateRetriever;
			default:
				return null;
		}
	}
	
	private Response treeviewResponse(IHTTPSession session) throws CorpusDataException, ParseException, ResolverException {
		return createJSONResponse(treeviewChildren(session));
	}
	
	private JSONArray treeviewChildren(IHTTPSession session) throws CorpusDataException, ParseException, ResolverException {
		Map<String,String> params = session.getParms();
		if (!params.containsKey(TreeviewConstants.Parameters.NODE_ID)) {
			return TreeviewElementNode.elementsToJSONArray(Collections.singletonList(corpus));
		}
		String id = params.get(TreeviewConstants.Parameters.NODE_ID);
		String[] split = Strings.split(id, '-');
		String eltId = split[0];
		Element elt = getElement(eltId);
		String ftor = split[1];
		switch (ftor) {
			case TreeviewConstants.NodeIdFunctors.CHILDREN: {
				@SuppressWarnings("rawtypes")
				Collection<TreeviewNode> nodes = ElementToChildrenTreeviewNodes.getChildren(elt);
				return TreeviewNode.nodesToJSONArray(nodes);
			}
			case TreeviewConstants.NodeIdFunctors.FEATURES: {
				@SuppressWarnings("rawtypes")
				Collection<TreeviewNode> nodes = TreeviewFeatureNode.getElementFeatureNodes(elt);
				return TreeviewNode.nodesToJSONArray(nodes);
			}
			case TreeviewConstants.NodeIdFunctors.ANNOTATIONS: {
				Section sec = DownCastElement.toSection(elt);
				String layerName = split[2];
				Layer layer = sec.getLayer(layerName);
				return TreeviewElementNode.elementsToJSONArray(layer);
			}
			case TreeviewConstants.NodeIdFunctors.EVALUATE: {
				String exprString = params.get(TreeviewConstants.Parameters.EXPRESSION);
				expressionParser.ReInit(new StringReader(exprString));
				Expression expr = expressionParser.expression();
				Evaluator eval = expr.resolveExpressions(libraryResolver);
				EvaluationContext ctx = new EvaluationContext(logger);
				Iterator<Element> elts = eval.evaluateElements(ctx, elt);
				return TreeviewElementNode.elementsToJSONArray(Iterators.loop(elts));
			}
		}
		throw new CorpusDataException("unknown functor: " + ftor);
	}

	@SuppressWarnings("unchecked")
	private Response documentInfoResponse(IHTTPSession session) throws CorpusDataException {
		Map<String,String> params = session.getParms();
		if (!params.containsKey(TreeviewConstants.Parameters.ELEMENT_ID)) {
			return createBadRequestResponse("missing parameter " + TreeviewConstants.Parameters.ELEMENT_ID);
		}
		String eltId = params.get(TreeviewConstants.Parameters.ELEMENT_ID);
		Element elt = getElement(eltId);
		Document doc = elt.accept(ElementDocument.INSTANCE, null);
		JSONObject result = new JSONObject();
		if (doc != null) {
			result.put("found", true);
			result.put("id", doc.getId());
			result.put("layers", getDocumentLayers(doc));
		}
		else {
			result.put("found", false);
		}
		return createJSONResponse(result);
	}
	
	@SuppressWarnings("unchecked")
	private static JSONArray getDocumentLayers(Document doc) {
		Set<String> layerNames = new TreeSet<String>();
		for (Section sec : Iterators.loop(doc.sectionIterator())) {
			for (Layer layer : sec.getAllLayers()) {
				layerNames.add(layer.getName());
			}
		}
		JSONArray result = new JSONArray();
		result.addAll(layerNames);
		return result;
	}

	private Response contentviewResponse(IHTTPSession session) throws ParserConfigurationException {
		Map<String,List<String>> params = Server.getArrayParams(session.getQueryParameterString());
		if (!params.containsKey(TreeviewConstants.Parameters.DOCUMENT_ID)) {
			return createBadRequestResponse("missing parameter " + TreeviewConstants.Parameters.DOCUMENT_ID);
		}
		String docId = params.get(TreeviewConstants.Parameters.DOCUMENT_ID).get(0);
		if (!corpus.hasDocument(docId)) {
			return createBadRequestResponse("no document " + docId);
		}
		Document doc = corpus.getDocument(docId);
		List<String> layers = params.containsKey(TreeviewConstants.Parameters.LAYERS) ? params.get(TreeviewConstants.Parameters.LAYERS) : Collections.emptyList();
		ContentViewCreator cvc = new ContentViewCreator("html", "tag");
		cvc.addDocument(doc, layers);
		org.w3c.dom.Document dom = cvc.getDocument();
		StringWriter w = new StringWriter();
		XMLUtils.writeDOMToFile(dom, null, w);
		return createTextResponse(w.toString().replace("\n", "<br/>"));
	}

	@SuppressWarnings("unchecked")
	private <P extends Element,I> Response createRetrieverResponse(IHTTPSession session, List<String> path, ItemsRetriever<P,I> retriever) throws Exception {
		if (!path.isEmpty()) {
			return createNotFoundResponse(session);
		}
		Map<String,String> params = session.getParms();
		Element eParent = getElement(params);
		P parent = retriever.parentType.cast(eParent);
		if (parent == null) {
			return createBadRequestResponse("element is not a " + retriever.parentType.getName() + ": " + params.get("uid"));
		}
		JSONArray result = new JSONArray();
		for (I item : Iterators.loop(retriever.getIterator(params, parent))) {
			JSONObject jItem = retriever.convert(item);
			result.add(jItem);
		}
		return createJSONResponse(result);
	}
	
	private final ItemsRetriever<Element,Element> evaluateRetriever = new ElementsRetriever<Element,Element>(ElementType.ANY) {
		@Override
		protected Iterator<Element> getIterator(Map<String, String> params, Element parent) throws Exception {
			Evaluator eval = getEvaluator(params);
			EvaluationContext ctx = new EvaluationContext(logger);
			return eval.evaluateElements(ctx, parent);
		}
		
		private Evaluator getEvaluator(Map<String,String> params) throws ResolverException, ParseException {
			Expression expr = getExpression(params);
			return expr.resolveExpressions(libraryResolver);
		}

		private Expression getExpression(Map<String,String> params) throws ParseException {
			if (params.containsKey("expr")) {
				String sExpr = params.get("expr");
				logger.fine("expression: " + sExpr);
				return parseExpression(sExpr);
			}
			return new Expression(NavigationLibrary.NAME, "$");
		}

		private Expression parseExpression(String sExpr) throws ParseException {
			expressionParser.ReInit(new StringReader(sExpr));
			return expressionParser.expression();
		}
	};

	private Element getElement(Map<String,String> params) throws CorpusDataException {
		if (params.containsKey("uid")) {
			String uid = params.get("uid");
			return getElement(uid);
		}
		return corpus;
	}

	private Element getElement(String uid) throws CorpusDataException {
		if (cache.containsKey(uid)) {
			return cache.get(uid);
		}
		Element result = lookupElement(uid);
		cache.put(uid, result);
		return result;
	}

	private Element lookupElement(String uid) throws CorpusDataException {
		logger.fine("lookup element: " + uid);
		if (hasUID(corpus, uid)) {
			return corpus;
		}
		for (Document doc : Iterators.loop(corpus.documentIterator())) {
			if (hasUID(doc, uid)) {
				return doc;
			}
			for (Section sec : Iterators.loop(doc.sectionIterator())) {
				if (hasUID(sec, uid)) {
					return sec;
				}
				for (Annotation a : sec.getAllAnnotations()) {
					if (hasUID(a, uid)) {
						return a;
					}
				}
				for (Relation rel : sec.getAllRelations()) {
					if (hasUID(rel, uid)) {
						return rel;
					}
					for (Tuple t : rel.getTuples()) {
						if (hasUID(t, uid)) {
							return t;
						}
					}
				}
			}
		}
		throw new CorpusDataException("element not found: " + uid);
	}
	
	private static boolean hasUID(Element elt, String uid) {
		return elt.getStringId().equals(uid);
	}
	
	private static Response createJSONResponse(Object obj) {
		return NanoHTTPD.newFixedLengthResponse(Status.OK, "application/json", obj.toString());
	}

	private static Response createTextResponse(String s) {
		return NanoHTTPD.newFixedLengthResponse(Status.OK, "text/plain", s);
	}
}
