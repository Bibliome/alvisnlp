<h1 class="module">Species</h1>

## Synopsis

Calls the [Species]() taxon tagger.

**This module is experimental.**

## Description

*Species* applies the [Species](XXX) taxon tagger on the contents of the sections. The *Species* software must be installed in the <a href="#speciesDir" class="param">speciesDir</a> directory. *Species* crerates an annotation for each taxon tagged by *Species*.

## Parameters

<a name="speciesDir">

### speciesDir

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Directory where the *Species* software is installed.

<a name="targetLayerName">

### targetLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Layer where to store the tagged taxon names.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module

<a name="taxidFeature">

### taxidFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the identifier of the tagged taxon.

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

