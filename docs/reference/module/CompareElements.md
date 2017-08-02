<h1 class="module">CompareElements</h1>

## Synopsis

Compares two sets of elements.

## Description

*CompareElements* evaluates <a href="#predicted" class="param">predicted</a> and <a href="#reference" class="param">reference</a> as element lists and compares them according to <a href="#similarity" class="param">similarity</a>. Detailed comparison, recall, precision and F-Score are written in <a href="#outFile" class="param">outFile</a>.

## Parameters

<a name="face">

### face

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated from a reference or predicted element as a string that will be written in <a href="#outFile" class="param">outFile</a>.

<a name="outFile">

### outFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
File where to write results.

<a name="predicted">

### predicted

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Set of predicted elements.

<a name="reference">

### reference

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Set of reference elements.

<a name="sections">

### sections

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Elements from which <a href="#predicted" class="param">predicted</a> and <a href="#reference" class="param">reference</a> are evaluated.

<a name="similarity">

### similarity

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.alvisnlp.modules.compare.ElementSimilarity" class="converter">ElementSimilarity</a>
</div>
Similarity function between two elements.

<a name="showFullMatches">

### showFullMatches

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to write matches where the similarity equals 1 (true positives).

<a name="showPrecision">

### showPrecision

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to write the precision.

<a name="showRecall">

### showRecall

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to write the recall.

