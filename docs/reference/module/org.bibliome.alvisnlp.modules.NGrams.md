<h1 class="module">NGrams</h1>

## Synopsis

Computes annotation n-grams.

## Description

*NGrams* computes the n-grams of annotations in <a href="#tokenLayerName" class="param">tokenLayerName</a> and creates an annotation for each n-gram. If <a href="#sentenceLayerName" class="param">sentenceLayerName</a> is set, then no n-gram will cross boundaries of annotations in this layer. If <a href="#keepAnnotations" class="param">keepAnnotations</a> is set, then *NGrams* will search for annotations with n-gram boundaries in these layers, if one annotation is found then it is recycled instead of creating a new annotation.

## Parameters

<a name="maxNGramSize">

### maxNGramSize

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximum number of tokens in n-grams.

<a name="targetLayerName">

### targetLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to add n-gram annotations, recycled annotations will also be added in this layer.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="keepAnnotations">

### keepAnnotations

<div class="param-level param-level-default-value">Default value: ``
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String[]" class="converter">String[]</a>
</div>
Name of layers where to search for recycled annotations.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `boolean:and(true, nav:layer:words())`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="sentenceLayerName">

### sentenceLayerName

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the sentence layer.

<a name="tokenLayerName">

### tokenLayerName

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the token layer.

