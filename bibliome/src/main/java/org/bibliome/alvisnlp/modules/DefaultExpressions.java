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


package org.bibliome.alvisnlp.modules;

import org.bibliome.alvisnlp.converters.expression.parser.ExpressionParser;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.ConstantsLibrary;
import alvisnlp.corpus.expressions.Expression;

public class DefaultExpressions {
	public static final Expression ANNOTATION_FORM = feature(Annotation.FORM_FEATURE_NAME);
	public static final Expression SELF = ExpressionParser.parseUnsafe("$");
	public static final Expression UNIQUE_ID = ExpressionParser.parseUnsafe("id:unique");
	public static final Expression TRUE = ConstantsLibrary.TRUE;
	public static final Expression FALSE = ConstantsLibrary.FALSE;
	public static final Expression DOCUMENT_ID = feature(Document.ID_FEATURE_NAME);
	public static final Expression SECTION_NAME = feature(Section.NAME_FEATURE_NAME);
	public static final Expression WORD_LEMMA = feature(DefaultNames.getCanonicalFormFeature());
	public static final Expression WORD_POS = feature(DefaultNames.getPosTagFeature());
	public static final Expression SECTION_WORDS = sectionLayer(DefaultNames.getWordLayer());
	public static final Expression DOCUMENT_WORDS = ExpressionParser.parseUnsafe("sections.layer:" + DefaultNames.getWordLayer());
	public static final Expression SECTION_SENTENCES = sectionLayer(DefaultNames.getSentenceLayer());
	public static final Expression SECTION_CONTENTS = ExpressionParser.parseUnsafe("contents");
	public static final Expression SECTION_DEPENDENCIES = ExpressionParser.parseUnsafe("relations:" + DefaultNames.getDependencyRelationName());
	public static final Expression SECTION_ANNOTATIONS = ExpressionParser.parseUnsafe("layer");
	public static final Expression SECTION_TUPLES = ExpressionParser.parseUnsafe("relations.tuples");
	public static final Expression ANNOTATION_START = ExpressionParser.parseUnsafe("start");
	public static final Expression ANNOTATION_END = ExpressionParser.parseUnsafe("end");
	public static final Expression CORPUS_DOCUMENTS = ExpressionParser.parseUnsafe("documents");
	public static final Expression DOCUMENT_SECTIONS = ExpressionParser.parseUnsafe("sections");
	
	public static Expression feature(String name) {
		return ExpressionParser.parseUnsafe("@" + name);
	}

	public static Expression sectionLayer(String name) {
		return ExpressionParser.parseUnsafe("layer:" + name);
	}
}
