<h1 class="module">MergeLayers</h1>

## Synopsis

Creates a new layer in each section containing all annotations in source layers.

## Description

 *MergeLayers* adds annotations in the layers <a href="#sourceLayers" class="param">sourceLayers</a> into the layer <a href="#targetLayer" class="param">targetLayer</a> .

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<mergelayers class="MergeLayers">
    <sourceLayers></sourceLayers>
    <targetLayer></targetLayer>
</mergelayers>
```

## Mandatory parameters

<h3 id="sourceLayers" class="param">sourceLayers</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Name of the layers where to get annotations.

<h3 id="targetLayer" class="param">targetLayer</h3>

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

## Deprecated parameters

<h3 id="sourceLayerNames" class="param">sourceLayerNames</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Deprecated alias for <a href="#sourceLayers" class="param">sourceLayers</a> .

<h3 id="targetLayerName" class="param">targetLayerName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#targetLayer" class="param">targetLayer</a> .

