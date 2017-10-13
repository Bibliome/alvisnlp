<h1 class="module">LayerComparator</h1>

## Synopsis

Compares annotations in two different layers.

## Description

*LayerComparator* traverses the annotations in the layers <a href="#predictedLayerName" class="param">predictedLayerName</a> and <a href="#referenceLayerName" class="param">referenceLayerName</a> in each section. This is useful when analyzing the result of named entity recognition systems. The result of the comparison is written for each section in terms of recall and precision into the file <a href="#outFile" class="param">outFile</a>. This file will also point boundary mismatches.

## Parameters

<a name="outFile">

### outFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
Path to the file where to store results.

<a name="predictedLayerName">

### predictedLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the source layer.

<a name="referenceLayerName">

### referenceLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the reference layer.

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

