<h1 class="module">TyDIProjector</h1>

## Synopsis

Projects terms from a TiDI export.

**This module is obsolete, superceded by org.bibliome.alvisnlp.modules.trie.TyDIExportProjector**

## Description

*TyDIProjector* reads different files from a [TyDI](https://migale.jouy.inra.fr/redmine/projects/tydi) text export, resolves all synonymies and projects the terms into sections.

The parameters <a href="#lemmaFile" class="param">lemmaFile</a>, <a href="#synonymsFile" class="param">synonymsFile</a>, <a href="#quasiSynonymsFile" class="param">quasiSynonymsFile</a>, <a href="#acronymsFile" class="param">acronymsFile</a> and <a href="#typographicVariationsFile" class="param">typographicVariationsFile</a> point to the paths to the corresponding TyDI file export.

The parameters <a href="#normalizeSpace" class="param">normalizeSpace</a>, <a href="#ignoreCase" class="param">ignoreCase</a>, <a href="#ignoreDiacritics" class="param">ignoreDiacritics</a> and <a href="#ignoreWhitespace" class="param">ignoreWhitespace</a> control the matching of entries on the sections.

The <a href="#subject" class="param">subject</a> parameter specifies which text of the section should be matched. There are two options:
  
* the entries are matched on the contents of the section, <a href="#subject" class="param">subject</a> can also control if matches boundaries coincide with word delimiters;
* the entries are matched on the feature value of annotations of a given layer separated by a whitespace, in this way entries can be searched against word lemmas for instance.



*TyDIProjector* creates an annotation for each matched entry and adds these annotations to the layer named <a href="#targetLayerName" class="param">targetLayerName</a>. The created annotations will have a feature named <a href="#canonicalFormFeature" class="param">canonicalFormFeature</a> containing the canonical form of the matched term. In addition, the created annotations will have the feature keys and values defined in <a href="#constantAnnotationFeatures" class="param">constantAnnotationFeatures</a>.

## Parameters

<a name="lemmaFile">

### lemmaFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the file containing lemmas.

<a name="mergeFile">

### mergeFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the merged terms file.

<a name="quasiSynonymsFile">

### quasiSynonymsFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the quasi-synonyms file.

<a name="synonymsFile">

### synonymsFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the synonyms file.

<a name="targetLayerName">

### targetLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to put match annotations.

<a name="acronymsFile">

### acronymsFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the acronyms file.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module

<a name="saveDictFile">

### saveDictFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
Path of the file where to save the dictionary.

<a name="typographicVariationsFile">

### typographicVariationsFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the typographic variations file.

<a name="canonicalFormFeature">

### canonicalFormFeature

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the term canonical form.

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

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="subject">

### subject

<div class="param-level param-level-default-value">Default value: `org.bibliome.alvisnlp.modules.projectors.ContentsSubject@38afe297`
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.alvisnlp.modules.projectors.Subject" class="converter">Subject</a>
</div>
Subject on which to project the dictionary.

