<h1 class="module">CompareElements</h1>

## Synopsis

Compares two sets of elements.

## Description

*CompareElements*evaluates <a href="#predicted" class="param">predicted</a> and <a href="#reference" class="param">reference</a> as element lists and compares them according to <a href="#similarity" class="param">similarity</a> . Detailed comparison, recall, precision and F-Score are written in <a href="#outFile" class="param">outFile</a> .

## Snippet



```xml
<compareelements class="CompareElements>
    <face></face>
    <outFile></outFile>
    <predicted></predicted>
    <reference></reference>
    <sections></sections>
    <similarity></similarity>
</compareelements>
```

## Mandatory parameters

<h3 id="face" class="param">face</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated from a reference or predicted element as a string that will be written in <a href="#outFile" class="param">outFile</a> .

<h3 id="outFile" class="param">outFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
File where to write results.

<h3 id="predicted" class="param">predicted</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Set of predicted elements.

<h3 id="reference" class="param">reference</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Set of reference elements.

<h3 id="sections" class="param">sections</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Elements from which <a href="#predicted" class="param">predicted</a> and <a href="#reference" class="param">reference</a> are evaluated.

<h3 id="similarity" class="param">similarity</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.compare.ElementSimilarity" class="converter">ElementSimilarity</a>
</div>
Similarity function between two elements.

## Optional parameters

<h3 id="showFullMatches" class="param">showFullMatches</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to write matches where the similarity equals 1 (true positives).

<h3 id="showPrecision" class="param">showPrecision</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to write the precision.

<h3 id="showRecall" class="param">showRecall</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to write the recall.

