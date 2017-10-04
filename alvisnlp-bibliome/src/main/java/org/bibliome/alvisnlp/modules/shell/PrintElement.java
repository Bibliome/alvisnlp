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

import org.bibliome.util.Strings;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.ElementVisitor;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;

class PrintElement implements ElementVisitor<String,Void> {
	final static PrintElement INSTANCE = new PrintElement();
	
	private PrintElement() {}
	
	@Override
	public String visit(Annotation a, Void param) {
		return
		"Annotation: " +
		a.getStringId() +
		" '" +
		Strings.escapeJava(a.getForm()) +
		"' " +
		a.getStart() +
		'-' +
		a.getEnd();
	}

	@Override
	public String visit(Corpus corpus, Void param) {
		return "Corpus";
	}

	@Override
	public String visit(Document doc, Void param) {
		return "Document: " + doc.getId();
	}

	@Override
	public String visit(Relation rel, Void param) {
		return "Relation: " + rel.getName();
	}

	@Override
	public String visit(Section sec, Void param) {
		return "Section: " + sec.getName() + " (" + sec.getOrder() + ')';
	}

	@Override
	public String visit(Tuple t, Void param) {
		StringBuilder sb = new StringBuilder("Tuple: ");
		sb.append(t.getRelation().getName());
		sb.append(" [");
		boolean notFirst = false;
		for (String role : t.getRoles()) {
			if (notFirst)
				sb.append(", ");
			else
				notFirst = true;
			sb.append(role);
			sb.append(" = ");
			sb.append(t.getArgument(role));
		}
		sb.append(']');
		return sb.toString();
	}

	@Override
	public String visit(Element e, Void param) {
		return e.toString();
	}
}
