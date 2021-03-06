<h1 class="module">CopyFeature</h1>

## Synopsis

Copy the value of a feature in another feature for a selection of elements.

**This module is experimental.**

## Description

*CopyFeature* evaluates <a href="#target" class="param">target</a> as a list of elements. For each element *CopyFeature* copies the last value of <a href="#sourceFeatureName" class="param">sourceFeatureName</a> into <a href="#targetFeatureName" class="param">targetFeatureName</a>.

*CopyFeature* is useful to build plans that select a particular feature to process.

## Parameters

<h3 name="sourceFeatureName" class="param">sourceFeatureName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 name="target" class="param">target</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
UNDOCUMENTED

<h3 name="targetFeatureName" class="param">targetFeatureName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

