<h1 class="module">LayerComparator</h1>

## Synopsis

Compares annotations in two different layers.

## Description

 *LayerComparator* traverses the annotations in the layers <a href="#predictedLayerName" class="param">predictedLayerName</a> and <a href="#referenceLayerName" class="param">referenceLayerName</a> in each section. This is useful when analyzing the result of named entity recognition systems. The result of the comparison is written for each section in terms of recall and precision into the file <a href="#outFile" class="param">outFile</a> . This file will also point boundary mismatches.

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<layercomparator class="LayerComparator>
    <outFile></outFile>
    <predictedLayerName></predictedLayerName>
    <referenceLayerName></referenceLayerName>
</layercomparator>
```

## Mandatory parameters

<h3 id="outFile" class="param">outFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
Path to the file where to store results.

<h3 id="predictedLayerName" class="param">predictedLayerName</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Name of the source layer.

<h3 id="referenceLayerName" class="param">referenceLayerName</h3>

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

