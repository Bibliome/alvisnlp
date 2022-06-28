<h1 class="module">NGrams</h1>

## Synopsis

Computes annotation n-grams.

## Description

*NGrams*computes the n-grams of annotations in <a href="#tokenLayerName" class="param">tokenLayerName</a> and creates an annotation for each n-gram. If <a href="#sentenceLayerName" class="param">sentenceLayerName</a> is set, then no n-gram will cross boundaries of annotations in this layer. If <a href="#keepAnnotations" class="param">keepAnnotations</a> is set, then*NGrams*will search for annotations with n-gram boundaries in these layers, if one annotation is found then it is recycled instead of creating a new annotation.

## Snippet



```xml
<ngrams class="NGrams>
    <maxNGramSize></maxNGramSize>
    <targetLayerName></targetLayerName>
</ngrams>
```

## Mandatory parameters

<h3 id="maxNGramSize" class="param">maxNGramSize</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximum number of tokens in n-grams.

<h3 id="targetLayerName" class="param">targetLayerName</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to add n-gram annotations, recycled annotations will also be added in this layer.

## Optional parameters

<h3 id="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="keepAnnotations" class="param">keepAnnotations</h3>

<div class="param-level param-level-default-value">Default value: ``
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Name of layers where to search for recycled annotations.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `boolean:and(true, nav:layer:words())`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

<h3 id="sentenceLayerName" class="param">sentenceLayerName</h3>

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the sentence layer.

<h3 id="tokenLayerName" class="param">tokenLayerName</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the token layer.

