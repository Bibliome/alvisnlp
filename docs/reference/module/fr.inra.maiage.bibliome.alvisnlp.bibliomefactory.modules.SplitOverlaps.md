<h1 class="module">SplitOverlaps</h1>

## Synopsis

Splits overlapping annotations.

## Description

 *SplitOverlaps* copies annotations from <a href="#checkedLayers" class="param">checkedLayers</a> into <a href="#modifiedLayer" class="param">modifiedLayer</a> and ensures that this layer does not contain overlapping annotations. If <a href="#checkedLayers" class="param">checkedLayers</a> contains overlaps, then *SplitOverlaps* splits annotations at the start or end positions of overlapping annotations.


* Features copied?
* Non-split annotations deep-copied?
* Split embedded annotations?

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<splitoverlaps class="SplitOverlaps">
    <checkedLayers></checkedLayers>
    <modifiedLayer></modifiedLayer>
</splitoverlaps>
```

## Mandatory parameters

<h3 id="checkedLayers" class="param">checkedLayers</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
UNDOCUMENTED

<h3 id="modifiedLayer" class="param">modifiedLayer</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Target layer.

## Optional parameters

<h3 id="indexFeature" class="param">indexFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the index of the split annotations.

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

<h3 id="indexFeatureName" class="param">indexFeatureName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#indexFeature" class="param">indexFeature</a> .

