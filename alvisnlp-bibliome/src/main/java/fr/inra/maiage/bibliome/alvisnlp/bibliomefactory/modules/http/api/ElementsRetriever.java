package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ArgumentElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;

abstract class ElementsRetriever<P extends Element,I extends Element> extends ItemsRetriever<P,I> {
	ElementsRetriever(ElementType<P> parentType) {
		super(parentType);
	}

	@Override
	protected JSONObject convert(I item) {
		return ElementToJSONConverter.convert(item);
	}

	static final ItemsRetriever<Corpus,Document> CORPUS_DOCUMENTS = new ElementsRetriever<Corpus,Document>(ElementType.CORPUS) {
		@Override
		protected Iterator<Document> getIterator(Map<String,String> params, Corpus parent) {
			return parent.documentIterator();
		}
	};

	static final ItemsRetriever<Document,Section> DOCUMENT_SECTIONS = new ElementsRetriever<Document,Section>(ElementType.DOCUMENT) {
		@Override
		protected Iterator<Section> getIterator(Map<String,String> params, Document parent) {
			return parent.sectionIterator();
		}
	};

	static final ItemsRetriever<Section,Annotation> SECTION_ANNOTATIONS = new ElementsRetriever<Section,Annotation>(ElementType.SECTION) {
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
	
	static final ItemsRetriever<Section,Relation> SECTION_RELATIONS = new ElementsRetriever<Section,Relation>(ElementType.SECTION) {
		@Override
		protected Iterator<Relation> getIterator(Map<String, String> params, Section parent) throws CorpusDataException {
			return parent.getAllRelations().iterator();
		}
	};
	
	static final ItemsRetriever<Relation,Tuple> RELATION_TUPLES = new ElementsRetriever<Relation,Tuple>(ElementType.RELATION) {
		@Override
		protected Iterator<Tuple> getIterator(Map<String, String> params, Relation parent) throws CorpusDataException {
			return parent.getTuples().iterator();
		}
	};
	
	static final ItemsRetriever<Tuple,ArgumentElement> TUPLE_ARGUMENTS = new ItemsRetriever<Tuple,ArgumentElement>(ElementType.TUPLE) {
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
			JSONObject result = ElementToJSONConverter.convert(arg);
			String role = item.getRole();
			result.put("role", role);
			return result;
		}
	};
	
	static final ItemsRetriever<Element,Element> ELEMENT_ANCESTORS = new ElementsRetriever<Element,Element>(ElementType.ANY) {
		@Override
		protected Iterator<Element> getIterator(Map<String,String> params, Element parent) throws Exception {
			List<Element> result = new LinkedList<Element>();
			Element elt = parent;
			while (true) {
				Element ancestor = elt.getParent();
				if (ancestor == elt) {
					break;
				}
				result.add(0, ancestor);
				elt = ancestor;
			}
			return result.iterator();
		}
	};
}
