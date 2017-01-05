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


package org.bibliome.alvisnlp.modules.shell;

import java.io.IOException;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.ElementVisitor;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;

class DumpData {
	private boolean features = false;
	private boolean arguments = false;
	private boolean layers = false;
	private boolean documents = false;
	private boolean sections = false;
	private boolean relations = false;
	private boolean path = true;
	
	boolean isFeatures() {
		return features;
	}
	
	boolean isArguments() {
		return arguments;
	}
	
	boolean isLayers() {
		return layers;
	}
	
	boolean isDocuments() {
		return documents;
	}

	boolean isSections() {
		return sections;
	}

	boolean isRelations() {
		return relations;
	}

	void setDocuments(boolean documents) {
		this.documents = documents;
	}

	void setSections(boolean sections) {
		this.sections = sections;
	}

	void setRelations(boolean relations) {
		this.relations = relations;
	}

	void setFeatures(boolean features) {
		this.features = features;
	}
	
	void setArguments(boolean arguments) {
		this.arguments = arguments;
	}
	
	void setLayers(boolean layers) {
		this.layers = layers;
	}
	
	static void indent(Appendable app, int depth) {
		try {
			for (int i = 0; i <= depth; ++i)
				app.append("    ");
		}
		catch (IOException e) {
			throw new Error(e);
		}
	}
	
	static void indent(int depth) {
		indent(System.out, depth);
	}
	
	private void features(int depth, Element elt) {
		if (!features)
			return;
		depth++;
		for (String key : elt.getFeatureKeys()) {
			indent(depth);
			System.out.print('@');
			identifier(System.out, key);
			System.out.print(" = ");
			string(System.out, elt.getLastFeature(key));
			System.out.print(" [");
			boolean notFirst = false;
			for (String value : elt.getFeature(key)) {
				if (notFirst)
					System.out.print(", ");
				else
					notFirst = true;
				string(System.out, value);
			}
			System.out.println(']');
		}
	}
	
	private static final ElementVisitor<Void,Boolean> DISPLAY_ELEMENT = new ElementVisitor<Void,Boolean>() {
		@Override
		public Void visit(Annotation a, Boolean param) {
			if (param) {
				visit(a.getSection(), param);
				System.out.print('.');
			}
			System.out.print("annotation ");
			System.out.print(a.getStart());
			System.out.print('-');
			System.out.print(a.getEnd());
			System.out.print(' ');
			string(System.out, a.getForm());
			return null;
		}

		@Override
		public Void visit(Corpus corpus, Boolean param) {
			System.out.print("corpus");
			return null;
		}

		@Override
		public Void visit(Document doc, Boolean param) {
			System.out.print("document:");
			identifier(System.out, doc.getId());
			return null;
		}

		@Override
		public Void visit(Relation rel, Boolean param) {
			if (param) {
				visit(rel.getSection(), param);
				System.out.print('.');
			}
			System.out.print("relation:");
			identifier(System.out, rel.getName());
			return null;
		}

		@Override
		public Void visit(Section sec, Boolean param) {
			if (param) {
				visit(sec.getDocument(), param);
				System.out.print('.');
			}
			System.out.print("section:");
			identifier(System.out, sec.getName());
			return null;
		}

		@Override
		public Void visit(Tuple t, Boolean param) {
			if (param) {
				visit(t.getRelation(), param);
				System.out.print('.');
			}
			System.out.print("tuple");
			return null;
		}

		@Override
		public Void visit(Element e, Boolean param) {
			System.out.print(e);
			return null;
		}
	};
	
	public void dump(Element elt) {
		dump(0, elt);
	}
	
	private void dump(int depth, Element elt) {
		indent(depth);
		elt.accept(DISPLAY_ELEMENT, path && depth == 0);
		System.out.println();
		features(depth, elt);
		elt.accept(dumpSubelements, depth + 1);
	}
	
	private final ElementVisitor<Void,Integer> dumpSubelements = new ElementVisitor<Void,Integer>() {
		@Override
		public Void visit(Annotation a, Integer param) {
			return null;
		}

		@Override
		public Void visit(Corpus corpus, Integer param) {
			if (documents) {
				for (Document doc : Iterators.loop(corpus.documentIterator())) {
					dump(param, doc);
				}
			}
			return null;
		}

		@Override
		public Void visit(Document doc, Integer param) {
			if (sections) {
				for (Section sec : Iterators.loop(doc.sectionIterator())) {
					dump(param, sec);
				}
			}
			return null;
		}

		@Override
		public Void visit(Relation rel, Integer param) {
			return null;
		}

		@Override
		public Void visit(Section sec, Integer param) {
			if (layers) {
				for (Layer layer : sec.getAllLayers()) {
					indent(param);
					System.out.print("layer:");
					identifier(System.out, layer.getName());
					System.out.println();
				}
			}
			if (relations) {
				for (Relation rel : sec.getAllRelations()) {
					dump(param, rel);
				}
			}
			return null;
		}

		@Override
		public Void visit(Tuple t, Integer param) {
			if (arguments) {
				int argDepth = param + 1;
				for (String role : t.getRoles()) {
					indent(param);
					System.out.print("arg:");
					identifier(System.out, role);
					System.out.println();
					Element arg = t.getArgument(role);
					dump(argDepth, arg);
				}
			}
			return null;
		}

		@Override
		public Void visit(Element e, Integer param) {
			return null;
		}
	};

	private static boolean isFirstLetter(char c) {
		return Character.isLetter(c) || c == '_';
	}
	
	private static boolean isInnerLetter(char c) {
		return Character.isLetter(c) || Character.isDigit(c) || c == '_' || c == '-';
	}
	
	private static boolean needsQuote(String s) {
		if (s.isEmpty())
			return true;
		char f = s.charAt(0);
		if (!isFirstLetter(f))
			return true;
		for (int i = 1; i < s.length(); ++i) {
			final char c = s.charAt(i);
			if (!isInnerLetter(c))
				return true;
		}
		return false;
	}

	public static final void identifier(Appendable sb, String s) {
		try {
			if (needsQuote(s)) {
				sb.append('\'');
				for (int i = 0; i < s.length(); ++i) {
					final char c = s.charAt(i);
					switch (c) {
						case '\'':
							sb.append("\\'");
							break;
						case '\n':
							sb.append("\\n");
							break;
						case '\t':
							sb.append("\\t");
							break;
						case '\\':
							sb.append("\\\\");
							break;
						default:
							sb.append(c);
					}
				}
				sb.append('\'');
			}
			else {
				sb.append(s);
			}
		}
		catch (IOException e) {
			throw new Error(e);
		}
	}

	public static final void string(Appendable sb, String s) {
		try {
			sb.append('"');
			for (int i = 0; i < s.length(); ++i) {
				final char c = s.charAt(i);
				switch (c) {
					case '\"':
						sb.append("\\\"");
						break;
					case '\n':
						sb.append("\\n");
						break;
					case '\t':
						sb.append("\\t");
						break;
					case '\\':
						sb.append("\\\\");
						break;
					default:
						sb.append(c);
				}
			}
			sb.append('"');
		}
		catch (IOException e) {
			throw new Error(e);
		}
	}
	
	public static final String identifier(String s) {
		if (needsQuote(s)) {
			StringBuilder sb = new StringBuilder();
			identifier(sb, s);
			return sb.toString();
		}
		return s;
	}
	
	public static final String string(String s) {
		StringBuilder sb = new StringBuilder();
		string(sb, s);
		return sb.toString();
	}
}
