<h1 class="module">SetFeature</h1>

## Synopsis

Set the value of a feature for a selection of elements.

**This module is experimental.**

## Description

 *SetFeature* evaluates <a href="#target" class="param">target</a> as a list of elements. For each element *SetFeature* set value of <a href="#featureName" class="param">featureName</a> to <a href="#featureValue" class="param">featureValue</a> .

 *SetFeature* is useful to build plans that select a particular feature to process.

## Snippet



```xml
<setfeature class="SetFeature>
    <featureName></featureName>
    <featureValue></featureValue>
    <target></target>
</setfeature>
```

## Mandatory parameters

<h3 id="featureName" class="param">featureName</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 id="featureValue" class="param">featureValue</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 id="target" class="param">target</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
UNDOCUMENTED

## Optional parameters

