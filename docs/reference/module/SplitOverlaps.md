<h1 class="module">SplitOverlaps</h1>

## Synopsis

Splits overlapping annotations.

**This module is experimental.**

## Description

*SplitOverlaps* copies annotations from <a href="#checkedlayerNames" class="param">checkedlayerNames</a> into <a href="#modifiedlayerName" class="param">modifiedlayerName</a> and ensures that this layer does not contain overlaping annotations. If <a href="#checkedlayerNames" class="param">checkedlayerNames</a> contains overlaps, then *SplitOverlaps* splits annotations at the start or end positions of overlapping annotations.


* Features copied?
* Non-split annotations deep-copied?
* Split embedded annotations?

## Parameters

<a name="checkedlayerNames">

### checkedlayerNames

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String[]" class="converter">String[]</a>
</div>
Source layers.

<a name="modifiedlayerName">

### modifiedlayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Target layer.

<a name="indexFeatureName">

### indexFeatureName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the index of the split annotations.

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

