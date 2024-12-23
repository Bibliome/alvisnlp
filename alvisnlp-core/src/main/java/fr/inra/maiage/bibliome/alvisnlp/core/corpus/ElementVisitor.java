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


package fr.inra.maiage.bibliome.alvisnlp.core.corpus;

/**
 * Element visitor.
 * @author rbossy
 *
 * @param <R> return type, use Void if none.
 * @param <P> parameter type, use Void if none.
 */
public interface ElementVisitor<R,P> {
	R visit(Annotation a, P param);
	R visit(Corpus corpus, P param);
	R visit(Document doc, P param);
	R visit(Relation rel, P param);
	R visit(Section sec, P param);
	R visit(Tuple t, P param);
	R visit(Element e, P param);
}
