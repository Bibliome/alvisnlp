<h1 class="module">MergeLayers</h1>

## Synopsis

Creates a new layer in each section containing all annotations in source layers.

## Description

*MergeLayers*adds annotations in the layers <a href="#sourceLayerNames" class="param">sourceLayerNames</a> into the layer <a href="#targetLayerName" class="param">targetLayerName</a> .

## Snippet



```xml
<mergelayers class="MergeLayers>
    <sourceLayerNames></sourceLayerNames>
    <targetLayerName></targetLayerName>
</mergelayers>
```

## Mandatory parameters

<h3 id="sourceLayerNames" class="param">sourceLayerNames</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Name of the layers where to get annotations.

<h3 id="targetLayerName" class="param">targetLayerName</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer to create.

## Optional parameters

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

