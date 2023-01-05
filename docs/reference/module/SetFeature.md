<h1 class="module">SetFeature</h1>

## Synopsis

Set the value of a feature for a selection of elements.

## Description

 *SetFeature* evaluates <a href="#target" class="param">target</a> as a list of elements. For each element *SetFeature* set value of <a href="#feature" class="param">feature</a> to <a href="#value" class="param">value</a> .

 *SetFeature* is useful to build plans that select a particular feature to process.

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<setfeature class="SetFeature">
    <feature></feature>
    <target></target>
    <value></value>
</setfeature>
```

## Mandatory parameters

<h3 id="feature" class="param">feature</h3>

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

<h3 id="value" class="param">value</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

## Optional parameters

## Deprecated parameters

<h3 id="featureName" class="param">featureName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#feature" class="param">feature</a> .

<h3 id="featureValue" class="param">featureValue</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#value" class="param">value</a> .

