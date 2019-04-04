package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
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
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ArgumentElement;
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

class APIResponseFactory extends ResponseFactory {
	private final LibraryResolver libraryResolver;
	private final Corpus corpus;
	private final Map<String,Element> cache = new HashMap<String,Element>();
	private final ExpressionParser expressionParser = new ExpressionParser((Reader) null);

	APIResponseFactory(Logger logger, LibraryResolver librearyResolver, Corpus corpus) {
		super(logger);
		this.libraryResolver = librearyResolver;
		this.corpus = corpus;
	}

	@Override
	protected Response createResponse(IHTTPSession session, List<String> path) {
		if (path.isEmpty()) {
			return createNotFoundResponse(session);
		}
		String cmd = path.remove(0);
		ItemsRetriever<?,?> retriever = getRetriever(cmd);
		if (retriever == null) {
			return createNotFoundResponse(session);
		}
		return retriever.getResponse(session, path);
	}
	
	private ItemsRetriever<?,?> getRetriever(String cmd) {
		switch (cmd) {
			case "features":
				return featuresRetriever;
			case "documents":
				return documentsRetriever;
			case "sections":
				return sectionsRetriever;
			case "layers":
				return layersRetriever;
			case "annotations":
				return annotationsRetriever;
			case "relations":
				return relationsRetriever;
			case "tuples":
				return tuplesRetriever;
			case "arguments":
				return argumentsRetriever;
			case "evaluate":
				return evaluateRetriever;
			default:
				return null;
		}
	}
	
	private static abstract class RetrieverParentHandler<P extends Element> {
		private final String name;
		
		public RetrieverParentHandler(String name) {
			super();
			this.name = name;
		}

		protected abstract P cast(Element elt);
		
		private String getName() {
			return name;
		}
	}
	
	private static final RetrieverParentHandler<Corpus> CORPUS_HANDLER = new RetrieverParentHandler<Corpus>("corpus") {
		@Override
		protected Corpus cast(Element elt) {
			return DownCastElement.toCorpus(elt);
		}
	};
	
	private static final RetrieverParentHandler<Document> DOCUMENT_HANDLER = new RetrieverParentHandler<Document>("document") {
		@Override
		protected Document cast(Element elt) {
			return DownCastElement.toDocument(elt);
		}
	};
	
	private static final RetrieverParentHandler<Section> SECTION_HANDLER = new RetrieverParentHandler<Section>("section") {
		@Override
		protected Section cast(Element elt) {
			return DownCastElement.toSection(elt);
		}
	};
	
	private static final RetrieverParentHandler<Relation> RELATION_HANDLER = new RetrieverParentHandler<Relation>("relation") {
		@Override
		protected Relation cast(Element elt) {
			return DownCastElement.toRelation(elt);
		}
	};

	private static final RetrieverParentHandler<Tuple> TUPLE_HANDLER = new RetrieverParentHandler<Tuple>("tuple") {
		@Override
		protected Tuple cast(Element elt) {
			return DownCastElement.toTuple(elt);
		}
	};
	
	private static final RetrieverParentHandler<Element> ELEMENT_HANDLER = new RetrieverParentHandler<Element>("element") {
		@Override
		protected Element cast(Element elt) {
			return elt;
		}
	};

	private abstract class ItemsRetriever<P extends Element,I> {
		private final RetrieverParentHandler<P> parentHandler;
		
		private ItemsRetriever(RetrieverParentHandler<P> parentHandler) {
			super();
			this.parentHandler = parentHandler;
		}

		@SuppressWarnings("unchecked")
		private Response getResponse(IHTTPSession session, List<String> path) {
			try {
				if (!path.isEmpty()) {
					return createNotFoundResponse(session);
				}
				Map<String,String> params = session.getParms();
				Element eParent = getElement(params);
				P parent = parentHandler.cast(eParent);
				if (parent == null) {
					return createBadRequestResponse("element is not a " + parentHandler.getName() + ": " + params.get("uid"));
				}
				JSONArray result = new JSONArray();
				for (I item : Iterators.loop(getIterator(params, parent))) {
					JSONObject jItem = convert(item);
					result.add(jItem);
				}
				return createJSONResponse(result);
			}
			catch (Exception e) {
				return createBadRequestResponse(e.getMessage());
			}
		}
		
		protected abstract Iterator<I> getIterator(Map<String,String> params, P parent) throws Exception;
		protected abstract JSONObject convert(I item);
	}
	
	private final ItemsRetriever<Element,Map.Entry<String,List<String>>> featuresRetriever = new ItemsRetriever<Element,Map.Entry<String,List<String>>>(ELEMENT_HANDLER) {
		@Override
		protected Iterator<Map.Entry<String,List<String>>> getIterator(Map<String,String> params, Element parent) throws Exception {
			return parent.getFeatures().entrySet().iterator();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected JSONObject convert(Map.Entry<String,List<String>> item) {
			JSONObject result = new JSONObject();
			String key = item.getKey();
			JSONArray values = new JSONArray();
			values.addAll(item.getValue());
			result.put("key", key);
			result.put("values", values);
			return result;
		}
	};

	private abstract class ElementsRetriever<P extends Element,I extends Element> extends ItemsRetriever<P,I> {
		private ElementsRetriever(RetrieverParentHandler<P> parentHandler) {
			super(parentHandler);
		}

		@Override
		protected JSONObject convert(I item) {
			cache.put(item.getStringId(), item);
			return ElementToJSONConverter.convert(item);
		}
	}
	
	private final ItemsRetriever<Corpus,Document> documentsRetriever = new ElementsRetriever<Corpus,Document>(CORPUS_HANDLER) {
		@Override
		protected Iterator<Document> getIterator(Map<String,String> params, Corpus parent) {
			return parent.documentIterator();
		}
	};

	private final ItemsRetriever<Document,Section> sectionsRetriever = new ElementsRetriever<Document,Section>(DOCUMENT_HANDLER) {
		@Override
		protected Iterator<Section> getIterator(Map<String,String> params, Document parent) {
			return parent.sectionIterator();
		}
	};
	
	private final ItemsRetriever<Section,Layer> layersRetriever = new ItemsRetriever<Section,Layer>(SECTION_HANDLER) {
		@Override
		protected Iterator<Layer> getIterator(Map<String, String> params, Section parent) throws Exception {
			return parent.getAllLayers().iterator();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected JSONObject convert(Layer item) {
			JSONObject result = new JSONObject();
			String layerName = item.getName();
			result.put("name", layerName);
			return result;
		}
	};

	private final ItemsRetriever<Section,Annotation> annotationsRetriever = new ElementsRetriever<Section,Annotation>(SECTION_HANDLER) {
		@Override
		protected Iterator<Annotation> getIterator(Map<String,String> params, Section parent) throws CorpusDataException {
			Layer layer = getLayer(params, parent);
			return layer.iterator();
		}
		
		private Layer getLayer(Map<String,String> params, Section sec) throws CorpusDataException {
			if (params.containsKey("layer")) {
				String layerName = params.get("layer");
				if (sec.hasLayer(layerName)) {
					return sec.getLayer(layerName);
				}
				throw new CorpusDataException("no layer " + layerName);
			}
			return sec.getAllAnnotations();
		}
	};
	
	private final ItemsRetriever<Section,Relation> relationsRetriever = new ElementsRetriever<Section,Relation>(SECTION_HANDLER) {
		@Override
		protected Iterator<Relation> getIterator(Map<String, String> params, Section parent) throws CorpusDataException {
			return parent.getAllRelations().iterator();
		}
	};
	
	private final ItemsRetriever<Relation,Tuple> tuplesRetriever = new ElementsRetriever<Relation,Tuple>(RELATION_HANDLER) {
		@Override
		protected Iterator<Tuple> getIterator(Map<String, String> params, Relation parent) throws CorpusDataException {
			return parent.getTuples().iterator();
		}
	};
	
	private final ItemsRetriever<Tuple,ArgumentElement> argumentsRetriever = new ItemsRetriever<Tuple,ArgumentElement>(TUPLE_HANDLER) {
		@Override
		protected Iterator<ArgumentElement> getIterator(Map<String, String> params, Tuple parent) throws Exception {
			Collection<ArgumentElement> result = new ArrayList<ArgumentElement>();
			for (String role : parent.getRoles()) {
				Element arg = parent.getArgument(role);
				ArgumentElement argElt = new ArgumentElement(parent, role, arg);
				result.add(argElt);
			}
			return result.iterator();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected JSONObject convert(ArgumentElement item) {
			Element arg = item.getArgument();
			cache.put(arg.getStringId(), arg);
			JSONObject result = ElementToJSONConverter.convert(arg);
			String role = item.getRole();
			result.put("role", role);
			return result;
		}
	};
	
	private final ItemsRetriever<Element,Element> evaluateRetriever = new ElementsRetriever<Element,Element>(ELEMENT_HANDLER) {
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
	
	@SuppressWarnings("serial")
	private static class CorpusDataException extends Exception {
		private CorpusDataException(String msg) {
			super(msg);
		}
	}

	private static boolean hasUID(Element elt, String uid) {
		return elt.getStringId().equals(uid);
	}
	
	private static Response createJSONResponse(Object obj) {
		return NanoHTTPD.newFixedLengthResponse(Status.OK, "application/json", obj.toString());
	}
}
