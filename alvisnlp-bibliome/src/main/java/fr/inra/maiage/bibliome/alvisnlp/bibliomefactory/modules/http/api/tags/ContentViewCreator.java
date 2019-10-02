package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.tags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.maltparser.core.helper.HashSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Pair;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.fragments.FragmentTag;
import fr.inra.maiage.bibliome.util.fragments.FragmentTagComparator;
import fr.inra.maiage.bibliome.util.fragments.FragmentTagIterator;
import fr.inra.maiage.bibliome.util.fragments.FragmentTagType;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

public class ContentViewCreator implements FragmentTagIterator<String,AnnotationInLayer> {
	private final Stack<Pair<AnnotationInLayer,Node>> openedElements = new Stack<Pair<AnnotationInLayer,Node>>();
	private final Document document;
	private final String htmlLayerName;
	private final String htmlTagFeatureKey;
	
	public ContentViewCreator(String htmlLayerName, String htmlTagFeatureKey) throws ParserConfigurationException {
		super();
		this.document = createDocument();
		this.htmlLayerName = htmlLayerName;
		this.htmlTagFeatureKey = htmlTagFeatureKey;
		addPair(null, document);
		Element top = createElement("div", "div-top");
		addPair(null, top);
	}

	private static Document createDocument() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.newDocument();
	}

	public Document getDocument() {
		return document;
	}

	@Override
	public void handleTag(String param, FragmentTag<AnnotationInLayer> tag) {
		FragmentTagType type = tag.getTagType();
		AnnotationInLayer ann = tag.getFragment();
		switch (type) {
			case OPEN: {
				Element elt = createElement(ann, "frag-notempty", "frag-left-closed");
				addPair(ann, elt);
				break;
			}
			case EMPTY: {
				createElement(ann, "frag-empty");
				break;
			}
			case CLOSE: {
				Stack<AnnotationInLayer> crossedAnnotations = new Stack<AnnotationInLayer>();
				while (true) {
					Pair<AnnotationInLayer,Node> pair = openedElements.pop();
					AnnotationInLayer parent = pair.first;
					Element parentElt = (Element) pair.second;
					if (parent == ann) {
						addCSSClasses(parentElt, "frag-right-closed");
						break;
					}
					crossedAnnotations.push(parent);
					addCSSClasses(parentElt, "frag-right-open");
				}
				while (!crossedAnnotations.isEmpty()) {
					AnnotationInLayer parent = crossedAnnotations.pop();
					Element parentElt = createElement(ann, "frag-notempty", "frag-left-open");
					addPair(parent, parentElt);
				}
			}
		}
	}
	
	private static void addCSSClasses(Element elt, String... cssClasses) {
		for (String cssClass : cssClasses) {
			if (elt.hasAttribute("class")) {
				String prevCSSClass = elt.getAttribute("class");
				elt.setAttribute("class", prevCSSClass + " " + cssClass);
			}
			else {
				elt.setAttribute("class", cssClass);
			}
		}
	}
	
	private Element createElement(AnnotationInLayer ann, String... cssClasses) {
		String tagName = getTagName(ann);
		Element result = createElement(tagName, cssClasses);
		result.setAttribute("data-eltId", ann.getAnnotation().getStringId());
		result.setAttribute("data-layer", ann.getLayerName());
		result.setAttribute("onclick", "focusFrags([this], event, true)");
		addCSSClasses(result, "frag", "layer-" + ann.getLayerName());
		return result;
	}

	private Element createElement(String tagName, String... cssClasses) {
		Element result = document.createElement(tagName);
		addCSSClasses(result, cssClasses);
		//System.err.println("openedElements = " + openedElements);
		Pair<AnnotationInLayer,Node> parent = openedElements.peek();
		//System.err.println("parent.second = " + parent.second);
		//System.err.println("result = " + result);
		parent.second.appendChild(result);
		return result;
	}

	private void addPair(AnnotationInLayer ann, Node node) {
		Pair<AnnotationInLayer,Node> pair = new Pair<AnnotationInLayer,Node>(ann, node);
		openedElements.push(pair);
	}

	private String getTagName(AnnotationInLayer ann) {
		String layerName = ann.getLayerName();
		if (layerName.equals(htmlLayerName)) {
			Annotation a = ann.getAnnotation();
			if (a.hasFeature(htmlTagFeatureKey)) {
				return a.getLastFeature(htmlTagFeatureKey);
			}
		}
		return "span";
	}

	@Override
	public void handleGap(String param, int from, int to) {
		Text text = document.createTextNode(Strings.clip(param, from, to));
		Pair<AnnotationInLayer,Node> parent = openedElements.peek();
		parent.second.appendChild(text);
	}

	@Override
	public void handleHead(String param, int to) {
		handleGap(param, 0, to);
	}

	@Override
	public void handleTail(String param, int from) {
		handleGap(param, from, param.length());
	}

	public void addDocument(fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document doc, Collection<String> layerNames) {
		Element docContainer = createElement("div", "doc-container");
		docContainer.setAttribute("data-eltid", doc.getStringId());
		addPair(null, docContainer);
		addDocId(doc);
		Element docBody = createElement("div", "doc-body");
		addPair(null, docBody);
		for (Section sec : Iterators.loop(doc.sectionIterator())) {
			addSection(sec, layerNames);
		}
		openedElements.pop();
		openedElements.pop();
	}
	
	private void addDocId(fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document doc) {
		Element docId = createElement("div", "doc-id");
		Element docIcon = document.createElement("img");
		docIcon.setAttribute("src", "/res/icons/blue-document.png");
		docIcon.setAttribute("height", "24");
		docIcon.setAttribute("width", "24");
		docIcon.setAttribute("alt", "Document");
		addCSSClasses(docIcon, "doc-icon");
		docId.appendChild(docIcon);
		Text txt = document.createTextNode(doc.getId());
		docId.appendChild(txt);
	}
	
	private void addSection(Section sec, Collection<String> layerNames) {
		Element secContainer = createElement("div", "sec-container");
		secContainer.setAttribute("data-eltid", sec.getStringId());
		addPair(null, secContainer);
		addSectionName(sec);
		Element secBody = createElement("div", "sec-body");
		addPair(null, secBody);
		List<FragmentTag<AnnotationInLayer>> tags = createTagList(sec, layerNames);
		FragmentTag.iterateTags(this, sec.getContents(), tags, 0);
		fillNesting(secBody);
		openedElements.pop();
		openedElements.pop();	
	}
	
	private void addSectionName(Section sec) {
		Element secName = createElement("div", "sec-name");
		Element secIcon = document.createElement("img");
		secIcon.setAttribute("src", "/res/icons/document-text.png");
		secIcon.setAttribute("height", "18");
		secIcon.setAttribute("width", "18");
		secIcon.setAttribute("alt", "Section");
		addCSSClasses(secIcon, "sec-icon");
		secName.appendChild(secIcon);
		Text txt = document.createTextNode(sec.getName());
		secName.appendChild(txt);
	}
	
	private List<FragmentTag<AnnotationInLayer>> createTagList(Section sec, Collection<String> layerNames) {
		List<FragmentTag<AnnotationInLayer>> result = new ArrayList<FragmentTag<AnnotationInLayer>>();
		if (htmlLayerName != null) {
			addLayerAnnotationsTags(result, sec, htmlLayerName);
		}
		for (String layerName : layerNames) {
			addLayerAnnotationsTags(result, sec, layerName);
		}
		Collections.sort(result, new FragmentTagComparator<AnnotationInLayer>(AnnotationInLayerStableComparator.INSTANCE));
		return result;
	}
	
	private static void addLayerAnnotationsTags(List<FragmentTag<AnnotationInLayer>> tags, Section sec, String layerName) {
		if (sec.hasLayer(layerName)) {
			Layer layer = sec.getLayer(layerName);
			for (Annotation a : layer) {
				AnnotationInLayer ann = new AnnotationInLayer(a, layerName);
				FragmentTag.createTags(tags, ann);
			}
		}
	}
	
	private static void fillNesting(Element secBody) {
		NodeList spans = secBody.getElementsByTagName("span");
		List<Element> spanList = XMLUtils.elementList(spans);
		Set<Element> remaining = new HashSet<Element>(spanList);
		Collection<Element> toRemove = new ArrayList<Element>();
		String cssClass = "frag-nesting-0";
		for (Element span : remaining) {
			List<Element> children = XMLUtils.childrenElements(span);
			if (children.isEmpty()) {
				toRemove.add(span);
				addCSSClasses(span, cssClass);
			}
		}
		remaining.removeAll(toRemove);
		toRemove.clear();
		for (int nesting = 1; !remaining.isEmpty(); ++nesting) {
			cssClass = "frag-nesting-" + nesting;
			for (Element span : remaining) {
				for (Element child : XMLUtils.childrenElements(span)) {
					if (!remaining.contains(child)) {
						toRemove.add(span);
						addCSSClasses(span, cssClass);
						break;
					}
				}
			}
			remaining.removeAll(toRemove);
			toRemove.clear();
		}
	}
}
