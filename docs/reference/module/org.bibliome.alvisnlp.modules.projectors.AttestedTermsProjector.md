<h1 class="module">AttestedTermsProjector</h1>

## Synopsis

Projects a list of terms given in tree-tagger format.

**This module is obsolete, superceded by org.bibliome.alvisnlp.modules.treetagger.TreeTaggerTermsProjector**

## Description

*AttestedTermsProjector* reads a list of terms from <a href="#termsFile" class="param">termsFile</a> and searches for these terms in sections. The terms must be in tree-tagger format: each line contains a token/POS/lemma and each term is terminated by a period/*SENT*. The searched string for each term is the concatenation of token surface forms, or lemma if <a href="#lemmaKeys" class="param">lemmaKeys</a> is true, separated with a space character.

The parameters <a href="#warnDuplicateValues" class="param">warnDuplicateValues</a>, <a href="#multipleValueAction" class="param">multipleValueAction</a>, <a href="#errorDuplicateValues" class="param">errorDuplicateValues</a> and <a href="#warnMultipleValues" class="param">warnMultipleValues</a> control who *AttestedTermsProjector* reacts when encountering duplicate terms.

The parameters <a href="#normalizeSpace" class="param">normalizeSpace</a>, <a href="#ignoreCase" class="param">ignoreCase</a>, <a href="#ignoreDiacritics" class="param">ignoreDiacritics</a> and <a href="#ignoreWhitespace" class="param">ignoreWhitespace</a> control the matching of entries on the sections.

The <a href="#subject" class="param">subject</a> parameter specifies which text of the section should be matched. There are two options:
  
* the entries are matched on the contents of the section, <a href="#subject" class="param">subject</a> can also control if matches boundaries coincide with word delimiters;
* the entries are matched on the feature value of annotations of a given layer separated by a whitespace, in this way entries can be searched against word lemmas for instance.



*AttestedTermsProjector* creates an annotation for each matched term and adds these annotations to the layer named <a href="#targetLayerName" class="param">targetLayerName</a>. The created annotations will have the features <a href="#termFeatureName" class="param">termFeatureName</a>, <a href="#posFeatureName" class="param">posFeatureName</a> and <a href="#lemmaFeatureName" class="param">lemmaFeatureName</a> containing the concatenation of the corresponding term tokens surface form, POS tag and lemma respectively. In addition, the created annotations will have the feature keys and values defined in <a href="#constantAnnotationFeatures" class="param">constantAnnotationFeatures</a>.

## Parameters

<a name="targetLayerName">

### targetLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to put match annotations.

<a name="termsFile">

### termsFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Attested terms file.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module

<a name="termFeatureName">

### termFeatureName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to write the term form.

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="errorDuplicateValues">

### errorDuplicateValues

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to stop when a duplicate entry is seen.

<a name="ignoreCase">

### ignoreCase

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Match ignoring case.

<a name="ignoreDiacritics">

### ignoreDiacritics

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Match ignoring diacritics.

<a name="ignoreWhitespace">

### ignoreWhitespace

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Match ignoring whitespace characters.

<a name="lemmaFeatureName">

### lemmaFeatureName

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to write the term lemma.

<a name="lemmaKeys">

### lemmaKeys

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to project lemmas instead of the forms.

<a name="multipleValueAction">

### multipleValueAction

<div class="param-level param-level-default-value">Default value: `add`
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.alvisnlp.modules.projectors.MultipleValueAction" class="converter">MultipleValueAction</a>
</div>
Either to stop when multiple entries with the same key is seen.

<a name="normalizeSpace">

### normalizeSpace

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Match normalizing whitespace.

<a name="posFeatureName">

### posFeatureName

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to write the term POS tags.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="subject">

### subject

<div class="param-level param-level-default-value">Default value: `org.bibliome.alvisnlp.modules.projectors.ContentsSubject@247bddad`
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.alvisnlp.modules.projectors.Subject" class="converter">Subject</a>
</div>
Subject on which to project the dictionary.

