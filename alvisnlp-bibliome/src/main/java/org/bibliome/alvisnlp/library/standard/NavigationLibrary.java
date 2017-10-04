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


package org.bibliome.alvisnlp.library.standard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bibliome.util.Iterators;
import org.bibliome.util.mappers.Mapper;
import org.bibliome.util.mappers.Mappers;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.ArgumentElement;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.ElementVisitor;
import alvisnlp.corpus.FeatureElement;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.corpus.expressions.VariableLibrary;

@Library("nav")
public abstract class NavigationLibrary extends FunctionLibrary {
	public static final String NAME = "nav";

	@Function(ftors=1)
	public static final Evaluator after(String layerName) {
		return new AnnotationsAfterEvaluator(layerName, true);
	}
	
	@Function
	public static final Evaluator after(Evaluator layerName) {
		return new AnnotationsAfterEvaluator(layerName, true);
	}

	@Function(ftors=1)
	public static final Evaluator before(String layerName) {
		return new AnnotationsBeforeEvaluator(layerName, true);
	}
	
	@Function
	public static final Evaluator before(Evaluator layerName) {
		return new AnnotationsBeforeEvaluator(layerName, true);
	}
	
	@Function(ftors=1)
	public static final Evaluator inside(String layerName) {
		return new AnnotationsInsideEvaluator(layerName, true);
	}
	
	@Function
	public static final Evaluator inside(Evaluator layerName) {
		return new AnnotationsInsideEvaluator(layerName, true);
	}

	@Function(ftors=1)
	public static final Evaluator outside(String layerName) {
		return new AnnotationsOutsideEvaluator(layerName, true);
	}
	
	@Function
	public static final Evaluator outside(Evaluator layerName) {
		return new AnnotationsOutsideEvaluator(layerName, true);
	}

	@Function(ftors=1)
	public static final Evaluator overlapping(String layerName) {
		return new AnnotationsOverlappingEvaluator(layerName, true);
	}
	
	@Function
	public static final Evaluator overlapping(Evaluator layerName) {
		return new AnnotationsOverlappingEvaluator(layerName, true);
	}

	@Function(ftors=1)
	public static final Evaluator span(String layerName) {
		return new AnnotationsSpanEvaluator(layerName, true);
	}
	
	@Function
	public static final Evaluator span(Evaluator layerName) {
		return new AnnotationsSpanEvaluator(layerName, true);
	}


	@Function(ftors=1)
	public static final Evaluator xafter(String layerName) {
		return new AnnotationsAfterEvaluator(layerName, false);
	}
	
	@Function
	public static final Evaluator xafter(Evaluator layerName) {
		return new AnnotationsAfterEvaluator(layerName, false);
	}

	@Function(ftors=1)
	public static final Evaluator xbefore(String layerName) {
		return new AnnotationsBeforeEvaluator(layerName, false);
	}
	
	@Function
	public static final Evaluator xbefore(Evaluator layerName) {
		return new AnnotationsBeforeEvaluator(layerName, false);
	}
	
	@Function(ftors=1)
	public static final Evaluator xinside(String layerName) {
		return new AnnotationsInsideEvaluator(layerName, false);
	}
	
	@Function
	public static final Evaluator xinside(Evaluator layerName) {
		return new AnnotationsInsideEvaluator(layerName, false);
	}

	@Function(ftors=1)
	public static final Evaluator xoutside(String layerName) {
		return new AnnotationsOutsideEvaluator(layerName, false);
	}
	
	@Function
	public static final Evaluator xoutside(Evaluator layerName) {
		return new AnnotationsOutsideEvaluator(layerName, false);
	}

	@Function(ftors=1)
	public static final Evaluator xoverlapping(String layerName) {
		return new AnnotationsOverlappingEvaluator(layerName, false);
	}
	
	@Function
	public static final Evaluator xoverlapping(Evaluator layerName) {
		return new AnnotationsOverlappingEvaluator(layerName, false);
	}

	@Function(ftors=1)
	public static final Evaluator xspan(String layerName) {
		return new AnnotationsSpanEvaluator(layerName, false);
	}
	
	@Function
	public static final Evaluator xspan(Evaluator layerName) {
		return new AnnotationsSpanEvaluator(layerName, false);
	}

	private static final Layer getLayer(Section sec, String layerName) {
		if (layerName == null)
			return sec.getAllAnnotations();
		if (sec.hasLayer(layerName))
			return sec.getLayer(layerName);
		return new Layer(sec);
	}
	
	@Function(ftors=1, nameTypes={NameType.LAYER})
	public static final Iterator<Element> layer(@SuppressWarnings("unused") EvaluationContext ctx, Element elt, String name) {
		Section sec = DownCastElement.toSection(elt);
		if (sec == null)
			return Iterators.emptyIterator();
		Layer layer = getLayer(sec, name);
		List<Element> list = layer.asElementList();
		return list.iterator();
	}
	
	@Function
	public static final Iterator<Element> layer(EvaluationContext ctx, Element elt, Evaluator layerName) {
		return layer(ctx, elt, layerName.evaluateString(ctx, elt));
	}
	
	@Function
	public static final Iterator<Element> layer(EvaluationContext ctx, Element elt) {
		return layer(ctx, elt, (String) null);
	}
	
	@Function(ftors=1, nameTypes={NameType.RELATION})
	public static final Iterator<Element> relations(@SuppressWarnings("unused") EvaluationContext ctx, Element elt, String name) {
		Section sec = DownCastElement.toSection(elt);
		if (sec == null)
			return Iterators.emptyIterator();
		if (name == null)
			return Iterators.upcast(sec.getAllRelations().iterator());
		if (sec.hasRelation(name))
			return Iterators.singletonIterator(sec.getRelation(name));
		return Iterators.emptyIterator();	
	}
	
	@Function
	public static final Iterator<Element> relations(EvaluationContext ctx, Element elt, Evaluator name) {
		return relations(ctx, elt, name.evaluateString(ctx, elt));
	}
	
	@Function
	public static final Iterator<Element> relations(EvaluationContext ctx, Element elt) {
		return relations(ctx, elt, (String) null);
	}

	@Function(firstFtor=".")
	public static final Evaluator path(Evaluator left, Evaluator right) {
		return new PathEvaluator(left, right);
	}
	
	@Function
	public static final Iterator<Element> tuples(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		Relation rel = DownCastElement.toRelation(elt);
		if (rel == null)
			return Iterators.emptyIterator();
		return Iterators.upcast(rel.getTuples().iterator());
	}

	private static final ElementVisitor<Section,Void> FIND_SECTION = new ElementVisitor<Section,Void>() {
		@Override
		public Section visit(Annotation a, Void param) {
			return a.getSection();
		}

		@Override
		public Section visit(Corpus corpus, Void param) {
			return null;
		}

		@Override
		public Section visit(Document doc, Void param) {
			return null;
		}

		@Override
		public Section visit(Relation rel, Void param) {
			return rel.getSection();
		}

		@Override
		public Section visit(Section sec, Void param) {
			return sec;
		}

		@Override
		public Section visit(Tuple t, Void param) {
			return t.getRelation().accept(this, param);
		}

		@Override
		public Section visit(Element e, Void param) {
			return null;
		}
	};
	
	@Function(ftors=2, nameTypes={NameType.RELATION, NameType.ARGUMENT})
	public static final Iterator<Element> tuples(@SuppressWarnings("unused") EvaluationContext ctx, Element elt, String relationName, String role) {
		Section sec = elt.accept(FIND_SECTION, null);
		if (sec == null)
			return Iterators.emptyIterator();
		if (!sec.hasRelation(relationName))
			return Iterators.emptyIterator();
		Relation rel = sec.getRelation(relationName);
		List<Element> tuples = new ArrayList<Element>();
		for (Tuple t : rel.getTuples()) {
			if (role == null) {
				for (Element arg : t.getAllArguments())
					if (arg == elt) {
						tuples.add(t);
						break;
					}
				continue;
			}
			if (t.hasArgument(role) && (t.getArgument(role) == elt))
				tuples.add(t);
		}
		return tuples.iterator();	
	}

	@Function(ftors=1, nameTypes={NameType.RELATION})
	public static final Iterator<Element> tuples(EvaluationContext ctx, Element elt, String relationName, Evaluator role) {
		return tuples(ctx, elt, relationName, role.evaluateString(ctx, elt));
	}

	@Function
	public static final Iterator<Element> tuples(EvaluationContext ctx, Element elt, Evaluator relationName, Evaluator role) {
		return tuples(ctx, elt, relationName.evaluateString(ctx, elt), role.evaluateString(ctx, elt));
	}

	@Function
	public static final Iterator<Element> tuples(EvaluationContext ctx, Element elt, Evaluator relationName) {
		return tuples(ctx, elt, relationName.evaluateString(ctx, elt), (String) null);
	}
	
	@Function(ftors=1, nameTypes={NameType.RELATION})
	public static final Iterator<Element> tuples(EvaluationContext ctx, Element elt, String relationName) {
		return tuples(ctx, elt, relationName, (String) null);
	}
	
	@Function(firstFtor="$")
	public static final Iterator<Element> self(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		return Iterators.singletonIterator(elt);
	}
	
	@Function(ftors=1, nameTypes={NameType.ARGUMENT})
	public static final Iterator<Element> args(@SuppressWarnings("unused") EvaluationContext ctx, Element elt, String role) {
		Tuple t = DownCastElement.toTuple(elt);
		if (t == null)
			return Iterators.emptyIterator();
		if (role == null)
			return Iterators.upcast(t.getAllArguments().iterator());
		if (t.hasArgument(role))
			return Iterators.singletonIterator(t.getArgument(role));
		return Iterators.emptyIterator();
	}
	
	@Function
	public static final Iterator<Element> args(EvaluationContext ctx, Element elt, Evaluator role) {
		return args(ctx, elt, role.evaluateString(ctx, elt));
	}
	
	@Function
	public static final Iterator<Element> args(EvaluationContext ctx, Element elt) {
		return args(ctx, elt, (String) null);
	}
	
	@Function
	public static final Iterator<Element> relation(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		Tuple t = DownCastElement.toTuple(elt);
		if (t == null)
			return Iterators.emptyIterator();
		return Iterators.singletonIterator(t.getRelation());
	}

	@Override
	public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
		if (ftors.size() == 1 && ftors.get(0).equals("|") && args.size() > 1)
			return new UnionEvaluator(resolver.resolveList(args));
		if (ftors.size() == 2 && ftors.get(0).equals("assign") && args.size() == 1) {
			VariableLibrary varLib = new VariableLibrary(ftors.get(1));
			varLib.newVariable(null);
			Evaluator e = args.get(0).resolveExpressions(varLib.newLibraryResolver(resolver));
			return new AssignEvaluator(e, varLib);
		}
		return cannotResolve(ftors, args);
	}

	private static final ElementVisitor<Corpus,Void> ELEMENT_CORPUS = new ElementVisitor<Corpus,Void>() {
		@Override
		public Corpus visit(Annotation a, Void param) {
			return a.getSection().accept(this, param);
		}

		@Override
		public Corpus visit(Corpus corpus, Void param) {
			return corpus;
		}

		@Override
		public Corpus visit(Document doc, Void param) {
			return doc.getCorpus();
		}

		@Override
		public Corpus visit(Relation rel, Void param) {
			return rel.getSection().accept(this, param);
		}

		@Override
		public Corpus visit(Section sec, Void param) {
			return sec.getDocument().accept(this, param);
		}

		@Override
		public Corpus visit(Tuple t, Void param) {
			return t.getRelation().accept(this, param);
		}

		@Override
		public Corpus visit(Element e, Void param) {
			return null;
		}
	};
	
	@Function
	public static final Iterator<Element> corpus(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		return Iterators.nonNullSingleton(elt.accept(ELEMENT_CORPUS, null));
	}
	
	private static final ElementVisitor<Document,Void> ELEMENT_DOCUMENT = new ElementVisitor<Document,Void>() {
		@Override
		public Document visit(Annotation a, Void param) {
			return a.getSection().accept(this, param);
		}

		@Override
		public Document visit(Corpus corpus, Void param) {
			return null;
		}

		@Override
		public Document visit(Document doc, Void param) {
			return doc;
		}

		@Override
		public Document visit(Relation rel, Void param) {
			return rel.getSection().accept(this, param);
		}

		@Override
		public Document visit(Section sec, Void param) {
			return sec.getDocument();
		}

		@Override
		public Document visit(Tuple t, Void param) {
			return t.getRelation().accept(this, param);
		}

		@Override
		public Document visit(Element e, Void param) {
			return null;
		}
	};
	
	@Function
	public static final Iterator<Element> document(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		return Iterators.nonNullSingleton(elt.accept(ELEMENT_DOCUMENT, null));
	}
	
	@Function(ftors=1)
	public static final Iterator<Element> documents(@SuppressWarnings("unused") EvaluationContext ctx, Element elt, String id) {
		Corpus corpus = DownCastElement.toCorpus(elt);
		if (corpus == null)
			return Iterators.emptyIterator();
		if (id == null)
			return Iterators.upcast(corpus.documentIterator());
		if (corpus.hasDocument(id))
			return Iterators.singletonIterator(corpus.getDocument(id));
		return Iterators.emptyIterator();
	}
	
	@Function
	public static final Iterator<Element> documents(EvaluationContext ctx, Element elt, Evaluator id) {
		return documents(ctx, elt, id.evaluateString(ctx, elt));
	}
	
	@Function
	public static final Iterator<Element> documents(EvaluationContext ctx, Element elt) {
		return documents(ctx, elt, (String) null);
	}
	
	private static final ElementVisitor<Section,Void> ELEMENT_SECTION = new ElementVisitor<Section,Void>() {
		@Override
		public Section visit(Annotation a, Void param) {
			return a.getSection();
		}

		@Override
		public Section visit(Corpus corpus, Void param) {
			return null;
		}

		@Override
		public Section visit(Document doc, Void param) {
			return null;
		}

		@Override
		public Section visit(Relation rel, Void param) {
			return rel.getSection();
		}

		@Override
		public Section visit(Section sec, Void param) {
			return sec;
		}

		@Override
		public Section visit(Tuple t, Void param) {
			return t.getRelation().accept(this, param);
		}

		@Override
		public Section visit(Element e, Void param) {
			return null;
		}
	};
	
	@Function
	public static final Iterator<Element> section(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		return Iterators.nonNullSingleton(elt.accept(ELEMENT_SECTION, null));
	}
	
	@Function(ftors=1, nameTypes={NameType.SECTION})
	public static final Iterator<Element> sections(@SuppressWarnings("unused") EvaluationContext ctx, Element elt, String name) {
		Document doc = DownCastElement.toDocument(elt);
		if (doc == null)
			return Iterators.emptyIterator();
		if (name == null)
			return Iterators.upcast(doc.sectionIterator());
		if (doc.hasSection(name))
			return Iterators.upcast(doc.sectionIterator(name));
		return Iterators.emptyIterator();
	}
	
	@Function
	public static final Iterator<Element> sections(EvaluationContext ctx, Element elt, Evaluator name) {
		return sections(ctx, elt, name.evaluateString(ctx, elt));
	}
	
	@Function
	public static final Iterator<Element> sections(EvaluationContext ctx, Element elt) {
		return sections(ctx, elt, (String) null);
	}
	
	@Function(ftors=1, nameTypes={NameType.FEATURE})
	public static final List<Element> features(@SuppressWarnings("unused") EvaluationContext ctx, Element elt, String key) {
		if (!elt.hasFeature(key))
			return Collections.emptyList();
		List<String> values = elt.getFeature(key);
		Mapper<String,Element> mapper = Mappers.deParam(new FeatureElement.FeatureElementMapper(elt), key);
		return Mappers.mappedList(mapper, values);
	}
	
	@Function
	public static final List<Element> features(EvaluationContext ctx, Element elt, Evaluator key) {
		return features(ctx, elt, key.evaluateString(ctx, elt));
	}
	
	@Function
	public static final List<Element> features(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		List<Element> result = new ArrayList<Element>();
		for (String key : elt.getFeatureKeys())
			for (String value : elt.getFeature(key))
				result.add(new FeatureElement(elt, key, value));
		return result;
	}

	@Function
	public static final List<Element> arguments(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		Tuple t = DownCastElement.toTuple(elt);
		if (t == null)
			return Collections.emptyList();
		List<Element> result = new ArrayList<Element>();
		for (String role : t.getRoles())
			result.add(new ArgumentElement(t, role, t.getArgument(role)));
		return result;
	}
	
	@Function
	public static final Iterator<Element> parent(@SuppressWarnings("unused") EvaluationContext ctx, Element elt) {
		return Iterators.singletonIterator(elt.getParent());
	}
}
