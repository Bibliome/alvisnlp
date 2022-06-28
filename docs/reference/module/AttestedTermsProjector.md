<h1 class="module">AttestedTermsProjector</h1>

## Synopsis

Projects a list of terms given in tree-tagger format.

**This module is obsolete, superceded by fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.treetagger.TreeTaggerTermsProjector**

## Description

*AttestedTermsProjector* reads a list of terms from <a href="#termsFile" class="param">termsFile</a> and searches for these terms in sections. The terms must be in tree-tagger format: each line contains a token/POS/lemma and each term is terminated by a period/*SENT*. The searched string for each term is the concatenation of token surface forms, or lemma if <a href="#lemmaKeys" class="param">lemmaKeys</a> is true, separated with a space character.

The parameters <a href="#warnDuplicateValues" class="param">warnDuplicateValues</a>, <a href="#multipleValueAction" class="param">multipleValueAction</a>, <a href="#errorDuplicateValues" class="param">errorDuplicateValues</a> and <a href="#warnMultipleValues" class="param">warnMultipleValues</a> control who *AttestedTermsProjector* reacts when encountering duplicate terms.

The parameters <a href="#normalizeSpace" class="param">normalizeSpace</a>, <a href="#ignoreCase" class="param">ignoreCase</a>, <a href="#ignoreDiacritics" class="param">ignoreDiacritics</a> and <a href="#ignoreWhitespace" class="param">ignoreWhitespace</a> control the matching of entries on the sections.

The <a href="#subject" class="param">subject</a> parameter specifies which text of the section should be matched. There are two options:
  
* the entries are matched on the contents of the section, <a href="#subject" class="param">subject</a> can also control if matches boundaries coincide with word delimiters;
* the entries are matched on the feature value of annotations of a given layer separated by a whitespace, in this way entries can be searched against word lemmas for instance.



*AttestedTermsProjector* creates an annotation for each matched term and adds these annotations to the layer named <a href="#targetLayerName" class="param">targetLayerName</a>. The created annotations will have the features <a href="#termFeatureName" class="param">termFeatureName</a>, <a href="#posFeatureName" class="param">posFeatureName</a> and <a href="#lemmaFeatureName" class="param">lemmaFeatureName</a> containing the concatenation of the corresponding term tokens surface form, POS tag and lemma respectively. In addition, the created annotations will have the feature keys and values defined in <a href="#constantAnnotationFeatures" class="param">constantAnnotationFeatures</a>.

## Mandatory parameters

<h3 name="targetLayerName" class="param">targetLayerName</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to put match annotations.

<h3 name="termsFile" class="param">termsFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Attested terms file.

## Optional parameters

<h3 name="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module

<h3 name="termFeatureName" class="param">termFeatureName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to write the term form.

<h3 name="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<h3 name="errorDuplicateValues" class="param">errorDuplicateValues</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to stop when a duplicate entry is seen.

<h3 name="ignoreCase" class="param">ignoreCase</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Match ignoring case.

<h3 name="ignoreDiacritics" class="param">ignoreDiacritics</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Match ignoring diacritics.

<h3 name="ignoreWhitespace" class="param">ignoreWhitespace</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Match ignoring whitespace characters.

<h3 name="lemmaFeatureName" class="param">lemmaFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to write the term lemma.

<h3 name="lemmaKeys" class="param">lemmaKeys</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to project lemmas instead of the forms.

<h3 name="multipleValueAction" class="param">multipleValueAction</h3>

<div class="param-level param-level-default-value">Default value: `add`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.projectors.MultipleValueAction" class="converter">MultipleValueAction</a>
</div>
Either to stop when multiple entries with the same key is seen.

<h3 name="normalizeSpace" class="param">normalizeSpace</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Match normalizing whitespace.

<h3 name="posFeatureName" class="param">posFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to write the term POS tags.

<h3 name="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<h3 name="subject" class="param">subject</h3>

<div class="param-level param-level-default-value">Default value: `fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.projectors.ContentsSubject@639c2c1d`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.projectors.Subject" class="converter">Subject</a>
</div>
Subject on which to project the dictionary.

