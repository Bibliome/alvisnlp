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

<h3 name="checkedlayerNames" class="param">checkedlayerNames</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String[]" class="converter">String[]</a>
</div>
Source layers.

<h3 name="modifiedlayerName" class="param">modifiedlayerName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Target layer.

<h3 name="indexFeatureName" class="param">indexFeatureName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the index of the split annotations.

<h3 name="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 name="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

