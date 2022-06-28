<h1 class="module">CompareFeatures</h1>

## Synopsis

Compare two features in a set of elements.

**This module is experimental.**

## Description

*CompareFeatures* compares the value of <a href="#referenceFeature" class="param">referenceFeature</a> and <a href="#predictedFeature" class="param">predictedFeature</a> in the elements specified by <a href="#items" class="param">items</a>.
			The comparison is aggregated using standard metrics (accuracy, recall, precision and F-score).
			*CompareFeatures* is useful to evaluate classification predictions against a reference.
			

The results are displayed in the log and written in the file specified by <a href="#outFile" class="param">outFile</a>.

By default the metrics are computed for each distinct value of <a href="#referenceFeature" class="param">referenceFeature</a> and <a href="#predictedFeature" class="param">predictedFeature</a>. If <a href="#classesOfInterest" class="param">classesOfInterest</a> is set, then *CompareFeatures* will only compute metrics for the specified values.

## Mandatory parameters

<h3 name="items" class="param">items</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Elements for which to compare both features. <a href="#items" class="param">items</a> is evaluated from the corpus.

<h3 name="outFile" class="param">outFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
File where to write results.

<h3 name="predictedFeature" class="param">predictedFeature</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the predicted value.

<h3 name="referenceFeature" class="param">referenceFeature</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the reference value.

## Optional parameters

<h3 name="classesOfInterest" class="param">classesOfInterest</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Values for which metrics are computed. All values if not set.

