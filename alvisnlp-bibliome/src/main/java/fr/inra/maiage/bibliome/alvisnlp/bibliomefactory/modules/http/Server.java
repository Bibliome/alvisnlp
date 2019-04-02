package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.converters.expression.parser.ExpressionParser;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.converters.expression.parser.ParseException;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.standard.NavigationLibrary;
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
import fr.inra.maiage.bibliome.util.Strings;

public class Server extends NanoHTTPD {
	private final Logger logger;
	private final Corpus corpus;
	private final Map<String,Element> cache = new HashMap<String,Element>();
	private final ExpressionParser expressionParser = new ExpressionParser((Reader) null);
	private final LibraryResolver librearyResolver;

	public Server(int port, Logger logger, Corpus corpus, LibraryResolver librearyResolver) {
		super(port);
		this.logger = logger;
		this.corpus = corpus;
		this.librearyResolver = librearyResolver;
	}

	@Override
	public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
		if (method != Method.GET) {
			return createBadRequestResponse("server only processes GET requests (not " + method + ")");
		}
		String uri = session.getUri();
		logger.fine("request URI: " + uri);
		List<String> path = parseUri(uri);
		if (path.isEmpty()) {
			return createDefaultResponse(session);
		}
		String cmd = path.remove(0);
		switch (cmd) {
			case "index.html":
			case "index.htm":
			case "home":
				return createDefaultResponse(session);
			case "api":
				return createAPIResponse(session, path);
		}
		return createNotFoundResponse(session);
	}

	private static List<String> parseUri(String uri) {
		List<String> path = Strings.split(uri, '/', -1);
		List<String> result = new LinkedList<String>();
		for (String s: path) {
			if (!s.isEmpty()) {
				result.add(s);
			}
		}
		return result;
	}
	
	private static Response createDefaultResponse(IHTTPSession session) {
		return showSession(session);
	}
	
	private static Response showSession(IHTTPSession session) {
        StringBuilder msg = new StringBuilder("<html><body><h1>Hello server</h1>\n");
        single(msg, "URI", "uri", session.getUri());
        single(msg, "Method", "method", session.getMethod().toString());
        multiple(msg, "Headers", session.getHeaders());
        multiple(msg, "Params", session.getParms());
        msg.append("</body></html>\n");
        return newFixedLengthResponse(msg.toString());
	}
	
	private static void title(StringBuilder sb, String title) {
		sb.append("<h2>");
		sb.append(title);
		sb.append("</h2>");
	}
	
	private static void openTable(StringBuilder sb) {
		sb.append("<table>");
	}
	
	private static void closeTable(StringBuilder sb) {
		sb.append("</table>");
	}
	
	private static void row(StringBuilder sb, String key, String value) {
		sb.append("<tr><th>");
		sb.append(key);
		sb.append("</th><td>");
		sb.append(value);
		sb.append("</td></tr>");
	}
	
	private static void single(StringBuilder sb, String title, String key, String value) {
		title(sb, title);
		openTable(sb);
		row(sb, key, value);
		closeTable(sb);
	}
	
	private static void multiple(StringBuilder sb, String title, Map<String,String> map) {
		title(sb, title);
		openTable(sb);
		for (Map.Entry<String,String> e : map.entrySet()) {
			row(sb, e.getKey(), e.getValue());
		}
		closeTable(sb);
	}
	
	private Response createNotFoundResponse(IHTTPSession session) {
		logger.info("not found: " + session.getUri());
		return newFixedLengthResponse(Status.NOT_FOUND, MIME_PLAINTEXT, "");		
	}
	
	private Response createBadRequestResponse(String msg) {
		logger.info("bad request: " + msg);
		return newFixedLengthResponse(Status.BAD_REQUEST, MIME_PLAINTEXT, msg);
	}
	
	private static Response createJSONResponse(Object obj) {
		return newFixedLengthResponse(Status.OK, "application/json", obj.toString());
	}
	
	private Response createAPIResponse(IHTTPSession session, List<String> path) {
		if (path.isEmpty()) {
			return createNotFoundResponse(session);
		}
		String cmd = path.remove(0);
		switch (cmd) {
			case "features":
				return getAPIFeaturesResponse(session, path);
			case "documents":
				return getAPIDocumentsResponse(session, path);
			case "sections":
				return getAPISectionsResponse(session, path);
			case "layers":
				return getAPILayersResponse(session, path);
			case "annotations":
				return getAPIAnnotationsResponse(session, path);
			case "relations":
				return getAPIRelationsResponse(session, path);
			case "tuples":
				return getAPITuplesResponse(session, path);
			case "arguments":
				return getAPIArgumentsResponse(session, path);
			case "evaluate":
				return getAPIEvaluateResponse(session, path);
			default:
				return createNotFoundResponse(session);
		}
	}

	private Response getAPIFeaturesResponse(IHTTPSession session, List<String> path) {
		try {
			if (!path.isEmpty()) {
				return createNotFoundResponse(session);
			}
			Map<String,String> params = session.getParms();
			Element elt = getElement(params);
			JSONObject jFeatures = ElementToJSONConverter.convertFeatures(elt);
			return createJSONResponse(jFeatures);
		}
		catch (CorpusDataException e) {
			return createBadRequestResponse(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private Response getAPIDocumentsResponse(IHTTPSession session, List<String> path) {
		try {
			if (!path.isEmpty()) {
				return createNotFoundResponse(session);
			}
			Map<String,String> params = session.getParms();
			Element elt = getElement(params);
			Corpus corpus = DownCastElement.toCorpus(elt);
			if (corpus == null) {
				return createBadRequestResponse("element is not a corpus: " + params.get("uid"));
			}
			JSONArray jDocs = new JSONArray();
			ElementToJSONConverter converter = new ElementToJSONConverter();
			for (Document doc : Iterators.loop(corpus.documentIterator())) {
				JSONObject jDoc = doc.accept(converter, null);
				jDocs.add(jDoc);
			}
			return createJSONResponse(jDocs);
		}
		catch (CorpusDataException e) {
			return createBadRequestResponse(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private Response getAPISectionsResponse(IHTTPSession session, List<String> path) {
		try {
			if (!path.isEmpty()) {
				return createNotFoundResponse(session);
			}
			Map<String,String> params = session.getParms();
			Element elt = getElement(params);
			Document doc = DownCastElement.toDocument(elt);
			if (doc == null) {
				return createBadRequestResponse("element is not a document: " + params.get("uid"));
			}
			JSONArray jSections = new JSONArray();
			ElementToJSONConverter converter = new ElementToJSONConverter();
			for (Section sec : Iterators.loop(doc.sectionIterator())) {
				JSONObject jSec = sec.accept(converter, null);
				jSections.add(jSec);
			}
			return createJSONResponse(jSections);
		}
		catch (CorpusDataException e) {
			return createBadRequestResponse(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	private Response getAPILayersResponse(IHTTPSession session, List<String> path) {
		try {
			if (!path.isEmpty()) {
				return createNotFoundResponse(session);
			}
			Map<String,String> params = session.getParms();
			Element elt = getElement(params);
			Section sec = DownCastElement.toSection(elt);
			if (sec == null) {
				return createBadRequestResponse("element is not a section: " + params.get("uid"));
			}
			JSONArray layerNames = new JSONArray();
			for (Layer layer : sec.getAllLayers()) {
				String layerName = layer.getName();
				layerNames.add(layerName);
			}
			return createJSONResponse(layerNames);
		}
		catch (CorpusDataException e) {
			return createBadRequestResponse(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private Response getAPIAnnotationsResponse(IHTTPSession session, List<String> path) {
		try {
			if (!path.isEmpty()) {
				return createNotFoundResponse(session);
			}
			Map<String,String> params = session.getParms();
			Element elt = getElement(params);
			Section sec = DownCastElement.toSection(elt);
			if (sec == null) {
				return createBadRequestResponse("element is not a section: " + params.get("uid"));
			}
			JSONArray jAnnotations = new JSONArray();
			ElementToJSONConverter converter = new ElementToJSONConverter();
			for (Annotation a : getLayer(params, sec)) {
				JSONObject jA = a.accept(converter, null);
				jAnnotations.add(jA);
			}
			return createJSONResponse(jAnnotations);
		}
		catch (CorpusDataException e) {
			return createBadRequestResponse(e.getMessage());
		}
	}
	
	private static Layer getLayer(Map<String,String> params, Section sec) throws CorpusDataException {
		if (params.containsKey("layer")) {
			String layerName = params.get("layer");
			if (sec.hasLayer(layerName)) {
				return sec.getLayer(layerName);
			}
			throw new CorpusDataException("no layer " + layerName);
		}
		return sec.getAllAnnotations();
	}

	@SuppressWarnings("unchecked")
	private Response getAPIRelationsResponse(IHTTPSession session, List<String> path) {
		try {
			if (!path.isEmpty()) {
				return createNotFoundResponse(session);
			}
			Map<String,String> params = session.getParms();
			Element elt = getElement(params);
			Section sec = DownCastElement.toSection(elt);
			if (sec == null) {
				return createBadRequestResponse("element is not a section: " + params.get("uid"));
			}
			JSONArray jRelations = new JSONArray();
			ElementToJSONConverter converter = new ElementToJSONConverter();
			for (Relation rel : sec.getAllRelations()) {
				JSONObject jRel = rel.accept(converter, null);
				jRelations.add(jRel);
			}
			return createJSONResponse(jRelations);
		}
		catch (CorpusDataException e) {
			return createBadRequestResponse(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private Response getAPITuplesResponse(IHTTPSession session, List<String> path) {
		try {
			if (!path.isEmpty()) {
				return createNotFoundResponse(session);
			}
			Map<String,String> params = session.getParms();
			Element elt = getElement(params);
			Relation rel = DownCastElement.toRelation(elt);
			if (rel == null) {
				return createBadRequestResponse("element is not a relation: " + params.get("uid"));
			}
			JSONArray jTuples = new JSONArray();
			ElementToJSONConverter converter = new ElementToJSONConverter();
			for (Tuple t : rel.getTuples()) {
				JSONObject jT = t.accept(converter, null);
				jTuples.add(jT);
			}
			return createJSONResponse(jTuples);
		}
		catch (CorpusDataException e) {
			return createBadRequestResponse(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private Response getAPIArgumentsResponse(IHTTPSession session, List<String> path) {
		try {
			if (!path.isEmpty()) {
				return createNotFoundResponse(session);
			}
			Map<String,String> params = session.getParms();
			Element elt = getElement(params);
			Tuple t = DownCastElement.toTuple(elt);
			if (t == null) {
				return createBadRequestResponse("element is not a tuple: " + params.get("uid"));
			}
			JSONObject jArgs = new JSONObject();
			ElementToJSONConverter converter = new ElementToJSONConverter();
			for (String role : t.getRoles()) {
				Element arg = t.getArgument(role);
				JSONObject jArg = arg.accept(converter, null);
				jArgs.put(role, jArg);
			}
			return createJSONResponse(jArgs);
		}
		catch (CorpusDataException e) {
			return createBadRequestResponse(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private Response getAPIEvaluateResponse(IHTTPSession session, List<String> path) {
		try {
			if (!path.isEmpty()) {
				return createNotFoundResponse(session);
			}
			Map<String,String> params = session.getParms();
			Element elt = getElement(params);
			Evaluator eval = getEvaluator(params);
			EvaluationContext ctx = new EvaluationContext(logger);
			Iterator<Element> it = eval.evaluateElements(ctx, elt);
			JSONArray jElts = new JSONArray();
			ElementToJSONConverter converter = new ElementToJSONConverter();
			for (Element e : Iterators.loop(it)) {
				JSONObject jE = e.accept(converter, null);
				jElts.add(jE);
			}
			return createJSONResponse(jElts);
		}
		catch (ResolverException|CorpusDataException|ParseException e) {
			return createBadRequestResponse(e.getMessage());
		}
	}
	
	private Evaluator getEvaluator(Map<String,String> params) throws ResolverException, ParseException {
		Expression expr = getExpression(params);
		return expr.resolveExpressions(librearyResolver);
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
	
	@SuppressWarnings("serial")
	private static class CorpusDataException extends Exception {
		private CorpusDataException(String msg) {
			super(msg);
		}
	}

	private static boolean hasUID(Element elt, String uid) {
		return elt.getStringId().equals(uid);
	}
}
 