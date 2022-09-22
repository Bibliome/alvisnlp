<h1 class="module">ClearLayers</h1>

## Synopsis

Completely remove layers from sections.

## Description

 *ClearLayers* removes the layers named after <a href="#layerNames" class="param">layerNames</a> from sections.

## Snippet



```xml
<clearlayers class="ClearLayers>
    <layerNames></layerNames>
</clearlayers>
```

## Mandatory parameters

<h3 id="layerNames" class="param">layerNames</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Names of layers to remove.

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

