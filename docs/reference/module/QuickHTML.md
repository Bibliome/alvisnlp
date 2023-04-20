<h1 class="module">QuickHTML</h1>

## Synopsis

Exports part of the layer contents in HTML.

**This module is experimental.**

## Description

 *QuickHTML* exports the annotations in HTML. Open with a browser the `index.html` file created in <a href="#outDir" class="param">outDir</a> to vizualize exported annotations.

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<quickhtml class="QuickHTML">
    <mentionLayers></mentionLayers>
    <outDir></outDir>
    <typeFeature></typeFeature>
</quickhtml>
```

## Mandatory parameters

<h3 id="mentionLayers" class="param">mentionLayers</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Layers containing mentions (or named entities).

<h3 id="outDir" class="param">outDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>
Directory where to place exported files.

<h3 id="typeFeature" class="param">typeFeature</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the type of the mention.

## Optional parameters

<h3 id="colorMap" class="param">colorMap</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Map of colors used to render mentions. Keys are mention types and values are colors in CSS syntax.

<h3 id="layoutLayer" class="param">layoutLayer</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Layer containing layout (HTML) annotations, like `i` , `b` or `p` .

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="documentTitle" class="param">documentTitle</h3>

<div class="param-level param-level-default-value">Default value: `@id`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string representing the document title.

<h3 id="features" class="param">features</h3>

<div class="param-level param-level-default-value">Default value: ``
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Mention features to display.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

<h3 id="tagFeature" class="param">tagFeature</h3>

<div class="param-level param-level-default-value">Default value: `tag`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the tag name for layout annotations.

## Deprecated parameters

<h3 id="classFeature" class="param">classFeature</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#typeFeature" class="param">typeFeature</a> /

<h3 id="colors" class="param">colors</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
List of colors used to render mentions. This is deprecated, use <a href="#colorMap" class="param">colorMap</a> instead.

<h3 id="layers" class="param">layers</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Deprecated alias for <a href="#mentionLayers" class="param">mentionLayers</a> .

