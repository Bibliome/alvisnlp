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


package alvisnlp.corpus;


// TODO: Auto-generated Javadoc
/**
 * The Class DefaultNames.
 * 
 * @author rbossy
 */
public final class DefaultNames {
    private static final String CANONICAL_FORM_FEATURE = "lemma";
    private static final String POS_TAG_FEATURE = "pos";
    private static final String STEM_FEATURE = "stem";
    private static final String WORD_TYPE_FEATURE = "wordType";
    private static final String NAMED_ENTITY_TYPE_FEATURE = "neType";
    private static final String WORD_LAYER = "words";
    private static final String SENTENCE_LAYER = "sentences";
    private static final String PARAGRAPH_LAYER = "paragraph";
    private static final String LANGUAGE_FEATURE = "lang";
    private static final String END_OF_SENTENCE_STATUS_FEATURE = "eos";
    private static final String DEPENDENCY_RELATION_NAME = "dependencies";
    private static final String DEPENDENCY_LABEL_FEATURE_NAME = "label";
    private static final String DEPENDENCY_SENTENCE_ROLE = "sentence";
    private static final String DEPENDENCY_HEAD_ROLE = "head";
    private static final String DEPENDENCY_DEPENDENT_ROLE = "dependent";
	private static final String PARSE_NUMBER_FEATURE_NAME = "parse";

	public static String getCanonicalFormFeature() {
		return CANONICAL_FORM_FEATURE;
	}
	
	public static String getPosTagFeature() {
		return POS_TAG_FEATURE;
	}
	
	public static String getStemFeature() {
		return STEM_FEATURE;
	}
	
	public static String getWordTypeFeature() {
		return WORD_TYPE_FEATURE;
	}
	
	public static String getNamedEntityTypeFeature() {
		return NAMED_ENTITY_TYPE_FEATURE;
	}
	
	public static String getWordLayer() {
		return WORD_LAYER;
	}
	
	public static String getSentenceLayer() {
		return SENTENCE_LAYER;
	}
	
	public static String getParagraphLayer() {
		return PARAGRAPH_LAYER;
	}
	
	public static String getLanguageFeature() {
		return LANGUAGE_FEATURE;
	}
	
	public static String getEndOfSentenceStatusFeature() {
		return END_OF_SENTENCE_STATUS_FEATURE;
	}
	
	public static String getDependencyRelationName() {
		return DEPENDENCY_RELATION_NAME;
	}
	
	public static String getDependencyLabelFeatureName() {
		return DEPENDENCY_LABEL_FEATURE_NAME;
	}
	
	public static String getDependencySentenceRole() {
		return DEPENDENCY_SENTENCE_ROLE;
	}
	
	public static String getDependencyHeadRole() {
		return DEPENDENCY_HEAD_ROLE;
	}
	
	public static String getDependencyDependentRole() {
		return DEPENDENCY_DEPENDENT_ROLE;
	}
	
	public static String getParseNumberFeatureName() {
		return PARSE_NUMBER_FEATURE_NAME;
	}
}
