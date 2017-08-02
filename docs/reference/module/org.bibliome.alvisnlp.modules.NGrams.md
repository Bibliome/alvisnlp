# org.bibliome.alvisnlp.modules.NGrams

## Synopsis

Computes annotation n-grams.

## Description

*org.bibliome.alvisnlp.modules.NGrams* computes the n-grams of annotations in [tokenLayerName](#tokenLayerName) and creates an annotation for each n-gram. If [sentenceLayerName](#sentenceLayerName) is set, then no n-gram will cross boundaries of annotations in this layer. If [keepAnnotations](#keepAnnotations) is set, then *org.bibliome.alvisnlp.modules.NGrams* will search for annotations with n-gram boundaries in these layers, if one annotation is found then it is recycled instead of creating a new annotation.

## Parameters

<a name="maxNGramSize">

### maxNGramSize

Optional

Type: [Integer](../converter/java.lang.Integer)

Maximum number of tokens in n-grams.

<a name="targetLayerName">

### targetLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer where to add n-gram annotations, recycled annotations will also be added in this layer.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="keepAnnotations">

### keepAnnotations

Default value: ``

Type: [String[]](../converter/java.lang.String[])

Name of layers where to search for recycled annotations.

<a name="sectionFilter">

### sectionFilter

Default value: `boolean:and(true, nav:layer:words())`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="sentenceLayerName">

### sentenceLayerName

Default value: `sentences`

Type: [String](../converter/java.lang.String)

Name of the sentence layer.

<a name="tokenLayerName">

### tokenLayerName

Default value: `words`

Type: [String](../converter/java.lang.String)

Name of the token layer.

