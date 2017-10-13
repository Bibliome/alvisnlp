<h1 class="module">SeSMig</h1>

## Synopsis

Detects sentence boundaries and creates one annotation for each sentence.

This module assumes WoSMig processed the same sections.

## Description

*SeSMig* scans for annotations in <a href="#wordLayerName" class="param">wordLayerName</a> and detects a sentence boundaries defined as either:
  
* an annotation whose feature <a href="#eosStatusFeature" class="param">eosStatusFeature</a> equals *eos*;
* an annotation whose surface form contains only characaters of the value of <a href="#strongPunctuations" class="param">strongPunctuations</a> and which is followed by an uppercase character;
* an annotation whose feature <a href="#eosStatusFeature" class="param">eosStatusFeature</a> equals *maybe-eos* and which is followed by an uppercase character.



*SeSMig* creates an annotation for each sentence and adds it into the <a href="#targetLayerName" class="param">targetLayerName</a>. The <a href="#eosStatusFeature" class="param">eosStatusFeature</a> of word annotations are given a new value:
  
* **eos**: for the last word of each sentence;
* **not-eos**: for all other words.



If <a href="#noBreakLayerName" class="param">noBreakLayerName</a> is defined, then *SeSMig* will prevent sentence boundaries inside annotations in this layer.

## Parameters

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module

<a name="noBreakLayerName">

### noBreakLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing annotations within which there cannot be sentence boundaries.

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="eosStatusFeature">

### eosStatusFeature

<div class="param-level param-level-default-value">Default value: `eos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature (in words) containing the end-of-sentence status (not-eos, maybe-eos).

<a name="formFeature">

### formFeature

<div class="param-level param-level-default-value">Default value: `form`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature containing the word surface form.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `boolean:and(true, nav:layer:words())`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="strongPunctuations">

### strongPunctuations

<div class="param-level param-level-default-value">Default value: `?.!`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
List of strong punctuations.

<a name="targetLayerName">

### targetLayerName

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store sentence annotations.

<a name="typeFeature">

### typeFeature

<div class="param-level param-level-default-value">Default value: `wordType`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to read word annotation type.

<a name="wordLayerName">

### wordLayerName

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing word annotations.

