<h1 class="module">GeniaTagger</h1>

## Synopsis

Runs Genia Tagger on annotations.

## Description

*GeniaTagger* executes theGenia Tagger on annotations from the layer <a href="#words" class="param">words</a> and record the results in the features specified by <a href="#pos" class="param">pos</a>, <a href="#lemma" class="param">lemma</a>, <a href="#chunk" class="param">chunk</a> and <a href="#entity" class="param">entity</a>. *GeniaTagger* reinforces sentences specified by annotations in the <a href="#sentences" class="param">sentences</a> layer.

## Mandatory parameters

<h3 name="geniaDir" class="param">geniaDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.io.File" class="converter">File</a>
</div>
Directory where geniatagger is installed.

## Optional parameters

<h3 name="chunkFeature" class="param">chunkFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 name="entityFeature" class="param">entityFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 name="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 name="geniaCharset" class="param">geniaCharset</h3>

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character encoding of geniatagger input and output.

<h3 name="geniaTaggerExecutable" class="param">geniaTaggerExecutable</h3>

<div class="param-level param-level-default-value">Default value: `./geniatagger`
</div>
<div class="param-type">Type: <a href="../converter/java.io.File" class="converter">File</a>
</div>
Name of the geniatagger executable file.

<h3 name="lemmaFeature" class="param">lemmaFeature</h3>

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 name="posFeature" class="param">posFeature</h3>

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 name="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `boolean:and(true, boolean:and(nav:layer:sentences(), nav:layer:words()))`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

<h3 name="sentenceFilter" class="param">sentenceFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Evaluated as a boolean with the sentence annotation as the context element. *GeniaTagger* only process the sentence if the result is true. To filter sentences that are too long for Genia Tagger, use "length < 1024".

<h3 name="sentenceLayerName" class="param">sentenceLayerName</h3>

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 name="treeTaggerTagset" class="param">treeTaggerTagset</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
UNDOCUMENTED

<h3 name="wordFormFeature" class="param">wordFormFeature</h3>

<div class="param-level param-level-default-value">Default value: `form`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 name="wordLayerName" class="param">wordLayerName</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

