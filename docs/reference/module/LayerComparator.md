<h1 class="module">LayerComparator</h1>

## Synopsis

Compares annotations in two different layers.

## Description

 *LayerComparator* traverses the annotations in the layers <a href="#predictedLayer" class="param">predictedLayer</a> and <a href="#referenceLayer" class="param">referenceLayer</a> in each section. This is useful when analyzing the result of named entity recognition systems. The result of the comparison is written for each section in terms of recall and precision into the file <a href="#outFile" class="param">outFile</a> . This file will also point boundary mismatches.

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<layercomparator class="LayerComparator">
    <outFile></outFile>
    <predictedLayer></predictedLayer>
    <referenceLayer></referenceLayer>
</layercomparator>
```

## Mandatory parameters

<h3 id="outFile" class="param">outFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
Path to the file where to store results.

<h3 id="predictedLayer" class="param">predictedLayer</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Name of the source layer.

<h3 id="referenceLayer" class="param">referenceLayer</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Name of the reference layer.

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

<h3 id="predictedLayerName" class="param">predictedLayerName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Deprecated alias for <a href="#predictedLayer" class="param">predictedLayer</a> .

<h3 id="referenceLayerName" class="param">referenceLayerName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Deprecated alias for <a href="#referenceLayer" class="param">referenceLayer</a> .

