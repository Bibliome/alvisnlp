<h1 class="module">YateaProjector</h1>

## Synopsis

synopsis

**This module is obsolete, superceded by org.bibliome.alvisnlp.modules.yatea.YateaTermsProjector**

## Description

synopsis

## Parameters

<a name="targetLayerName">

### targetLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to put match annotations.

<a name="yateaFile">

### yateaFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>


<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module

<a name="termLemma">

### termLemma

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="termPOS">

### termPOS

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


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

<a name="head">

### head

<div class="param-level param-level-default-value">Default value: `head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


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

<a name="mnpOnly">

### mnpOnly

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>


<a name="modifier">

### modifier

<div class="param-level param-level-default-value">Default value: `modifier`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="monoHeadId">

### monoHeadId

<div class="param-level param-level-default-value">Default value: `mono-head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


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

<a name="projectLemmas">

### projectLemmas

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>


<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="subject">

### subject

<div class="param-level param-level-default-value">Default value: `org.bibliome.alvisnlp.modules.projectors.ContentsSubject@3bd82cf5`
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.alvisnlp.modules.projectors.Subject" class="converter">Subject</a>
</div>
Subject on which to project the dictionary.

<a name="termId">

### termId

<div class="param-level param-level-default-value">Default value: `term-id`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


