<h1 class="module">GeniaTagger</h1>

## Synopsis

Runs Genia Tagger on annotations.

## Description

*GeniaTagger* executes theGenia Tagger on annotations from the layer <a href="#words" class="param">words</a> and record the results in the features specified by <a href="#pos" class="param">pos</a>, <a href="#lemma" class="param">lemma</a>, <a href="#chunk" class="param">chunk</a> and <a href="#entity" class="param">entity</a>. *GeniaTagger* reinforces sentences specified by annotations in the <a href="#sentences" class="param">sentences</a> layer.

## Parameters

<a name="geniaDir">

### geniaDir

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.io.File" class="converter">File</a>
</div>
Directory where geniatagger is installed.

<a name="chunkFeature">

### chunkFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<a name="entityFeature">

### entityFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="geniaCharset">

### geniaCharset

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character encoding of geniatagger input and output.

<a name="geniaTaggerExecutable">

### geniaTaggerExecutable

<div class="param-level param-level-default-value">Default value: `geniatagger`
</div>
<div class="param-type">Type: <a href="../converter/java.io.File" class="converter">File</a>
</div>
Name of the geniatagger executable file.

<a name="lemmaFeature">

### lemmaFeature

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<a name="posFeature">

### posFeature

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `boolean:and(true, boolean:and(nav:layer:sentences(), nav:layer:words()))`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="sentenceFilter">

### sentenceFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Evaluated as a boolean with the sentence annotation as the context element. *GeniaTagger* only process the sentence if the result is true. To filter sentences that are too long for Genia Tagger, use "length < 1024".

<a name="sentenceLayerName">

### sentenceLayerName

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<a name="treeTaggerTagset">

### treeTaggerTagset

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
UNDOCUMENTED

<a name="wordFormFeature">

### wordFormFeature

<div class="param-level param-level-default-value">Default value: `form`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<a name="wordLayerName">

### wordLayerName

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

