# org.bibliome.alvisnlp.modules.segmig.SeSMig

## Synopsis

Detects sentence boundaries and creates one annotation for each sentence.

This module assumes WoSMig processed the same sections.

## Description

*org.bibliome.alvisnlp.modules.segmig.SeSMig* scans for annotations in [wordLayerName](#wordLayerName) and detects a sentence boundaries defined as either:
      
* an annotation whose feature [eosStatusFeature](#eosStatusFeature) equals *eos*;
* an annotation whose surface form contains only characaters of the value of [strongPunctuations](#strongPunctuations) and which is followed by an uppercase character;
* an annotation whose feature [eosStatusFeature](#eosStatusFeature) equals *maybe-eos* and which is followed by an uppercase character.



*org.bibliome.alvisnlp.modules.segmig.SeSMig* creates an annotation for each sentence and adds it into the [targetLayerName](#targetLayerName). The [eosStatusFeature](#eosStatusFeature) of word annotations are given a new value:
      
* **eos**: for the last word of each sentence;
* **not-eos**: for all other words.



If [noBreakLayerName](#noBreakLayerName) is defined, then *org.bibliome.alvisnlp.modules.segmig.SeSMig* will prevent sentence boundaries inside annotations in this layer.

## Parameters

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="noBreakLayerName">

### noBreakLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer containing annotations within which there cannot be sentence boundaries.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="eosStatusFeature">

### eosStatusFeature

Default value: `eos`

Type: [String](../converter/java.lang.String)

Name of the feature (in words) containing the end-of-sentence status (not-eos, maybe-eos).

<a name="formFeature">

### formFeature

Default value: `form`

Type: [String](../converter/java.lang.String)

Name of the feature containing the word surface form.

<a name="sectionFilter">

### sectionFilter

Default value: `boolean:and(true, nav:layer:words())`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="strongPunctuations">

### strongPunctuations

Default value: `?.!`

Type: [String](../converter/java.lang.String)

List of strong punctuations.

<a name="targetLayerName">

### targetLayerName

Default value: `sentences`

Type: [String](../converter/java.lang.String)

Name of the layer where to store sentence annotations.

<a name="typeFeature">

### typeFeature

Default value: `wordType`

Type: [String](../converter/java.lang.String)

Name of the feature where to read word annotation type.

<a name="wordLayerName">

### wordLayerName

Default value: `words`

Type: [String](../converter/java.lang.String)

Name of the layer containing word annotations.

