package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
		if (cmd.equals("treeview")) {
			return treeviewResponse(session);
		}
		ItemsRetriever<?,?> retriever = getRetriever(cmd);
		if (retriever == null) {
			return createNotFoundResponse(session);
		}
		return createResponse(session, path, retriever);
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
	
	private Response treeviewResponse(IHTTPSession session) throws CorpusDataException {
		return createJSONResponse(treeviewChildren(session));
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray treeviewChildren(IHTTPSession session) throws CorpusDataException {
		JSONArray result = new JSONArray();
		Map<String,String> params = session.getParms();
		if (!params.containsKey("parentId")) {
			JSONObject jCorpus = corpus.accept(ElementToTreeviewJSONConverter.INSTANCE, null);
			result.add(jCorpus);
			return result;
		}
		String id = params.get("parentId");
		int dash = id.indexOf('-');
		String ftor = id.substring(0, dash);
		switch (ftor) {
			case "children": {
				String eltId = id.substring(dash+1);
				Element elt = getElement(eltId);
				return elt.accept(ElementToTreeviewChildrenJSONConverter.INSTANCE, result);
			}
			case "features": {
				String eltId = id.substring(dash+1);
				Element elt = getElement(eltId);
				for (String key : elt.getFeatureKeys()) {
					String value = elt.getLastFeature(key);
					JSONObject jFeat = new JSONObject();
					jFeat.put("id", String.format("values-%s-%s", elt.getStringId(), key));
					jFeat.put("text", String.format("<span class=\"feature-node\"><span class=\"feature-key\">%s</span>: <span class=\"feature-value\">%s</span></span>", key, value));
					jFeat.put("hasChildren", false);
					result.add(jFeat);
				}
				return result;
			}
			case "annotations": {
				String info = id.substring(dash+1);
				int dash2 = info.indexOf("-");
				String eltId = info.substring(0, dash2);
				Element elt = getElement(eltId);
				Section sec = DownCastElement.toSection(elt);
				String layerName = info.substring(dash2+1);
				Layer layer = sec.getLayer(layerName);
				for (Annotation a : layer) {
					JSONObject jA = a.accept(ElementToTreeviewJSONConverter.INSTANCE, null);
					result.add(jA);
				}
				return result;
			}
		}
		throw new CorpusDataException("unknown functor: " + ftor);
	}
		
	@SuppressWarnings("unchecked")
	private <P extends Element,I> Response createResponse(IHTTPSession session, List<String> path, ItemsRetriever<P,I> retriever) throws Exception {
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
}
