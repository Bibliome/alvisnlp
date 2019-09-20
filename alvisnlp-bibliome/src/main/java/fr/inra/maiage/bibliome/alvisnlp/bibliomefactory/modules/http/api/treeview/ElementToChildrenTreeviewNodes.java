package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.treeview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ElementVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.util.Iterators;

@SuppressWarnings("rawtypes")
public enum ElementToChildrenTreeviewNodes implements ElementVisitor<Collection<TreeviewNode>,Collection<TreeviewNode>> {
	INSTANCE;
	
	public static Collection<TreeviewNode> getChildren(Element elt) {
		return elt.accept(INSTANCE, new ArrayList<TreeviewNode>());
	}
	
	private static Collection<TreeviewNode> addFeaturesChild(Collection<TreeviewNode> nodes, Element elt) {
		nodes.add(new TreeviewFeaturesNode(elt));
		return nodes;
	}
	
	private static Collection<TreeviewNode> addChildren(Collection<TreeviewNode> nodes, Iterable<? extends Element> elements) {
		for (Element elt : elements) {
			nodes.add(TreeviewElementNode.toTreeviewNode(elt, null));
		}
		return nodes;
	}

	private static Collection<TreeviewNode> addChildren(Collection<TreeviewNode> nodes, Iterator<? extends Element> elements) {
		return addChildren(nodes, Iterators.loop(elements));
	}
	
	@Override
	public Collection<TreeviewNode> visit(Annotation a, Collection<TreeviewNode> param) {
		return addFeaturesChild(param, a);
	}

	@Override
	public Collection<TreeviewNode> visit(Corpus corpus, Collection<TreeviewNode> param) {
		addFeaturesChild(param, corpus);
		return addChildren(param, corpus.documentIterator());
	}

	@Override
	public Collection<TreeviewNode> visit(Document doc, Collection<TreeviewNode> param) {
		addFeaturesChild(param, doc);
		return addChildren(param, doc.sectionIterator());
	}

	@Override
	public Collection<TreeviewNode> visit(Relation rel, Collection<TreeviewNode> param) {
		addFeaturesChild(param, rel);
		return addChildren(param, rel.getTuples());
	}

	@Override
	public Collection<TreeviewNode> visit(Section sec, Collection<TreeviewNode> param) {
		addFeaturesChild(param, sec);
		for (Layer layer : sec.getAllLayers()) {
			String layerName = layer.getName();
			param.add(new TreeviewLayerNode(sec, layerName));
		}
		return addChildren(param, sec.getAllRelations());
	}

	@Override
	public Collection<TreeviewNode> visit(Tuple t, Collection<TreeviewNode> param) {
		addFeaturesChild(param, t);
		for (String role : t.getRoles()) {
			Element arg = t.getArgument(role);
			param.add(TreeviewElementNode.toTreeviewNode(arg, role));
		}
		return param;
	}

	@Override
	public Collection<TreeviewNode> visit(Element e, Collection<TreeviewNode> param) {
		return addFeaturesChild(param, e);
	}
}
